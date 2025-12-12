package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.nervisking.ClanManager;
import ex.nervisking.models.Clan;
import ex.nervisking.models.Member;

import java.util.UUID;

@CommandArg(name = "pvp", permission = true)
public record PvpArgument(ClanManager clanManager) implements CommandArgument {

    @Override
    public void execute(Sender sender, Arguments args) {
        if (args.lacksMinArgs(2)) {
            sender.helpLang("pvp.usage");
            return;
        }

        UUID uuid = sender.getUniqueId();
        Clan clan = clanManager.getClan(uuid);
        if (clan == null) {
            sender.sendLang("no-clan");
            return;
        }

        boolean status = args.getBoolean(1);
        switch (args.get(0).toUpperCase()) {
            case "CLAN" -> {
                if (clan.isManager(uuid)) {
                    clan.setPvp(status);
                    sender.sendLang((status ? "pvp.clan.enabled" : "pvp.clan.disabled"));
                } else {
                    sender.sendLang("not-leader");
                }
            }
            case "ALLY" -> {
                if (clan.isManager(uuid)) {
                    clan.setPvpAlly(status);
                    sender.sendLang((status ? "pvp.ally.enabled" : "pvp.ally.disabled"));
                } else {
                    sender.sendLang("not-leader");
                }
            }
            case "ONLY" -> {
                Member member = clan.getMember(uuid);
                if (member != null) {
                    member.setPvp(status);
                    sender.sendLang((status ? "pvp.only.enabled" : "pvp.only.disabled"));
                } else {
                    sender.sendLang("pvp.not-member");
                }
            }
            default -> sender.helpLang("pvp.usage");
        }
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.add("CLAN", "ALLY", "ONLY");
        }
        if (args.has(2)) {
            completions.addBooleanValues();
        }

        return completions;
    }
}