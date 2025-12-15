package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.model.ParseVariable;
import ex.nervisking.ClanManager;
import ex.nervisking.models.Clan;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@CommandArg(name = "unban", permission = true)
public record UnBanArgument(ClanManager clanManager) implements CommandArgument {

    @Override
    public void execute(Sender sender, Arguments args) {
        if (args.lacksMinArgs(1)) {
            sender.helpLang("unban.usage");
            return;
        }

        OfflinePlayer offlinePlayer = args.getOfflinePlayer(0);
        if (offlinePlayer == null) {
            sender.neverConnected(args.get(0));
            return;
        }

        Clan clan = clanManager.getClan(sender.getUniqueId());
        if (clan == null) {
            sender.sendLang("no-clan");
            return;
        }

        if (!clan.isManager(sender.getUniqueId())) {
            sender.sendLang("not-leader");
            return;
        }

        if (!clan.isBanned(offlinePlayer.getUniqueId())) {
            sender.sendLang("unban.not-banned");
            return;
        }

        clan.unbanMember(offlinePlayer.getUniqueId());
        sender.sendLang("unban.success", ParseVariable.adD("%player%", offlinePlayer.getName()).add("%clan%", clan.getClanName()));
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        Clan clan = clanManager.getClan(sender.getUniqueId());
        if (clan == null) {
            return completions;
        }
        if (args.has(1)) {
            for (var s : clan.getBannedMembers()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
                if (offlinePlayer.hasPlayedBefore()) {
                    completions.add(offlinePlayer.getName());
                }
            }
        }

        return completions;
    }
}