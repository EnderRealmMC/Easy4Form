package cn.enderrealm.easy4form.internal;

import java.util.List;

/**
 * 自定义表单组件定义。
 */
public sealed interface CustomComponent
        permits CustomComponent.Label, CustomComponent.Input, CustomComponent.Toggle,
        CustomComponent.Slider, CustomComponent.StepSlider, CustomComponent.Dropdown {

    /**
     * 获取组件 ID。
     *
     * @return 组件 ID
     */
    String id();

    /**
     * 文本标签。
     *
     * @param id   组件 ID
     * @param text 标签文本
     */
    record Label(String id, String text) implements CustomComponent {
    }

    /**
     * 文本输入框。
     *
     * @param id           组件 ID
     * @param text         标签文本
     * @param placeholder  占位符
     * @param defaultValue 默认值
     */
    record Input(String id, String text, String placeholder, String defaultValue) implements CustomComponent {
    }

    /**
     * 开关。
     *
     * @param id           组件 ID
     * @param text         标签文本
     * @param defaultValue 默认值
     */
    record Toggle(String id, String text, boolean defaultValue) implements CustomComponent {
    }

    /**
     * 滑块。
     *
     * @param id           组件 ID
     * @param text         标签文本
     * @param min          最小值
     * @param max          最大值
     * @param step         步长
     * @param defaultValue 默认值
     */
    record Slider(String id, String text, float min, float max, float step, float defaultValue)
            implements CustomComponent {
    }

    /**
     * 步进滑块。
     *
     * @param id           组件 ID
     * @param text         标签文本
     * @param steps        步骤选项
     * @param defaultStep  默认步骤下标
     */
    record StepSlider(String id, String text, List<String> steps, int defaultStep) implements CustomComponent {

        public StepSlider {
            steps = List.copyOf(steps);
        }
    }

    /**
     * 下拉框。
     *
     * @param id            组件 ID
     * @param text          标签文本
     * @param options       选项
     * @param defaultOption 默认选项下标
     */
    record Dropdown(String id, String text, List<String> options, int defaultOption) implements CustomComponent {

        public Dropdown {
            options = List.copyOf(options);
        }
    }
}
