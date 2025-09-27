package cn.enderrealm.easy4form.api;

import cn.enderrealm.easy4form.Easy4form;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Builder for creating custom forms - Multi-Version API Proxy
 * <p>
 * 用于创建自定义表单的构建器 - 多版本API代理
 * <p>
 * This class provides a unified API interface that automatically routes to the appropriate version
 * based on the current configuration. It supports multiple API versions seamlessly.
 * <p>
 * 此类提供统一的API接口，根据当前配置自动路由到适当的版本。它无缝支持多个API版本。
 */
public class CustomFormBuilder {

    /**
     * Create a new CustomFormBuilder instance
     * <p>
     * 创建新的CustomFormBuilder实例
     *
     * @return A new CustomFormBuilder instance / 新的CustomFormBuilder实例
     */
    public static CustomFormBuilder create() {
        try {
            return (CustomFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "createCustomFormBuilder", new Class<?>[]{}
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route createCustomFormBuilder call: " + e.getMessage());
            return null;
        }
    }

    /**
     * Set the title of the form
     * <p>
     * 设置表单标题
     *
     * @param title The title / 标题
     * @return This builder instance / 构建器实例
     */
    public CustomFormBuilder title(String title) {
        try {
            return (CustomFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "customFormBuilderTitle", 
                new Class<?>[]{CustomFormBuilder.class, String.class}, 
                this, title
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route customFormBuilderTitle call: " + e.getMessage());
            return this;
        }
    }

    /**
     * Add a label to the form
     * <p>
     * 向表单添加标签
     *
     * @param id The component ID / 组件ID
     * @param text The label text / 标签文本
     * @return This builder instance / 构建器实例
     */
    public CustomFormBuilder label(String id, String text) {
        try {
            return (CustomFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "customFormBuilderLabel", 
                new Class<?>[]{CustomFormBuilder.class, String.class, String.class}, 
                this, id, text
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route customFormBuilderLabel call: " + e.getMessage());
            return this;
        }
    }

    /**
     * Add a label to the form with auto-generated ID
     * <p>
     * 向表单添加标签（自动生成ID）
     *
     * @param text The label text / 标签文本
     * @return This builder instance / 构建器实例
     */
    public CustomFormBuilder label(String text) {
        try {
            return (CustomFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "customFormBuilderLabelAuto", 
                new Class<?>[]{CustomFormBuilder.class, String.class}, 
                this, text
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route customFormBuilderLabelAuto call: " + e.getMessage());
            return this;
        }
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
     */
    public CustomFormBuilder input(String id, String text, String placeholder, String defaultValue) {
        try {
            return (CustomFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "customFormBuilderInput", 
                new Class<?>[]{CustomFormBuilder.class, String.class, String.class, String.class, String.class}, 
                this, id, text, placeholder, defaultValue
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route customFormBuilderInput call: " + e.getMessage());
            return this;
        }
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
     */
    public CustomFormBuilder toggle(String id, String text, boolean defaultValue) {
        try {
            return (CustomFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "customFormBuilderToggle", 
                new Class<?>[]{CustomFormBuilder.class, String.class, String.class, boolean.class}, 
                this, id, text, defaultValue
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route customFormBuilderToggle call: " + e.getMessage());
            return this;
        }
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
     */
    public CustomFormBuilder slider(String id, String text, float min, float max, float step, float defaultValue) {
        try {
            return (CustomFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "customFormBuilderSlider", 
                new Class<?>[]{CustomFormBuilder.class, String.class, String.class, float.class, float.class, float.class, float.class}, 
                this, id, text, min, max, step, defaultValue
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route customFormBuilderSlider call: " + e.getMessage());
            return this;
        }
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
     */
    public CustomFormBuilder stepSlider(String id, String text, List<String> steps, int defaultStep) {
        try {
            return (CustomFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "customFormBuilderStepSlider", 
                new Class<?>[]{CustomFormBuilder.class, String.class, String.class, List.class, int.class}, 
                this, id, text, steps, defaultStep
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route customFormBuilderStepSlider call: " + e.getMessage());
            return this;
        }
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
     */
    public CustomFormBuilder dropdown(String id, String text, List<String> options, int defaultOption) {
        try {
            return (CustomFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "customFormBuilderDropdown", 
                new Class<?>[]{CustomFormBuilder.class, String.class, String.class, List.class, int.class}, 
                this, id, text, options, defaultOption
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route customFormBuilderDropdown call: " + e.getMessage());
            return this;
        }
    }

    /**
     * Set the response handler for the form
     * <p>
     * 设置表单的响应处理器
     *
     * @param responseHandler The response handler / 响应处理器
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder responseHandler(Consumer<Map<String, Object>> responseHandler) {
        try {
            return (CustomFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "customFormBuilderResponseHandler", 
                new Class<?>[]{CustomFormBuilder.class, Consumer.class}, 
                this, responseHandler
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route customFormBuilderResponseHandler call: " + e.getMessage());
            return this;
        }
    }

    /**
     * Send the form to a player
     * <p>
     * 向玩家发送表单
     *
     * @param player The player to send the form to / 接收表单的玩家
     */
    public void send(Player player) {
        try {
            Easy4form.getInstance().getVersionManager().routeMethodCall(
                "customFormBuilderSend", 
                new Class<?>[]{CustomFormBuilder.class, Player.class}, 
                this, player
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route customFormBuilderSend call: " + e.getMessage());
        }
    }
}