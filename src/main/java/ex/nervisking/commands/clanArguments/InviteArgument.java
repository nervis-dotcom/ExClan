package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.model.ParseVariable;
import ex.nervisking.manager.RequestInvite;
import ex.nervisking.manager.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandArg(name = "invite", description = "Invita a un jugador a tu clan.", permission = true)
public class InviteArgument implements CommandArgument {

    public final ClanManager clanManager;
    private final RequestInvite requestInvite;

    public InviteArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
        this.requestInvite = plugin.getRequestInvite();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        UUID uuid = sender.getUniqueId();
        Clan clan = clanManager.getClan(uuid);
        if (clan == null) {
            sender.sendLang("no-clan");
            return;
        }
        if (!clan.isManager(uuid)) {
            sender.sendLang("not-leader");
            return;
        }

        if (args.isEmpty()) {
            sender.helpLang("invite.missing-player");
            return;
        }

        Player playerName = args.getOnlinePlayer(0);
        if (playerName == null) {
            sender.noOnline(args.get(0));
            return;
        }

        if (clanManager.isInClan(playerName.getUniqueId())) {
            sender.sendLang("invite.already-in-clan");
            return;
        }

        if (clan.isBanned(playerName.getUniqueId())) {
            sender.sendLang("invite.banned");
            return;
        }

        if (requestInvite.hasInvite(clan.getClanName(), playerName.getUniqueId())) {
            sender.sendLang("invite.already-invited");
            return;
        }

        requestInvite.addInvite(clan.getClanName(), uuid, playerName.getUniqueId());
        sender.sendLang("invite.sent-success", ParseVariable.adD("%player%", playerName.getName()));
        utilsManagers.sendMessage(playerName, language.getString("clan", "invite.sent-to-player").replace("%clan%", clan.getClanName()));
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.addPlayerOnline( player -> !sender.is(player));
        }
        return completions;
    }
}