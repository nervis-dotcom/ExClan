package ex.nervisking.events;

import ex.api.base.event.Event;
import ex.nervisking.ExClan;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ExEvents extends Event<ExClan> {

    @EventHandler
    public void onPlayerTrade(PlayerTradeEvent event) {
        Player player = event.getPlayer();

    }
}