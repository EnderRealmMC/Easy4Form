package cn.enderrealm.easy4form.internal.paper;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Java Dialog 事件监听器。
 *
 * <p>该监听器随 {@link PaperDialogProvider} 一起创建，只在支持 Paper Dialog API
 * 的服务端加载。</p>
 */
final class PaperDialogListener implements Listener {

    private final PaperDialogProvider provider;

    /**
     * 创建监听器。
     *
     * @param provider 所属 Provider
     */
    PaperDialogListener(PaperDialogProvider provider) {
        this.provider = provider;
    }

    /**
     * 处理 Dialog 自定义点击动作。
     *
     * @param event 点击事件
     */
    @EventHandler
    public void onCustomClick(PlayerCustomClickEvent event) {
        if (!(event.getCommonConnection() instanceof PlayerGameConnection connection)) {
            return;
        }
        Player player = connection.getPlayer();
        if (player == null) {
            return;
        }
        Key identifier = event.getIdentifier();
        if (!PaperDialogProvider.NAMESPACE.equals(identifier.namespace())) {
            return;
        }
        provider.handleClick(player, identifier.value(), event.getDialogResponseView());
    }

    /**
     * 玩家退出时清理待处理会话。
     *
     * @param event 退出事件
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        provider.remove(event.getPlayer());
    }
}
