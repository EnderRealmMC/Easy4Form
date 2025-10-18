package cn.enderrealm.easy4form.api;

import cn.enderrealm.easy4form.Easy4form;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.UUID;

/**
 * Utility class for player-related operations - Multi-Version API Proxy
 * <p>
 * 玩家相关操作的工具类 - 多版本API代理
 * <p>
 * This class provides a unified API interface that automatically routes to the appropriate version
 * based on the current configuration. It supports multiple API versions seamlessly.
 * <p>
 * 此类提供统一的API接口，根据当前配置自动路由到适当的版本。它无缝支持多个API版本。
 */
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
        try {
            return (Boolean) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "isBedrockPlayer", 
                new Class<?>[]{Player.class}, 
                player
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route isBedrockPlayer call: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if a player is a Bedrock player by UUID
     * <p>
     * 通过UUID检查玩家是否为基岩版玩家
     *
     * @param uuid The player's UUID / 玩家的UUID
     * @return true if the player is a Bedrock player / 如果玩家是基岩版玩家则返回true
     */
    public static boolean isBedrockPlayerByUUID(UUID uuid) {
        try {
            return (Boolean) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "isBedrockPlayerByUUID", 
                new Class<?>[]{UUID.class}, 
                uuid
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route isBedrockPlayerByUUID call: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the FloodgatePlayer instance for a player
     * <p>
     * 获取玩家的FloodgatePlayer实例
     *
     * @param player The player / 玩家
     * @return The FloodgatePlayer instance, or null if not a Bedrock player / FloodgatePlayer实例，如果不是基岩版玩家则返回null
     */
    public static FloodgatePlayer getFloodgatePlayer(Player player) {
        try {
            return (FloodgatePlayer) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "getFloodgatePlayer", 
                new Class<?>[]{Player.class}, 
                player
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route getFloodgatePlayer call: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get the FloodgatePlayer instance for a player by UUID
     * <p>
     * 通过UUID获取玩家的FloodgatePlayer实例
     *
     * @param uuid The player's UUID / 玩家的UUID
     * @return The FloodgatePlayer instance, or null if not a Bedrock player / FloodgatePlayer实例，如果不是基岩版玩家则返回null
     */
    public static FloodgatePlayer getFloodgatePlayer(UUID uuid) {
        try {
            return (FloodgatePlayer) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "getFloodgatePlayerByUUID", 
                new Class<?>[]{UUID.class}, 
                uuid
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route getFloodgatePlayerByUUID call: " + e.getMessage());
            return null;
        }
    }

    /**
     * Check if a player is a Bedrock player by UUID (alternative method)
     * <p>
     * 通过UUID检查玩家是否为基岩版玩家（替代方法）
     *
     * @param uuid The player's UUID / 玩家的UUID
     * @return true if the player is a Bedrock player / 如果玩家是基岩版玩家则返回true
     */
    public static boolean isBedrockPlayer(UUID uuid) {
        try {
            return (Boolean) Easy4form.getInstance().getVersionManager().routeMethodCall(
                "isBedrockPlayerUUID", 
                new Class<?>[]{UUID.class}, 
                uuid
            );
        } catch (Exception e) {
            Easy4form.getInstance().getLogger().severe("Failed to route isBedrockPlayerUUID call: " + e.getMessage());
            return false;
        }
    }
}