package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandArg(name = "kick", description = "Expulsa a un jugador del clan.", permission = true)
public class KickArgument implements CommandArgument {

    public final ClanManager clanManager;

    public KickArgument(ExClan plugin) {
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
        if (!clan.isManager(uuid)) {
            sender.sendMessage("%prefix% &cNo eres líder del clan.");
            return;
        }

        if (args.isEmpty()) {
            sender.sendMessage("%prefix% &cDebes indicar un jugador para invitar.");
            return;
        }

        OfflinePlayer playerName = args.getOfflinePlayer(0);
        if (playerName == null) {
            sender.neverConnected(args.get(0));
            return;
        }

        if (!clan.hasMember(playerName.getUniqueId())) {
            sender.sendMessage("%prefix% &cEl jugador no está en tu clan.");
            return;
        }

        if (playerName.isOnline()) {
            utilsManagers.sendMessage(playerName.getPlayer(), "%prefix% &cHas sido expulsado del clan.");
        }

        clan.removeMember(playerName.getUniqueId());

        clan.getOnlineAll().forEach(member -> {
            utilsManagers.sendMessage(member, "%prefix% &aEl jugador: " + playerName.getName() + " ha sido expulsado del clan por " + sender.getName() + ".");
        });
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            Clan clan = clanManager.getClan(sender.getUniqueId());
            if (clan != null) {
                completions.add(clan.getOnlineAll().stream().map(Player::getName).toList());
            }
        }
        return completions;
    }
}