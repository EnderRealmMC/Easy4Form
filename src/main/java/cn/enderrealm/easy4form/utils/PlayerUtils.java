package cn.enderrealm.easy4form.utils;

import cn.enderrealm.easy4form.Easy4form;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

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
     * @param uuid 玩家 UUID
     * @return 基岩版玩家返回 true
     */
    public static boolean isBedrockPlayerByUUID(UUID uuid) {
        String uuidPrefix = Easy4form.getInstance().getConfig().getString("uuid-prefix", "00000000-0000");
        return uuid.toString().startsWith(uuidPrefix);
    }

    /**
     * 检查 UUID 是否为基岩版玩家。
     *
     * @param uuid 玩家 UUID
     * @return 基岩版玩家返回 true
     */
    public static boolean isBedrockPlayer(UUID uuid) {
        String detectionMode = Easy4form.getInstance().getConfig().getString("detection-mode", "floodgate");
        if ("floodgate".equalsIgnoreCase(detectionMode)) {
            try {
                return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
            } catch (Exception exception) {
                if (DebugUtils.isDebugEnabled()) {
                    Easy4form.getInstance().getLogger().warning(
                            "Failed to check Bedrock player via Floodgate API: " + exception.getMessage());
                }
                return isBedrockPlayerByUUID(uuid);
            }
        }
        if ("uuid".equalsIgnoreCase(detectionMode)) {
            return isBedrockPlayerByUUID(uuid);
        }
        return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
    }
}
