package cn.enderrealm.easy4form.api;

import cn.enderrealm.easy4form.Easy4form;
import cn.enderrealm.easy4form.internal.ButtonDefinition;
import cn.enderrealm.easy4form.internal.FormDefinition;
import cn.enderrealm.easy4form.internal.FormProvider;
import cn.enderrealm.easy4form.internal.FormResponse;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * 用于创建简单表单的构建器。
 */
public class SimpleFormBuilder {

    private final List<ButtonDefinition> buttons = new ArrayList<>();
    private String title;
    private String content;
    private Consumer<Integer> responseHandler;

    /**
     * 创建新的 SimpleFormBuilder 实例。
     */
    public SimpleFormBuilder() {
    }

    /**
     * 创建新的 SimpleFormBuilder 实例。
     *
     * @return 新的构建器实例
     */
    public static SimpleFormBuilder create() {
        return new SimpleFormBuilder();
    }

    /**
     * 设置表单标题。
     *
     * @param title 标题
     * @return 构建器实例
     */
    public SimpleFormBuilder title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置表单内容。
     *
     * @param content 内容
     * @return 构建器实例
     */
    public SimpleFormBuilder content(String content) {
        this.content = content;
        return this;
    }

    /**
     * 添加按钮。
     *
     * @param text 按钮文本
     * @return 构建器实例
     */
    public SimpleFormBuilder button(String text) {
        buttons.add(ButtonDefinition.text(text));
        return this;
    }

    /**
     * 添加带资源包图片的按钮。
     *
     * @param text      按钮文本
     * @param imagePath 资源包中图片的路径
     * @return 构建器实例
     */
    public SimpleFormBuilder button(String text, String imagePath) {
        buttons.add(ButtonDefinition.path(text, imagePath));
        return this;
    }

    /**
     * 添加带 URL 图片的按钮。
     *
     * @param text     按钮文本
     * @param imageUrl 图片 URL
     * @return 构建器实例
     */
    public SimpleFormBuilder buttonWithUrl(String text, String imageUrl) {
        buttons.add(ButtonDefinition.url(text, imageUrl));
        return this;
    }

    /**
     * 设置响应处理器。
     *
     * @param responseHandler 响应处理器
     * @return 构建器实例
     */
    public SimpleFormBuilder responseHandler(Consumer<Integer> responseHandler) {
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
                Easy4form.warnNoProvider(player);
                return;
            }
            FormDefinition.Simple form = new FormDefinition.Simple(title, content, buttons);
            Consumer<FormResponse> handler = responseHandler == null ? null : response -> {
                if (response instanceof FormResponse.Simple simple) {
                    responseHandler.accept(simple.buttonId());
                    return;
                }
                responseHandler.accept(null);
            };
            provider.send(player, form, handler);
        } catch (Exception exception) {
            Easy4form.getInstance().getLogger().log(
                    Level.SEVERE,
                    "Failed to send simple form: " + exception.getMessage(),
                    exception
            );
        }
    }
}
