package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.model.ParseVariable;
import ex.nervisking.manager.ClanManager;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "tag", description = "Cambia el tag de tu clan.", permission = true)
public record TagArgument(ClanManager clanManager) implements CommandArgument {

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

        if (args.isEmpty()) {
            sender.sendLang("tag.usage");
            return;
        }

        String tag = args.get(0);
        if (utilsManagers.isValidText(utilsManagers.removeColorCodes(tag), "^[a-zA-Z0-9_\\- &#]+$", 3, 100)) {
            sender.sendLang("tag.invalid-tag");
            return;
        }

        sender.sendLang("tag.changed", ParseVariable.adD("%tag%", tag));
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