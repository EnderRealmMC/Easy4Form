package cn.enderrealm.easy4form.api.v2;

import cn.enderrealm.easy4form.api.v2.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.*;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Builder for creating custom forms - API v2
 * <p>
 * 用于创建自定义表单的构建器 - API v2
 */
public class CustomFormBuilder {
    private final CustomForm.Builder builder;
    private Player player;
    private Consumer<Map<String, Object>> responseHandler;
    private final Map<String, Integer> componentIndexMap = new HashMap<>();
    private int componentIndex = 0;

    /**
     * Create a new CustomFormBuilder
     * <p>
     * 创建新的CustomFormBuilder
     */
    public CustomFormBuilder() {
        this.builder = CustomForm.builder();
        this.player = null;
    }

    /**
     * Create a new CustomFormBuilder with player, title and response handler
     * <p>
     * 创建新的CustomFormBuilder（带玩家、标题和响应处理器）
     *
     * @param player The player / 玩家
     * @param title The form title / 表单标题
     * @param responseHandler The response handler / 响应处理器
     */
    public CustomFormBuilder(Player player, String title, Consumer<Map<String, Object>> responseHandler) {
        this.builder = CustomForm.builder().title(title);
        this.player = player;
        this.responseHandler = responseHandler;
    }

    /**
     * Add a label to the form
     * <p>
     * 向表单添加标签
     *
     * @param name The component name / 组件名称
     * @param text The label text / 标签文本
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder label(String name, String text) {
        componentIndexMap.put(name, componentIndex++);
        builder.label(text);
        return this;
    }

    /**
     * Add a label to the form with auto-generated name
     * <p>
     * 向表单添加标签（自动生成名称）
     *
     * @param text The label text / 标签文本
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder labelAuto(String text) {
        String autoName = "label_" + componentIndex;
        return label(autoName, text);
    }

    /**
     * Add an input component
     * <p>
     * 添加输入组件
     *
     * @param name The component name / 组件名称
     * @param text The input label / 输入标签
     * @param placeholder The placeholder text / 占位符文本
     * @param defaultValue The default value / 默认值
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder input(String name, String text, String placeholder, String defaultValue) {
        componentIndexMap.put(name, componentIndex++);
        builder.component(InputComponent.of(text, placeholder, defaultValue));
        return this;
    }

    /**
     * Add an input component without default value
     * <p>
     * 添加无默认值的输入组件
     *
     * @param name The component name / 组件名称
     * @param text The input label / 输入标签
     * @param placeholder The placeholder text / 占位符文本
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder input(String name, String text, String placeholder) {
        return input(name, text, placeholder, "");
    }

    /**
     * Add a toggle component
     * <p>
     * 添加开关组件
     *
     * @param name The component name / 组件名称
     * @param text The toggle label / 开关标签
     * @param defaultValue The default value / 默认值
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder toggle(String name, String text, boolean defaultValue) {
        componentIndexMap.put(name, componentIndex++);
        builder.component(ToggleComponent.of(text, defaultValue));
        return this;
    }

    /**
     * Add a toggle component with default false
     * <p>
     * 添加默认为false的开关组件
     *
     * @param name The component name / 组件名称
     * @param text The toggle label / 开关标签
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder toggle(String name, String text) {
        return toggle(name, text, false);
    }

    /**
     * Add a slider component
     * <p>
     * 添加滑块组件
     *
     * @param name The component name / 组件名称
     * @param text The slider label / 滑块标签
     * @param min The minimum value / 最小值
     * @param max The maximum value / 最大值
     * @param step The step value / 步长值
     * @param defaultValue The default value / 默认值
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder slider(String name, String text, float min, float max, float step, float defaultValue) {
        componentIndexMap.put(name, componentIndex++);
        builder.component(SliderComponent.of(text, min, max, step, defaultValue));
        return this;
    }

    /**
     * Add a slider component with default minimum value
     * <p>
     * 添加默认为最小值的滑块组件
     *
     * @param name The component name / 组件名称
     * @param text The slider label / 滑块标签
     * @param min The minimum value / 最小值
     * @param max The maximum value / 最大值
     * @param step The step value / 步长值
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder slider(String name, String text, float min, float max, float step) {
        return slider(name, text, min, max, step, min);
    }

    /**
     * Add a dropdown component
     * <p>
     * 添加下拉框组件
     *
     * @param name The component name / 组件名称
     * @param text The dropdown label / 下拉框标签
     * @param options The dropdown options / 下拉框选项
     * @param defaultIndex The default selected index / 默认选中索引
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder dropdown(String name, String text, List<String> options, int defaultIndex) {
        componentIndexMap.put(name, componentIndex++);
        builder.component(DropdownComponent.of(text, options, defaultIndex));
        return this;
    }

    /**
     * Add a dropdown component with default first option
     * <p>
     * 添加默认选中第一个选项的下拉框组件
     *
     * @param name The component name / 组件名称
     * @param text The dropdown label / 下拉框标签
     * @param options The dropdown options / 下拉框选项
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder dropdown(String name, String text, List<String> options) {
        return dropdown(name, text, options, 0);
    }

    /**
     * Add a step slider component
     * <p>
     * 添加步进滑块组件
     *
     * @param name The component name / 组件名称
     * @param text The step slider label / 步进滑块标签
     * @param steps The step options / 步进选项
     * @param defaultIndex The default selected index / 默认选中索引
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder stepSlider(String name, String text, List<String> steps, int defaultIndex) {
        componentIndexMap.put(name, componentIndex++);
        builder.component(StepSliderComponent.of(text, steps, defaultIndex));
        return this;
    }

    /**
     * Add a step slider component with default first step
     * <p>
     * 添加默认选中第一个步进的步进滑块组件
     *
     * @param name The component name / 组件名称
     * @param text The step slider label / 步进滑块标签
     * @param steps The step options / 步进选项
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder stepSlider(String name, String text, List<String> steps) {
        return stepSlider(name, text, steps, 0);
    }

    /**
     * Set the title of the form
     * <p>
     * 设置表单标题
     *
     * @param title The title / 标题
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder title(String title) {
        builder.title(title);
        return this;
    }

    /**
     * Set the response handler
     * <p>
     * 设置响应处理器
     *
     * @param responseHandler The response handler / 响应处理器
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder responseHandler(Consumer<Map<String, Object>> responseHandler) {
        this.responseHandler = responseHandler;
        return this;
    }

    /**
     * Send the form to a player
     * <p>
     * 向玩家发送表单
     *
     * @param player The player / 玩家
     */
    public void send(Player player) {
        this.player = player;
        send();
    }

