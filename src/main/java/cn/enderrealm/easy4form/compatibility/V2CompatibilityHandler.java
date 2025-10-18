package cn.enderrealm.easy4form.compatibility;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/**
 * V2 API Compatibility Handler
 * <p>
 * 处理v2 API兼容性转换的专用类
 * 
 * @author EnderRealm
 * @since 1.0
 */
public class V2CompatibilityHandler {
    
    // 使用WeakHashMap来缓存代理对象对应的v2实例，避免内存泄漏
    private final Map<Object, Object> proxyToV2InstanceMap = new WeakHashMap<>();
    
    /**
     * Handle method invocation with v2 compatibility
     * <p>
     * 使用v2兼容性处理方法调用
     *
     * @param methodName The method name / 方法名
     * @param builderType The builder type prefix / 构建器类型前缀
     * @param paramTypes Parameter types / 参数类型
     * @param args Method arguments / 方法参数
     * @return Method result / 方法结果
     */
    public Object handleMethodInvocation(String methodName, String builderType, Class<?>[] paramTypes, Object... args) throws Exception {
        if (args.length > 0 && args[0] != null) {
            Object proxyInstance = args[0];
            
            // Get or create the corresponding v2 instance for this proxy
            Object v2BuilderInstance = getOrCreateV2Instance(proxyInstance, builderType);
            Class<?> v2BuilderClass = v2BuilderInstance.getClass();
            
            // Handle responseHandler methods with special conversion
            if ("responseHandler".equals(methodName)) {
                handleResponseHandler(v2BuilderInstance, v2BuilderClass, builderType, args);
                return proxyInstance; // Return the proxy instance to maintain type consistency
            }
            
            // For other methods, use direct invocation with parameter conversion
            Class<?>[] newParamTypes = new Class<?>[paramTypes.length - 1];
            Object[] newArgs = new Object[args.length - 1];
            System.arraycopy(paramTypes, 1, newParamTypes, 0, newParamTypes.length);
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            
            Method method = v2BuilderClass.getMethod(methodName, newParamTypes);
            Object result = method.invoke(v2BuilderInstance, newArgs);
            
            // Always return the proxy instance for builder pattern methods to avoid ClassCastException
            return proxyInstance;
        }
        return null;
    }
    
    /**
     * Get or create v2 instance for the given proxy
     * <p>
     * 为给定的代理获取或创建v2实例
     */
    private Object getOrCreateV2Instance(Object proxyInstance, String builderType) throws Exception {
        // Check if we already have a v2 instance for this proxy
        Object v2Instance = proxyToV2InstanceMap.get(proxyInstance);
        if (v2Instance != null) {
            return v2Instance;
        }
        
        // Create new v2 instance
        String v2ClassName = getV2ClassName(builderType);
        Class<?> v2BuilderClass = Class.forName(v2ClassName);
        v2Instance = v2BuilderClass.getDeclaredConstructor().newInstance();
        
        // Cache the instance
        proxyToV2InstanceMap.put(proxyInstance, v2Instance);
        return v2Instance;
    }
    
    /**
     * Get v2 implementation class name
     * <p>
     * 获取v2实现类名称
     */
    private String getV2ClassName(String builderType) {
        switch (builderType) {
            case "simpleFormBuilder":
                return "cn.enderrealm.easy4form.api.v2.SimpleFormBuilder";
            case "modalFormBuilder":
                return "cn.enderrealm.easy4form.api.v2.ModalFormBuilder";
            case "customFormBuilder":
                return "cn.enderrealm.easy4form.api.v2.CustomFormBuilder";
            default:
                throw new IllegalArgumentException("Unknown builder type: " + builderType);
        }
    }
    
    /**
     * Handle responseHandler with compatibility conversion
     * <p>
     * 处理带兼容性转换的响应处理器
     */
    private Object handleResponseHandler(Object builderInstance, Class<?> builderClass, String builderType, Object... args) throws Exception {
        if (args.length < 2 || !(args[1] instanceof Consumer)) {
            return null;
        }
        
        switch (builderType) {
            case "simpleFormBuilder":
                return handleSimpleFormResponseHandler(builderInstance, builderClass, args);
            case "modalFormBuilder":
                return handleModalFormResponseHandler(builderInstance, builderClass, args);
            case "customFormBuilder":
                return handleCustomFormResponseHandler(builderInstance, builderClass, args);
            default:
                return null;
        }
    }
    
    /**
     * Handle SimpleForm responseHandler conversion
     * <p>
     * 处理SimpleForm响应处理器转换
     */
    @SuppressWarnings("unchecked")
    private Object handleSimpleFormResponseHandler(Object builderInstance, Class<?> builderClass, Object... args) throws Exception {
        Consumer<Integer> v1Handler = (Consumer<Integer>) args[1];
        
        // Create a wrapper that converts v2's -1 (closed) back to v1's null for compatibility
        Consumer<Integer> v2CompatibleHandler = (response) -> {
            if (response != null && response == -1) {
                // v2 uses -1 for closed forms, convert back to null for v1 compatibility
                v1Handler.accept(null);
            } else {
                // Valid button click, pass through
                v1Handler.accept(response);
            }
        };
        
        // Call v2's responseHandler method with the compatible wrapper
        Method responseHandlerMethod = builderClass.getMethod("responseHandler", Consumer.class);
        return responseHandlerMethod.invoke(builderInstance, v2CompatibleHandler);
    }
    
    /**
     * Handle ModalForm responseHandler conversion
     * <p>
     * 处理ModalForm响应处理器转换
     */
    @SuppressWarnings("unchecked")
    private Object handleModalFormResponseHandler(Object builderInstance, Class<?> builderClass, Object... args) throws Exception {
        Consumer<Boolean> v1Handler = (Consumer<Boolean>) args[1];
        
        // Create a wrapper for v2 compatibility
        Consumer<Boolean> v2CompatibleHandler = (response) -> {
            // Pass through directly as both v1 and v2 use Boolean
            v1Handler.accept(response);
        };
        
        // Call v2's responseHandler method
        Method responseHandlerMethod = builderClass.getMethod("responseHandler", Consumer.class);
        return responseHandlerMethod.invoke(builderInstance, v2CompatibleHandler);
    }
    
    /**
     * Handle CustomForm responseHandler conversion
     * <p>
     * 处理CustomForm响应处理器转换
     */
    @SuppressWarnings("unchecked")
    private Object handleCustomFormResponseHandler(Object builderInstance, Class<?> builderClass, Object... args) throws Exception {
        Consumer<Map<String, Object>> v1Handler = (Consumer<Map<String, Object>>) args[1];
        
        // Create a wrapper for v2 compatibility
        Consumer<Map<String, Object>> v2CompatibleHandler = (response) -> {
            // Pass through directly as both v1 and v2 use Map<String, Object>
            v1Handler.accept(response);
        };
        
        // Call v2's responseHandler method
        Method responseHandlerMethod = builderClass.getMethod("responseHandler", Consumer.class);
        return responseHandlerMethod.invoke(builderInstance, v2CompatibleHandler);
    }
}