package cn.enderrealm.easy4form.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Version Manager - Manages API version mapping and configuration
 * <p>
 * 版本管理器 - 管理API版本映射和配置
 */
public class VersionManager {
    
    private final JavaPlugin plugin;
    private String currentApiVersion;
    private boolean enableMigrationWarnings;
    private final Set<String> warnedMessages;
    
    public VersionManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.warnedMessages = new HashSet<>();
        loadConfiguration();
    }
    
    /**
     * Load configuration from config.yml
     * <p>
     * 从config.yml加载配置
     */
    private void loadConfiguration() {
        FileConfiguration config = plugin.getConfig();
        
        // Load API version settings from the simplified config structure
        this.currentApiVersion = config.getString("api-version", "v1");
        
        // Load migration/warning settings
        this.enableMigrationWarnings = config.getBoolean("migration-warnings", true);
        
        plugin.getLogger().info("Version Manager loaded - Current API version: " + currentApiVersion);
    }
    
    /**
     * Get the current API version
     * <p>
     * 获取当前API版本
     *
     * @return The current API version / 当前API版本
     */
    public String getCurrentApiVersion() {
        return currentApiVersion;
    }

    /**
     * Check if migration warnings are enabled
     * <p>
     * 检查迁移警告是否启用
     *
     * @return true if migration warnings are enabled / 如果迁移警告启用则返回true
     */
    public boolean areMigrationWarningsEnabled() {
        return enableMigrationWarnings;
    }
    
    /**
     * Log a migration warning message
     * <p>
     * 记录迁移警告消息
     *
     * @param message The warning message / 警告消息
     */
    public void logMigrationWarning(String message) {
        if (!enableMigrationWarnings) {
            return;
        }
        
        // Check if we should only warn once per session
        if (warnedMessages.contains(message)) {
            return;
        }
        
        // Log the warning with English text
        plugin.getLogger().warning("[MIGRATION WARNING] " + message + " - Please consider migrating to the new API package for better future compatibility.");
        
        // Add to warned messages to avoid spam
        warnedMessages.add(message);
    }
    

    
    /**
     * Get a configuration value with default fallback
     * <p>
     * 获取配置值，如果不存在则使用默认值
     *
     * @param path The configuration path / 配置路径
     * @param defaultValue The default value / 默认值
     * @param <T> The value type / 值类型
     * @return The configuration value or default / 配置值或默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfigValue(String path, T defaultValue) {
        return (T) plugin.getConfig().get(path, defaultValue);
    }
    
    // ========== Dynamic API Version Routing ==========
    
    /**
     * Route method call to appropriate version using reflection
     * <p>
     * 使用反射将方法调用路由到适当的版本
     *
     * @param methodName The method name / 方法名
     * @param paramTypes Parameter types / 参数类型
     * @param args Method arguments / 方法参数
     * @return Method result / 方法结果
     */
    public Object routeMethodCall(String methodName, Class<?>[] paramTypes, Object... args) {
        try {
            // Use configured API version to construct class name
            String fullClassName = "cn.enderrealm.easy4form.api." + currentApiVersion + ".Easy4FormAPI";
            Class<?> targetClass = Class.forName(fullClassName);
            Method method = targetClass.getMethod(methodName, paramTypes);
            return method.invoke(null, args);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // Fallback to v1 implementation
            try {
                String fallbackClassName = "cn.enderrealm.easy4form.api.v1.Easy4FormAPI";
                Class<?> fallbackClass = Class.forName(fallbackClassName);
                Method method = fallbackClass.getMethod(methodName, paramTypes);
                return method.invoke(null, args);
            } catch (Exception fallbackException) {
                plugin.getLogger().severe("Failed to route method call " + methodName + ": " + fallbackException.getMessage());
                throw new RuntimeException("API routing failed", fallbackException);
            }
        }
    }
    
    /**
     * Route constructor call to appropriate version using reflection
     * <p>
     * 使用反射将构造函数调用路由到适当的版本
     *
     * @param className The class name without version package / 类名（不包含版本包）
     * @param paramTypes Parameter types / 参数类型
     * @param args Constructor arguments / 构造函数参数
     * @return New instance / 新实例
     */
    public Object routeConstructorCall(String className, Class<?>[] paramTypes, Object... args) {
        try {
            // Use configured API version to construct class name
            String fullClassName = "cn.enderrealm.easy4form.api." + currentApiVersion + "." + className;
            Class<?> targetClass = Class.forName(fullClassName);
            return targetClass.getConstructor(paramTypes).newInstance(args);
        } catch (Exception e) {
            // Fallback to v1 implementation
            try {
                String fallbackClassName = "cn.enderrealm.easy4form.api.v1." + className;
                Class<?> fallbackClass = Class.forName(fallbackClassName);
                return fallbackClass.getConstructor(paramTypes).newInstance(args);
            } catch (Exception fallbackException) {
                plugin.getLogger().severe("Failed to route constructor call " + className + ": " + fallbackException.getMessage());
                throw new RuntimeException("API routing failed", fallbackException);
            }
        }
    }
}