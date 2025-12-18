package ex.nervisking.commands;

import ex.api.base.command.CommandArgumentBase;
import ex.api.base.command.CommandInfo;
import ex.nervisking.commands.clanArguments.*;
import ex.nervisking.ExClan;

@CommandInfo(name = "clan", description = "Gestión de clans", permission = true, noConsole = true)
public class ClanCommand extends CommandArgumentBase {

    public ClanCommand(ExClan plugin) {
        super(
                new CreateArgument(plugin.getClanManager()), new DisBandeArgument(plugin.getClanManager()), new TagArgument(plugin.getClanManager()), new InviteArgument(plugin),
                new JoinArgument(plugin), new LeaveArgument(plugin.getClanManager()), new KickArgument(plugin.getClanManager()), new PromoteArgument(plugin.getClanManager()),
                new DemoteArgument(plugin.getClanManager()), new ChatArgument(plugin.getClanManager()), new DelegateArgument(plugin.getClanManager()), new WebhooksArgument(plugin.getClanManager()),
                new AllysArgument(plugin), new PvpArgument(plugin.getClanManager()), new BanArgument(plugin.getClanManager()), new UnBanArgument(plugin.getClanManager()),
                new SymbolsArgument(plugin.getClanManager()), new ChestArgument(plugin), new BankArgument(plugin), new IconArgument(plugin.getClanManager()),
                new HomeArgument(plugin), new GuiArgument(plugin.getClanManager())
        );
    }
}