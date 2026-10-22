package cn.enderrealm.easy4form.internal;

/**
 * 表单承载平台。
 */
public enum FormPlatform {

    /**
     * 基岩版表单，默认由 Cumulus 提供。
     */
    BEDROCK_FORM("bedrock-form", "Bedrock form", "cumulus"),

    /**
     * Java 版 Dialog，默认不启用，由 Paper 1.21.7+ 的 Dialog API 提供。
     */
    JAVA_DIALOG("java-dialog", "Java dialog", "none");

    private final String configKey;
    private final String displayName;
    private final String defaultProvider;

    FormPlatform(String configKey, String displayName, String defaultProvider) {
        this.configKey = configKey;
        this.displayName = displayName;
        this.defaultProvider = defaultProvider;
    }

    /**
     * 获取配置文件中对应的键名。
     *
     * @return 配置键名
     */
    public String configKey() {
        return configKey;
    }

    /**
     * 获取用于日志输出的平台名称。
     *
     * @return 平台名称
     */
    public String displayName() {
        return displayName;
    }

    /**
     * 获取未配置时使用的默认 Provider 名称。
     *
     * @return 默认 Provider 名称
     */
    public String defaultProvider() {
        return defaultProvider;
    }
}
