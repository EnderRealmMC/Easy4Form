package cn.enderrealm.easy4form;

import cn.enderrealm.easy4form.internal.FormProviderRegistry;
import cn.enderrealm.easy4form.internal.cumulus.CumulusProvider;
import cn.enderrealm.easy4form.utils.DebugUtils;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Easy4Form 插件主类。
 */
public final class Easy4form extends JavaPlugin {

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

        if (getServer().getPluginManager().getPlugin("floodgate") == null) {
            getLogger().warning("Floodgate plugin not found! Easy4Form requires Floodgate to work.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        formProviderRegistry.register(new CumulusProvider());
        getLogger().info("Easy4Form has been enabled! Cumulus provider registered for Bedrock forms.");
    }

    @Override
    public void onDisable() {
        formProviderRegistry.clear();
        getLogger().info("Easy4Form has been disabled.");
        instance = null;
    }
}
