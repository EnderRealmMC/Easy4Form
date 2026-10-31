package cn.enderrealm.easy4form.api;

import cn.enderrealm.easy4form.Easy4form;
import cn.enderrealm.easy4form.utils.PlayerUtils;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Easy4Form 统一 API 入口。
 */
public class Easy4FormAPI {

    /**
     * 检查玩家是否为基岩版玩家。
     *
     * @param player 要检查的玩家
     * @return 基岩版玩家返回 true
     */
    public static boolean isBedrockPlayer(Player player) {
        try {
            return PlayerUtils.isBedrockPlayer(player);
        } catch (Exception exception) {
            Easy4form.getInstance().getLogger().log(
                    Level.SEVERE,
                    "Failed to check Bedrock player: " + exception.getMessage(),
                    exception
            );
            return false;
        }
    }

    /**
     * 检查 UUID 是否为基岩版玩家。
     *
     * @param uuid 要检查的玩家 UUID
     * @return 基岩版玩家返回 true
     */
    public static boolean isBedrockPlayer(UUID uuid) {
        try {
            return PlayerUtils.isBedrockPlayer(uuid);
        } catch (Exception exception) {
            Easy4form.getInstance().getLogger().log(
                    Level.SEVERE,
                    "Failed to check Bedrock player: " + exception.getMessage(),
                    exception
            );
            return false;
        }
    }

    /**
     * 判断玩家当前是否可接收表单。
     *
     * <p>返回 true 表示至少有一个表单 Provider 能接管该玩家，包括基岩版
     * 与 Java 版 Dialog；返回 false 时发送方法会被静默忽略，响应处理器
     * 不会触发，调用方需要自行提供兜底交互。</p>
     *
     * @param player 要检查的玩家
     * @return 存在可接管该玩家的 Provider 时返回 true
     */
    public static boolean isFormSupported(Player player) {
        try {
            return hasActiveProvider(player);
        } catch (Exception exception) {
            Easy4form.getInstance().getLogger().log(
                    Level.SEVERE,
                    "Failed to check form support: " + exception.getMessage(),
                    exception
            );
            return false;
        }
    }

    /**
     * 向玩家发送简单表单。
     *
     * @param player          接收表单的玩家
     * @param title           表单标题
     * @param content         表单内容
     * @param buttons         按钮文本
     * @param responseHandler 响应处理器
     */
    public static void sendSimpleForm(
            Player player,
            String title,
            String content,
            List<String> buttons,
            Consumer<Integer> responseHandler
    ) {
        try {
            if (!hasActiveProvider(player)) {
                Easy4form.warnNoProvider(player);
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
        } catch (Exception exception) {
            Easy4form.getInstance().getLogger().log(
                    Level.SEVERE,
                    "Failed to send simple form: " + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * 向玩家发送带资源包图片按钮的简单表单。
     *
     * @param player          接收表单的玩家
     * @param title           表单标题
     * @param content         表单内容
     * @param buttonTexts     按钮文本
     * @param buttonImages    按钮图片（资源包路径）
     * @param responseHandler 响应处理器
     */
    public static void sendSimpleFormWithImages(
            Player player,
            String title,
            String content,
            List<String> buttonTexts,
            List<String> buttonImages,
            Consumer<Integer> responseHandler
    ) {
        try {
            if (!hasActiveProvider(player)) {
                Easy4form.warnNoProvider(player);
                return;
            }
            if (buttonTexts.size() != buttonImages.size()) {
                throw new IllegalArgumentException("Button texts and images lists must have the same size");
            }
            SimpleFormBuilder builder = new SimpleFormBuilder()
                    .title(title)
                    .content(content)
                    .responseHandler(responseHandler);
            for (int index = 0; index < buttonTexts.size(); index++) {
                String imagePath = buttonImages.get(index);
                if (imagePath != null && !imagePath.isEmpty()) {
                    builder.button(buttonTexts.get(index), imagePath);
                } else {
                    builder.button(buttonTexts.get(index));
                }
            }
            builder.send(player);
        } catch (Exception exception) {
            Easy4form.getInstance().getLogger().log(
                    Level.SEVERE,
                    "Failed to send simple form with images: " + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * 向玩家发送带 URL 图片按钮的简单表单。
     *
     * @param player          接收表单的玩家
     * @param title           表单标题
     * @param content         表单内容
     * @param buttonTexts     按钮文本
     * @param buttonImageUrls 按钮图片 URL
     * @param responseHandler 响应处理器
     */
    public static void sendSimpleFormWithUrlImages(
            Player player,
            String title,
            String content,
            List<String> buttonTexts,
            List<String> buttonImageUrls,
            Consumer<Integer> responseHandler
    ) {
        try {
            if (!hasActiveProvider(player)) {
                Easy4form.warnNoProvider(player);
                return;
            }
            if (buttonTexts.size() != buttonImageUrls.size()) {
                throw new IllegalArgumentException("Button texts and image URLs lists must have the same size");
            }
            SimpleFormBuilder builder = new SimpleFormBuilder()
                    .title(title)
                    .content(content)
                    .responseHandler(responseHandler);
            for (int index = 0; index < buttonTexts.size(); index++) {
                String imageUrl = buttonImageUrls.get(index);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    builder.buttonWithUrl(buttonTexts.get(index), imageUrl);
                } else {
                    builder.button(buttonTexts.get(index));
                }
            }
            builder.send(player);
        } catch (Exception exception) {
            Easy4form.getInstance().getLogger().log(
                    Level.SEVERE,
                    "Failed to send simple form with URL images: " + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * 向玩家发送模态表单。
     *
     * @param player          接收表单的玩家
     * @param title           表单标题
     * @param content         表单内容
     * @param trueButtonText  确认按钮文本
     * @param falseButtonText 取消按钮文本
     * @param responseHandler 响应处理器
     */
    public static void sendModalForm(
            Player player,
            String title,
            String content,
            String trueButtonText,
            String falseButtonText,
            Consumer<Boolean> responseHandler
    ) {
        try {
            if (!hasActiveProvider(player)) {
                Easy4form.warnNoProvider(player);
                return;
            }
            new ModalFormBuilder()
                    .title(title)
                    .content(content)
                    .button1(trueButtonText)
                    .button2(falseButtonText)
                    .responseHandler(responseHandler)
                    .send(player);
        } catch (Exception exception) {
            Easy4form.getInstance().getLogger().log(
                    Level.SEVERE,
                    "Failed to send modal form: " + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * 创建自定义表单构建器。
     *
     * @param player          接收表单的玩家
     * @param title           表单标题
     * @param responseHandler 响应处理器
     * @return 自定义表单构建器；没有可用 Provider 时返回 null
     */
    public static CustomFormBuilder createCustomForm(
            Player player,
            String title,
            Consumer<Map<String, Object>> responseHandler
    ) {
        try {
            if (!hasActiveProvider(player)) {
                Easy4form.warnNoProvider(player);
                return null;
            }
            return new CustomFormBuilder()
                    .title(title)
                    .responseHandler(responseHandler);
        } catch (Exception exception) {
            Easy4form.getInstance().getLogger().log(
                    Level.SEVERE,
                    "Failed to create custom form: " + exception.getMessage(),
                    exception
            );
            return null;
        }
    }

    /**
     * 判断是否存在可接管该玩家的 Provider。
     *
     * @param player 玩家
     * @return 存在可用 Provider 时返回 true
     */
    private static boolean hasActiveProvider(Player player) {
        Easy4form plugin = Easy4form.getInstance();
        return plugin != null && plugin.getFormProviderRegistry().hasProvider(player);
    }
}
