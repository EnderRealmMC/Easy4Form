package cn.enderrealm.easy4form.internal.paper;

import cn.enderrealm.easy4form.utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 客户端协议版本解析器。
 *
 * <p>ViaVersion 会把握手包中的协议版本改写为服务端版本，因此装有 ViaVersion 时
 * 必须通过 ViaAPI 读取客户端真实版本。未安装 ViaVersion 时，能连接到当前服务端的
 * 客户端必然使用服务端协议，而 Dialog Provider 只在 Paper 1.21.8+ 上启用，
 * 因此无需再读取每个玩家的协议版本；这也避免依赖 1.18 中不存在的
 * {@code Player#getProtocolVersion()}。</p>
 */
final class ViaProtocolHook {

    private static final String VIA_PLUGIN_NAME = "ViaVersion";

    private final Object viaApi;
    private final Method getPlayerVersion;
    private final boolean viaPresent;

    private ViaProtocolHook(Object viaApi, Method getPlayerVersion, boolean viaPresent) {
        this.viaApi = viaApi;
        this.getPlayerVersion = getPlayerVersion;
        this.viaPresent = viaPresent;
    }

    /**
     * 创建协议版本解析器。
     *
     * @return 解析器
     */
    static ViaProtocolHook create() {
        Plugin viaPlugin = Bukkit.getPluginManager().getPlugin(VIA_PLUGIN_NAME);
        if (viaPlugin == null || !viaPlugin.isEnabled()) {
            return new ViaProtocolHook(null, null, false);
        }
        try {
            Class<?> viaClass = Class.forName("com.viaversion.viaversion.api.Via");
            Object viaApi = viaClass.getMethod("getAPI").invoke(null);
            Method getPlayerVersion = Class.forName("com.viaversion.viaversion.api.ViaAPI")
                    .getMethod("getPlayerVersion", UUID.class);
            return new ViaProtocolHook(viaApi, getPlayerVersion, true);
        } catch (ReflectiveOperationException | LinkageError exception) {
            DebugUtils.debug("ViaVersion", "Failed to resolve ViaAPI: " + exception.getMessage());
            return new ViaProtocolHook(null, null, true);
        }
    }

    /**
     * 判断玩家客户端协议是否不低于指定版本。
     *
     * @param player          玩家
     * @param minimumProtocol 最低协议版本
     * @return 满足时返回 true；ViaVersion 存在但版本无法读取时返回 false
     */
    boolean isAtLeast(Player player, int minimumProtocol) {
        if (!viaPresent) {
            return true;
        }
        if (viaApi == null) {
            return false;
        }
        try {
            Object version = getPlayerVersion.invoke(viaApi, player.getUniqueId());
            return version instanceof Integer value && value >= minimumProtocol;
        } catch (ReflectiveOperationException | RuntimeException exception) {
            DebugUtils.debug(
                    "ViaVersion",
                    "Failed to read client version for " + player.getName() + ": " + exception.getMessage()
            );
            return false;
        }
    }
}
