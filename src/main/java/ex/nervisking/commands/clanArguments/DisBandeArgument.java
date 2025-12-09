package ex.nervisking.commands.clanArguments;

import ex.api.base.command.Arguments;
import ex.api.base.command.CommandArg;
import ex.api.base.command.CommandArgument;
import ex.api.base.command.Sender;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "disband", description = "Elimina tu clan.", permission = true)
public class DisBandeArgument implements CommandArgument {

    public final ClanManager clanManager;

    public DisBandeArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        UUID uuid = sender.getUniqueId();
        Clan clanName = clanManager.getClan(uuid);
        if (clanName == null) {
            sender.sendMessage("%prefix% &cNo estás en un clan.");
            return;
        }
        if (!clanName.isLader(uuid)) {
            sender.sendMessage("%prefix% &cNo eres el líder del clan.");
            return;
        }

        clanManager.removeClan(clanName.getClanName());
        sender.sendMessage("%prefix% &cClan eliminado correctamente.");
        utilsManagers.sendBroadcastMessage("&bSe a eliminado el clan '" + clanName.getClanName() + "'");
    }
}