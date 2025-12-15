package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.nervisking.ClanManager;
import ex.nervisking.models.Clan;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandArg(name = "kick", description = "Expulsa a un jugador del clan.", permission = true)
public record KickArgument(ClanManager clanManager) implements CommandArgument {

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
            sender.helpLang("kick.no-player");
            return;
        }

        OfflinePlayer playerName = args.getOfflinePlayer(0);
        if (playerName == null) {
            sender.neverConnected(args.get(0));
            return;
        }

        if (sender.getUniqueId().equals(playerName.getUniqueId())) {
            sender.sendLang("kick.self-ban");
            return;
        }

        if (clan.isLader(playerName.getUniqueId())) {
            sender.sendLang("kick.cannot-leader");
            return;
        }

        if (!clan.hasMember(playerName.getUniqueId())) {
            sender.sendLang("not-member");
            return;
        }

        if (playerName.isOnline()) {
            utilsManagers.sendMessage(playerName.getPlayer(), language.getString("clan", "kick.kicked"));
        }

        clan.removeMember(playerName.getUniqueId());
        clan.getOnlineAll().forEach(member -> utilsManagers.sendMessage(member, language.getString("clan", "kick.notify-members")
                .replace("%player%", playerName.getName())
                .replace("%sender%", sender.getName())));
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            Clan clan = clanManager.getClan(sender.getUniqueId());
            if (clan != null) {
                completions.add(clan.getOnlineAll().stream().map(Player::getName).filter(s -> !s.equals(sender.getName())).toList());
            }
        }
        return completions;
    }
}