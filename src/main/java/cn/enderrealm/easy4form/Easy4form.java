package cn.enderrealm.easy4form;

import cn.enderrealm.easy4form.api.Easy4FormAPI;
import cn.enderrealm.easy4form.manager.VersionManager;
import cn.enderrealm.easy4form.utils.DebugUtils;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Easy4Form - A simplified Form API for Floodgate
 * <p>
 * Easy4Form - 基于Floodgate的简化Form接口
 */
public final class Easy4form extends JavaPlugin {

    private static Easy4form instance;
    private VersionManager versionManager;

    /**
     * Get the instance of the plugin
     * <p>
     * 获取插件实例
     *
     * @return The plugin instance / 插件实例
     */
    public static Easy4form getInstance() {
        return instance;
    }

    /**
     * Get the version manager
     * <p>
     * 获取版本管理器
     *
     * @return The version manager / 版本管理器
     */
    public VersionManager getVersionManager() {
        return versionManager;
    }

    @Override
    public void onEnable() {
        // Set instance
        instance = this;

        // Save default configuration
        saveDefaultConfig();
        
        // Initialize debug utils
        DebugUtils.init(this);
        
        // Initialize version manager
        versionManager = new VersionManager(this);
        
        // Log startup message
        getLogger().info("Easy4Form has been enabled! A simplified Form API for Floodgate.");
        
        // Check if Floodgate is installed
        if (getServer().getPluginManager().getPlugin("floodgate") == null) {
            getLogger().warning("Floodgate plugin not found! Easy4Form requires Floodgate to work.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Log detection mode
        String detectionMode = getConfig().getString("detection-mode", "floodgate");
        getLogger().info("Using " + detectionMode + " mode to detect Bedrock players.");
        
        if ("uuid".equalsIgnoreCase(detectionMode)) {
            String uuidPrefix = getConfig().getString("uuid-prefix", "00000000-0000");
            getLogger().info("UUID prefix for Bedrock players: " + uuidPrefix);
        }
        
        // Debug mode
        if (getConfig().getBoolean("debug")) {
            getLogger().info("Debug mode is enabled.");
        }
        
        getLogger().info("Floodgate detected, Easy4Form is ready to use!");
    }


    @Override
    public void onDisable() {
        getLogger().info("Easy4Form has been disabled.");
        instance = null;
    }

}
