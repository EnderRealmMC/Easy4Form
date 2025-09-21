package cn.enderrealm.easy4form.api;

import cn.enderrealm.easy4form.Easy4form;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Builder for creating custom forms - Legacy API Proxy
 * <p>
 * 用于创建自定义表单的构建器 - 传统API代理
 * 
 * @deprecated This class is deprecated and will be removed in future versions.
 *             Please use {@link cn.enderrealm.easy4form.apiv1.CustomFormBuilder} instead.
 *             此类已弃用，将在未来版本中移除。请使用 {@link cn.enderrealm.easy4form.apiv1.CustomFormBuilder} 代替。
 */
@Deprecated
public class CustomFormBuilder {
    private cn.enderrealm.easy4form.apiv1.CustomFormBuilder delegate;

    public CustomFormBuilder() {
        Easy4form.getInstance().getVersionManager().logMigrationWarning(
            "Using deprecated CustomFormBuilder. Please migrate to apiv1.CustomFormBuilder"
        );
        this.delegate = new cn.enderrealm.easy4form.apiv1.CustomFormBuilder();
    }

    /**
     * Set the title of the form
     * <p>
     * 设置表单标题
     *
     * @param title The title / 标题
     * @return This builder instance / 构建器实例
     * @deprecated Use {@link cn.enderrealm.easy4form.apiv1.CustomFormBuilder#title(String)} instead
     */
    @Deprecated
    public CustomFormBuilder title(String title) {
        delegate.title(title);
        return this;
    }

    private int labelCounter = 0;

    /**
     * Add a label to the form
     * <p>
     * 向表单添加标签
     *
     * @param text The label text / 标签文本
     * @return This builder instance / 构建器实例
     * @deprecated Use {@link cn.enderrealm.easy4form.apiv1.CustomFormBuilder#label(String, String)} instead
     */
    @Deprecated
    public CustomFormBuilder label(String text) {
        // Generate sequential ID for backward compatibility
        delegate.label("auto_label_" + (labelCounter++), text);
        return this;
    }

    /**
     * Add an input field to the form
     * <p>
     * 向表单添加输入框
     *
     * @param id The component ID / 组件ID
     * @param text The label text / 标签文本
     * @param placeholder The placeholder text / 占位符文本
     * @param defaultValue The default value / 默认值
     * @return The builder instance / 构建器实例
     * @deprecated Use {@link cn.enderrealm.easy4form.apiv1.CustomFormBuilder#input(String, String, String, String)} instead
     */
    @Deprecated
    public CustomFormBuilder input(String id, String text, String placeholder, String defaultValue) {
        delegate.input(id, text, placeholder, defaultValue);
        return this;
    }

    /**
     * Add a toggle (switch) to the form
     * <p>
     * 向表单添加开关
     *
     * @param id The component ID / 组件ID
     * @param text The label text / 标签文本
     * @param defaultValue The default value / 默认值
     * @return The builder instance / 构建器实例
     * @deprecated Use {@link cn.enderrealm.easy4form.apiv1.CustomFormBuilder#toggle(String, String, boolean)} instead
     */
    @Deprecated
    public CustomFormBuilder toggle(String id, String text, boolean defaultValue) {
        delegate.toggle(id, text, defaultValue);
        return this;
    }

    /**
     * Add a slider to the form
     * <p>
     * 向表单添加滑块
     *
     * @param id The component ID / 组件ID
     * @param text The label text / 标签文本
     * @param min The minimum value / 最小值
     * @param max The maximum value / 最大值
     * @param step The step value / 步长
     * @param defaultValue The default value / 默认值
     * @return The builder instance / 构建器实例
     * @deprecated Use {@link cn.enderrealm.easy4form.apiv1.CustomFormBuilder#slider(String, String, float, float, float, float)} instead
     */
    @Deprecated
    public CustomFormBuilder slider(String id, String text, float min, float max, float step, float defaultValue) {
        delegate.slider(id, text, min, max, step, defaultValue);
        return this;
    }

    /**
     * Add a step slider to the form
     * <p>
     * 向表单添加步进滑块
     *
     * @param id The component ID / 组件ID
     * @param text The label text / 标签文本
     * @param steps The steps / 步骤
     * @param defaultStep The default step / 默认步骤
     * @return The builder instance / 构建器实例
     * @deprecated Use {@link cn.enderrealm.easy4form.apiv1.CustomFormBuilder#stepSlider(String, String, List, int)} instead
     */
    @Deprecated
    public CustomFormBuilder stepSlider(String id, String text, List<String> steps, int defaultStep) {
        delegate.stepSlider(id, text, steps, defaultStep);
        return this;
    }

    /**
     * Add a dropdown to the form
     * <p>
     * 向表单添加下拉菜单
     *
     * @param id The component ID / 组件ID
     * @param text The label text / 标签文本
     * @param options The options / 选项
     * @param defaultOption The default option / 默认选项
     * @return The builder instance / 构建器实例
     * @deprecated Use {@link cn.enderrealm.easy4form.apiv1.CustomFormBuilder#dropdown(String, String, List, int)} instead
     */
    @Deprecated
    public CustomFormBuilder dropdown(String id, String text, List<String> options, int defaultOption) {
        delegate.dropdown(id, text, options, defaultOption);
        return this;
    }

    /**
     * Set the response handler for the form
     * <p>
     * 设置表单的响应处理器
     *
     * @param responseHandler The response handler / 响应处理器
     * @return The builder instance / 构建器实例
     * @deprecated Use {@link cn.enderrealm.easy4form.apiv1.CustomFormBuilder#responseHandler(Consumer)} instead
     */
    @Deprecated
    public CustomFormBuilder responseHandler(Consumer<Map<String, Object>> responseHandler) {
        delegate.responseHandler(responseHandler);
        return this;
    }

    /**
     * Send the form to a player
     * <p>
     * 向玩家发送表单
     *
     * @param player The player to send the form to / 接收表单的玩家
     * @deprecated Use {@link cn.enderrealm.easy4form.apiv1.CustomFormBuilder#send(Player)} instead
     */
    @Deprecated
    public void send(Player player) {
        delegate.send(player);
    }
}