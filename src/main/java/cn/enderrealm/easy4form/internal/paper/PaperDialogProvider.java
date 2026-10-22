package cn.enderrealm.easy4form.internal.paper;

import cn.enderrealm.easy4form.Easy4form;
import cn.enderrealm.easy4form.internal.ButtonDefinition;
import cn.enderrealm.easy4form.internal.CustomComponent;
import cn.enderrealm.easy4form.internal.FormDefinition;
import cn.enderrealm.easy4form.internal.FormPlatform;
import cn.enderrealm.easy4form.internal.FormProvider;
import cn.enderrealm.easy4form.internal.FormResponse;
import cn.enderrealm.easy4form.utils.PlayerUtils;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * 使用 Paper Dialog API 发送 Java 版表单的 Provider。
 *
 * <p>Paper 1.21.8 没有 Dialog 关闭事件，协议中也没有客户端到服务端的关闭包。
 * 因此这里利用原版 {@code multi_action} 的 {@code exit_action}（Escape 与页脚按钮共用）
 * 绑定一个自定义点击动作，从而在关闭时收到 {@code PlayerCustomClickEvent}。</p>
 *
 * <p>该类只能在 Paper 1.21.8+ 与 Java 21 环境中加载，插件主类通过反射创建实例，
 * 初始化完成后所有表单调用都走 {@link FormProvider} 接口。</p>
 */
public final class PaperDialogProvider implements FormProvider {

    /**
     * 自定义点击动作使用的命名空间。
     */
    static final String NAMESPACE = "easy4form";

    /**
     * 支持 Dialog 的最低客户端协议版本，对应 Minecraft 1.21.6。
     */
    static final int MIN_CLIENT_PROTOCOL = 771;

    private static final int INPUT_WIDTH = 200;
    private static final int MAX_TEXT_LENGTH = 256;

