package ex.nervisking.commands.clanArguments;

import ex.api.base.command.Arguments;
import ex.api.base.command.CommandArg;
import ex.api.base.command.CommandArgument;
import ex.api.base.command.Sender;
import ex.nervisking.gui.MainClan;
import ex.nervisking.manager.ClanManager;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "gui", permission = true)
public record GuiArgument(ClanManager clanManager) implements CommandArgument {

    @Override
    public void execute(Sender sender, Arguments args) {
        UUID uuid = sender.getUniqueId();
        Clan clanName = clanManager.getClan(uuid);
        if (clanName == null) {
            sender.sendLang("no-clan");
            return;
        }

        openMenu(new MainClan(sender.getPlayer(), clanName));
    }
}