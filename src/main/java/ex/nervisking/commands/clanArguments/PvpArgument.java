package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import ex.nervisking.models.Member;

import java.util.UUID;

@CommandArg(name = "pvp", permission = true)
public class PvpArgument implements CommandArgument {

    private final ClanManager clanManager;

    public PvpArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        if (args.lacksMinArgs(2)) {
            sender.help("Usa /clan pvp <clan | ally | only> <true/false>");
            return;
        }

        UUID uuid = sender.getUniqueId();
        Clan clan = clanManager.getClan(uuid);
        if (clan == null) {
            sender.sendMessage("%prefix% &cNo estás en un clan.");
            return;
        }

        boolean status = args.getBoolean(1);
        switch (args.get(0).toUpperCase()) {
            case "CLAN" -> {
                if (clan.isManager(uuid)) {
                    clan.setPvp(status);
                    sender.sendMessage("%prefix% &aPvp clan " + (status ? "activado" : "desactivado") + ".");
                } else {
                    sender.sendMessage("%prefix% &cNo eres líder del clan.");
                }
            }
            case "ALLY" -> {
                if (clan.isManager(uuid)) {
                    clan.setPvpAlly(status);
                    sender.sendMessage("%prefix% &aPvp ally " + (status ? "activado" : "desactivado") + ".");
                } else {
                    sender.sendMessage("%prefix% &cNo eres líder del clan.");
                }
            }
            case "ONLY" -> {
                Member member = clan.getMember(uuid);
                if (member != null) {
                    member.setPvp(status);
                    sender.sendMessage("%prefix% &aPvp " + (status ? "activado" : "desactivado"));
                } else {
                    sender.sendMessage("%prefix% &cNo eres miembro del clan.");
                }
            }
            default -> sender.help("Usa /clan pvp <clan | ally | only> <true/false>");
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