package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.hook.VaultHook;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.manager.BankManager;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "bank", permission = true)
public class BankArgument implements CommandArgument {

    private final BankManager clanBankLocks;
    private final ClanManager clanManager;

    public BankArgument(ExClan plugin) {
        this.clanBankLocks = plugin.getBankManager();
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        UUID uuid = sender.getUniqueId();
        Clan clanName = clanManager.getClan(uuid);
        if (clanName == null) {
            sender.sendMessage("%prefix% &cNo estás en un clan.");
            return;
        }
        if (args.isEmpty()) {
            sender.help("Usa /clan bank <deposit | withdraw | balance>");
            return;
        }

        if (!VaultHook.isEconomyEnabled()) {
            sender.sendMessage("%prefix% &cEconomia no habilitada.");
            return;
        }

        switch (args.get(0).toLowerCase()) {
            case "deposit" -> {
                if (args.lacksMinArgs(2)) {
                    sender.help("Usa /clan bank deposit <amount>");
                    return;
                }

                double amount;
                try {
                    amount = args.getDouble(1);
                } catch (NumberFormatException e) {
                    sender.invalidityAmount();
                    return;
                }
                clanBankLocks.depositToClan(sender.asPlayer(), clanName, amount);
            }
            case "withdraw" -> {
                if (!clanName.isManager(uuid)) {
                    sender.sendMessage("%prefix% &cNo eres el líder del clan.");
                    return;
                }

                if (args.lacksMinArgs(2)) {
                    sender.help("Usa /clan bank withdraw <amount>");
                    return;
                }
                double amount;
                try {
                    amount = args.getDouble(1);
                } catch (NumberFormatException e) {
                    sender.invalidityAmount();
                    return;
                }
                clanBankLocks.withdrawFromClan(sender.asPlayer(), clanName, amount);
            }
            case "balance" -> {
                if (!clanName.isManager(uuid)) {
                    sender.sendMessage("%prefix% &cNo eres el líder del clan.");
                    return;
                }
                sender.sendMessage("%prefix% &aEl banco del clan tiene $" + clanName.getBankDouble());
            }
        }
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.add("deposit", "withdraw", "balance");
        }

        if (args.has(2) && args.equalsIgnoreCase(0,"deposit", "withdraw")) {
            completions.addConsecutive(100);
        }
        return completions;
    }
}