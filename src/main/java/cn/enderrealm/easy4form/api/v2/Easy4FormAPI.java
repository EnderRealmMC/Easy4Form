package cn.enderrealm.easy4form.api.v2;

import cn.enderrealm.easy4form.api.v2.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Easy4Form API v2 - A simplified Form API for Floodgate (using new Cumulus interfaces)
 * <p>
 * Easy4Form API v2 - 基于Floodgate的简化Form接口（使用新版Cumulus接口）
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
        return PlayerUtils.isBedrockPlayer(player);
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
        return PlayerUtils.getFloodgatePlayer(player);
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
        return PlayerUtils.getFloodgatePlayer(uuid);
    }

    /**
     * Send a simple form to a player
     * <p>
     * 向玩家发送简单表单
     *
     * @param player The player / 玩家
     * @param title The form title / 表单标题
     * @param content The form content / 表单内容
     * @param buttons The button texts / 按钮文本列表
     * @param responseHandler The response handler / 响应处理器
     */
    public static void sendSimpleForm(Player player, String title, String content, List<String> buttons, Consumer<Integer> responseHandler) {
        if (!isBedrockPlayer(player)) {
            return;
        }
        
        SimpleFormBuilder builder = new SimpleFormBuilder()
                .title(title)
                .content(content)
                .responseHandler(responseHandler);
        
        for (String buttonText : buttons) {
            builder.button(buttonText);
        }
        
        builder.send(player);
    }

    /**
     * Send a simple form with images to a player
     * <p>
     * 向玩家发送带图片的简单表单
     *
     * @param player The player / 玩家
     * @param title The form title / 表单标题
     * @param content The form content / 表单内容
     * @param buttonTexts The button texts / 按钮文本列表
     * @param buttonImages The button image paths / 按钮图片路径列表
     * @param responseHandler The response handler / 响应处理器
     */
    public static void sendSimpleFormWithImages(Player player, String title, String content, List<String> buttonTexts, List<String> buttonImages, Consumer<Integer> responseHandler) {
        if (!isBedrockPlayer(player)) {
            return;
        }
        
        if (buttonTexts.size() != buttonImages.size()) {
            throw new IllegalArgumentException("Button texts and images lists must have the same size");
        }
        
        SimpleFormBuilder builder = new SimpleFormBuilder()
                .title(title)
                .content(content)
                .responseHandler(responseHandler);
        
        for (int i = 0; i < buttonTexts.size(); i++) {
            String imagePath = buttonImages.get(i);
            if (imagePath != null && !imagePath.isEmpty()) {
                builder.button(buttonTexts.get(i), imagePath);
            } else {
                builder.button(buttonTexts.get(i));
            }
        }
        
        builder.send(player);
    }

    /**
     * Send a simple form with URL images to a player
     * <p>
     * 向玩家发送带URL图片的简单表单
     *
     * @param player The player / 玩家
     * @param title The form title / 表单标题
     * @param content The form content / 表单内容
     * @param buttonTexts The button texts / 按钮文本列表
     * @param buttonImageUrls The button image URLs / 按钮图片URL列表
     * @param responseHandler The response handler / 响应处理器
     */
    public static void sendSimpleFormWithUrlImages(Player player, String title, String content, List<String> buttonTexts, List<String> buttonImageUrls, Consumer<Integer> responseHandler) {
        if (!isBedrockPlayer(player)) {
            return;
        }
        
        if (buttonTexts.size() != buttonImageUrls.size()) {
            throw new IllegalArgumentException("Button texts and image URLs lists must have the same size");
        }
        
        SimpleFormBuilder builder = new SimpleFormBuilder()
                .title(title)
                .content(content)
                .responseHandler(responseHandler);
        
        for (int i = 0; i < buttonTexts.size(); i++) {
            String imageUrl = buttonImageUrls.get(i);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                builder.buttonWithUrl(buttonTexts.get(i), imageUrl);
            } else {
                builder.button(buttonTexts.get(i));
            }
        }
        
        builder.send(player);
    }

    /**
     * Send a modal form to a player
     * <p>
     * 向玩家发送模态表单
     *
     * @param player The player / 玩家
     * @param title The form title / 表单标题
     * @param content The form content / 表单内容
     * @param trueButtonText The true button text / 确认按钮文本
     * @param falseButtonText The false button text / 取消按钮文本
     * @param responseHandler The response handler / 响应处理器
     */
    public static void sendModalForm(Player player, String title, String content, String trueButtonText, String falseButtonText, Consumer<Boolean> responseHandler) {
        if (!isBedrockPlayer(player)) {
            return;
        }
        
        new ModalFormBuilder()
                .title(title)
                .content(content)
                .button1(trueButtonText)
                .button2(falseButtonText)
                .responseHandler(responseHandler)
                .send(player);
    }

    /**
     * Create a custom form builder
     * <p>
     * 创建自定义表单构建器
     *
     * @param player The player / 玩家
     * @param title The form title / 表单标题
     * @param responseHandler The response handler / 响应处理器
     * @return The custom form builder / 自定义表单构建器
     */
    public static CustomFormBuilder createCustomForm(Player player, String title, Consumer<Map<String, Object>> responseHandler) {
        if (!isBedrockPlayer(player)) {
            return null;
        }
        
        return new CustomFormBuilder()
                .title(title)
                .responseHandler(responseHandler);
    }

    /**
     * Check if a player UUID belongs to a Bedrock player
     * <p>
     * 检查UUID是否属于基岩版玩家
     *
     * @param uuid The player UUID / 玩家UUID
     * @return true if the UUID belongs to a Bedrock player / 如果UUID属于基岩版玩家则返回true
     */
    public static boolean isBedrockPlayer(UUID uuid) {
        return PlayerUtils.isBedrockPlayer(uuid);
    }
}