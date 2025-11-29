package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

@CommandArg(name = "create", description = "Crea un clan nuevo.", permission = true)
public class CreateArgument implements CommandArgument {

    public final ClanManager clanManager;

    public CreateArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        if (args.isEmpty()) {
            sender.sendMessage("%prefix% &cDebes poner un nombre para el clan.");
            return;
        }

        UUID uuid = sender.getUniqueId();
        if (clanManager.isInClan(uuid)) {
            sender.sendMessage("%prefix% &cYa estás en un clan.");
            return;
        }

        String clanName = args.get(0);
        if (clanManager.hasClan(clanName)) {
            sender.sendMessage("%prefix% &cEl clan ya existe.");
            return;
        }

        if (utilsManagers.isValidText(clanName) || utilsManagers.hasColorCodes(clanName)) {
            sender.sendMessage("%prefix% &cEl nombre no es valido.");
            return;
        }

        sender.sendMessage("%prefix% &a¡Clan '" + clanName + "' creado con éxito!");
        clanManager.addClan(clanName, new Clan(clanName, clanName, sender.getName(), uuid, new ArrayList<>(), new HashSet<>(), 0, "", null));
        utilsManagers.sendBroadcastMessage("&bSe a creado un nuevo clan '" + clanName + "'");
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.add("[name]");
        }
        return completions;
    }
}