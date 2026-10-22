package cn.enderrealm.easy4form.internal;

import java.util.Map;

/**
 * Provider 返回的表单响应。
 */
public sealed interface FormResponse
        permits FormResponse.Simple, FormResponse.Modal, FormResponse.Custom, FormResponse.Closed {

    /**
     * 简单表单响应。
     *
     * @param buttonId 被点击的按钮下标
     */
    record Simple(int buttonId) implements FormResponse {
    }

    /**
     * 模态表单响应。
     *
     * @param firstButton 是否点击了第一个按钮
     */
    record Modal(boolean firstButton) implements FormResponse {
    }

    /**
     * 自定义表单响应。
     *
     * @param values 组件 ID 到值的映射
     */
    record Custom(Map<String, Object> values) implements FormResponse {
    }

    /**
     * 表单被关闭或响应无效。
     */
    record Closed() implements FormResponse {
    }
}
