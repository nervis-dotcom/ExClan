package ex.nervisking.events;

import ex.api.base.event.Event;
import ex.nervisking.manager.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.config.MainConfig;
import ex.nervisking.models.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinAndLeaveEvent extends Event<ExClan> {

    private final MainConfig config;
    private final ClanManager clanManager;

    public JoinAndLeaveEvent() {
        this.clanManager = plugin.getClanManager();
        this.config = plugin.getMainConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Clan clan = clanManager.getClan(player.getUniqueId());
        if (clan != null) {
            if (clan.isLader(player.getUniqueId()) && !clan.getLeaderName().equals(player.getName())) {
                clan.upateName(player.getName());
            }
            if (config.hasJoinMessage()) {
                clan.getOnlineAll().forEach(member -> {
                    if (!member.equals(player)) {
                        sendMessage(member, config.getJoinMessage().replace("%player%", player.getName()));
                    }
                });
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (config.hasLeaveMessage()) {
            Player player = event.getPlayer();
            Clan clan = clanManager.getClan(player.getUniqueId());
            if (clan != null) {
                clan.getOnlineAll().forEach(member -> {
                    if (!member.equals(player)) {
                        sendMessage(member, config.getLeaveMessage().replace("%player%", player.getName()));
                    }
                });
            }
        }
    }
}