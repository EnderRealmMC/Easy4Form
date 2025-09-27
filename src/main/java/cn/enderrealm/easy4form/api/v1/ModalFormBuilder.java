package cn.enderrealm.easy4form.api.v1;

import cn.enderrealm.easy4form.api.v1.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.ModalForm;
import org.geysermc.cumulus.response.ModalFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.function.Consumer;

/**
 * Builder for creating modal forms (dialog with two buttons) - API v1
 * <p>
 * 用于创建模态表单（带有两个按钮的对话框）的构建器 - API v1
 */
public class ModalFormBuilder {
    private String title = "";
    private String content = "";
    private String button1 = "";
    private String button2 = "";
    private Consumer<Boolean> responseHandler;

    /**
     * Set the title of the form
     * <p>
     * 设置表单标题
     *
     * @param title The title / 标题
     * @return The builder instance / 构建器实例
     */
    public ModalFormBuilder title(String title) {
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
    public ModalFormBuilder content(String content) {
        this.content = content;
        return this;
    }

    /**
     * Set the text for the first button (true button)
     * <p>
     * 设置第一个按钮（确认按钮）的文本
     *
     * @param text The button text / 按钮文本
     * @return The builder instance / 构建器实例
     */
    public ModalFormBuilder button1(String text) {
        this.button1 = text;
        return this;
    }

    /**
     * Set the text for the second button (false button)
     * <p>
     * 设置第二个按钮（取消按钮）的文本
     *
     * @param text The button text / 按钮文本
     * @return The builder instance / 构建器实例
     */
    public ModalFormBuilder button2(String text) {
        this.button2 = text;
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
    public ModalFormBuilder responseHandler(Consumer<Boolean> responseHandler) {
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

        ModalForm.Builder formBuilder = ModalForm.builder()
                .title(title)
                .content(content)
                .button1(button1)
                .button2(button2);

        formBuilder.responseHandler((form, responseData) -> {
            ModalFormResponse response = form.parseResponse(responseData);
            if (response.isCorrect() && responseHandler != null) {
                responseHandler.accept(response.getClickedButtonId() == 0);
            } else if (responseHandler != null) {
                responseHandler.accept(null);
            }
        });

        floodgatePlayer.sendForm(formBuilder.build());
    }
}