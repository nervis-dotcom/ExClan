package ex.nervisking.commands.clanArguments;

import ex.api.base.command.Arguments;
import ex.api.base.command.CommandArg;
import ex.api.base.command.CommandArgument;
import ex.api.base.command.Sender;
import ex.api.base.model.CustomColor;
import ex.api.base.model.ParseVariable;
import ex.api.base.utils.DiscordWebhooks;
import ex.nervisking.manager.ClanManager;
import ex.nervisking.models.Clan;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@CommandArg(name = "delegate", description = "Delega a un jugador del clan.", permission = true)
public record DelegateArgument(ClanManager clanManager) implements CommandArgument {

    @Override
    public void execute(Sender sender, Arguments args) {
        UUID uuid = sender.getUniqueId();
        Clan clan = clanManager.getClan(uuid);
        if (clan == null) {
            sender.sendLang("no-clan");
            return;
        }
        if (!clan.isLader(uuid)) {
            sender.sendLang("not-leader");
            return;
        }

        if (args.isEmpty()) {
            sender.sendLang("delegate.no-player");
            return;
        }

        OfflinePlayer offlinePlayer = args.getOfflinePlayer(0);
        if (offlinePlayer == null) {
            sender.neverConnected(args.get(0));
            return;
        }


        if (!clan.hasMember(offlinePlayer.getUniqueId())) {
            sender.sendLang("not-member");
            return;
        }
        var member = clan.getMember(offlinePlayer.getUniqueId());

        clan.setDelegate(member, offlinePlayer.getName());

        sender.sendLang("delegate.success", ParseVariable.adD("%player%", offlinePlayer.getName()));

        clan.getOnlineAll().forEach(player -> utilsManagers.sendMessage(player, language.getString("clan", "delegate.notify-members")
                .replace("%player%", offlinePlayer.getName())));

        if (clan.getDiscord() != null) {
            DiscordWebhooks
                    .of(clan.getDiscord())
                    .setBotName(clan.getClanName())
                    .setTitle("announcement")
                    .setColor(CustomColor.YELLOW)
                    .setAvatarByPlayer(sender.getName())
                    .setDescription(language.getString("clan", "delegate.discord").replace("%player%", offlinePlayer.getName()))
                    .hideError()
                    .sendAsync();
        }
    }
}