package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import ex.nervisking.models.Member;
import org.bukkit.OfflinePlayer;

import java.util.Objects;

@CommandArg(name = "ban", permission = true)
public class BanArgument implements CommandArgument {

    private final ClanManager clanManager;

    public BanArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        if (args.lacksMinArgs(1)) {
            sender.help("Usa /clan ban <player>");
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

        if (sender.getUniqueId().equals(offlinePlayer.getUniqueId())) {
            sender.sendMessage("%prefix% &cNo puedes banearte a ti mismo.");
            return;
        }

        if (clan.isBanned(offlinePlayer.getUniqueId())) {
            sender.sendMessage("%prefix% &cEste jugador ya está baneado.");
            return;
        }

        clan.banMember(offlinePlayer.getUniqueId());
        sender.sendMessage("%prefix% &aHaz baneado al jugador: " + offlinePlayer.getName());
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        Clan clan = clanManager.getClan(sender.getUniqueId());
        if (clan == null) {
            return completions;
        }
        if (args.has(1)) {
            completions.add(clan.getMembers().stream().map(Member::getName).filter(Objects::nonNull).toList());
        }

        return completions;
    }
}