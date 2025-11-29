package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import ex.nervisking.models.Rank;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandArg(name = "promote", description = "Promueve a un jugador del clan.", permission = true)
public class PromoteArgument implements CommandArgument {

    public final ClanManager clanManager;

    public PromoteArgument(ExClan plugin) {
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
        if (!clan.isManager(uuid)) {
            sender.sendMessage("%prefix% &cNo eres líder del clan.");
            return;
        }

        if (args.lacksMinArgs(2)) {
            sender.sendMessage("%prefix% &cDebes indicar un jugador y un rango.");
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

        Rank rank = Rank.fromString(args.get(1));
        if (rank == null) {
            sender.sendMessage("%prefix% &cEl rango no es valido.");
            return;
        }

        Rank rackMember = clan.getMemberRank(playerName.getUniqueId());
        if (rackMember == rank) {
            sender.sendMessage("%prefix% &cEl jugador ya es de ese rango.");
            return;
        }

        if (rank.getLevel() < rackMember.getLevel()) {
            sender.sendMessage("%prefix% &cEl rango que intentas colocar al jugador es de un nivel mas bajo de que tiene usa el argumento 'demote' si es lo que quieres hacer.");
            return;
        }

        clan.setMemberRank(playerName.getUniqueId(), rank);
        sender.sendMessage("%prefix% &aJugador " + playerName.getName() + " promovido a: " + rank.getDisplayName() + ".");
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