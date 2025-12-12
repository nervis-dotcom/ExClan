package ex.nervisking.commands.clanArguments;

import ex.api.base.command.Arguments;
import ex.api.base.command.CommandArg;
import ex.api.base.command.CommandArgument;
import ex.api.base.command.Sender;
import ex.api.base.model.ParseVariable;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "leave", description = "Salir del clan.", permission = true)
public record LeaveArgument(ClanManager clanManager) implements CommandArgument {

    @Override
    public void execute(Sender sender, Arguments arguments) {
        UUID uuid = sender.getUniqueId();
        Clan clan = clanManager.getClan(uuid);
        if (clan == null) {
            sender.sendLang("no-clan");
            return;
        }
        if (clan.isLader(uuid)) {
            sender.sendLang("leave.is-leader");
            return;
        }

        if (clan.hasMember(uuid)) {
            clan.removeMember(uuid);
            sender.sendLang("leave.success", ParseVariable.adD("%clan%", clan.getClanName()));

            clan.getOnlineAll().forEach(member -> utilsManagers.sendMessage(member, language.getString("clan", "leave.notify-members")
                    .replace("%player%", sender.getName())));
        }
    }
}