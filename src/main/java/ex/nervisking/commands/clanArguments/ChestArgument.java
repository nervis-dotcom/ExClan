package ex.nervisking.commands.clanArguments;

import ex.api.base.command.Arguments;
import ex.api.base.command.CommandArg;
import ex.api.base.command.CommandArgument;
import ex.api.base.command.Sender;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "chest", permission = true)
public class ChestArgument implements CommandArgument {

    public final ClanManager clanManager;

    public ChestArgument(ExClan plugin) {
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
        if (!clanName.isManager(uuid)) {
            sender.sendMessage("%prefix% &cNo eres el líder del clan.");
            return;
        }

        try {
            clanName.getChest().openSharedChest(sender.asPlayer());
        } catch (Exception e) {
            sender.sendMessage("%prefix% &cError al abrir el chest.");
        }
    }
}