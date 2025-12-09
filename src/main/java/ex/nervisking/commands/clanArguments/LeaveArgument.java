package ex.nervisking.commands.clanArguments;

import ex.api.base.command.Arguments;
import ex.api.base.command.CommandArg;
import ex.api.base.command.CommandArgument;
import ex.api.base.command.Sender;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "leave", description = "Salir del clan.", permission = true)
public class LeaveArgument implements CommandArgument {

    public final ClanManager clanManager;

    public LeaveArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public void execute(Sender sender, Arguments arguments) {
        UUID uuid = sender.getUniqueId();
        Clan clan = clanManager.getClan(uuid);
        if (clan == null) {
            sender.sendMessage("%prefix% &cNo estás en un clan.");
            return;
        }
        if (clan.isLader(uuid)) {
            sender.sendMessage("%prefix% &cNo puedes salir del clan siendo líder, Elimina el clan o delega a un nuevo líder.");
            return;
        }

        if (clan.hasMember(uuid)) {
            clan.removeMember(uuid);
            sender.sendMessage("%prefix% &aHas salido del clan: " + clan.getClanName());

            clan.getOnlineAll().forEach(member -> utilsManagers.sendMessage(member, "%prefix% &aEl jugador: " + sender.getName() + " ha salido del clan."));
        }
    }
}