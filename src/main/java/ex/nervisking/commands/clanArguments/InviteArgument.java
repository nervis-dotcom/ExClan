package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.nervisking.manager.RequestInvite;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandArg(name = "invite", description = "Invita a un jugador a tu clan.", permission = true)
public class InviteArgument implements CommandArgument {

    public final ClanManager clanManager;
    private final RequestInvite requestInvite;

    public InviteArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
        this.requestInvite = plugin.getRequestInvite();
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

        if (args.isEmpty()) {
            sender.sendMessage("%prefix% &cDebes indicar un jugador para invitar.");
            return;
        }

        Player playerName = args.getOnlinePlayer(0);
        if (playerName == null) {
            sender.noOnline(args.get(0));
            return;
        }

        if (clanManager.isInClan(playerName.getUniqueId())) {
            sender.sendMessage("%prefix% &cEl jugador ya está en un clan.");
            return;
        }

        if (clan.isBanned(playerName.getUniqueId())) {
            sender.sendMessage("%prefix% &cEl jugador está baneado del clan.");
            return;
        }

        if (requestInvite.hasInvite(clan.getClanName(), playerName.getUniqueId())) {
            sender.sendMessage("%prefix% &cYa haz enviado una invitación al jugador para unir al clan.");
            return;
        }

        requestInvite.addInvite(clan.getClanName(), uuid, playerName.getUniqueId());
        utilsManagers.sendMessage(playerName, "%prefix% &aTienes una nueva para unite al clan: " + clan.getClanName() + ".");
        sender.sendMessage("%prefix% &aInvitación enviada a " + playerName.getName() + ".");
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.addPlayerOnline( player -> !sender.is(player));
        }
        return completions;
    }
}