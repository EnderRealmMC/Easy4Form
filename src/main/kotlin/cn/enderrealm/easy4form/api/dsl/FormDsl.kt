@file:JvmName("FormDsl")

package cn.enderrealm.easy4form.api.dsl

import org.bukkit.entity.Player

/**
 * Kotlin DSL 作用域标记，禁止不同表单的作用域互相穿透调用。
 */
@DslMarker
@Target(AnnotationTarget.CLASS)
annotation class Easy4FormDsl

/**
 * 无参数动作回调。
 *
 * 使用函数式接口而非 Kotlin 函数类型，避免回调类型跨插件类加载器时
 * 解析到两份不同的 `kotlin.jvm.functions` 定义。
 */
fun interface FormAction {
    /**
     * 执行动作。
     */
    fun run()
}

/**
 * 简单表单按钮点击回调。
 */
fun interface ButtonClick {
    /**
     * 按钮被点击。
     *
     * @param buttonId 按钮下标，从 0 开始，与 Java 门面的响应值一致
     */
    fun onClick(buttonId: Int)
}

/**
 * 自定义表单提交回调。
 */
fun interface FormSubmit {
    /**
     * 表单被有效提交。
     *
     * @param values 组件响应值，按组件 ID 访问
     */
    fun onSubmit(values: FormValues)
}

/**
 * 以 DSL 方式向玩家发送简单表单。
 *
 * 关闭语义与 Java 门面完全一致：玩家关闭表单或响应非法时触发
 * [SimpleFormScope.closed]，否则触发对应按钮的 [ButtonClick]。
 * 没有可用 Provider 接管该玩家时表单被静默忽略，任何回调都不会触发，
 * 需要兜底交互请先调用 `Easy4FormAPI.isFormSupported(player)`。
 *
 * @param title 表单标题
 * @param content 表单正文，为 null 时不设置正文
 * @param buttons 按钮配置块
 */
fun Player.simpleForm(
    title: String,
    content: String? = null,
    buttons: SimpleFormScope.() -> Unit
) {
    SimpleFormScope().apply(buttons).send(this, title, content)
}

/**
 * 以 DSL 方式向玩家发送模态表单。
 *
 * 关闭语义与 Java 门面完全一致：玩家关闭表单或响应非法时触发
 * [ModalFormScope.closed]；确认与取消分别触发 [ModalFormScope.confirm]
 * 与 [ModalFormScope.cancel]。按钮文本必须由调用方提供，库内不内置任何
 * 未走 i18n 的默认文案。
 *
 * @param title 表单标题
 * @param content 表单正文
 * @param trueButton 第一个按钮文本，通常表示确认
 * @param falseButton 第二个按钮文本，通常表示取消
 * @param block 回调配置块
 */
fun Player.modalForm(
    title: String,
    content: String,
    trueButton: String,
    falseButton: String,
    block: ModalFormScope.() -> Unit
) {
    ModalFormScope().apply(block).send(this, title, content, trueButton, falseButton)
}

/**
 * 以 DSL 方式向玩家发送自定义表单。
 *
 * 关闭语义与 Java 门面完全一致：Java 门面在关闭或响应非法时给出空 Map，
 * 因此 DSL 把空 Map 折叠为 [CustomFormScope.closed]，非空才触发
 * [CustomFormScope.submitted]。
 *
 * @param title 表单标题
 * @param block 组件与回调配置块
 */
fun Player.customForm(
    title: String,
    block: CustomFormScope.() -> Unit
) {
    CustomFormScope().apply(block).send(this, title)
}
