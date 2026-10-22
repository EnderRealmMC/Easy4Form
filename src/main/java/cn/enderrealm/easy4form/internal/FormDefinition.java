package cn.enderrealm.easy4form.internal;

import java.util.List;

/**
 * 与具体表单平台无关的表单定义。
 */
public sealed interface FormDefinition
        permits FormDefinition.Simple, FormDefinition.Modal, FormDefinition.Custom {

    /**
     * 简单表单定义。
     *
     * @param title   标题
     * @param content 内容
     * @param buttons 按钮列表
     */
    record Simple(String title, String content, List<ButtonDefinition> buttons) implements FormDefinition {

        public Simple {
            buttons = List.copyOf(buttons);
        }
    }

    /**
     * 模态表单定义。
     *
     * @param title   标题
     * @param content 内容
     * @param button1 第一个按钮
     * @param button2 第二个按钮
     */
    record Modal(String title, String content, String button1, String button2) implements FormDefinition {
    }

    /**
     * 自定义表单定义。
     *
     * @param title      标题
     * @param components 按显示顺序排列的组件
     */
    record Custom(String title, List<CustomComponent> components) implements FormDefinition {

        public Custom {
            components = List.copyOf(components);
        }
    }
}
