package ex.nervisking.commands.clanArguments;

import ex.api.base.command.Arguments;
import ex.api.base.command.CommandArg;
import ex.api.base.command.CommandArgument;
import ex.api.base.command.Sender;
import ex.nervisking.manager.ClanManager;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "disband", description = "Elimina tu clan.", permission = true)
public record DisBandeArgument(ClanManager clanManager) implements CommandArgument {

    @Override
    public void execute(Sender sender, Arguments args) {
        UUID uuid = sender.getUniqueId();
        Clan clanName = clanManager.getClan(uuid);
        if (clanName == null) {
            sender.sendLang("no-clan");
            return;
        }
        if (!clanName.isLader(uuid)) {
            sender.sendLang("not-leader");
            return;
        }

        clanManager.removeClan(clanName.getClanName());

        sender.sendLang("disband.success");
        utilsManagers.sendBroadcastMessage(language.getString("clan", "disband.broadcast").replace("%clan%", clanName.getClanName()));
    }
}