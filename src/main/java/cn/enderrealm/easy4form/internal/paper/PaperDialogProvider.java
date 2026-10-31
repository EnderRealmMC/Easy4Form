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
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
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
 * <p>Paper 1.21.7 没有 Dialog 关闭事件，协议中也没有客户端到服务端的关闭包。
 * 因此这里利用原版 {@code multi_action} 的 {@code exit_action}（Escape 与页脚按钮共用）
 * 绑定一个自定义点击动作，从而在关闭时收到 {@code PlayerCustomClickEvent}。</p>
 *
 * <p>该类只能在 Paper 1.21.7+ 与 Java 21 环境中加载，插件主类通过反射创建实例，
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

    /**
     * 按钮统一宽度；原版默认过窄，长中文按钮名会被截断。
     */
    private static final int BUTTON_WIDTH = 200;

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
     * <p>
     * 无按钮时对应原版通知 Dialog（参考 ViaBedrock 对 ActionForm 的处理）。</p>
     *
     * @param form    简单表单
     * @param session 本次发送的会话
     * @return Dialog 实例
     */
    private Dialog buildSimple(FormDefinition.Simple form, Session session) {
        if (form.buttons().isEmpty()) {
            return createDialog(
                    form.title(),
                    body(form.content()),
                    List.of(),
                    DialogType.notice(closeButton(session)),
                    true
            );
        }
        List<ActionButton> actions = new ArrayList<>(form.buttons().size());
        for (int index = 0; index < form.buttons().size(); index++) {
            ButtonDefinition button = form.buttons().get(index);
            actions.add(actionButton(button.text(), session.key("b" + index)));
        }
        return createDialog(
                form.title(),
                body(form.content()),
                List.of(),
                DialogType.multiAction(actions, closeButton(session), 1),
                true
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
                DialogType.multiAction(actions, closeButton(session), 1),
                true
        );
    }

    /**
     * 构建自定义表单，对应带输入控件的 Java 版多动作 Dialog。
     * <p>
     * 对齐 ViaBedrock 的自定义表单处理：禁止 Escape 关闭（避免误触丢失已输入内容）。
     * 提交按钮置于正文动作区；取消按钮作为页脚退出按钮固定显示，无需滚动即可关闭，
     * 与基岩版右上角固定关闭按钮的体验保持一致。</p>
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
        List<ActionButton> actions = List.of(
                actionButton(dialogButtonComponent("submit-key", "gui.done", "submit", "Submit"), session.key("submit"))
        );
        return createDialog(
                form.title(),
                bodies,
                inputs,
                DialogType.multiAction(actions, closeButton(session), 1),
                false
        );
    }

    /**
     * 创建统一配置的 Dialog。
     *
     * @param title              标题
     * @param body               正文
     * @param inputs             输入组件
     * @param type               Dialog 类型
     * @param canCloseWithEscape 是否允许 Escape 关闭
     * @return Dialog 实例
     */
    private Dialog createDialog(
            String title,
            List<DialogBody> body,
            List<DialogInput> inputs,
            DialogType type,
            boolean canCloseWithEscape
    ) {
        DialogBase base = DialogBase.builder(component(sanitizeSingleLine(title)))
                .canCloseWithEscape(canCloseWithEscape)
                .pause(false)
                .afterAction(DialogBase.DialogAfterAction.CLOSE)
                .body(body)
                .inputs(inputs)
                .build();
        return Dialog.create(builder -> builder.empty().base(base).type(type));
    }

    /**
     * 将平台无关组件映射为 Java Dialog 的正文或输入控件。
     * <p>
     * Java Dialog 的正文整体渲染在输入控件之前，而基岩表单按元素顺序混排文本与控件。
     * 因此纯文本采用即时判定：尚无输入控件时放入正文（顺序正确），
     * 已有输入控件后则插入只读单行假输入保持顺序（参考 ViaBedrock 的处理）。
     * 控件标签忠实翻译，不拆行、不前置；多行文本的呈现由内容侧约束。</p>
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
            addPlainText(label.text(), index, bodies, inputs);
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
            // 标题外置：单选控件的按钮内嵌"标题: 选中项"格式，长标题会拉长整行，
            // 因此标题文本前置为独立文本行，控件内仅显示选中项
            addPlainText(stepSlider.text(), index, bodies, inputs);
            addSingleOption(
                    key,
                    Component.empty(),
                    stepSlider.steps(),
                    stepSlider.defaultStep(),
                    inputs
            );
            return;
        }
        if (component instanceof CustomComponent.Dropdown dropdown) {
            addPlainText(dropdown.text(), index, bodies, inputs);
            addSingleOption(
                    key,
                    Component.empty(),
                    dropdown.options(),
                    dropdown.defaultOption(),
                    inputs
            );
        }
    }

    /**
     * 添加纯文本行：尚无输入控件时放入正文，已有输入控件后插入只读假输入保持顺序。
     *
     * @param text    文本
     * @param index   组件下标，用于生成假输入键
     * @param bodies  正文列表
     * @param inputs  输入列表
     */
    private void addPlainText(
            String text,
            int index,
            List<DialogBody> bodies,
            List<DialogInput> inputs
    ) {
        for (String line : splitLines(text)) {
            // 空行跳过：空标签的假输入框会渲染成一条空框线，形似分割线
            if (line.isEmpty()) {
                continue;
            }
            if (inputs.isEmpty()) {
                bodies.add(DialogBody.plainMessage(component(line)));
            } else {
                inputs.add(dummyTextInput(line, index));
            }
        }
    }

    /**
     * 创建展示一行文本的只读假输入。
     * <p>
     * Java Dialog 没有纯文本输入元素，这里用单行多行文本框模拟；
     * 输入键固定不参与响应读取，仅用于保持元素顺序（参考 ViaBedrock 的 dummy 输入）。</p>
     *
     * @param line  文本行
     * @param index 组件下标
     * @return 假输入
     */
    private DialogInput dummyTextInput(String line, int index) {
        return DialogInput.text(
                "t" + index,
                INPUT_WIDTH,
                component(line),
                true,
                "",
                Integer.MAX_VALUE,
                TextDialogInput.MultilineOptions.create(null, 1)
        );
    }

    /**
     * 添加单选下拉输入。
     * <p>
     * Java 版没有原生下拉组件，由 {@code single_option} 输入承担；
     * 标题已由调用方前置为文本行，这里隐藏标题仅显示选中项，
     * 避免空标题产生孤立冒号；选项为单行组件，换行符清洗为空格。</p>
     *
     * @param key          输入键
     * @param label        标题（不渲染，仅为满足 API 签名）
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
            // 基岩版空下拉的响应契约为下标 0；Java 编码要求选项非空，
            // 补一个视觉为空的选项保持响应一致（参考 ViaBedrock）。
            inputs.add(DialogInput.singleOption(
                    key,
                    INPUT_WIDTH,
                    List.of(SingleOptionDialogInput.OptionEntry.create(
                            "0", Component.empty(), true
                    )),
                    label,
                    false
            ));
            return;
        }
        int initialIndex = Math.max(0, Math.min(defaultIndex, options.size() - 1));
        List<SingleOptionDialogInput.OptionEntry> entries = new ArrayList<>(options.size());
        for (int index = 0; index < options.size(); index++) {
            entries.add(SingleOptionDialogInput.OptionEntry.create(
                    Integer.toString(index),
                    component(sanitizeSingleLine(options.get(index))),
                    index == initialIndex
            ));
        }
        inputs.add(DialogInput.singleOption(key, INPUT_WIDTH, entries, label, false));
    }

    /**
     * 创建带自定义点击动作的按钮。
     * <p>
     * Java 版文本组件在按钮标签中不渲染换行符（原版渲染限制），
     * 保留换行符会显示为占位字符，这里清洗为空格；
     * 统一加宽按钮避免长文本截断。</p>
     *
     * @param text 按钮文本
     * @param key  自定义点击动作键
     * @return 按钮实例
     */
    private static ActionButton actionButton(String text, String key) {
        return actionButton(component(sanitizeSingleLine(text)), key);
    }

    /**
     * 创建带自定义点击动作的按钮，标签直接使用已构造的组件。
     *
     * <p>用于 {@link Component#translatable(String)} 等无需旧版颜色代码清洗的标签，
     * 例如库内按钮的原版翻译键；统一加宽按钮避免长文本截断。</p>
     *
     * @param label 按钮标签组件
     * @param key   自定义点击动作键
     * @return 按钮实例
     */
    private static ActionButton actionButton(Component label, String key) {
        return ActionButton.builder(label)
                .width(BUTTON_WIDTH)
                .action(DialogAction.customClick(Key.key(NAMESPACE, key), null))
                .build();
    }

    /**
     * 解析库内按钮的标签组件。
     *
     * <p>默认使用原版翻译键（{@code submit-key} / {@code close-key}），由客户端按自身
     * 语言文件解析，自动覆盖全部 Minecraft 语言且无需维护任何翻译；仅在配置显式留空时
     * 回退到固定文本，兼容旧版 {@code §} 颜色代码。</p>
     *
     * <p>注意：必须用 {@code getString(path, keyDefault)} 的默认值参数承载原版键，而非
     * 依赖 Bukkit 的 defaults 合并层——双参 {@code getString} 在路径缺失时直接返回
     * {@code def}，不会回退到 jar 内 config.yml 默认值。</p>
     *
     * @param keyField    翻译键配置项名（{@code java-dialog-text} 下的子键）
     * @param keyDefault  翻译键的兜底默认值（原版翻译键）
     * @param textField   固定文本配置项名（翻译键显式留空时使用）
     * @param textDefault 固定文本的兜底默认值
     * @return 按钮标签组件
     */
    private Component dialogButtonComponent(String keyField, String keyDefault, String textField, String textDefault) {
        String translationKey = plugin.getConfig().getString("java-dialog-text." + keyField, keyDefault);
        if (translationKey != null && !translationKey.trim().isEmpty()) {
            return Component.translatable(translationKey.trim());
        }
        return component(sanitizeSingleLine(
                plugin.getConfig().getString("java-dialog-text." + textField, textDefault)
        ));
    }

    /**
     * 创建页脚关闭按钮，同时接管 Escape。
     *
     * @param session 本次发送的会话
     * @return 关闭按钮
     */
    private ActionButton closeButton(Session session) {
        return actionButton(
                dialogButtonComponent("close-key", "gui.cancel", "close", "Close"),
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
     * 将文本按换行符拆分为行。
     *
     * @param text 原始文本
     * @return 行列表，至少包含一行
     */
    private static String[] splitLines(String text) {
        if (text == null || text.isEmpty()) {
            return new String[]{""};
        }
        return text.split("\n", -1);
    }

    /**
     * 将文本清洗为单行：换行符替换为空格。
     * <p>
     * Java 版文本组件在按钮标签、标题、选项等单行表面不渲染换行符，
     * 保留会显示为占位字符（PUA）。</p>
     *
     * @param text 原始文本
     * @return 单行文本
     */
    private static String sanitizeSingleLine(String text) {
        if (text == null || text.indexOf('\n') < 0) {
            return text;
        }
        return text.replace('\n', ' ');
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
     * <p>
     * Java Dialog 的正文组件不渲染换行符，需要按行拆分为多个正文条目
     * （参考 ViaBedrock 的表单转换实现）。</p>
     *
     * @param content 正文文本
     * @return 正文列表
     */
    private static List<DialogBody> body(String content) {
        if (content == null || content.isEmpty()) {
            return List.of();
        }
        List<DialogBody> bodies = new ArrayList<>();
        for (String line : splitLines(content)) {
            bodies.add(DialogBody.plainMessage(component(line)));
        }
        return bodies;
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
