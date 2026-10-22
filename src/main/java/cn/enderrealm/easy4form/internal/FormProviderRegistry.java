package cn.enderrealm.easy4form.internal;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 表单 Provider 注册表。
 */
public final class FormProviderRegistry {

    private final List<FormProvider> providers = new CopyOnWriteArrayList<>();

    /**
     * 注册 Provider。
     *
     * @param provider Provider
     */
    public void register(FormProvider provider) {
        providers.add(Objects.requireNonNull(provider, "provider"));
    }

    /**
     * 清空所有 Provider。
     */
    public void clear() {
        providers.clear();
    }

    /**
     * 查找支持指定玩家的第一个 Provider。
     *
     * @param player 玩家
     * @return 匹配的 Provider，不存在时返回 null
     */
    public FormProvider find(Player player) {
        if (player == null) {
            return null;
        }
        for (FormProvider provider : providers) {
            if (provider.supports(player)) {
                return provider;
            }
        }
        return null;
    }
}
