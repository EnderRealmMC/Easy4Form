package cn.enderrealm.easy4form.api.v1.utils;

import cn.enderrealm.easy4form.Easy4form;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.UUID;

/**
 * Utility class for player-related operations (API v1)
 * <p>
 * 玩家相关操作的工具类 (API v1)
 * 
 * @deprecated This API is based on the old Cumulus interface and is no longer maintained.
 *             It can still be used but is not recommended. Please use the main proxy package
 *             which automatically handles compatibility with the new interface, or directly
 *             use the v2 package for new projects.
 *             <p>
 *             该API基于旧版Cumulus接口，已废弃且不再维护。仍可使用但不推荐。
 *             请使用主代理包（会自动处理新接口兼容性）或直接使用v2包进行新项目开发。
 */
@Deprecated
public class PlayerUtils {

    /**
     * Check if a player is a Bedrock player
     * <p>
     * 检查玩家是否为基岩版玩家
     *
     * @param player The player to check / 要检查的玩家
     * @return true if the player is a Bedrock player / 如果玩家是基岩版玩家则返回true
     */
    public static boolean isBedrockPlayer(Player player) {
        String detectionMode = Easy4form.getInstance().getConfig().getString("detection.mode", "floodgate");
        
        if ("floodgate".equalsIgnoreCase(detectionMode)) {
            // Use Floodgate API to detect Bedrock players
            try {
                return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
            } catch (Exception e) {
                if (Easy4form.getInstance().getConfig().getBoolean("detection.debug")) {
                    Easy4form.getInstance().getLogger().warning("Failed to check if player is a Bedrock player using Floodgate API: " + e.getMessage());
                    e.printStackTrace();
                }
                // Fallback to UUID check if Floodgate API fails
                return isBedrockPlayerByUUID(player.getUniqueId());
            }
        } else if ("uuid".equalsIgnoreCase(detectionMode)) {
            // Use UUID prefix to detect Bedrock players
            return isBedrockPlayerByUUID(player.getUniqueId());
        }
        
        // Default to Floodgate API if detection mode is invalid
        return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
    }

    /**
     * Check if a player is a Bedrock player by UUID prefix
     * <p>
     * 通过UUID前缀检查玩家是否为基岩版玩家
     *
     * @param uuid The player UUID to check / 要检查的玩家UUID
     * @return true if the player is a Bedrock player / 如果玩家是基岩版玩家则返回true
     */
    public static boolean isBedrockPlayerByUUID(UUID uuid) {
        String uuidPrefix = Easy4form.getInstance().getConfig().getString("detection.uuid-prefix", "00000000-0000");
        return uuid.toString().startsWith(uuidPrefix);
    }

    /**
     * Get the FloodgatePlayer instance for a player
     * <p>
     * 获取玩家的FloodgatePlayer实例
     *
     * @param player The player / 玩家
     * @return The FloodgatePlayer instance, or null if the player is not a Bedrock player
     *         / FloodgatePlayer实例，如果玩家不是基岩版玩家则返回null
     */
    public static FloodgatePlayer getFloodgatePlayer(Player player) {
        if (!isBedrockPlayer(player)) {
            return null;
        }
        try {
            return FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        } catch (Exception e) {
            if (Easy4form.getInstance().getConfig().getBoolean("detection.debug")) {
                Easy4form.getInstance().getLogger().warning("Failed to get FloodgatePlayer for " + player.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Get the FloodgatePlayer instance for a player UUID
     * <p>
     * 通过UUID获取FloodgatePlayer实例
     *
     * @param uuid The player UUID / 玩家UUID
     * @return The FloodgatePlayer instance, or null if the player is not a Bedrock player
     *         / FloodgatePlayer实例，如果玩家不是基岩版玩家则返回null
     */
    public static FloodgatePlayer getFloodgatePlayer(UUID uuid) {
        if (!isBedrockPlayer(uuid)) {
            return null;
        }
        try {
            return FloodgateApi.getInstance().getPlayer(uuid);
        } catch (Exception e) {
            if (Easy4form.getInstance().getConfig().getBoolean("detection.debug")) {
                Easy4form.getInstance().getLogger().warning("Failed to get FloodgatePlayer for UUID " + uuid + ": " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Check if a player UUID is a Bedrock player
     * <p>
     * 检查玩家UUID是否为基岩版玩家
     *
     * @param uuid The player UUID to check / 要检查的玩家UUID
     * @return true if the player is a Bedrock player / 如果玩家是基岩版玩家则返回true
     */
    public static boolean isBedrockPlayer(UUID uuid) {
        String detectionMode = Easy4form.getInstance().getConfig().getString("detection.mode", "floodgate");
        
        if ("floodgate".equalsIgnoreCase(detectionMode)) {
            // Use Floodgate API to detect Bedrock players
            try {
                return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
            } catch (Exception e) {
                if (Easy4form.getInstance().getConfig().getBoolean("detection.debug")) {
                    Easy4form.getInstance().getLogger().warning("Failed to check if UUID is a Bedrock player using Floodgate API: " + e.getMessage());
                    e.printStackTrace();
                }
                // Fallback to UUID check if Floodgate API fails
                return isBedrockPlayerByUUID(uuid);
            }
        } else if ("uuid".equalsIgnoreCase(detectionMode)) {
            // Use UUID prefix to detect Bedrock players
            return isBedrockPlayerByUUID(uuid);
        }
        
        // Default to Floodgate API if detection mode is invalid
        return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
    }
}