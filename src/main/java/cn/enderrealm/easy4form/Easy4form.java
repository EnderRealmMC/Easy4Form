package cn.enderrealm.easy4form;

import cn.enderrealm.easy4form.api.Easy4FormAPI;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Easy4Form - A simplified Form API for Floodgate
 * <p>
 * Easy4Form - 基于Floodgate的简化Form接口
 */
public final class Easy4form extends JavaPlugin {

    private static Easy4form instance;

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

    @Override
    public void onEnable() {
        // Set instance
        instance = this;

        // Save default config
        saveDefaultConfig();
        
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
