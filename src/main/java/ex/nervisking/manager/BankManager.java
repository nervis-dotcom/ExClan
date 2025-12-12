package ex.nervisking.manager;

import ex.api.base.command.Sender;
import ex.api.base.hook.VaultHook;
import ex.api.base.model.ParseVariable;
import ex.api.base.service.Service;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import org.jetbrains.annotations.NotNull;

public class BankManager extends Service<ExClan> {

    private final ClanBankLocks bankLocks = new ClanBankLocks();

    public void depositToClan(Sender sender, @NotNull Clan clan, double amount) {
        if (!bankLocks.tryLock(clan.getClanName())) {
            sender.sendLang("bank.lock.deposit");
            return;
        }

        try {
            // Validar que el jugador tiene dinero
            if (VaultHook.getBalance(sender.getPlayer()) < amount) {
                sender.sendLang("bank.deposit.not-enough-money");
                return;
            }

            // Retirar al jugador
            VaultHook.removeMoney(sender.getPlayer(), amount);

            // Depositar atómico
            double newBalance = clan.depositDouble(amount);

            sender.sendLang("bank.deposit.success", ParseVariable.neW().add("%amount%", amount).add("%new_balance%", newBalance));
        } finally {
            bankLocks.unlock(clan.getClanName());
        }
    }

    public void withdrawFromClan(Sender sender, @NotNull Clan clan, double amount) {
        String name = clan.getClanName();

        // Evitar operaciones simultáneas
        if (!bankLocks.tryLock(name)) {
            sender.sendLang("bank.lock.withdraw");
            return;
        }

        try {
            // Validación
            if (amount <= 0) {
                sender.sendLang("bank.withdraw.invalid-amount");
                return;
            }

            // Validar que el clan tenga saldo
            if (clan.getBank() < amount) {
                sender.sendLang("bank.withdraw.not-enough-bank");
                return;
            }

            // Retiro atómico
            double newBalance = clan.withdrawDouble(amount);

            // Dar al jugador
            VaultHook.addMoney(sender.getPlayer(), amount);

            sender.sendLang("bank.withdraw.success", ParseVariable.neW().add("%amount%", amount).add("%new_balance%", newBalance));
        } finally {
            bankLocks.unlock(name);
        }
    }
}