package ex.nervisking.commands.clanArguments;

import ex.api.base.command.Arguments;
import ex.api.base.command.CommandArg;
import ex.api.base.command.CommandArgument;
import ex.api.base.command.Sender;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.gui.GuiIcon;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "icon", permission = true)
public class IconArgument implements CommandArgument {

    public final ClanManager clanManager;

    public IconArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public void execute(Sender sender, Arguments arguments) {
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

        openMenu(new GuiIcon(sender.asPlayer(), clanName));
    }
}