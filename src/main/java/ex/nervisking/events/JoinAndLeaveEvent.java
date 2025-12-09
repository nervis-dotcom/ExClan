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
            if (clan.isLader(player.getUniqueId()) && !clan.getLeaderName().equals(player.getName())) {
                clan.upateName(player.getName());
            }
            clan.getOnlineAll().forEach(member -> {
                if (!member.equals(player)) {
                    sendMessage(member, "%prefix% &aTu compañero de equipo: " + player.getName() + " ha ingresado al servidor.");
                }
            });
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Clan clan = clanManager.getClan(player.getUniqueId());
        if (clan != null) {
            clan.getOnlineAll().forEach(member -> {
                if (!member.equals(player)) {
                    sendMessage(member, "%prefix% &aTu compañero de equipo: " + player.getName() + " ha salido del servidor.");
                }
            });
        }
    }
}