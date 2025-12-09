package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.manager.ChatManager;
import ex.nervisking.models.Chat;

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
            sender.help("Usa /clan chat <CLAN/ALLY/OFF>");
            return;
        }
        UUID uuid = sender.getUniqueId();
        if (!clanManager.isInClan(uuid)) {
            sender.sendMessage("%prefix% &cNo estás en un clan.");
            return;
        }

        switch (args.get(0).toUpperCase()) {
            case "CLAN" -> {
                chatManager.setChat(uuid, Chat.CLAN);
                sender.sendMessage("%prefix% &aHas entrado al chat del clan.");
            }
            case "ALLY" -> {
                chatManager.setChat(uuid, Chat.ALLY);
                sender.sendMessage("%prefix% &aHas entrado al chat de los allies.");
            }
            case "OFF" -> {
                chatManager.removeChat(uuid);
                sender.sendMessage("%prefix% &aHas salido al chat global.");
            }
            default -> sender.help("Usa /clan chat <CLAN/ALLY/OFF>");
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