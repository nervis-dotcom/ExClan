package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "tag", description = "Cambia el tag de tu clan.", permission = true)
public class TagArgument implements CommandArgument {

    public final ClanManager clanManager;

    public TagArgument(ExClan plugin) {
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

        if (args.isEmpty()) {
            sender.sendMessage("%prefix% &cDebes poner un tag para tu clan.");
            return;
        }

        String tag = args.get(0);
        if (utilsManagers.isValidText(utilsManagers.removeColorCodes(tag), "^[a-zA-Z0-9_\\- &#]+$", 3, 100)) {
            sender.sendMessage("%prefix% &cEl tag no es valido.");
            return;
        }

        sender.sendMessage("%prefix% &aTag cambiado a: " + tag);
        clanName.setClanTag(tag);
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.add("[tag]");
        }
        return completions;
    }
}