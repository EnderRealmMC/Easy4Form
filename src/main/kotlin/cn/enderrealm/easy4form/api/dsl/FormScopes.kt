package cn.enderrealm.easy4form.api.dsl

import cn.enderrealm.easy4form.api.CustomFormBuilder
import cn.enderrealm.easy4form.api.ModalFormBuilder
import cn.enderrealm.easy4form.api.SimpleFormBuilder
import org.bukkit.entity.Player
import java.util.function.Consumer

/**
 * 简单表单 DSL 作用域。
 *
 * 只做参数收集与逐按钮回调分发，不新增任何底层能力；所有表单元数据
 * 仍由 [SimpleFormBuilder] 构建。
 */
@Easy4FormDsl
class SimpleFormScope internal constructor() {

    /** 底层 Java 构建器。 */
    private val builder = SimpleFormBuilder()

    /** 按钮下标到点击回调的映射。 */
    private val clicks = HashMap<Int, ButtonClick>()

    /** 关闭回调。 */
    private var closedHandler: FormAction? = null

    /** 已添加按钮数量，用于分配下标。 */
    private var buttonCount = 0

    /**
     * 添加纯文本按钮。
     *
     * @param text 按钮文本
     */
    fun button(text: String) {
        buttonCount++
        builder.button(text)
    }

    /**
     * 添加带点击回调的纯文本按钮。
     *
     * @param text 按钮文本
     * @param onClick 点击回调，参数为按钮下标
     */
    fun button(text: String, onClick: ButtonClick) {
        clicks[buttonCount++] = onClick
        builder.button(text)
    }

    /**
     * 添加带资源包图片的按钮。
     *
     * @param text 按钮文本
     * @param imagePath 资源包图片路径
     * @param onClick 点击回调，参数为按钮下标
     */
    fun button(text: String, imagePath: String, onClick: ButtonClick) {
        clicks[buttonCount++] = onClick
        builder.button(text, imagePath)
    }

    /**
     * 添加带 URL 图片的按钮。
     *
     * @param text 按钮文本
     * @param imageUrl 图片 URL
     * @param onClick 点击回调，参数为按钮下标
     */
    fun buttonWithUrl(text: String, imageUrl: String, onClick: ButtonClick) {
        clicks[buttonCount++] = onClick
        builder.buttonWithUrl(text, imageUrl)
    }

    /**
     * 设置关闭回调。
     *
     * @param handler 关闭或响应非法时执行的动作
     */
    fun closed(handler: FormAction) {
        closedHandler = handler
    }

    /**
     * 组装定义并发送给玩家。
     *
     * @param player 接收表单的玩家
     * @param title 表单标题
     * @param content 表单正文，为 null 时不设置
     */
    internal fun send(player: Player, title: String, content: String?) {
        builder.title(title)
        if (content != null) {
            builder.content(content)
        }
        builder.responseHandler(Consumer<Int?> { buttonId ->
            if (buttonId == null) {
                closedHandler?.run()
            } else {
                clicks[buttonId]?.onClick(buttonId)
            }
        })
        builder.send(player)
    }
}

/**
 * 模态表单 DSL 作用域。
 *
 * 只做回调拆分，表单数据仍由 [ModalFormBuilder] 构建。
 */
@Easy4FormDsl
class ModalFormScope internal constructor() {

    /** 底层 Java 构建器。 */
    private val builder = ModalFormBuilder()

    /** 确认回调。 */
    private var confirmHandler: FormAction? = null

    /** 取消回调。 */
    private var cancelHandler: FormAction? = null

    /** 关闭回调。 */
    private var closedHandler: FormAction? = null

    /**
     * 设置确认回调。
     *
     * @param handler 玩家点击第一个按钮时执行的动作
     */
    fun confirm(handler: FormAction) {
        confirmHandler = handler
    }

    /**
     * 设置取消回调。
     *
     * @param handler 玩家点击第二个按钮时执行的动作
     */
    fun cancel(handler: FormAction) {
        cancelHandler = handler
    }

    /**
     * 设置关闭回调。
     *
     * @param handler 关闭或响应非法时执行的动作
     */
    fun closed(handler: FormAction) {
        closedHandler = handler
    }

