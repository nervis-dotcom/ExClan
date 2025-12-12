package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.model.ParseVariable;
import ex.nervisking.manager.RequestInvite;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import ex.nervisking.models.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandArg(name = "join", description = "acepta una invitación a un clan.", permission = true)
public class JoinArgument implements CommandArgument {

    public final ClanManager clanManager;
    private final RequestInvite requestInvite;

    public JoinArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
        this.requestInvite = plugin.getRequestInvite();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        if (args.isEmpty()) {
            sender.helpLang("no-name-clan");
            return;
        }

        String clanName = args.get(0);

        UUID uuid = sender.getUniqueId();
        if (clanManager.isInClan(uuid)) {
            sender.sendLang("already-in-clan");
            return;
        }

        RequestInvite.Request request = requestInvite.getInvite(clanName, uuid);
        if (request == null) {
            sender.sendLang("join.no-invite");
            return;
        }

        if (request.isExpired()) {
            sender.sendLang("join.expired");
            return;
        }

        Clan clan = clanManager.getClan(clanName);
        if (clan == null) {
            sender.sendLang("not-exist");
            return;
        }

        if (clan.isBanned(uuid)) {
            sender.sendLang("join.banned");
            return;
        }

        clan.addMember(uuid, Rank.MEMBER);
        requestInvite.removeInvite(clanName, uuid);
        sender.sendLang("join.success", ParseVariable.adD("%clan%", clanName));
        Player player = Bukkit.getPlayer(request.sender());
        if (player != null && player.isOnline()) {
            utilsManagers.sendMessage(player, language.getString("clan", "join.notify-leader").replace("%player%", sender.getName()));
        }
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (sender.isConsole()) {
            return completions;
        }

        if (args.has(1)) {
            completions.add(requestInvite.getInvite(sender.getUniqueId()));
        }
        return completions;
    }
}