package cn.enderrealm.easy4form.api;

import cn.enderrealm.easy4form.Easy4form;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Easy4Form API - Multi-Version API Proxy
 * <p>
 * Easy4Form API - 多版本API代理
 * <p>
 * This class provides a unified API interface that automatically routes to the appropriate version
 * based on the current configuration. It supports multiple API versions seamlessly.
 * <p>
 * 此类提供统一的API接口，根据当前配置自动路由到适当的版本。它无缝支持多个API版本。
 */
public class Easy4FormAPI {

    /**
     * Check if a player is a Bedrock player
     * <p>
     * 检查玩家是否为基岩版玩家
     *
     * @param player The player to check / 要检查的玩家
     * @return true if the player is a Bedrock player / 如果玩家是基岩版玩家则返回true
     */
    public static boolean isBedrockPlayer(Player player) {
        try {
            return (Boolean) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "isBedrockPlayer", new Class<?>[]{Player.class}, player
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route isBedrockPlayer call: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the FloodgatePlayer instance for a player
     * <p>
     * 获取玩家的FloodgatePlayer实例
     *
     * @param player The player / 玩家
     * @return The FloodgatePlayer instance, or null if the player is not a Bedrock player
     *         / FloodgatePlayer实例，如果玩家不是基岩版玩家则返回null
     */
    public static FloodgatePlayer getFloodgatePlayer(Player player) {
        try {
            return (FloodgatePlayer) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "getFloodgatePlayer", new Class<?>[]{Player.class}, player
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route getFloodgatePlayer call: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get the FloodgatePlayer instance for a player UUID
     * <p>
     * 通过UUID获取FloodgatePlayer实例
     *
     * @param uuid The player UUID / 玩家UUID
     * @return The FloodgatePlayer instance, or null if the player is not a Bedrock player
     *         / FloodgatePlayer实例，如果玩家不是基岩版玩家则返回null
     */
    public static FloodgatePlayer getFloodgatePlayer(UUID uuid) {
        try {
            return (FloodgatePlayer) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "getFloodgatePlayer", new Class<?>[]{UUID.class}, uuid
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route getFloodgatePlayer call: " + e.getMessage());
            return null;
        }
    }

    /**
     * Send a simple form to a player
     * <p>
     * 向玩家发送简单表单
     *
     * @param player The player to send the form to / 接收表单的玩家
     * @param title The title of the form / 表单标题
     * @param content The content of the form / 表单内容
     * @param buttons The buttons to display / 要显示的按钮
     * @param responseHandler The response handler / 响应处理器
     */
    public static void sendSimpleForm(Player player, String title, String content, List<String> buttons, Consumer<Integer> responseHandler) {
        try {
            Easy4form.getInstance().getVersionManager().routeMethodCall(
                "sendSimpleForm", 
                new Class<?>[]{Player.class, String.class, String.class, List.class, Consumer.class}, 
                player, title, content, buttons, responseHandler
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route sendSimpleForm call: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Send a simple form with image buttons to a player
     * <p>
     * 向玩家发送带图片按钮的简单表单
     *
     * @param player The player to send the form to / 接收表单的玩家
     * @param title The title of the form / 表单标题
     * @param content The content of the form / 表单内容
     * @param buttonTexts The button texts / 按钮文本
     * @param buttonImages The button images (resource pack paths) / 按钮图片（资源包路径）
     * @param responseHandler The response handler / 响应处理器
     */
    public static void sendSimpleFormWithImages(Player player, String title, String content, List<String> buttonTexts, List<String> buttonImages, Consumer<Integer> responseHandler) {
        try {
            Easy4form.getInstance().getVersionManager().routeMethodCall(
                "sendSimpleFormWithImages", 
                new Class<?>[]{Player.class, String.class, String.class, List.class, List.class, Consumer.class}, 
                player, title, content, buttonTexts, buttonImages, responseHandler
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route sendSimpleFormWithImages call: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Send a simple form with URL image buttons to a player
     * <p>
     * 向玩家发送带URL图片按钮的简单表单
     *
     * @param player The player to send the form to / 接收表单的玩家
     * @param title The title of the form / 表单标题
     * @param content The content of the form / 表单内容
     * @param buttonTexts The button texts / 按钮文本
     * @param buttonImageUrls The button image URLs / 按钮图片URL
     * @param responseHandler The response handler / 响应处理器
     */
    public static void sendSimpleFormWithUrlImages(Player player, String title, String content, List<String> buttonTexts, List<String> buttonImageUrls, Consumer<Integer> responseHandler) {
        try {
            Easy4form.getInstance().getVersionManager().routeMethodCall(
                "sendSimpleFormWithUrlImages", 
                new Class<?>[]{Player.class, String.class, String.class, List.class, List.class, Consumer.class}, 
                player, title, content, buttonTexts, buttonImageUrls, responseHandler
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route sendSimpleFormWithUrlImages call: " + e.getMessage());
        }
    }

    /**
     * Send a modal form to a player
     * <p>
     * 向玩家发送模态表单
     *
     * @param player The player to send the form to / 接收表单的玩家
     * @param title The title of the form / 表单标题
     * @param content The content of the form / 表单内容
     * @param trueButtonText The text for the true button / 确认按钮文本
     * @param falseButtonText The text for the false button / 取消按钮文本
     * @param responseHandler A consumer that handles the response / 处理响应的消费者函数
     */
    public static void sendModalForm(Player player, String title, String content, String trueButtonText, String falseButtonText, Consumer<Boolean> responseHandler) {
        try {
            Easy4form.getInstance().getVersionManager().routeMethodCall(
                "sendModalForm", 
                new Class<?>[]{Player.class, String.class, String.class, String.class, String.class, Consumer.class}, 
                player, title, content, trueButtonText, falseButtonText, responseHandler
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route sendModalForm call: " + e.getMessage());
        }
    }

    /**
     * Send a custom form to a player
     * <p>
     * 向玩家发送自定义表单
     *
     * @param player          The player to send the form to / 接收表单的玩家
     * @param title           The title of the form / 表单标题
     * @param responseHandler A consumer that handles the response / 处理响应的消费者函数
     * @return A CustomFormBuilder instance for adding elements / 用于添加元素的CustomFormBuilder实例
     */
    public static cn.enderrealm.easy4form.api.CustomFormBuilder createCustomForm(Player player, String title, Consumer<Map<String, Object>> responseHandler) {
        try {
            return (cn.enderrealm.easy4form.api.CustomFormBuilder) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "createCustomForm", 
                new Class<?>[]{Player.class, String.class, Consumer.class}, 
                player, title, responseHandler
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route createCustomForm call: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Check if a player UUID is a Bedrock player
     * <p>
     * 检查玩家UUID是否为基岩版玩家
     *
     * @param uuid The player UUID to check / 要检查的玩家UUID
     * @return true if the player is a Bedrock player / 如果玩家是基岩版玩家则返回true
     */
    public static boolean isBedrockPlayer(UUID uuid) {
        try {
            return (Boolean) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "isBedrockPlayer", new Class<?>[]{UUID.class}, uuid
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route isBedrockPlayer call: " + e.getMessage());
            return false;
        }
    }
}