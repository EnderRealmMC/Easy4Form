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
     * 注册 Provider，同一平台只保留最后一次注册的实现。
     *
     * @param provider Provider
     */
    public void register(FormProvider provider) {
        Objects.requireNonNull(provider, "provider");
        providers.removeIf(existing -> existing.platform() == provider.platform());
        providers.add(provider);
    }

    /**
     * 清空所有 Provider。
     */
    public void clear() {
        providers.clear();
    }

    /**
     * 判断是否没有任何已注册的 Provider。
     *
     * @return 没有 Provider 时返回 true
     */
    public boolean isEmpty() {
        return providers.isEmpty();
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

    /**
     * 判断是否存在可接管指定玩家的 Provider。
     *
     * @param player 玩家
     * @return 存在时返回 true
     */
    public boolean hasProvider(Player player) {
        return find(player) != null;
    }
}
