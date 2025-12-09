package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@CommandArg(name = "unban", permission = true)
public class UnBanArgument  implements CommandArgument {

    private final ClanManager clanManager;

    public UnBanArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        if (args.lacksMinArgs(1)) {
            sender.help("Usa /clan unban <player>");
        }

        OfflinePlayer offlinePlayer = args.getOfflinePlayer(0);
        if (offlinePlayer == null) {
            sender.neverConnected(args.get(0));
            return;
        }

        Clan clan = clanManager.getClan(sender.getUniqueId());
        if (clan == null) {
            sender.sendMessage("%prefix% &cNo estás en un clan.");
            return;
        }

        if (!clan.isManager(sender.getUniqueId())) {
            sender.sendMessage("%prefix% &cNo eres líder del clan.");
            return;
        }

        if (!clan.isBanned(offlinePlayer.getUniqueId())) {
            sender.sendMessage("%prefix% &cEste jugador no está baneado.");
            return;
        }

        clan.unbanMember(offlinePlayer.getUniqueId());
        sender.sendMessage("%prefix% &aHaz desbaneado al jugador: " + offlinePlayer.getName());
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        Clan clan = clanManager.getClan(sender.getUniqueId());
        if (clan == null) {
            return completions;
        }
        if (args.has(1)) {
            for (var s : clan.getBannedMembers()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
                if (offlinePlayer.hasPlayedBefore()) {
                    completions.add(offlinePlayer.getName());
                }
            }
        }

        return completions;
    }
}