    private final Easy4form plugin;
    private final ViaProtocolHook viaProtocolHook;
    private final PaperDialogListener listener;
    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    /**
     * 创建 Java Dialog Provider 并注册事件监听器。
     *
     * <p>服务端缺少 Dialog API 或版本过低时，构造函数会抛出链接错误，
     * 由插件主类捕获并降级为不启用。</p>
     */
    public PaperDialogProvider() {
        this.plugin = Easy4form.getInstance();
        if (plugin == null) {
            throw new IllegalStateException("Easy4Form is not initialized");
        }
        this.viaProtocolHook = ViaProtocolHook.create();
        this.listener = new PaperDialogListener(this);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public String name() {
        return "paper";
    }

    @Override
    public FormPlatform platform() {
        return FormPlatform.JAVA_DIALOG;
    }

    @Override
    public boolean supports(Player player) {
        return player != null
                && !PlayerUtils.isBedrockPlayer(player)
                && viaProtocolHook.isAtLeast(player, MIN_CLIENT_PROTOCOL);
    }

    @Override
    public boolean send(Player player, FormDefinition form, Consumer<FormResponse> responseHandler) {
        if (player == null || form == null) {
            return false;
        }
        Session session = new Session(form, responseHandler);
        Dialog dialog = buildDialog(form, session);
        if (dialog == null) {
            return false;
        }
        sessions.put(player.getUniqueId(), session);
        try {
            player.showDialog(dialog);
            return true;
        } catch (RuntimeException exception) {
            sessions.remove(player.getUniqueId(), session);
            throw exception;
        }
    }

    @Override
    public void close() {
        HandlerList.unregisterAll(listener);
        sessions.clear();
    }

    /**
     * 根据表单定义构建 Dialog。
     *
     * @param form    表单定义
     * @param session 本次发送的会话
     * @return Dialog 实例，不支持的表单类型返回 null
     */
    private Dialog buildDialog(FormDefinition form, Session session) {
        if (form instanceof FormDefinition.Simple simple) {
            return buildSimple(simple, session);
        }
        if (form instanceof FormDefinition.Modal modal) {
            return buildModal(modal, session);
        }
        if (form instanceof FormDefinition.Custom custom) {
            return buildCustom(custom, session);
        }
        return null;
    }

    /**
     * 构建简单表单，对应 Java 版的多动作 Dialog。
     *
     * @param form    简单表单
     * @param session 本次发送的会话
     * @return Dialog 实例
     */
    private Dialog buildSimple(FormDefinition.Simple form, Session session) {
        List<ActionButton> actions = new ArrayList<>(form.buttons().size());
        for (int index = 0; index < form.buttons().size(); index++) {
            ButtonDefinition button = form.buttons().get(index);
            actions.add(actionButton(button.text(), session.key("b" + index)));
        }
        return createDialog(
                form.title(),
                body(form.content()),
                List.of(),
                DialogType.multiAction(actions, closeButton(session), 1)
        );
    }

    /**
     * 构建模态表单，对应 Java 版的多动作 Dialog。
     *
     * <p>不使用 confirmation 类型，因为确认框的 Escape 等价于点击“否”，
     * 无法与显式拒绝区分；多动作 Dialog 可以让关闭走独立的 exit_action。</p>
     *
     * @param form    模态表单
     * @param session 本次发送的会话
     * @return Dialog 实例
     */
    private Dialog buildModal(FormDefinition.Modal form, Session session) {
        List<ActionButton> actions = List.of(
                actionButton(form.button1(), session.key("m0")),
                actionButton(form.button2(), session.key("m1"))
        );
        return createDialog(
                form.title(),
                body(form.content()),
                List.of(),
                DialogType.multiAction(actions, closeButton(session), 1)
        );
    }

    /**
     * 构建自定义表单，对应带输入控件的 Java 版多动作 Dialog。
     *
     * @param form    自定义表单
     * @param session 本次发送的会话
     * @return Dialog 实例
     */
    private Dialog buildCustom(FormDefinition.Custom form, Session session) {
        List<DialogBody> bodies = new ArrayList<>();
        List<DialogInput> inputs = new ArrayList<>();
        int index = 0;
        for (CustomComponent component : form.components()) {
            addComponent(component, index, bodies, inputs);
            index++;
        }
        String submitText = plugin.getConfig().getString("java-dialog-text.submit", "Submit");
        return createDialog(
                form.title(),
                bodies,
                inputs,
                DialogType.multiAction(
                        List.of(actionButton(submitText, session.key("submit"))),
                        closeButton(session),
                        1
                )
        );
    }

    /**
     * 创建统一配置的 Dialog。
     *
     * @param title  标题
     * @param body   正文
     * @param inputs 输入组件
     * @param type   Dialog 类型
     * @return Dialog 实例
     */
    private Dialog createDialog(
            String title,
            List<DialogBody> body,
            List<DialogInput> inputs,
            DialogType type
    ) {
        DialogBase base = DialogBase.builder(component(title))
                .canCloseWithEscape(true)
                .pause(false)
                .afterAction(DialogBase.DialogAfterAction.CLOSE)
                .body(body)
                .inputs(inputs)
                .build();
        return Dialog.create(builder -> builder.empty().base(base).type(type));
    }

    /**
     * 将平台无关组件映射为 Java Dialog 的正文或输入控件。
     *
     * @param component 平台无关组件
     * @param index     组件在表单中的绝对下标
     * @param bodies    正文列表
     * @param inputs    输入列表
     */
    private void addComponent(
            CustomComponent component,
            int index,
            List<DialogBody> bodies,
            List<DialogInput> inputs
    ) {
        String key = "i" + index;
        if (component instanceof CustomComponent.Label label) {
            bodies.add(DialogBody.plainMessage(component(label.text())));
            return;
        }
        if (component instanceof CustomComponent.Input input) {
            String initial = input.defaultValue() == null ? "" : input.defaultValue();
            inputs.add(DialogInput.text(
                    key,
                    INPUT_WIDTH,
                    component(input.text()),
                    true,
                    initial,
                    MAX_TEXT_LENGTH,
                    null
            ));
            return;
        }
        if (component instanceof CustomComponent.Toggle toggle) {
            inputs.add(DialogInput.bool(
                    key,
                    component(toggle.text()),
                    toggle.defaultValue(),
                    "On",
                    "Off"
            ));
            return;
        }
        if (component instanceof CustomComponent.Slider slider) {
            Float step = slider.step() > 0 ? Float.valueOf(slider.step()) : null;
            inputs.add(DialogInput.numberRange(
                    key,
                    INPUT_WIDTH,
                    component(slider.text()),
                    "%s: %s",
                    slider.min(),
                    slider.max(),
                    slider.defaultValue(),
                    step
            ));
            return;
        }
        if (component instanceof CustomComponent.StepSlider stepSlider) {
            addSingleOption(
                    key,
                    component(stepSlider.text()),
                    stepSlider.steps(),
                    stepSlider.defaultStep(),
                    inputs
            );
            return;
        }
        if (component instanceof CustomComponent.Dropdown dropdown) {
            addSingleOption(
                    key,
                    component(dropdown.text()),
                    dropdown.options(),
                    dropdown.defaultOption(),
                    inputs
            );
        }
    }

    /**
     * 添加单选下拉输入。
     *
     * @param key          输入键
     * @param label        标题
     * @param options      选项文本
     * @param defaultIndex 默认选项下标
     * @param inputs       输入列表
     */
    private void addSingleOption(
            String key,
            Component label,
            List<String> options,
            int defaultIndex,
            List<DialogInput> inputs
    ) {
        if (options.isEmpty()) {
            return;
        }
        int initialIndex = Math.max(0, Math.min(defaultIndex, options.size() - 1));
        List<SingleOptionDialogInput.OptionEntry> entries = new ArrayList<>(options.size());
        for (int index = 0; index < options.size(); index++) {
            entries.add(SingleOptionDialogInput.OptionEntry.create(
                    Integer.toString(index),
                    component(options.get(index)),
                    index == initialIndex
            ));
        }
        inputs.add(DialogInput.singleOption(key, INPUT_WIDTH, entries, label, true));
    }

    /**
     * 创建带自定义点击动作的按钮。
     *
     * @param text 按钮文本
     * @param key  自定义点击动作键
     * @return 按钮实例
     */
    private static ActionButton actionButton(String text, String key) {
        return ActionButton.builder(component(text))
                .action(DialogAction.customClick(Key.key(NAMESPACE, key), null))
                .build();
    }

    /**
     * 创建页脚关闭按钮，同时接管 Escape。
     *
     * @param session 本次发送的会话
     * @return 关闭按钮
     */
    private ActionButton closeButton(Session session) {
        return actionButton(
                plugin.getConfig().getString("java-dialog-text.close", "Close"),
                session.key("close")
        );
    }

    /**
     * 处理 Dialog 自定义点击动作。
     *
     * @param player       点击的玩家
     * @param keyValue     动作键
     * @param responseView 输入响应，可能为 null
     */
    void handleClick(Player player, String keyValue, DialogResponseView responseView) {
        Session session = sessions.get(player.getUniqueId());
        if (session == null || !keyValue.startsWith(session.prefix)) {
            return;
        }
        String suffix = keyValue.substring(session.prefix.length());
        FormResponse response = responseFor(session.form, suffix, responseView);
        if (response == null) {
            return;
        }
        sessions.remove(player.getUniqueId(), session);
        Consumer<FormResponse> responseHandler = session.responseHandler;
        if (responseHandler == null) {
            return;
        }
        try {
            responseHandler.accept(response);
        } catch (RuntimeException exception) {
            plugin.getLogger().log(Level.SEVERE, "Failed to handle Java dialog response", exception);
        }
    }

    /**
     * 将点击动作映射为平台无关响应。
     *
     * @param form         表单定义
     * @param suffix       会话键后缀
     * @param responseView 输入响应，可能为 null
     * @return 表单响应，无法识别时返回 null
     */
    private FormResponse responseFor(
            FormDefinition form,
            String suffix,
            DialogResponseView responseView
    ) {
        if ("close".equals(suffix)) {
            return new FormResponse.Closed();
        }
        if (form instanceof FormDefinition.Simple simple) {
            if (!suffix.startsWith("b")) {
                return null;
            }
            Integer index = parseInt(suffix.substring(1));
            if (index == null || index < 0 || index >= simple.buttons().size()) {
                return null;
            }
            return new FormResponse.Simple(index);
        }
        if (form instanceof FormDefinition.Modal) {
            if ("m0".equals(suffix)) {
                return new FormResponse.Modal(true);
            }
            if ("m1".equals(suffix)) {
                return new FormResponse.Modal(false);
            }
            return null;
        }
        if (form instanceof FormDefinition.Custom custom) {
            if (!"submit".equals(suffix)) {
                return null;
            }
            return new FormResponse.Custom(readCustomValues(custom, responseView));
        }
        return null;
    }

    /**
     * 按组件顺序读取自定义表单的输入值。
     *
     * <p>与 Cumulus 行为保持一致：label 也占用一个组件下标并映射为 null。</p>
     *
     * @param form         自定义表单
     * @param responseView 输入响应，可能为 null
     * @return 组件 ID 到值的映射
     */
    private Map<String, Object> readCustomValues(
            FormDefinition.Custom form,
            DialogResponseView responseView
    ) {
        Map<String, Object> values = new LinkedHashMap<>();
        int index = 0;
        for (CustomComponent component : form.components()) {
            String key = "i" + index;
            Object value;
            if (component instanceof CustomComponent.Label) {
                value = null;
            } else if (component instanceof CustomComponent.Input) {
                value = responseView == null ? null : responseView.getText(key);
            } else if (component instanceof CustomComponent.Toggle) {
                value = responseView == null ? null : responseView.getBoolean(key);
            } else if (component instanceof CustomComponent.Slider) {
                value = responseView == null ? null : responseView.getFloat(key);
            } else if (component instanceof CustomComponent.StepSlider
                    || component instanceof CustomComponent.Dropdown) {
                value = responseView == null ? null : parseInt(responseView.getText(key));
            } else {
                value = null;
            }
            values.put(component.id(), value);
            index++;
        }
        return values;
    }

    /**
     * 移除玩家的待处理会话。
     *
     * @param player 玩家
     */
    void remove(Player player) {
        if (player != null) {
            sessions.remove(player.getUniqueId());
        }
    }

    /**
     * 解析输入值下标。
     *
     * @param value 文本值
     * @return 下标；缺失或非法时返回 null
     */
    private static Integer parseInt(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * 将旧版颜色代码文本转换为 Adventure 组件。
     *
     * @param text 文本
     * @return 组件
     */
    private static Component component(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        return LegacyComponentSerializer.legacySection().deserialize(text);
    }

    /**
     * 正文列表；空内容不生成正文。
     *
     * @param content 正文文本
     * @return 正文列表
     */
    private static List<DialogBody> body(String content) {
        if (content == null || content.isEmpty()) {
            return List.of();
        }
        return List.of(DialogBody.plainMessage(component(content)));
    }

    /**
     * 一次表单发送的会话。
     */
    private static final class Session {

        private final String prefix;
        private final FormDefinition form;
        private final Consumer<FormResponse> responseHandler;

        private Session(FormDefinition form, Consumer<FormResponse> responseHandler) {
            long nonce = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
            this.prefix = "f" + Long.toString(nonce, 36) + "_";
            this.form = form;
            this.responseHandler = responseHandler;
        }

        /**
         * 生成带会话前缀的动作键。
         *
         * @param suffix 后缀
         * @return 动作键
         */
        private String key(String suffix) {
            return prefix + suffix;
        }
    }
}
