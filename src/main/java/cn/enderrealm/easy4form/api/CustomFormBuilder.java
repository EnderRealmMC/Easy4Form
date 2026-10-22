package cn.enderrealm.easy4form.api;

import cn.enderrealm.easy4form.Easy4form;
import cn.enderrealm.easy4form.internal.CustomComponent;
import cn.enderrealm.easy4form.internal.FormDefinition;
import cn.enderrealm.easy4form.internal.FormProvider;
import cn.enderrealm.easy4form.internal.FormResponse;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * 用于创建自定义表单的构建器。
 */
public class CustomFormBuilder {

    private final List<CustomComponent> components = new ArrayList<>();
    private String title;
    private Consumer<Map<String, Object>> responseHandler;

    /**
     * 创建新的 CustomFormBuilder 实例。
     */
    public CustomFormBuilder() {
    }

    /**
     * 创建新的 CustomFormBuilder 实例。
     *
     * @return 新的构建器实例
     */
    public static CustomFormBuilder create() {
        return new CustomFormBuilder();
    }

    /**
     * 设置表单标题。
     *
     * @param title 标题
     * @return 构建器实例
     */
    public CustomFormBuilder title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 添加标签。
     *
     * @param id   组件 ID
     * @param text 标签文本
     * @return 构建器实例
     */
    public CustomFormBuilder label(String id, String text) {
        components.add(new CustomComponent.Label(id, text));
        return this;
    }

    /**
     * 添加标签并自动生成组件 ID。
     *
     * @param text 标签文本
     * @return 构建器实例
     */
    public CustomFormBuilder label(String text) {
        return label("label_" + components.size(), text);
    }

    /**
     * 添加文本输入框。
     *
     * @param id           组件 ID
     * @param text         标签文本
     * @param placeholder  占位符文本
     * @param defaultValue 默认值
     * @return 构建器实例
     */
    public CustomFormBuilder input(String id, String text, String placeholder, String defaultValue) {
        components.add(new CustomComponent.Input(id, text, placeholder, defaultValue));
        return this;
    }

    /**
     * 添加开关。
     *
     * @param id           组件 ID
     * @param text         标签文本
     * @param defaultValue 默认值
     * @return 构建器实例
     */
    public CustomFormBuilder toggle(String id, String text, boolean defaultValue) {
        components.add(new CustomComponent.Toggle(id, text, defaultValue));
        return this;
    }

    /**
     * 添加滑块。
     *
     * @param id           组件 ID
     * @param text         标签文本
     * @param min          最小值
     * @param max          最大值
     * @param step         步长
     * @param defaultValue 默认值
     * @return 构建器实例
     */
    public CustomFormBuilder slider(String id, String text, float min, float max, float step, float defaultValue) {
        components.add(new CustomComponent.Slider(id, text, min, max, step, defaultValue));
        return this;
    }

    /**
     * 添加步进滑块。
     *
     * @param id          组件 ID
     * @param text        标签文本
     * @param steps       步骤选项
     * @param defaultStep 默认步骤下标
     * @return 构建器实例
     */
    public CustomFormBuilder stepSlider(String id, String text, List<String> steps, int defaultStep) {
        components.add(new CustomComponent.StepSlider(id, text, steps, defaultStep));
        return this;
    }

    /**
     * 添加下拉框。
     *
     * @param id            组件 ID
     * @param text          标签文本
     * @param options       选项
     * @param defaultOption 默认选项下标
     * @return 构建器实例
     */
    public CustomFormBuilder dropdown(String id, String text, List<String> options, int defaultOption) {
        components.add(new CustomComponent.Dropdown(id, text, options, defaultOption));
        return this;
    }

    /**
     * 设置响应处理器。
     *
     * @param responseHandler 响应处理器
     * @return 构建器实例
     */
    public CustomFormBuilder responseHandler(Consumer<Map<String, Object>> responseHandler) {
        this.responseHandler = responseHandler;
        return this;
    }

    /**
     * 向玩家发送表单。
     *
     * @param player 接收表单的玩家
     */
    public void send(Player player) {
        try {
            FormProvider provider = Easy4form.getInstance().getFormProviderRegistry().find(player);
            if (provider == null) {
                return;
            }
            FormDefinition.Custom form = new FormDefinition.Custom(title, components);
            Consumer<FormResponse> handler = responseHandler == null ? null : response -> {
                if (response instanceof FormResponse.Custom custom) {
                    responseHandler.accept(custom.values());
                    return;
                }
                responseHandler.accept(new HashMap<>());
            };
            provider.send(player, form, handler);
        } catch (Exception exception) {
            Easy4form.getInstance().getLogger().log(
                    Level.SEVERE,
                    "Failed to send custom form: " + exception.getMessage(),
                    exception
            );
        }
    }
}
