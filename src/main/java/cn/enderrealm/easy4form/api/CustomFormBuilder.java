package cn.enderrealm.easy4form.api;

import org.bukkit.entity.Player;
import org.geysermc.cumulus.CustomForm;
import org.geysermc.cumulus.component.*;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Builder for creating custom forms with various input elements
 * <p>
 * 用于创建带有各种输入元素的自定义表单的构建器
 */
public class CustomFormBuilder {
    private String title = "";
    private final List<Component> components = new ArrayList<>();
    private final Map<Integer, String> componentIds = new HashMap<>();
    private Consumer<Map<String, Object>> responseHandler;

    /**
     * Set the title of the form
     * <p>
     * 设置表单标题
     *
     * @param title The title / 标题
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Add a label to the form
     * <p>
     * 向表单添加标签
     *
     * @param id The component ID / 组件ID
     * @param text The label text / 标签文本
     * @return The builder instance / 构建器实例
     */
    public CustomFormBuilder label(String id, String text) {
        componentIds.put(components.size(), id);
        components.add(LabelComponent.of(text));
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
     */
    public CustomFormBuilder input(String id, String text, String placeholder, String defaultValue) {
        componentIds.put(components.size(), id);
        components.add(InputComponent.of(text, placeholder, defaultValue));
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
     */
    public CustomFormBuilder toggle(String id, String text, boolean defaultValue) {
        componentIds.put(components.size(), id);
        components.add(ToggleComponent.of(text, defaultValue));
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
     */
    public CustomFormBuilder slider(String id, String text, float min, float max, float step, float defaultValue) {
        componentIds.put(components.size(), id);
        components.add(SliderComponent.of(text, min, max, step, defaultValue));
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
     */
    public CustomFormBuilder stepSlider(String id, String text, List<String> steps, int defaultStep) {
        componentIds.put(components.size(), id);
        components.add(StepSliderComponent.of(text, steps, defaultStep));
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
     */
    public CustomFormBuilder dropdown(String id, String text, List<String> options, int defaultOption) {
        componentIds.put(components.size(), id);
        components.add(DropdownComponent.of(text, options, defaultOption));
        return this;
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
        this.responseHandler = responseHandler;
        return this;
    }

    /**
     * Send the form to a player
     * <p>
     * 向玩家发送表单
     *
     * @param player The player to send the form to / 接收表单的玩家
     */
    public void send(Player player) {
        if (!PlayerUtils.isBedrockPlayer(player)) {
            return;
        }

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        if (floodgatePlayer == null) {
            return;
        }

        CustomForm.Builder formBuilder = CustomForm.builder().title(title);

        for (Component component : components) {
            formBuilder.component(component);
        }

        formBuilder.responseHandler((form, responseData) -> {
            CustomFormResponse response = form.parseResponse(responseData);
            if (response.isCorrect() && responseHandler != null) {
                Map<String, Object> result = new HashMap<>();
                for (int i = 0; i < components.size(); i++) {
                    String id = componentIds.get(i);
                    if (id == null) continue;
                    
                    Component component = components.get(i);
                    if (component instanceof LabelComponent) {
                        // Labels don't have a value
                        continue;
                    }
                    
                    Object value = null;
                    if (component instanceof InputComponent) {
                        value = response.getInput(i);
                    } else if (component instanceof ToggleComponent) {
                        value = response.getToggle(i);
                    } else if (component instanceof SliderComponent) {
                        value = response.getSlider(i);
                    } else if (component instanceof StepSliderComponent) {
                        value = response.getStepSlide(i);
                    } else if (component instanceof DropdownComponent) {
                        value = response.getDropdown(i);
                    }
                    
                    result.put(id, value);
                }
                
                responseHandler.accept(result);
            } else if (responseHandler != null) {
                responseHandler.accept(null);
            }
        });

        floodgatePlayer.sendForm(formBuilder.build());
    }
}