package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.manager.ChatManager;
import ex.nervisking.models.chat.Chat;

import java.util.UUID;

@CommandArg(name = "chat", description = "enviar mensaje a todo los del clan.", permission = true)
public class ChatArgument implements CommandArgument {

    private final ChatManager chatManager;
    private final ClanManager clanManager;

    public ChatArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
        this.chatManager = plugin.getChatManager();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        if (args.isEmpty()) {
            sender.helpLang("chat.usage");
            return;
        }
        UUID uuid = sender.getUniqueId();
        if (!clanManager.isInClan(uuid)) {
            sender.sendLang("no-clan");
            return;
        }

        switch (args.get(0).toUpperCase()) {
            case "CLAN" -> {
                chatManager.setChat(uuid, Chat.CLAN);
                sender.sendLang("chat.clan");
            }
            case "ALLY" -> {
                chatManager.setChat(uuid, Chat.ALLY);
                sender.sendLang("chat.ally");
            }
            case "OFF" -> {
                chatManager.removeChat(uuid);
                sender.sendLang("chat.off");
            }
            default -> sender.helpLang("chat.usage");
        }
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.add("CLAN", "ALLY", "OFF");
        }

        return completions;
    }
}