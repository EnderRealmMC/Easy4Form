package cn.enderrealm.easy4form.api.v2;

import cn.enderrealm.easy4form.api.v2.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.function.Consumer;

/**
 * Builder for creating simple forms (button list forms) - API v2
 * <p>
 * 用于创建简单表单（按钮列表表单）的构建器 - API v2
 */
public class SimpleFormBuilder {
    private final SimpleForm.Builder builder;
    private Consumer<Integer> responseHandler;

    /**
     * Create a new SimpleFormBuilder
     * <p>
     * 创建新的SimpleFormBuilder
     */
    public SimpleFormBuilder() {
        this.builder = SimpleForm.builder();
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
        builder.title(title);
        return this;
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
        builder.content(content);
        return this;
    }

    /**
     * Add a button without image
     * <p>
     * 添加无图片按钮
     *
     * @param text The button text / 按钮文本
     * @return The builder instance / 构建器实例
     */
    public SimpleFormBuilder button(String text) {
        builder.button(text);
        return this;
    }

    /**
     * Add a button with image path
     * <p>
     * 添加带图片路径的按钮
     *
     * @param text The button text / 按钮文本
     * @param imagePath The image path / 图片路径
     * @return The builder instance / 构建器实例
     */
    public SimpleFormBuilder button(String text, String imagePath) {
        builder.button(text, FormImage.Type.PATH, imagePath);
        return this;
    }

    /**
     * Add a button with image URL
     * <p>
     * 添加带图片URL的按钮
     *
     * @param text The button text / 按钮文本
     * @param imageUrl The image URL / 图片URL
     * @return The builder instance / 构建器实例
     */
    public SimpleFormBuilder buttonWithUrl(String text, String imageUrl) {
        builder.button(text, FormImage.Type.URL, imageUrl);
        return this;
    }

    /**
     * Set the response handler
     * <p>
     * 设置响应处理器
     *
     * @param responseHandler The response handler / 响应处理器
     * @return The builder instance / 构建器实例
     */
    public SimpleFormBuilder responseHandler(Consumer<Integer> responseHandler) {
        this.responseHandler = responseHandler;
        return this;
    }

    /**
     * Send the form to a player
     * <p>
     * 向玩家发送表单
     *
     * @param player The player / 玩家
     */
    public void send(Player player) {
        if (!PlayerUtils.isBedrockPlayer(player)) {
            return;
        }

        FloodgatePlayer floodgatePlayer = PlayerUtils.getFloodgatePlayer(player);
        if (floodgatePlayer == null) {
            return;
        }

        // Build the form with response handler
        SimpleForm form;
        if (responseHandler != null) {
            form = builder.validResultHandler(formResponse -> 
                responseHandler.accept(formResponse.clickedButtonId())
            ).closedOrInvalidResultHandler(() -> 
                responseHandler.accept(-1) // Form was closed or invalid
            ).build();
        } else {
            form = builder.build();
        }

        // Send the form
        floodgatePlayer.sendForm(form);
    }
}