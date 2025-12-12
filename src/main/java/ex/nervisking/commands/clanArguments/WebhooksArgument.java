package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.model.ParseVariable;
import ex.api.base.utils.LinkUtils;
import ex.nervisking.ClanManager;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "webhooks", description = "agrega un link para enviar mensaje a un canal de discord.", permission = true)
public record WebhooksArgument(ClanManager clanManager) implements CommandArgument {

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

        if (args.isEmpty()) {
            sender.helpLang("webhooks.usage");
            return;
        }

        switch (args.toLowerCase(0)) {
            case "add" -> {
                if (args.lacksMinArgs(2)) {
                    sender.sendLang("webhooks.add-usage");
                    return;
                }

                String link = args.get(1);
                if (!LinkUtils.isLinkFrom(link, "discord.com/api/webhooks")) {
                    sender.sendLang("webhooks.invalid-link");
                    return;
                }
                clanName.setDiscord(link);
                sender.sendLang("webhooks.added");
            }
            case "remove" -> {
                clanName.setDiscord(null);
                sender.sendLang("webhooks.removed");
            }

            case "get" -> {
                String link = clanName.getDiscord();
                if (link == null) {
                    sender.sendLang("webhooks.no-link");
                } else {
                    sender.sendLang("webhooks.get", ParseVariable.adD("%link%", link));
                }
            }

            default -> sender.helpLang("webhooks.usage");
        }
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.add("add", "remove", "get");
        }
        return completions;
    }
}