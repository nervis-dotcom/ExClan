package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.nervisking.manager.RequestInvite;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import ex.nervisking.models.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandArg(name = "join", description = "acepta una invitación a un clan.", permission = true)
public class JoinArgument implements CommandArgument {

    public final ClanManager clanManager;
    private final RequestInvite requestInvite;

    public JoinArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
        this.requestInvite = plugin.getRequestInvite();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        if (args.isEmpty()) {
            sender.sendMessage("%prefix% &cDebes poner el nombre del clan.");
            return;
        }

        String clanName = args.get(0);

        UUID uuid = sender.getUniqueId();
        if (clanManager.isInClan(uuid)) {
            sender.sendMessage("%prefix% &cYa estás en un clan.");
            return;
        }

        RequestInvite.Request request = requestInvite.getInvite(clanName, uuid);
        if (request == null) {
            sender.sendMessage("%prefix% &cNo tienes una invitación para este clan.");
            return;
        }

        if (request.isExpired()) {
            sender.sendMessage("%prefix% &cLa invitación ha expirado.");
            return;
        }

        Clan clan = clanManager.getClan(clanName);
        if (clan == null) {
            sender.sendMessage("%prefix% &cEl clan no existe.");
            return;
        }

        if (clan.isBanned(uuid)) {
            sender.sendMessage("%prefix% &cEstas Baneado del clan.");
            return;
        }

        clan.addMember(uuid, Rank.MEMBER);
        requestInvite.removeInvite(clanName, uuid);
        sender.sendMessage("%prefix% &aHaz ingresado al clan: " + clanName);
        Player player = Bukkit.getPlayer(request.sender());
        if (player != null && player.isOnline()) {
            utilsManagers.sendMessage(player, "%prefix% &aEl jugador " + sender.getName() + " ha aceptado la invitación al clan.");
        }
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (sender.isConsole()) {
            return completions;
        }

        if (args.has(1)) {
            completions.add(requestInvite.getInvite(sender.getUniqueId()));
        }
        return completions;
    }
}