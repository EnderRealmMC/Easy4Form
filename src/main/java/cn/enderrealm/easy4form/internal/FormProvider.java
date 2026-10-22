package cn.enderrealm.easy4form.internal;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * 表单平台 Provider。
 */
public interface FormProvider {

    /**
     * 获取 Provider 名称。
     *
     * @return Provider 名称
     */
    String name();

    /**
     * 获取该 Provider 承载的表单平台。
     *
     * @return 表单平台
     */
    FormPlatform platform();

    /**
     * 判断该 Provider 是否可以为指定玩家发送表单。
     *
     * @param player 玩家
     * @return 支持时返回 true
     */
    boolean supports(Player player);

    /**
     * 向玩家发送表单。
     *
     * @param player          玩家
     * @param form            表单定义
     * @param responseHandler 响应处理器，可为 null
     * @return 发送成功时返回 true
     */
    boolean send(Player player, FormDefinition form, Consumer<FormResponse> responseHandler);
}
