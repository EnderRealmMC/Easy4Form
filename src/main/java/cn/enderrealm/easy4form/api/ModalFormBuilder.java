package cn.enderrealm.easy4form.api;

import cn.enderrealm.easy4form.Easy4form;
import cn.enderrealm.easy4form.internal.FormDefinition;
import cn.enderrealm.easy4form.internal.FormProvider;
import cn.enderrealm.easy4form.internal.FormResponse;
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * 用于创建模态表单的构建器。
 */
public class ModalFormBuilder {

    private String title;
    private String content;
    private String button1;
    private String button2;
    private Consumer<Boolean> responseHandler;

    /**
     * 创建新的 ModalFormBuilder 实例。
     */
    public ModalFormBuilder() {
    }

    /**
     * 创建新的 ModalFormBuilder 实例。
     *
     * @return 新的构建器实例
     */
    public static ModalFormBuilder create() {
        return new ModalFormBuilder();
    }

    /**
     * 设置表单标题。
     *
     * @param title 标题
     * @return 构建器实例
     */
    public ModalFormBuilder title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置表单内容。
     *
     * @param content 内容
     * @return 构建器实例
     */
    public ModalFormBuilder content(String content) {
        this.content = content;
        return this;
    }

    /**
     * 设置第一个按钮（通常是“是”或“确认”）。
     *
     * @param text 按钮文本
     * @return 构建器实例
     */
    public ModalFormBuilder button1(String text) {
        this.button1 = text;
        return this;
    }

    /**
     * 设置第二个按钮（通常是“否”或“取消”）。
     *
     * @param text 按钮文本
     * @return 构建器实例
     */
    public ModalFormBuilder button2(String text) {
        this.button2 = text;
        return this;
    }

    /**
     * 设置响应处理器。
     *
     * @param responseHandler 响应处理器
     * @return 构建器实例
     */
    public ModalFormBuilder responseHandler(Consumer<Boolean> responseHandler) {
        this.responseHandler = responseHandler;
        return this;
    }

    /**
     * 向玩家发送表单。
     *
     * @param player 接收表单的玩家
     */
    public void send(Player player) {
        try {
            FormProvider provider = Easy4form.getInstance().getFormProviderRegistry().find(player);
            if (provider == null) {
                return;
            }
            FormDefinition.Modal form = new FormDefinition.Modal(title, content, button1, button2);
            Consumer<FormResponse> handler = responseHandler == null ? null : response -> {
                if (response instanceof FormResponse.Modal modal) {
                    responseHandler.accept(modal.firstButton());
                    return;
                }
                responseHandler.accept(null);
            };
            provider.send(player, form, handler);
        } catch (Exception exception) {
            Easy4form.getInstance().getLogger().log(
                    Level.SEVERE,
                    "Failed to send modal form: " + exception.getMessage(),
                    exception
            );
        }
    }
}
