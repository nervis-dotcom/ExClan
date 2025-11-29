package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.utils.LinkUtils;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "webhooks", description = "agrega un link para enviar mensaje a un canal de discord.", permission = true)
public class WebhooksArgument implements CommandArgument {

    public final ClanManager clanManager;

    public WebhooksArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        UUID uuid = sender.getUniqueId();
        if (!clanManager.isInClan(uuid)) {
            sender.sendMessage("%prefix% &cNo estás en un clan.");
            return;
        }

        Clan clanName = clanManager.getClan(uuid);
        if (!clanName.isManager(uuid)) {
            sender.sendMessage("%prefix% &cNo eres líder del clan.");
            return;
        }

        if (args.isEmpty()) {
            sender.sendMessage("%prefix% &aUsa /clan webhooks <add/remove/get>");
            return;
        }

        switch (args.toLowerCase(0)) {
            case "add" -> {
                if (args.lacksMinArgs(2)) {
                    sender.sendMessage("%prefix% &aUsa /clan webhooks add <link>");
                    return;
                }

                String link = args.get(1);
                if (!LinkUtils.isLinkFrom(link, "discord.com/api/webhooks")) {
                    sender.sendMessage("%prefix% &cEl link no es valido.");
                    return;
                }
                clanName.setDiscordWebhooks(link);
                sender.sendMessage("%prefix% &aLink agregado correctamente.");
            }
            case "remove" -> {
                clanName.setDiscordWebhooks(null);
                sender.sendMessage("%prefix% &aLink removido correctamente.");
            }

            case "get" -> {
                String link = clanName.getDiscordWebhooks();
                if (link == null) {
                    sender.sendMessage("%prefix% &cNo hay link agregado.");
                } else {
                    sender.sendMessage("%prefix% &aLink: " + link);
                }
            }

            default -> sender.sendMessage("%prefix% &aUsa /clan webhooks <add/remove/get>");
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