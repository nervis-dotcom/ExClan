package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.command.CommandArgument;
import ex.api.base.model.ParseVariable;
import ex.nervisking.ClanManager;
import ex.nervisking.models.Clan;
import ex.nervisking.models.Rank;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@CommandArg(name = "demote", description = "Demote a un jugador del clan.", permission = true)
public record DemoteArgument(ClanManager clanManager) implements CommandArgument {

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

        if (args.lacksMinArgs(2)) {
            sender.sendLang("demote.usage");
            return;
        }

        OfflinePlayer playerName = args.getOfflinePlayer(0);
        if (playerName == null) {
            sender.neverConnected(args.get(0));
            return;
        }

        if (sender.getUniqueId().equals(playerName.getUniqueId())) {
            sender.sendLang("demote.self");
            return;
        }

        if (clan.isLader(playerName.getUniqueId())) {
            sender.sendLang("demote.cannot-demote-leader");
            return;
        }

        if (!clan.hasMember(playerName.getUniqueId())) {
            sender.sendLang("not-member");
            return;
        }

        Rank rank = Rank.fromString(args.get(1));
        if (rank == null) {
            sender.sendLang("demote.invalid-rank");
            return;
        }

        Rank rackMember = clan.getMemberRank(playerName.getUniqueId());
        if (rackMember == rank) {
            sender.sendLang("demote.same-rank");
            return;
        }

        if (rank.getLevel() > rackMember.getLevel()) {
            sender.sendLang("demote.higher-rank");
            return;
        }
        clan.setMemberRank(playerName.getUniqueId(), rank);
        sender.sendLang("demote.success", ParseVariable.adD("%player%", playerName.getName()).add("%rank%", rank.getDisplayName()));
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        Clan clan = clanManager.getClan(sender.getUniqueId());
        if (clan == null) {
            return completions;
        }
        if (args.has(1)) {
            completions.add(clan.getOnlineAll().stream().map(OfflinePlayer::getName).toList());
        }
        if (args.has(2)) {
            completions.add(Rank.getRank());
        }

        return completions;
    }
}