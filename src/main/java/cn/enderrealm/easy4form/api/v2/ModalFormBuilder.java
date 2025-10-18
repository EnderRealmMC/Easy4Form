package cn.enderrealm.easy4form.api.v2;

import cn.enderrealm.easy4form.api.v2.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.function.Consumer;

/**
 * Builder for creating modal forms (yes/no forms) - API v2
 * <p>
 * 用于创建模态表单（是/否表单）的构建器 - API v2
 */
public class ModalFormBuilder {
    private final ModalForm.Builder builder;
    private Consumer<Boolean> responseHandler;

    /**
     * Create a new ModalFormBuilder
     * <p>
     * 创建新的ModalFormBuilder
     */
    public ModalFormBuilder() {
        this.builder = ModalForm.builder();
    }

    /**
     * Set the title of the form
     * <p>
     * 设置表单标题
     *
     * @param title The title / 标题
     * @return The builder instance / 构建器实例
     */
    public ModalFormBuilder title(String title) {
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
    public ModalFormBuilder content(String content) {
        builder.content(content);
        return this;
    }

    /**
     * Set the first button (true button)
     * <p>
     * 设置第一个按钮（确认按钮）
     *
     * @param text The button text / 按钮文本
     * @return The builder instance / 构建器实例
     */
    public ModalFormBuilder button1(String text) {
        builder.button1(text);
        return this;
    }

    /**
     * Set the second button (false button)
     * <p>
     * 设置第二个按钮（取消按钮）
     *
     * @param text The button text / 按钮文本
     * @return The builder instance / 构建器实例
     */
    public ModalFormBuilder button2(String text) {
        builder.button2(text);
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
    public ModalFormBuilder responseHandler(Consumer<Boolean> responseHandler) {
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
        ModalForm form;
        if (responseHandler != null) {
            form = builder.validResultHandler(formResponse -> 
                responseHandler.accept(formResponse.clickedButtonId() == 0)
            ).closedOrInvalidResultHandler(() -> 
                responseHandler.accept(false) // Form was closed or invalid
            ).build();
        } else {
            form = builder.build();
        }

        // Send the form
        floodgatePlayer.sendForm(form);
    }
}