package cn.enderrealm.easy4form.internal.floodgate;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Floodgate 可用性检查。
 *
 * <p>该类不引用任何 Floodgate 类型，可在 Floodgate 缺失时安全加载。</p>
 */
public final class FloodgateAvailability {

    private static final String PLUGIN_NAME = "floodgate";

    private FloodgateAvailability() {
    }

    /**
     * 判断 Floodgate 插件是否已加载并启用。
     *
     * @return 可用时返回 true
     */
    public static boolean isAvailable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        return plugin != null && plugin.isEnabled();
    }
}
