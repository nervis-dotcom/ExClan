package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.nervisking.manager.ClanManager;
import ex.nervisking.models.chat.Chat;

import java.util.UUID;

@CommandArg(name = "chat", description = "enviar mensaje a todo los del clan.", permission = true)
public record ChatArgument(ClanManager clanManager) implements CommandArgument {

    @Override
    public void execute(Sender sender, Arguments args) {
        if (args.isEmpty()) {
            sender.helpLang("chat.usage");
            return;
        }
        UUID uuid = sender.getUniqueId();
        var clan = clanManager.getClan(uuid);
        if (clan == null) {
            sender.sendLang("no-clan");
            return;
        }

        switch (args.get(0).toUpperCase()) {
            case "CLAN" -> {
                clan.setChat(uuid, Chat.CLAN);
                sender.sendLang("chat.clan");
            }
            case "ALLY" -> {
                clan.setChat(uuid, Chat.ALLY);
                sender.sendLang("chat.ally");
            }
            case "OFF" -> {
                clan.setChat(uuid, Chat.NONE);
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