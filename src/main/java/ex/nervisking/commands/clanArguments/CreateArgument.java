package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.model.ParseVariable;
import ex.nervisking.ClanManager;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "create", description = "Crea un clan nuevo.", permission = true)
public record CreateArgument(ClanManager clanManager) implements CommandArgument {

    @Override
    public void execute(Sender sender, Arguments args) {
        if (args.isEmpty()) {
            sender.helpLang("no-name-clan");
            return;
        }

        UUID uuid = sender.getUniqueId();
        if (clanManager.isInClan(uuid)) {
            sender.sendLang("already-in-clan");
            return;
        }

        String clanName = args.get(0);
        if (clanManager.hasClan(clanName)) {
            sender.sendLang("create.exist");
            return;
        }

        if (utilsManagers.isValidText(clanName) || utilsManagers.hasColorCodes(clanName)) {
            sender.sendLang("invalid-name");
            return;
        }

        sender.sendLang("create.success", ParseVariable.adD("%clan%", clanName));
        utilsManagers.sendBroadcastMessage(language.getString("clan", "create.broadcast").replace("%clan%", clanName));
        clanManager.addClan(clanName, new Clan(clanName, sender.getName(), uuid));
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.add("[name]");
        }
        return completions;
    }
}