    /**
     * 组装定义并发送给玩家。
     *
     * @param player 接收表单的玩家
     * @param title 表单标题
     * @param content 表单正文
     * @param trueButton 第一个按钮文本
     * @param falseButton 第二个按钮文本
     */
    internal fun send(
        player: Player,
        title: String,
        content: String,
        trueButton: String,
        falseButton: String
    ) {
        builder
            .title(title)
            .content(content)
            .button1(trueButton)
            .button2(falseButton)
        builder.responseHandler(Consumer<Boolean?> { confirmed ->
            when (confirmed) {
                null -> closedHandler?.run()
                true -> confirmHandler?.run()
                false -> cancelHandler?.run()
            }
        })
        builder.send(player)
    }
}

/**
 * 自定义表单 DSL 作用域。
 *
 * 组件方法与 [CustomFormBuilder] 一一对应，不暴露内部组件定义；
 * label 仍会占用一个组件下标，自动 ID 规则沿用门面的 `label_<组件数>`。
 */
@Easy4FormDsl
class CustomFormScope internal constructor() {

    /** 底层 Java 构建器。 */
    private val builder = CustomFormBuilder()

    /** 提交回调。 */
    private var submitHandler: FormSubmit? = null

    /** 关闭回调。 */
    private var closedHandler: FormAction? = null

    /**
     * 添加标签，自动生成组件 ID。
     *
     * @param text 标签文本
     */
    fun label(text: String) {
        builder.label(text)
    }

    /**
     * 添加标签。
     *
     * @param id 组件 ID
     * @param text 标签文本
     */
    fun label(id: String, text: String) {
        builder.label(id, text)
    }

    /**
     * 添加文本输入框。
     *
     * @param id 组件 ID
     * @param text 标签文本
     * @param placeholder 占位符文本
     * @param defaultValue 默认值
     */
    fun input(
        id: String,
        text: String,
        placeholder: String = "",
        defaultValue: String = ""
    ) {
        builder.input(id, text, placeholder, defaultValue)
    }

    /**
     * 添加开关。
     *
     * @param id 组件 ID
     * @param text 标签文本
     * @param defaultValue 默认值
     */
    fun toggle(id: String, text: String, defaultValue: Boolean = false) {
        builder.toggle(id, text, defaultValue)
    }

    /**
     * 添加滑块。
     *
     * @param id 组件 ID
     * @param text 标签文本
     * @param min 最小值
     * @param max 最大值
     * @param step 步长
     * @param defaultValue 默认值，默认取最小值
     */
    fun slider(
        id: String,
        text: String,
        min: Float,
        max: Float,
        step: Float = 1.0f,
        defaultValue: Float = min
    ) {
        builder.slider(id, text, min, max, step, defaultValue)
    }

    /**
     * 添加步进滑块。
     *
     * @param id 组件 ID
     * @param text 标签文本
     * @param steps 步骤选项
     * @param defaultStep 默认步骤下标
     */
    fun stepSlider(
        id: String,
        text: String,
        vararg steps: String,
        defaultStep: Int = 0
    ) {
        builder.stepSlider(id, text, steps.asList(), defaultStep)
    }

    /**
     * 添加下拉框。
     *
     * @param id 组件 ID
     * @param text 标签文本
     * @param options 选项
     * @param defaultOption 默认选项下标
     */
    fun dropdown(
        id: String,
        text: String,
        vararg options: String,
        defaultOption: Int = 0
    ) {
        builder.dropdown(id, text, options.asList(), defaultOption)
    }

    /**
     * 设置提交回调。
     *
     * @param handler 表单有效提交时执行的动作，参数为组件响应值
     */
    fun submitted(handler: FormSubmit) {
        submitHandler = handler
    }

    /**
     * 设置关闭回调。
     *
     * @param handler 关闭或响应非法时执行的动作
     */
    fun closed(handler: FormAction) {
        closedHandler = handler
    }

    /**
     * 组装定义并发送给玩家。
     *
     * @param player 接收表单的玩家
     * @param title 表单标题
     */
    internal fun send(player: Player, title: String) {
        builder.title(title)
        builder.responseHandler(Consumer<MutableMap<String, Any>?> { values ->
            if (values == null || values.isEmpty()) {
                closedHandler?.run()
            } else {
                submitHandler?.onSubmit(FormValues(values))
            }
        })
        builder.send(player)
    }
}
