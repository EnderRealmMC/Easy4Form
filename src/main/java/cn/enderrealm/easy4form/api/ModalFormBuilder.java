package cn.enderrealm.easy4form.api;

import cn.enderrealm.easy4form.Easy4form;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Builder for creating modal forms - Multi-Version API Proxy
 * <p>
 * 用于创建模态表单的构建器 - 多版本API代理
 * <p>
 * This class provides a unified API interface that automatically routes to the appropriate version
 * based on the current configuration. It supports multiple API versions seamlessly.
 * <p>
 * 此类提供统一的API接口，根据当前配置自动路由到适当的版本。它无缝支持多个API版本。
 */
public class ModalFormBuilder {

    /**
     * Create a new ModalFormBuilder instance
     * <p>
     * 创建新的ModalFormBuilder实例
     *
     * @return A new ModalFormBuilder instance / 新的ModalFormBuilder实例
     */
    public static ModalFormBuilder create() {
        try {
            // Create a new proxy instance
            ModalFormBuilder proxy = new ModalFormBuilder();
            
            // Initialize the actual builder instance through version manager
            Easy4form.getInstance().getVersionManager().routeMethodCall(
                "createModalForm", 
                new Class<?>[]{}, 
                proxy
            );
            
            return proxy;
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route createModalForm call: " + e.getMessage());
            e.printStackTrace();
            return new ModalFormBuilder();
        }
    }

    /**
     * Set the title of the form
     * <p>
     * 设置表单标题
     *
     * @param title The title / 标题
     * @return This builder instance / 构建器实例
     */
    public ModalFormBuilder title(String title) {
        try {
            return (ModalFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "modalFormBuilderTitle", 
                new Class<?>[]{ModalFormBuilder.class, String.class}, 
                this, title
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route modalFormBuilderTitle call: " + e.getMessage());
            return this;
        }
    }

    /**
     * Set the content of the form
     * <p>
     * 设置表单内容
     *
     * @param content The content / 内容
     * @return This builder instance / 构建器实例
     */
    public ModalFormBuilder content(String content) {
        try {
            return (ModalFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "modalFormBuilderContent", 
                new Class<?>[]{ModalFormBuilder.class, String.class}, 
                this, content
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route modalFormBuilderContent call: " + e.getMessage());
            return this;
        }
    }

    /**
     * Set the first button (usually "Yes" or "Confirm")
     * <p>
     * 设置第一个按钮（通常是"是"或"确认"）
     *
     * @param text The button text / 按钮文本
     * @return This builder instance / 构建器实例
     */
    public ModalFormBuilder button1(String text) {
        try {
            return (ModalFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "modalFormBuilderButton1", 
                new Class<?>[]{ModalFormBuilder.class, String.class}, 
                this, text
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route modalFormBuilderButton1 call: " + e.getMessage());
            return this;
        }
    }

    /**
     * Set the second button (usually "No" or "Cancel")
     * <p>
     * 设置第二个按钮（通常是"否"或"取消"）
     *
     * @param text The button text / 按钮文本
     * @return This builder instance / 构建器实例
     */
    public ModalFormBuilder button2(String text) {
        try {
            return (ModalFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "modalFormBuilderButton2", 
                new Class<?>[]{ModalFormBuilder.class, String.class}, 
                this, text
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route modalFormBuilderButton2 call: " + e.getMessage());
            return this;
        }
    }

    /**
     * Set the response handler for the form
     * <p>
     * 设置表单的响应处理器
     *
     * @param responseHandler The response handler / 响应处理器
     * @return This builder instance / 构建器实例
     */
    public ModalFormBuilder responseHandler(Consumer<Boolean> responseHandler) {
        try {
            return (ModalFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "modalFormBuilderResponseHandler", 
                new Class<?>[]{ModalFormBuilder.class, Consumer.class}, 
                this, responseHandler
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route modalFormBuilderResponseHandler call: " + e.getMessage());
            return this;
        }
    }

    /**
     * Send the form to a player
     * <p>
     * 向玩家发送表单
     *
     * @param player The player to send the form to / 接收表单的玩家
     */
    public void send(Player player) {
        try {
            Easy4form.getInstance().getVersionManager().routeMethodCall(
                "modalFormBuilderSend", 
                new Class<?>[]{ModalFormBuilder.class, Player.class}, 
                this, player
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route modalFormBuilderSend call: " + e.getMessage());
        }
    }
}