package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.model.ParseVariable;
import ex.nervisking.ClanManager;
import ex.nervisking.models.Clan;
import ex.nervisking.models.Member;
import org.bukkit.OfflinePlayer;

import java.util.Objects;

@CommandArg(name = "ban", permission = true)
public record BanArgument(ClanManager clanManager) implements CommandArgument {

    @Override
    public void execute(Sender sender, Arguments args) {
        if (args.lacksMinArgs(1)) {
            sender.helpLang("ban.usage");
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

        if (sender.getUniqueId().equals(offlinePlayer.getUniqueId())) {
            sender.sendLang("ban.self-ban");
            return;
        }

        if (clan.isBanned(offlinePlayer.getUniqueId())) {
            sender.sendLang("ban.already-banned");
            return;
        }

        clan.banMember(offlinePlayer.getUniqueId());
        sender.sendLang("ban.success", ParseVariable.adD("%player%", offlinePlayer.getName()));
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        Clan clan = clanManager.getClan(sender.getUniqueId());
        if (clan == null) {
            return completions;
        }
        if (args.has(1)) {
            completions.add(clan.getMembers().stream().map(Member::getName).filter(Objects::nonNull).toList());
        }

        return completions;
    }
}