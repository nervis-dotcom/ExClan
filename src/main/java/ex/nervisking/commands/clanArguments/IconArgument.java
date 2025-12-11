package ex.nervisking.commands.clanArguments;

import ex.api.base.command.Arguments;
import ex.api.base.command.CommandArg;
import ex.api.base.command.CommandArgument;
import ex.api.base.command.Sender;
import ex.nervisking.ClanManager;
import ex.nervisking.gui.GuiIcon;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "icon", permission = true)
public record IconArgument(ClanManager clanManager) implements CommandArgument {

    @Override
    public void execute(Sender sender, Arguments arguments) {
        UUID uuid = sender.getUniqueId();
        Clan clanName = clanManager.getClan(uuid);
        if (clanName == null) {
            sender.sendLang("no_clan");
            return;
        }
        if (!clanName.isLader(uuid)) {
            sender.sendLang("not_leader");
            return;
        }

        openMenu(new GuiIcon(sender.getPlayer(), clanName));
    }
}