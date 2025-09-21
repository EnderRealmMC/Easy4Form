package cn.enderrealm.easy4form.api;

import cn.enderrealm.easy4form.Easy4form;
import cn.enderrealm.easy4form.manager.VersionManager;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.UUID;

/**
 * Player utilities for Easy4Form - Legacy API Proxy
 * <p>
 * Easy4Form的玩家工具类 - 传统API代理
 * 
 * @deprecated This class is deprecated and will be removed in future versions.
 *             Please use {@link cn.enderrealm.easy4form.apiv1.utils.PlayerUtils} instead.
 *             此类已弃用，将在未来版本中移除。请使用 {@link cn.enderrealm.easy4form.apiv1.utils.PlayerUtils} 代替。
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
     * @deprecated Use {@link cn.enderrealm.easy4form.apiv1.utils.PlayerUtils#isBedrockPlayer(Player)} instead
     */
    @Deprecated
    public static boolean isBedrockPlayer(Player player) {
        Easy4form.getInstance().getVersionManager().logMigrationWarning(
            "Using deprecated PlayerUtils.isBedrockPlayer(Player). Please migrate to apiv1.utils.PlayerUtils"
        );
        return cn.enderrealm.easy4form.apiv1.utils.PlayerUtils.isBedrockPlayer(player);
    }

    /**
     * Check if a player UUID is a Bedrock player by UUID prefix
     * <p>
     * 通过UUID前缀检查玩家是否为基岩版玩家
     *
     * @param uuid The player UUID to check / 要检查的玩家UUID
     * @return true if the player is a Bedrock player / 如果玩家是基岩版玩家则返回true
     * @deprecated Use {@link cn.enderrealm.easy4form.apiv1.utils.PlayerUtils#isBedrockPlayerByUUID(UUID)} instead
     */
    @Deprecated
    public static boolean isBedrockPlayerByUUID(UUID uuid) {
        Easy4form.getInstance().getVersionManager().logMigrationWarning(
            "Using deprecated PlayerUtils.isBedrockPlayerByUUID(UUID). Please migrate to apiv1.utils.PlayerUtils"
        );
        return cn.enderrealm.easy4form.apiv1.utils.PlayerUtils.isBedrockPlayerByUUID(uuid);
    }

    /**
     * Get the FloodgatePlayer instance for a player
     * <p>
     * 获取玩家的FloodgatePlayer实例
     *
     * @param player The player / 玩家
     * @return The FloodgatePlayer instance, or null if the player is not a Bedrock player
     *         / FloodgatePlayer实例，如果玩家不是基岩版玩家则返回null
     * @deprecated Use {@link cn.enderrealm.easy4form.apiv1.utils.PlayerUtils#getFloodgatePlayer(Player)} instead
     */
    @Deprecated
    public static FloodgatePlayer getFloodgatePlayer(Player player) {
        Easy4form.getInstance().getVersionManager().logMigrationWarning(
            "Using deprecated PlayerUtils.getFloodgatePlayer(Player). Please migrate to apiv1.utils.PlayerUtils"
        );
        return cn.enderrealm.easy4form.apiv1.utils.PlayerUtils.getFloodgatePlayer(player);
    }

    /**
     * Get the FloodgatePlayer instance for a player UUID
     * <p>
     * 通过UUID获取FloodgatePlayer实例
     *
     * @param uuid The player UUID / 玩家UUID
     * @return The FloodgatePlayer instance, or null if the player is not a Bedrock player
     *         / FloodgatePlayer实例，如果玩家不是基岩版玩家则返回null
     * @deprecated Use {@link cn.enderrealm.easy4form.apiv1.utils.PlayerUtils#getFloodgatePlayer(UUID)} instead
     */
    @Deprecated
    public static FloodgatePlayer getFloodgatePlayer(UUID uuid) {
        Easy4form.getInstance().getVersionManager().logMigrationWarning(
            "Using deprecated PlayerUtils.getFloodgatePlayer(UUID). Please migrate to apiv1.utils.PlayerUtils"
        );
        return cn.enderrealm.easy4form.apiv1.utils.PlayerUtils.getFloodgatePlayer(uuid);
    }

    /**
     * Check if a player UUID is a Bedrock player
     * <p>
     * 检查玩家UUID是否为基岩版玩家
     *
     * @param uuid The player UUID to check / 要检查的玩家UUID
     * @return true if the player is a Bedrock player / 如果玩家是基岩版玩家则返回true
     * @deprecated Use {@link cn.enderrealm.easy4form.apiv1.utils.PlayerUtils#isBedrockPlayer(UUID)} instead
     */
    @Deprecated
    public static boolean isBedrockPlayer(UUID uuid) {
        Easy4form.getInstance().getVersionManager().logMigrationWarning(
            "Using deprecated PlayerUtils.isBedrockPlayer(UUID). Please migrate to apiv1.utils.PlayerUtils"
        );
        return cn.enderrealm.easy4form.apiv1.utils.PlayerUtils.isBedrockPlayer(uuid);
    }
}