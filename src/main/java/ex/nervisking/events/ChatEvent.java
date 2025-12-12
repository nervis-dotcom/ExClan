package ex.nervisking.events;

import ex.api.base.event.Event;
import ex.api.base.model.CustomColor;
import ex.api.base.utils.DiscordWebhooks;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.config.MainConfig;
import ex.nervisking.manager.ChatManager;
import ex.nervisking.models.chat.Chat;
import ex.nervisking.models.Clan;
import ex.nervisking.models.chat.ChatFormat;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ChatEvent extends Event<ExClan> {

    private final MainConfig config;
    private final ChatManager chatManager;
    private final ClanManager clanManager;

    public ChatEvent() {
        this.config = plugin.getMainConfig();
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
            ChatFormat chatFormat = config.getChat("clan");
            if (chatFormat == null) {
                return;
            }

            Component playerComponent = setColorsComponent(player, chatFormat.format());
            if (chatFormat.playerHover().enabled()) {
                Component hoverText = getListComponent(player, chatFormat.playerHover().hoverText());
                playerComponent = playerComponent.hoverEvent(HoverEvent.showText(hoverText));
            }

            if (chatFormat.playerHover().clickEnabled()) {
                ClickEvent clickEvent = action(player, chatFormat.playerHover().clickTag(), chatFormat.playerHover().clickAction());
                if (clickEvent != null) {
                    playerComponent = playerComponent.clickEvent(clickEvent);
                }
            }

            Component messageComponent = setColorsComponent(message);
            if (chatFormat.messageHover().enabled()) {
                Component hoverText = getListComponent(player, chatFormat.messageHover().hoverText());
                messageComponent = messageComponent.hoverEvent(HoverEvent.showText(hoverText));
            }

            if (chatFormat.messageHover().clickEnabled()) {
                ClickEvent clickEvent = action(player, chatFormat.messageHover().clickTag(), chatFormat.messageHover().clickAction());
                if (clickEvent != null) {
                    messageComponent = messageComponent.clickEvent(clickEvent);
                }
            }
            Component finalPlayerComponent = playerComponent.append(messageComponent);
            clan.getOnlineAll().forEach(members -> members.sendMessage(finalPlayerComponent));

            if (clan.hasDiscord()) {
                DiscordWebhooks.of(clan.getDiscord())
                        .setBotName(clan.getClanName())
                        .setTitle("message")
                        .setColor(CustomColor.PURE_RANDOM)
                        .setContent(message)
                        .setAvatarByPlayer(player.getName())
                        .sendAsync();
            }
            event.setCancelled(true);
        } else if (chat == Chat.ALLY) {
            ChatFormat chatFormat = config.getChat("allay");
            if (chatFormat == null) {
                return;
            }

            Component playerComponent = setColorsComponent(player, chatFormat.format());
            if (chatFormat.playerHover().enabled()) {
                Component hoverText = getListComponent(player, chatFormat.playerHover().hoverText());
                playerComponent = playerComponent.hoverEvent(HoverEvent.showText(hoverText));
            }

            if (chatFormat.playerHover().clickEnabled()) {
                ClickEvent clickEvent = action(player, chatFormat.playerHover().clickTag(), chatFormat.playerHover().clickAction());
                if (clickEvent != null) {
                    playerComponent = playerComponent.clickEvent(clickEvent);
                }
            }

            Component messageComponent = setColorsComponent(message);
            if (chatFormat.messageHover().enabled()) {
                Component hoverText = getListComponent(player, chatFormat.messageHover().hoverText());
                messageComponent = messageComponent.hoverEvent(HoverEvent.showText(hoverText));
            }

            if (chatFormat.messageHover().clickEnabled()) {
                ClickEvent clickEvent = action(player, chatFormat.messageHover().clickTag(), chatFormat.messageHover().clickAction());
                if (clickEvent != null) {
                    messageComponent = messageComponent.clickEvent(clickEvent);
                }
            }
            Component finalPlayerComponent = playerComponent.append(messageComponent);
            clanManager.getOnlineAll(clan).forEach(members -> members.sendMessage(finalPlayerComponent));
            event.setCancelled(true);
        }
    }

    public ClickEvent action(@Nullable Player player, @NotNull String action, @NotNull String value) {
        String parsedValue = player != null ? setPlaceholders(player, value) : setColoredMessage(value);
        return switch (action) {
            case "SUGGEST" -> ClickEvent.suggestCommand(parsedValue);
            case "EXECUTE" -> ClickEvent.runCommand(parsedValue);
            case "OPEN" -> ClickEvent.openUrl(parsedValue);
            case "COPY" -> ClickEvent.copyToClipboard(parsedValue);
            default -> null;
        };
    }

    private Component getListComponent(Player source, @NotNull List<String> lines) {
        Component hoverText = Component.empty();
        for(int i = 0; i < lines.size(); ++i) {
            Component line = setColorsComponent(source, lines.get(i));
            hoverText = hoverText.append(line);
            if (i != lines.size() - 1) {
                hoverText = hoverText.append(Component.newline());
            }
        }
        return hoverText;
    }
}