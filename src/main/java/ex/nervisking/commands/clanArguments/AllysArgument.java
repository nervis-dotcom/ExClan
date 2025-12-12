package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.model.ParseVariable;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.manager.AllysInvite;
import ex.nervisking.models.Clan;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandArg(name = "ally", permission = true)
public class AllysArgument implements CommandArgument {

    private final AllysInvite allysInvite;
    private final ClanManager clanManager;

    public AllysArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
        this.allysInvite = new AllysInvite();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        if (args.lacksMinArgs(2)) {
            sender.helpLang("ally.usage");
            return;
        }

        String name = args.get(1);

        Clan clanTarget = clanManager.getClan(name);
        if (clanTarget == null) {
            sender.sendLang("not-exist");
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

        if (clanTarget.getClanName().equalsIgnoreCase(clan.getClanName())) {
            sender.sendLang("self");
            return;
        }

        switch (args.get(0).toUpperCase()) {
            case "INVITE" -> {
                if (clan.isAlly(name)) {
                    sender.sendLang("ally.already-allies");
                    return;
                }
                if (clanTarget.isManagerOnline()) {
                    if (allysInvite.hasInvite(clan.getClanName(), clanTarget.getClanName())) {
                        sender.sendLang("ally.invite.already-sent");
                        return;
                    }
                    allysInvite.addInvite(clan.getClanName(), clanTarget.getClanName(), sender.getUniqueId());
                    sender.sendLang("ally.invite.sent", ParseVariable.adD("%clan%", clanTarget.getClanName()));
                } else {
                    sender.sendLang("ally.invite.no-manager-online");
                }
            }
            case "JOINT" -> {
                if (clan.isAlly(name)) {
                    sender.sendLang("ally.already-allies");
                    return;
                }

                AllysInvite.Request request = allysInvite.getInvite(clan.getClanName(), clanTarget.getClanName());
                if (request == null) {
                    sender.sendLang("ally.joint.no-invite");
                    return;
                }

                if (request.isExpired()) {
                    sender.sendLang("ally.joint.expired");
                    return;
                }

                clanTarget.addAlly(clan.getClanName());
                clan.addAlly(clanTarget.getClanName());
                sender.sendLang("ally.joint.success-sender", ParseVariable.adD("%clan%", clanTarget.getClanName()));
                allysInvite.removeInvite(clan.getClanName(), clanTarget.getClanName());
                Player player = Bukkit.getPlayer(request.sender());
                if (player != null && player.isOnline()) {
                    utilsManagers.sendMessage(player, language.getString("clan", "ally.joint.notify-owner").replace("%player%", sender.getName()));
                }
            }
            case "UNALLY" -> {
                if (!clan.isAlly(clanTarget.getClanName())) {
                    sender.sendLang("ally.unally.not-ally");
                    return;
                }

                clan.removeAlly(clanTarget.getClanName());
                clanTarget.removeAlly(clan.getClanName());

                sender.sendLang("ally.unally.removed", ParseVariable.adD("%clan%", clanTarget.getClanName()));
            }
            default -> sender.helpLang("ally.usage");
        }
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.add("INVITE", "JOINT", "UNALLY");
        }
        if (args.has(2) && args.equalsIgnoreCase(0,"INVITE", "UNALLY")) {
            completions.add(clanManager.getClanData().keySet());
        }

        if (args.has(2) && args.get(0).equalsIgnoreCase("JOINT")) {
            Clan clan = clanManager.getClan(sender.getUniqueId());
            if (clan == null) {
                return completions;
            }
            completions.add(allysInvite.getInvite(clan.getClanName()));
        }

        return completions;
    }
}