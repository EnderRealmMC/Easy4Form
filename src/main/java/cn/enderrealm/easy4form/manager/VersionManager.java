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
    
    // Map to store actual builder instances for each proxy instance
    private final java.util.Map<Object, Object> proxyToActualBuilderMap = new java.util.concurrent.ConcurrentHashMap<>();
    
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
        // For FormBuilder creation, we need to return the proxy class instance
        // that wraps the actual implementation, not the implementation itself
        try {
            if ("SimpleFormBuilder".equals(builderType)) {
                // Create a new proxy SimpleFormBuilder instance
                return new cn.enderrealm.easy4form.api.SimpleFormBuilder();
            } else if ("CustomFormBuilder".equals(builderType)) {
                // Create a new proxy CustomFormBuilder instance  
                return new cn.enderrealm.easy4form.api.CustomFormBuilder();
            } else if ("ModalFormBuilder".equals(builderType)) {
                // Create a new proxy ModalFormBuilder instance
                return new cn.enderrealm.easy4form.api.ModalFormBuilder();
            } else {
                throw new IllegalArgumentException("Unknown builder type: " + builderType);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to create " + builderType + ": " + e.getMessage());
            throw new RuntimeException(builderType + " creation failed", e);
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
        
        // The first argument should be the FormBuilder proxy instance
        if (args.length > 0 && args[0] != null) {
            try {
                Object proxyInstance = args[0];
                
                // Get or create the actual builder instance for this proxy
                Object actualBuilder = proxyToActualBuilderMap.get(proxyInstance);
                if (actualBuilder == null) {
                    String builderType = getBuilderTypeFromPrefix(prefix);
                    actualBuilder = createActualFormBuilder(builderType);
                    proxyToActualBuilderMap.put(proxyInstance, actualBuilder);
                }
                
                // Create new parameter types and arguments without the proxy builder instance
                Class<?>[] newParamTypes = new Class<?>[paramTypes.length - 1];
                Object[] newArgs = new Object[args.length - 1];
                System.arraycopy(paramTypes, 1, newParamTypes, 0, newParamTypes.length);
                System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                
                // Invoke the method on the actual implementation
                Class<?> actualBuilderClass = actualBuilder.getClass();
                Method method = actualBuilderClass.getMethod(actualMethodName, newParamTypes);
                Object result = method.invoke(actualBuilder, newArgs);
                
                // If the result is the actual builder instance, return the proxy instead
                if (result == actualBuilder) {
                    return proxyInstance; // Return the proxy instance
                }
                return result;
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to invoke " + prefix + " method " + actualMethodName + ": " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(prefix + " method invocation failed", e);
            }
        }
        return null;
    }
    
    private String getBuilderTypeFromPrefix(String prefix) {
        switch (prefix) {
            case "simpleFormBuilder":
                return "SimpleFormBuilder";
            case "customFormBuilder":
                return "CustomFormBuilder";
            case "modalFormBuilder":
                return "ModalFormBuilder";
            default:
                throw new IllegalArgumentException("Unknown builder prefix: " + prefix);
        }
    }
    
    private Object createActualFormBuilder(String builderType) {
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
                plugin.getLogger().severe("Failed to create actual " + builderType + ": " + fallbackException.getMessage());
                throw new RuntimeException(builderType + " creation failed", fallbackException);
            }
        }
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
        // Handle FormBuilder creation with parameters - these should be routed to version-specific implementations
        if ("createSimpleForm".equals(methodName) && paramTypes.length == 0) {
            return createFormBuilder("SimpleFormBuilder");
        }
        if ("createCustomForm".equals(methodName) && paramTypes.length == 0) {
            return createFormBuilder("CustomFormBuilder");
        }
        if ("createModalForm".equals(methodName) && paramTypes.length == 0) {
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
        
        // Handle createCustomForm with parameters - need to return proxy with actual builder mapped
        if ("createCustomForm".equals(methodName) && paramTypes.length > 0) {
            try {
                // Create the actual builder using version-specific API
                Object actualBuilder = null;
                try {
                    String fullClassName = "cn.enderrealm.easy4form.api." + currentApiVersion + ".Easy4FormAPI";
                    Class<?> targetClass = Class.forName(fullClassName);
                    Method method = targetClass.getMethod(methodName, paramTypes);
                    actualBuilder = method.invoke(null, args);
                } catch (Exception e) {
                    // Fallback to v1
                    String fallbackClassName = "cn.enderrealm.easy4form.api.v1.Easy4FormAPI";
                    Class<?> fallbackClass = Class.forName(fallbackClassName);
                    Method method = fallbackClass.getMethod(methodName, paramTypes);
                    actualBuilder = method.invoke(null, args);
                }
                
                // Create proxy and map it to actual builder
                Object proxyBuilder = new cn.enderrealm.easy4form.api.CustomFormBuilder();
                proxyToActualBuilderMap.put(proxyBuilder, actualBuilder);
                return proxyBuilder;
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to route createCustomForm call: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("createCustomForm routing failed", e);
            }
        }
        
        // Handle createSimpleForm with parameters - need to return proxy with actual builder mapped
        if ("createSimpleForm".equals(methodName) && paramTypes.length > 0) {
            try {
                // Create the actual builder using version-specific API
                Object actualBuilder = null;
                try {
                    String fullClassName = "cn.enderrealm.easy4form.api." + currentApiVersion + ".Easy4FormAPI";
                    Class<?> targetClass = Class.forName(fullClassName);
                    Method method = targetClass.getMethod(methodName, paramTypes);
                    actualBuilder = method.invoke(null, args);
                } catch (Exception e) {
                    // Fallback to v1
                    String fallbackClassName = "cn.enderrealm.easy4form.api.v1.Easy4FormAPI";
                    Class<?> fallbackClass = Class.forName(fallbackClassName);
                    Method method = fallbackClass.getMethod(methodName, paramTypes);
                    actualBuilder = method.invoke(null, args);
                }
                
                // Create proxy and map it to actual builder
                Object proxyBuilder = new cn.enderrealm.easy4form.api.SimpleFormBuilder();
                proxyToActualBuilderMap.put(proxyBuilder, actualBuilder);
                return proxyBuilder;
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to route createSimpleForm call: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("createSimpleForm routing failed", e);
            }
        }
        
        // Handle createModalForm with parameters - need to return proxy with actual builder mapped
        if ("createModalForm".equals(methodName) && paramTypes.length > 0) {
            try {
                // Create the actual builder using version-specific API
                Object actualBuilder = null;
                try {
                    String fullClassName = "cn.enderrealm.easy4form.api." + currentApiVersion + ".Easy4FormAPI";
                    Class<?> targetClass = Class.forName(fullClassName);
                    Method method = targetClass.getMethod(methodName, paramTypes);
                    actualBuilder = method.invoke(null, args);
                } catch (Exception e) {
                    // Fallback to v1
                    String fallbackClassName = "cn.enderrealm.easy4form.api.v1.Easy4FormAPI";
                    Class<?> fallbackClass = Class.forName(fallbackClassName);
                    Method method = fallbackClass.getMethod(methodName, paramTypes);
                    actualBuilder = method.invoke(null, args);
                }
                
                // Create proxy and map it to actual builder
                Object proxyBuilder = new cn.enderrealm.easy4form.api.ModalFormBuilder();
                proxyToActualBuilderMap.put(proxyBuilder, actualBuilder);
                return proxyBuilder;
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to route createModalForm call: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("createModalForm routing failed", e);
            }
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
                fallbackException.printStackTrace();
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