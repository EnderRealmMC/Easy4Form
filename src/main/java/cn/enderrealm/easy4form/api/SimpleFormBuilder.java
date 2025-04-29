package cn.enderrealm.easy4form.api;

import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Builder for creating simple forms (button list forms)
 * <p>
 * 用于创建简单表单（按钮列表表单）的构建器
 */
public class SimpleFormBuilder {
    private String title = "";
    private String content = "";
    private final List<ButtonComponent> buttons = new ArrayList<>();
    private Consumer<Integer> responseHandler;

    /**
     * Set the title of the form
     * <p>
     * 设置表单标题
     *
     * @param title The title / 标题
     * @return The builder instance / 构建器实例
     */
    public SimpleFormBuilder title(String title) {
        this.title = title;
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
        this.content = content;
        return this;
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
        buttons.add(ButtonComponent.of(text));
        return this;
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
        buttons.add(ButtonComponent.of(text, FormImage.of(FormImage.Type.PATH, imagePath)));
        return this;
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
        buttons.add(ButtonComponent.of(text, FormImage.of(FormImage.Type.URL, imageUrl)));
        return this;
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
        this.responseHandler = responseHandler;
        return this;
    }

    /**
     * Send the form to a player
     * <p>
     * 向玩家发送表单
     *
     * @param player The player to send the form to / 接收表单的玩家
     */
    public void send(Player player) {
        if (!PlayerUtils.isBedrockPlayer(player)) {
            return;
        }

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        if (floodgatePlayer == null) {
            return;
        }

        SimpleForm.Builder formBuilder = SimpleForm.builder()
                .title(title)
                .content(content);

        for (ButtonComponent button : buttons) {
            // 从ButtonComponent中提取文本和图像，然后添加到SimpleForm.Builder中
            if (button.image() != null) {
                formBuilder.button(button.text(), button.image());
            } else {
                formBuilder.button(button.text());
            }
        }

        formBuilder.responseHandler((form, responseData) -> {
            SimpleFormResponse response = form.parseResponse(responseData);
            if (response.isCorrect() && responseHandler != null) {
                responseHandler.accept(response.getClickedButtonId());
            } else if (responseHandler != null) {
                responseHandler.accept(null);
            }
        });

        floodgatePlayer.sendForm(formBuilder.build());
    }
}