package cn.enderrealm.easy4form.internal;

/**
 * 简单表单按钮定义。
 *
 * @param text       按钮文本
 * @param imageType  图片类型
 * @param imageValue 图片路径或 URL
 */
public record ButtonDefinition(String text, ImageType imageType, String imageValue) {

    /**
     * 按钮图片类型。
     */
    public enum ImageType {
        NONE,
        PATH,
        URL
    }

    /**
     * 创建纯文本按钮。
     *
     * @param text 按钮文本
     * @return 按钮定义
     */
    public static ButtonDefinition text(String text) {
        return new ButtonDefinition(text, ImageType.NONE, null);
    }

    /**
     * 创建带资源包图片的按钮。
     *
     * @param text 按钮文本
     * @param path 资源包图片路径
     * @return 按钮定义
     */
    public static ButtonDefinition path(String text, String path) {
        return new ButtonDefinition(text, ImageType.PATH, path);
    }

    /**
     * 创建带 URL 图片的按钮。
     *
     * @param text 按钮文本
     * @param url  图片 URL
     * @return 按钮定义
     */
    public static ButtonDefinition url(String text, String url) {
        return new ButtonDefinition(text, ImageType.URL, url);
    }
}
