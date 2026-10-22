package cn.enderrealm.easy4form;

import cn.enderrealm.easy4form.internal.FormPlatform;
import cn.enderrealm.easy4form.internal.FormProvider;
import cn.enderrealm.easy4form.internal.FormProviderRegistry;
import cn.enderrealm.easy4form.internal.cumulus.CumulusProvider;
import cn.enderrealm.easy4form.internal.floodgate.FloodgateAvailability;
import cn.enderrealm.easy4form.utils.DebugUtils;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Easy4Form 插件主类。
 */
public final class Easy4form extends JavaPlugin {

    private static final String PROVIDER_NONE = "none";
    private static final String PROVIDER_CUMULUS = "cumulus";

    private static Easy4form instance;
    private final FormProviderRegistry formProviderRegistry = new FormProviderRegistry();

    /**
     * 获取插件实例。
     *
     * @return 插件实例
     */
    public static Easy4form getInstance() {
        return instance;
    }

    /**
     * 获取表单 Provider 注册表。
     *
     * @return Provider 注册表
     */
    public FormProviderRegistry getFormProviderRegistry() {
        return formProviderRegistry;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        DebugUtils.init(this);

        logDetectionStatus();
        registerConfiguredProviders();

        if (formProviderRegistry.isEmpty()) {
            getLogger().warning("No form provider is active; form API calls will be ignored.");
        }
        getLogger().info("Easy4Form has been enabled.");
    }

    /**
     * 输出玩家检测模式与 Floodgate 可用性。
     */
    private void logDetectionStatus() {
        if (FloodgateAvailability.isAvailable()) {
            return;
        }
        String detectionMode = getConfig().getString("detection-mode", "floodgate");
        if ("uuid".equalsIgnoreCase(detectionMode)) {
            getLogger().info("Floodgate not found; using UUID prefix detection.");
            return;
        }
        getLogger().warning(
                "Floodgate not found; detection-mode 'floodgate' falls back to UUID prefix detection."
        );
    }

    /**
     * 按配置注册各平台的表单 Provider，缺失或不支持时降级为不启用。
     */
    private void registerConfiguredProviders() {
        for (FormPlatform platform : FormPlatform.values()) {
            String configured = getConfig().getString(
                    "providers." + platform.configKey(),
                    platform.defaultProvider()
            );
            String providerName = configured == null ? PROVIDER_NONE : configured.trim();
            if (providerName.isEmpty() || PROVIDER_NONE.equalsIgnoreCase(providerName)) {
                getLogger().info(platform.displayName() + " provider disabled by configuration.");
                continue;
            }

            FormProvider provider = createProvider(platform, providerName);
            if (provider == null) {
                continue;
            }
            formProviderRegistry.register(provider);
            getLogger().info(
                    platform.displayName() + " provider '" + provider.name() + "' enabled."
            );
        }
    }

    /**
     * 创建指定平台的 Provider，不可用时返回 null。
     *
     * @param platform     表单平台
     * @param providerName 配置中指定的 Provider 名称
     * @return Provider 实例，不可用时返回 null
     */
    private FormProvider createProvider(FormPlatform platform, String providerName) {
        if (platform == FormPlatform.BEDROCK_FORM && PROVIDER_CUMULUS.equalsIgnoreCase(providerName)) {
            if (!FloodgateAvailability.isAvailable()) {
                getLogger().warning(
                        "Bedrock form provider 'cumulus' requires Floodgate; Bedrock forms are disabled."
                );
                return null;
            }
            return new CumulusProvider();
        }
        if (platform == FormPlatform.JAVA_DIALOG) {
            getLogger().warning(
                    "Java dialog provider '" + providerName
                            + "' is not implemented yet; Java dialogs are disabled."
            );
            return null;
        }
        getLogger().warning(
                "Unknown " + platform.displayName() + " provider '" + providerName
                        + "'; forms for this platform are disabled."
        );
        return null;
    }

    @Override
    public void onDisable() {
        formProviderRegistry.clear();
        getLogger().info("Easy4Form has been disabled.");
        instance = null;
    }
}