    /**
     * Send the form to the player
     * <p>
     * 向玩家发送表单
     */
    public void send() {
        if (!PlayerUtils.isBedrockPlayer(player)) {
            return;
        }

        FloodgatePlayer floodgatePlayer = PlayerUtils.getFloodgatePlayer(player);
        if (floodgatePlayer == null) {
            return;
        }

        // Build the form with response handler
        CustomForm form;
        if (responseHandler != null) {
            form = builder.validResultHandler(formResponse -> {
                // 使用HashMap替换TypeSafeMap，直接返回原始类型
                Map<String, Object> responseMap = new HashMap<>();
                
                // Map component responses by name using the new iterator approach
                formResponse.reset(); // Reset iterator to start
                for (Map.Entry<String, Integer> entry : componentIndexMap.entrySet()) {
                    String componentName = entry.getKey();
                    int index = entry.getValue();
                    
                    try {
                        Object value = formResponse.valueAt(index);
                        responseMap.put(componentName, value);
                    } catch (IllegalArgumentException e) {
                        // Index out of bounds, skip this component
                        responseMap.put(componentName, null);
                    }
                }
                
                responseHandler.accept(responseMap);
            }).closedOrInvalidResultHandler(() -> {
                responseHandler.accept(new HashMap<>());
            }).build();
        } else {
            form = builder.build();
        }

        // Send the form
        floodgatePlayer.sendForm(form);
    }
}