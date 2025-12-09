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
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@CommandArg(name = "delegate", description = "Delega a un jugador del clan.", permission = true)
public class DelegateArgument implements CommandArgument {

    public final ClanManager clanManager;

    public DelegateArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        UUID uuid = sender.getUniqueId();
        Clan clan = clanManager.getClan(uuid);
        if (clan == null) {
            sender.sendMessage("%prefix% &cNo estás en un clan.");
            return;
        }
        if (!clan.isLader(uuid)) {
            sender.sendMessage("%prefix% &cNo eres el líder del clan.");
            return;
        }

        if (args.isEmpty()) {
            sender.sendMessage("%prefix% &cDebes poner un jugador para delegar.");
            return;
        }

        OfflinePlayer offlinePlayer = args.getOfflinePlayer(0);
        if (offlinePlayer == null) {
            sender.neverConnected(args.get(0));
            return;
        }

        if (!clan.hasMember(offlinePlayer.getUniqueId())) {
            sender.sendMessage("%prefix% &cEl jugador no está en tu clan.");
            return;
        }

        clan.setDelegate(offlinePlayer.getUniqueId(), offlinePlayer.getName());
        sender.sendMessage("%prefix% &aDelegado a: " + offlinePlayer.getName() + " tu haz quedado como sub líder.");

        clan.getOnlineAll().forEach(player -> {
            utilsManagers.sendMessage(player, "%prefix% &aEl clan tiene un nuevo líder: " + offlinePlayer.getName() + ".");
        });

        if (clan.getDiscord() != null) {
            DiscordWebhooks
                    .of(clan.getDiscord())
                    .setBotName(clan.getClanName())
                    .setTitle("announcement")
                    .setColor(CustomColor.YELLOW)
                    .setAvatarByPlayer(sender.getName())
                    .setDescription("El clan tiene un nuevo líder: " + offlinePlayer.getName() + ".")
                    .hideError()
                    .sendAsync();
        }
    }
}