package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
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
            sender.help("Usa /clan allys <invite | joint> <name>");
            return;
        }

        String name = args.get(1);

        Clan clanTarget = clanManager.getClan(name);
        if (clanTarget == null) {
            sender.sendMessage("%prefix% &cNo existe el clan.");
            return;
        }

        Clan clan = clanManager.getClan(sender.getUniqueId());
        if (clan == null) {
            sender.sendMessage("%prefix% &cNo estás en un clan.");
            return;
        }

        if (!clan.isManager(sender.getUniqueId())) {
            sender.sendMessage("%prefix% &cNo eres líder del clan.");
            return;
        }

        if (clanTarget.getClanName().equalsIgnoreCase(clan.getClanName())) {
            sender.sendMessage("%prefix% &cNo puedes unirte a ti mismo.");
            return;
        }

        switch (args.get(0).toUpperCase()) {
            case "INVITE" -> {
                if (clan.isAlly(name)) {
                    sender.sendMessage("%prefix% &cYa son aliados.");
                    return;
                }
                if (clanTarget.isManagerOnline()) {
                    if (allysInvite.hasInvite(clan.getClanName(), clanTarget.getClanName())) {
                        sender.sendMessage("%prefix% &cYa tienes una invitación para este clan.");
                        return;
                    }
                    allysInvite.addInvite(clan.getClanName(), clanTarget.getClanName(), sender.getUniqueId());
                    sender.sendMessage("%prefix% &aInvitación enviada a " + clanTarget.getClanName() + ".");
                } else {
                    sender.sendMessage("%prefix% &cNo ay un líder conectado que acepte tu invitación.");
                }
            }
            case "JOINT" -> {
                if (clan.isAlly(name)) {
                    sender.sendMessage("%prefix% &cYa son aliados.");
                    return;
                }

                AllysInvite.Request request = allysInvite.getInvite(clan.getClanName(), clanTarget.getClanName());
                if (request == null) {
                    sender.sendMessage("%prefix% &cNo tienes una invitación para este clan.");
                    return;
                }

                if (request.isExpired()) {
                    sender.sendMessage("%prefix% &cLa invitación ha expirado.");
                    return;
                }

                clanTarget.addAlly(clan.getClanName());
                clan.addAlly(clanTarget.getClanName());
                sender.sendMessage("%prefix% &aHaz ingresado al clan: " + clanTarget.getClanName());
                allysInvite.removeInvite(clan.getClanName(), clanTarget.getClanName());
                Player player = Bukkit.getPlayer(request.sender());
                if (player != null && player.isOnline()) {
                    utilsManagers.sendMessage(player, "%prefix% &aEl jugador " + sender.getName() + " ha aceptado la invitación al clan.");
                }
            }
            case "UNALLY" -> {
                if (!clan.isAlly(clanTarget.getClanName())) {
                    sender.sendMessage("%prefix% &cNo tienes una alianza con este clan.");
                    return;
                }

                clan.removeAlly(clanTarget.getClanName());
                clanTarget.removeAlly(clan.getClanName());

                sender.sendMessage("%prefix% &aHas roto la alianza con el clan " + clanTarget.getClanName() + ".");
            }
            default -> sender.help("Usa /clan allys <invite | joint> <name>");
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