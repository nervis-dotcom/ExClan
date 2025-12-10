package ex.nervisking.commands;

import ex.api.base.command.CommandArgumentBase;
import ex.api.base.command.CommandInfo;
import ex.nervisking.commands.clanArguments.*;
import ex.nervisking.ExClan;

@CommandInfo(name = "clan", description = "Gestión de clans", permission = true, noConsole = true)
public class ClanCommand extends CommandArgumentBase {

    public ClanCommand(ExClan plugin) {
        super(
                new CreateArgument(plugin), new DisBandeArgument(plugin), new TagArgument(plugin), new InviteArgument(plugin),
                new JoinArgument(plugin), new LeaveArgument(plugin), new KickArgument(plugin), new PromoteArgument(plugin),
                new DemoteArgument(plugin), new ChatArgument(plugin), new DelegateArgument(plugin), new WebhooksArgument(plugin),
                new AllysArgument(plugin), new PvpArgument(plugin), new BanArgument(plugin), new UnBanArgument(plugin),
                new SymbolsArgument(plugin), new ChestArgument(plugin), new BankArgument(plugin)
        );
    }
}