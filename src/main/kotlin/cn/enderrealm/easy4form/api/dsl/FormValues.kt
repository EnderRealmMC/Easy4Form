package cn.enderrealm.easy4form.api.dsl

/**
 * 自定义表单响应值的类型化视图。
 *
 * 只做类型转换，不改变门面的响应语义：底层数据始终是 Provider 返回的
 * `Map<String, Object>`，key 为组件 ID。取值失败时返回 null，不抛异常。
 *
 * @param raw 原始响应数据
 */
class FormValues internal constructor(private val raw: Map<String, Any>) {

    /**
     * 全部组件 ID。
     */
    val keys: Set<String>
        get() = raw.keys

    /**
     * 按组件 ID 取原始值。
     *
     * @param key 组件 ID
     * @return 原始值，不存在时为 null
     */
    operator fun get(key: String): Any? = raw[key]

    /**
     * 取文本输入框的值。
     *
     * @param key 组件 ID
     * @return 输入文本，类型不匹配或缺失时为 null
     */
    fun string(key: String): String? = raw[key] as? String

    /**
     * 取开关的值。
     *
     * @param key 组件 ID
     * @return 开关状态，类型不匹配或缺失时为 null
     */
    fun boolean(key: String): Boolean? = raw[key] as? Boolean

    /**
     * 取滑块的值。
     *
     * @param key 组件 ID
     * @return 滑块数值，类型不匹配或缺失时为 null
     */
    fun float(key: String): Float? = (raw[key] as? Number)?.toFloat()

    /**
     * 取步进滑块或下拉框选中项的下标。
     *
     * @param key 组件 ID
     * @return 选中下标，类型不匹配或缺失时为 null
     */
    fun intIndex(key: String): Int? = (raw[key] as? Number)?.toInt()

    /**
     * 输出原始响应数据，便于调试。
     *
     * @return 原始数据的字符串表示
     */
    override fun toString(): String = raw.toString()
}
