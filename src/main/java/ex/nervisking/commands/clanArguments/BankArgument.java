package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.hook.VaultHook;
import ex.api.base.model.ParseVariable;
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
            sender.sendLang("no-clan");
            return;
        }
        if (args.isEmpty()) {
            sender.helpLang("bank.usage");
            return;
        }

        if (!VaultHook.isEconomyEnabled()) {
            sender.sendLang("bank.economy-disabled");
            return;
        }

        switch (args.get(0).toLowerCase()) {
            case "deposit" -> {
                if (args.lacksMinArgs(2)) {
                    sender.helpLang("bank.deposit.usage");
                    return;
                }

                double amount;
                try {
                    amount = args.getDouble(1);
                } catch (NumberFormatException e) {
                    sender.invalidityAmount();
                    return;
                }
                clanBankLocks.depositToClan(sender, clanName, amount);
            }
            case "withdraw" -> {
                if (!clanName.isManager(uuid)) {
                    sender.sendLang("not-leader");
                    return;
                }

                if (args.lacksMinArgs(2)) {
                    sender.helpLang("bank.withdraw.usage");
                    return;
                }
                double amount;
                try {
                    amount = args.getDouble(1);
                } catch (NumberFormatException e) {
                    sender.invalidityAmount();
                    return;
                }
                clanBankLocks.withdrawFromClan(sender, clanName, amount);
            }
            case "balance" -> {
                if (!clanName.isManager(uuid)) {
                    sender.sendLang("not-leader");
                    return;
                }
                sender.sendLang("bank.balance.show", ParseVariable.neW().add("%amount%", clanName.getBankDouble()));
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