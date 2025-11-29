package ex.nervisking.events;

import ex.api.base.event.Event;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinAndLeaveEvent extends Event<ExClan> {

    public final ClanManager clanManager;

    public JoinAndLeaveEvent() {
        this.clanManager = plugin.getClanManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Clan clan = clanManager.getClan(player.getUniqueId());
        if (clan != null) {
            for (var member : clan.getOnlineAll()) {
                if (!member.equals(player)) {
                    sendMessage(member, "");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Clan clan = clanManager.getClan(event.getPlayer().getUniqueId());
        if (clan != null) {
            for (var member : clan.getOnlineAll()) {
                sendMessage(member, "");
            }
        }
    }
}