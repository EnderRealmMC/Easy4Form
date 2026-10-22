package cn.enderrealm.easy4form.internal.floodgate;

import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

/**
 * Floodgate API 桥接。
 *
 * <p>调用前必须先通过 {@link FloodgateAvailability#isAvailable()} 判断，
 * 避免在 Floodgate 缺失时加载该类。</p>
 */
public final class FloodgateBridge {

    private FloodgateBridge() {
    }

    /**
     * 通过 Floodgate API 判断指定 UUID 是否为基岩版玩家。
     *
     * @param uuid 玩家 UUID
     * @return 基岩版玩家返回 true
     */
    public static boolean isFloodgatePlayer(UUID uuid) {
        return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
    }
}
