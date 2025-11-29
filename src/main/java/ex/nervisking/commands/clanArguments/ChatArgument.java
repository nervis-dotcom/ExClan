package ex.nervisking.commands.clanArguments;

import ex.api.base.command.Arguments;
import ex.api.base.command.CommandArg;
import ex.api.base.command.CommandArgument;
import ex.api.base.command.Sender;
import ex.api.base.model.CustomColor;
import ex.api.base.utils.DiscordWebhooks;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "chat", description = "enviar mensaje a todo los del clan.", permission = true)
public class ChatArgument implements CommandArgument {

    public final ClanManager clanManager;

    public ChatArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        UUID uuid = sender.getUniqueId();
        if (!clanManager.isInClan(uuid)) {
            sender.sendMessage("%prefix% &cNo estás en un clan.");
            return;
        }

        Clan clan = clanManager.getClan(uuid);
        String message = args.join(0);

        for (var player: clan.getOnlineAll()) {
            utilsManagers.sendMessage(player, "&7[" + clan.getClanTag() + "&7] &b" + sender.getName() + " &8-> &f" + message);
        }

        if (clan.getDiscordWebhooks() != null) {
            DiscordWebhooks
                    .of(clan.getDiscordWebhooks())
                    .setBotName(clan.getClanName())
                    .setTitle("message")
                    .setColor(CustomColor.PURE_RANDOM)
                    .setDescription(message)
                    .setAvatarByPlayer(sender.getName())
                    .hideError()
                    .sendAsync();
        }
    }
}