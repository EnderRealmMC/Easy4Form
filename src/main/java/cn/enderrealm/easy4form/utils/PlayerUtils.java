package cn.enderrealm.easy4form.utils;

import cn.enderrealm.easy4form.Easy4form;
import cn.enderrealm.easy4form.internal.floodgate.FloodgateAvailability;
import cn.enderrealm.easy4form.internal.floodgate.FloodgateBridge;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * 玩家平台检测工具。
 */
public final class PlayerUtils {

    private PlayerUtils() {
    }

    /**
     * 检查玩家是否为基岩版玩家。
     *
     * @param player 玩家
     * @return 基岩版玩家返回 true
     */
    public static boolean isBedrockPlayer(Player player) {
        return player != null && isBedrockPlayer(player.getUniqueId());
    }

    /**
     * 通过 UUID 前缀检查玩家是否为基岩版玩家。
     *
     * <p>仅做前缀匹配，不检查 Floodgate 是否安装；常规判断请使用 {@link #isBedrockPlayer(UUID)}。</p>
     *
     * @param uuid 玩家 UUID
     * @return 基岩版玩家返回 true
     */
    public static boolean isBedrockPlayerByUUID(UUID uuid) {
        String uuidPrefix = getConfigString("uuid-prefix", "00000000-0000");
        return uuid != null && uuid.toString().startsWith(uuidPrefix);
    }

    /**
     * 检查 UUID 是否为基岩版玩家。
     *
     * <p>Floodgate 不存在时直接返回 false，因为 Cumulus 与其一同缺失，基岩版表单能力不存在；
     * Floodgate 已安装但未启用或 API 调用失败时才回退到 UUID 前缀检测。</p>
     *
     * @param uuid 玩家 UUID
     * @return 基岩版玩家返回 true
     */
    public static boolean isBedrockPlayer(UUID uuid) {
        if (!FloodgateAvailability.isPresent()) {
            return false;
        }
        String detectionMode = getConfigString("detection-mode", "floodgate");
        if ("uuid".equalsIgnoreCase(detectionMode)) {
            return isBedrockPlayerByUUID(uuid);
        }
        if (FloodgateAvailability.isAvailable()) {
            try {
                return FloodgateBridge.isFloodgatePlayer(uuid);
            } catch (Exception | LinkageError exception) {
                Easy4form plugin = Easy4form.getInstance();
                if (plugin != null && DebugUtils.isDebugEnabled()) {
                    plugin.getLogger().warning(
                            "Failed to check Bedrock player via Floodgate API: " + exception.getMessage());
                }
            }
        }
        return isBedrockPlayerByUUID(uuid);
    }

    /**
     * 读取配置字符串，插件未初始化时返回默认值。
     *
     * @param path         配置路径
     * @param defaultValue 默认值
     * @return 配置值
     */
    private static String getConfigString(String path, String defaultValue) {
        Easy4form plugin = Easy4form.getInstance();
        return plugin == null ? defaultValue : plugin.getConfig().getString(path, defaultValue);
    }
}
