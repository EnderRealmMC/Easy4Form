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
    private static final String PROVIDER_PAPER = "paper";

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
        if (!FloodgateAvailability.isPresent()) {
            getLogger().warning(
                    "Floodgate not found; Bedrock players will be treated as Java players and Bedrock forms are disabled."
            );
            return;
        }
        String detectionMode = getConfig().getString("detection-mode", "floodgate");
        if ("uuid".equalsIgnoreCase(detectionMode)) {
            getLogger().info("Floodgate is installed but not enabled; using UUID prefix detection.");
            return;
        }
        getLogger().warning(
                "Floodgate is installed but not enabled; detection-mode 'floodgate' falls back to UUID prefix detection."
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
        if (platform == FormPlatform.JAVA_DIALOG && PROVIDER_PAPER.equalsIgnoreCase(providerName)) {
            return createPaperDialogProvider();
        }
        getLogger().warning(
                "Unknown " + platform.displayName() + " provider '" + providerName
                        + "'; forms for this platform are disabled."
        );
        return null;
    }

    /**
     * 通过反射创建 Paper Dialog Provider。
     *
     * <p>Paper 1.21.8 的 Dialog API 在旧服务端不存在，反射可避免类加载失败；
     * 初始化完成后所有调用都走 {@link FormProvider} 接口，不再有反射开销。</p>
     *
     * @return Provider 实例；当前服务端或 Java 版本不支持时返回 null
     */
    private FormProvider createPaperDialogProvider() {
        try {
            Class<?> providerClass = Class.forName(
                    "cn.enderrealm.easy4form.internal.paper.PaperDialogProvider"
            );
            return (FormProvider) providerClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
            getLogger().warning(
                    "Java dialog provider 'paper' requires Paper 1.21.8+ and Java 21; "
                            + "Java dialogs are disabled. ("
                            + exception.getClass().getSimpleName() + ": " + exception.getMessage() + ")"
            );
            return null;
        }
    }

    @Override
    public void onDisable() {
        formProviderRegistry.clear();
        getLogger().info("Easy4Form has been disabled.");
        instance = null;
    }
}
