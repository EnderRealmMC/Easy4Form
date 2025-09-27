package cn.enderrealm.easy4form.api;

import cn.enderrealm.easy4form.Easy4form;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Builder for creating simple forms - Multi-Version API Proxy
 * <p>
 * 用于创建简单表单的构建器 - 多版本API代理
 * <p>
 * This class provides a unified API interface that automatically routes to the appropriate version
 * based on the current configuration. It supports multiple API versions seamlessly.
 * <p>
 * 此类提供统一的API接口，根据当前配置自动路由到适当的版本。它无缝支持多个API版本。
 */
public class SimpleFormBuilder {

    /**
     * Create a new SimpleFormBuilder instance
     * <p>
     * 创建新的SimpleFormBuilder实例
     *
     * @return A new SimpleFormBuilder instance / 新的SimpleFormBuilder实例
     */
    public static SimpleFormBuilder create() {
        try {
            return (SimpleFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "createSimpleFormBuilder", new Class<?>[]{}
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route createSimpleFormBuilder call: " + e.getMessage());
            return null;
        }
    }

    /**
     * Set the title of the form
     * <p>
     * 设置表单标题
     *
     * @param title The title / 标题
     * @return The builder instance / 构建器实例
     */
    public SimpleFormBuilder title(String title) {
        try {
            return (SimpleFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "simpleFormBuilderTitle", 
                new Class<?>[]{SimpleFormBuilder.class, String.class}, 
                this, title
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route simpleFormBuilderTitle call: " + e.getMessage());
            return this;
        }
    }

    /**
     * Set the content of the form
     * <p>
     * 设置表单内容
     *
     * @param content The content / 内容
     * @return The builder instance / 构建器实例
     */
    public SimpleFormBuilder content(String content) {
        try {
            return (SimpleFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "simpleFormBuilderContent", 
                new Class<?>[]{SimpleFormBuilder.class, String.class}, 
                this, content
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route simpleFormBuilderContent call: " + e.getMessage());
            return this;
        }
    }

    /**
     * Add a button to the form
     * <p>
     * 向表单添加按钮
     *
     * @param text The button text / 按钮文本
     * @return The builder instance / 构建器实例
     */
    public SimpleFormBuilder button(String text) {
        try {
            return (SimpleFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "simpleFormBuilderButton", 
                new Class<?>[]{SimpleFormBuilder.class, String.class}, 
                this, text
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route simpleFormBuilderButton call: " + e.getMessage());
            return this;
        }
    }
    
    /**
     * Add a button with an image to the form
     * <p>
     * 向表单添加带图片的按钮
     *
     * @param text The button text / 按钮文本
     * @param imagePath The path to the image in the resource pack / 资源包中图片的路径
     * @return The builder instance / 构建器实例
     */
    public SimpleFormBuilder button(String text, String imagePath) {
        try {
            return (SimpleFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "simpleFormBuilderButtonWithImage", 
                new Class<?>[]{SimpleFormBuilder.class, String.class, String.class}, 
                this, text, imagePath
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route simpleFormBuilderButtonWithImage call: " + e.getMessage());
            return this;
        }
    }
    
    /**
     * Add a button with an image from URL to the form
     * <p>
     * 向表单添加带URL图片的按钮
     *
     * @param text The button text / 按钮文本
     * @param imageUrl The URL of the image / 图片的URL
     * @return The builder instance / 构建器实例
     */
    public SimpleFormBuilder buttonWithUrl(String text, String imageUrl) {
        try {
            return (SimpleFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "simpleFormBuilderButtonWithUrl", 
                new Class<?>[]{SimpleFormBuilder.class, String.class, String.class}, 
                this, text, imageUrl
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route simpleFormBuilderButtonWithUrl call: " + e.getMessage());
            return this;
        }
    }

    /**
     * Set the response handler for the form
     * <p>
     * 设置表单的响应处理器
     *
     * @param responseHandler The response handler / 响应处理器
     * @return The builder instance / 构建器实例
     */
    public SimpleFormBuilder responseHandler(Consumer<Integer> responseHandler) {
        try {
            return (SimpleFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "simpleFormBuilderResponseHandler", 
                new Class<?>[]{SimpleFormBuilder.class, Consumer.class}, 
                this, responseHandler
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route simpleFormBuilderResponseHandler call: " + e.getMessage());
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
                "simpleFormBuilderSend", 
                new Class<?>[]{SimpleFormBuilder.class, Player.class}, 
                this, player
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route simpleFormBuilderSend call: " + e.getMessage());
        }
    }
}