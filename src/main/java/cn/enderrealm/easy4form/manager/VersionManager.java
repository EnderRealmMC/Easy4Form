package cn.enderrealm.easy4form.manager;

import cn.enderrealm.easy4form.compatibility.V2CompatibilityHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Version Manager - Manages API version mapping and configuration
 * <p>
 * 版本管理器 - 管理API版本映射和配置
 */
public class VersionManager {
    
    private final JavaPlugin plugin;
    private String currentApiVersion;
    private boolean enableMigrationWarnings;
    private final V2CompatibilityHandler compatibilityHandler;
    
    public VersionManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.compatibilityHandler = new V2CompatibilityHandler();
        loadConfiguration();
        
        // Check if migration warning should be displayed
        checkVersionMigrationWarning();
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
        this.enableMigrationWarnings = config.getBoolean("migration-warnings", true);
        
        plugin.getLogger().info("Version Manager loaded - Current API version: " + currentApiVersion);
    }
    
    // Supported API versions in order (latest first)
    private static final String[] SUPPORTED_VERSIONS = {"v2", "v1"};
    private static final String LATEST_VERSION = SUPPORTED_VERSIONS[0];
    
    /**
     * Check version migration warning
     * <p>
     * 检查版本迁移警告
     */
    private void checkVersionMigrationWarning() {
        if (!enableMigrationWarnings) {
            return;
        }
        
        if (!LATEST_VERSION.equals(currentApiVersion)) {
            plugin.getLogger().warning(String.format(
                "[Easy4Form] You are using %s API version. Consider upgrading to %s for better performance and more features.",
                currentApiVersion, LATEST_VERSION
            ));
            plugin.getLogger().warning(String.format(
                "[Easy4Form] Set api-version to '%s' in config.yml to upgrade and unlock new features.",
                LATEST_VERSION
            ));
        }
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
     * Get the compatibility handler
     * <p>
     * 获取兼容性处理器
     *
     * @return The compatibility handler / 兼容性处理器
     */
    public V2CompatibilityHandler getCompatibilityHandler() {
        return compatibilityHandler;
    }
    
    /**
     * Create a FormBuilder instance of the specified type
     * <p>
     * 创建指定类型的FormBuilder实例
     *
     * @param builderType The builder type (e.g., "SimpleFormBuilder") / 构建器类型
     * @return FormBuilder instance / FormBuilder实例
     */
    private Object createFormBuilder(String builderType) {
        try {
            String builderClassName = "cn.enderrealm.easy4form.api." + currentApiVersion + "." + builderType;
            Class<?> builderClass = Class.forName(builderClassName);
            return builderClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // Fallback to v1
            try {
                String fallbackClassName = "cn.enderrealm.easy4form.api.v1." + builderType;
                Class<?> fallbackClass = Class.forName(fallbackClassName);
                return fallbackClass.getDeclaredConstructor().newInstance();
            } catch (Exception fallbackException) {
                plugin.getLogger().severe("Failed to create " + builderType + ": " + fallbackException.getMessage());
                throw new RuntimeException(builderType + " creation failed", fallbackException);
            }
        }
    }
    
    /**
     * Invoke a method on a FormBuilder instance
     * <p>
     * 在FormBuilder实例上调用方法
     *
     * @param methodName The full method name / 完整方法名
     * @param prefix The method prefix to remove / 要移除的方法前缀
     * @param paramTypes Parameter types / 参数类型
     * @param args Method arguments / 方法参数
     * @return Method result / 方法结果
     */
    private Object invokeFormBuilderMethod(String methodName, String prefix, Class<?>[] paramTypes, Object... args) {
        // Extract the actual method name (remove prefix)
        String actualMethodName = methodName.substring(prefix.length());
        actualMethodName = actualMethodName.substring(0, 1).toLowerCase() + actualMethodName.substring(1);
        
        // The first argument should be the FormBuilder instance
        if (args.length > 0 && args[0] != null) {
            try {
                // If configured for v2, use compatibility handler
                if (isVersionV2OrGreater(currentApiVersion)) {
                    return compatibilityHandler.handleMethodInvocation(actualMethodName, prefix, paramTypes, args);
                }
                
                // For v1, use direct invocation
                Object builderInstance = args[0];
                Class<?> builderClass = builderInstance.getClass();
                
                // Create new parameter types and arguments without the builder instance
                Class<?>[] newParamTypes = new Class<?>[paramTypes.length - 1];
                Object[] newArgs = new Object[args.length - 1];
                System.arraycopy(paramTypes, 1, newParamTypes, 0, newParamTypes.length);
                System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                
                Method method = builderClass.getMethod(actualMethodName, newParamTypes);
                return method.invoke(builderInstance, newArgs);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to invoke " + prefix + " method " + actualMethodName + ": " + e.getMessage());
                throw new RuntimeException(prefix + " method invocation failed", e);
            }
        }
        return null;
    }
    
    /**
     * Check if version is v2 or greater
     * <p>
     * 检查版本是否为v2或更高版本
     *
     * @param version The version string / 版本字符串
     * @return true if version >= v2 / 如果版本大于等于v2则返回true
     */
    private boolean isVersionV2OrGreater(String version) {
        if (version == null || !version.startsWith("v")) {
            return false;
        }
        try {
            int versionNumber = Integer.parseInt(version.substring(1));
            return versionNumber >= 2;
        } catch (NumberFormatException e) {
            return false;
        }
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
        // Handle FormBuilder creation
        if ("createSimpleFormBuilder".equals(methodName)) {
            return createFormBuilder("SimpleFormBuilder");
        }
        if ("createCustomFormBuilder".equals(methodName)) {
            return createFormBuilder("CustomFormBuilder");
        }
        if ("createModalFormBuilder".equals(methodName)) {
            return createFormBuilder("ModalFormBuilder");
        }
        
        // Handle FormBuilder method calls
        if (methodName.startsWith("simpleFormBuilder")) {
            return invokeFormBuilderMethod(methodName, "simpleFormBuilder", paramTypes, args);
        }
        if (methodName.startsWith("customFormBuilder")) {
            return invokeFormBuilderMethod(methodName, "customFormBuilder", paramTypes, args);
        }
        if (methodName.startsWith("modalFormBuilder")) {
            return invokeFormBuilderMethod(methodName, "modalFormBuilder", paramTypes, args);
        }
        
        // Handle regular API methods
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