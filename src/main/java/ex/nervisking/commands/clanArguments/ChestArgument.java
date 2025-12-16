package ex.nervisking.commands.clanArguments;

import ex.api.base.command.Arguments;
import ex.api.base.command.CommandArg;
import ex.api.base.command.CommandArgument;
import ex.api.base.command.Sender;
import ex.api.base.model.ParseVariable;
import ex.nervisking.manager.ClanManager;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "chest", permission = true)
public record ChestArgument(ClanManager clanManager) implements CommandArgument {

    @Override
    public void execute(Sender sender, Arguments args) {
        UUID uuid = sender.getUniqueId();
        Clan clanName = clanManager.getClan(uuid);
        if (clanName == null) {
            sender.sendLang("no-clan");
            return;
        }
        if (!clanName.isManager(uuid)) {
            sender.sendLang("not-leader");
            return;
        }

        try {
            clanName.getChest().openSharedChest(sender.getPlayer());
        } catch (Exception e) {
            sender.sendLang("error", ParseVariable.adD("%error%", e.getMessage()));
        }
    }
}