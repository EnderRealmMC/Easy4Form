package cn.enderrealm.easy4form.utils;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Debug utility class for Easy4Form
 * <p>
 * Easy4Form调试工具类
 */
public class DebugUtils {
    
    private static JavaPlugin plugin;
    
    /**
     * Initialize the debug utils with plugin instance
     * <p>
     * 使用插件实例初始化调试工具
     *
     * @param pluginInstance The plugin instance / 插件实例
     */
    public static void init(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
    }
    
    /**
     * Check if debug mode is enabled
     * <p>
     * 检查调试模式是否启用
     *
     * @return true if debug mode is enabled / 如果调试模式启用则返回true
     */
    public static boolean isDebugEnabled() {
        if (plugin == null) {
            return false;
        }
        return plugin.getConfig().getBoolean("debug", false);
    }
    
    /**
     * Log a debug message if debug mode is enabled
     * <p>
     * 如果启用调试模式则记录调试消息
     *
     * @param message The debug message / 调试消息
     */
    public static void debug(String message) {
        if (plugin != null && isDebugEnabled()) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }
    
    /**
     * Log a debug message with category if debug mode is enabled
     * <p>
     * 如果启用调试模式则记录带分类的调试消息
     *
     * @param category The debug category / 调试分类
     * @param message The debug message / 调试消息
     */
    public static void debug(String category, String message) {
        if (plugin != null && isDebugEnabled()) {
            plugin.getLogger().info("[DEBUG][" + category + "] " + message);
        }
    }
}