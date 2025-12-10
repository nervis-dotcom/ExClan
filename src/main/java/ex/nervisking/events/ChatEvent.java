package ex.nervisking.events;

import ex.api.base.event.Event;
import ex.api.base.model.CustomColor;
import ex.api.base.utils.DiscordWebhooks;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.manager.ChatManager;
import ex.nervisking.models.Chat;
import ex.nervisking.models.Clan;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.UUID;

public class ChatEvent extends Event<ExClan> {

    private final ChatManager chatManager;
    private final ClanManager clanManager;

    public ChatEvent() {
        this.chatManager = plugin.getChatManager();
        this.clanManager = plugin.getClanManager();
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Chat chat = chatManager.getChat(uuid);
        if (chat == null) {
            return;
        }

        Clan clan = clanManager.getClan(uuid);
        if (clan == null) {
            return;
        }

        String message = event.signedMessage().message();

        if (chat == Chat.CLAN) {
            clan.getOnlineAll().forEach(members -> sendMessage(members, "&7[&fClan&7] &b" + player.getName() + " &8-> &f" + message));

            if (clan.hasDiscord()) {
                DiscordWebhooks
                        .of(clan.getDiscord())
                        .setBotName(clan.getClanName())
                        .setTitle("message")
                        .setColor(CustomColor.PURE_RANDOM)
                        .setDescription(message)
                        .setAvatarByPlayer(player.getName())
                        .sendAsync();
            }
            event.setCancelled(true);
        } else if (chat == Chat.ALLY) {
            clanManager.getOnlineAll(clan).forEach(members -> sendMessage(members, "&7[&fAlly&7] &b" + player.getName() + " &8-> &f" + message));
            event.setCancelled(true);
        }
    }
}