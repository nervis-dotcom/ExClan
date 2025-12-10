package ex.nervisking.manager;

import ex.api.base.hook.VaultHook;
import ex.api.base.service.Service;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BankManager extends Service<ExClan> {

    private final ClanBankLocks bankLocks = new ClanBankLocks();

    public void depositToClan(Player player, @NotNull Clan clan, double amount) {
        if (!bankLocks.tryLock(clan.getClanName())) {
            sendMessage(player, "%prefix% &cHay otra operación en curso.");
            return;
        }

        try {
            // Validar que el jugador tiene dinero
            if (VaultHook.getBalance(player) < amount) {
                sendMessage(player, "%prefix% &cNo tienes suficiente dinero.");
                return;
            }

            // Retirar al jugador
            VaultHook.removeMoney(player, amount);

            // Depositar atómico
            double newBalance = clan.depositDouble(amount);

            sendMessage(player, "%prefix% &aDepositaste " + amount + " al banco del clan. Nuevo balance: " + newBalance);
        } finally {
            bankLocks.unlock(clan.getClanName());
        }
    }

    public void withdrawFromClan(Player player, @NotNull Clan clan, double amount) {
        String name = clan.getClanName();

        // Evitar operaciones simultáneas
        if (!bankLocks.tryLock(name)) {
            sendMessage(player, "%prefix% &cEl banco del clan está ocupado con otra operación.");
            return;
        }

        try {
            // Validación
            if (amount <= 0) {
                sendMessage(player, "%prefix% &cEl monto debe ser mayor a 0.");
                return;
            }

            // Validar que el clan tenga saldo
            if (clan.getBank() < amount) {
                sendMessage(player, "%prefix% &cEl banco del clan no tiene suficiente dinero.");
                return;
            }

            // Retiro atómico
            double newBalance = clan.withdrawDouble(amount);

            // Dar al jugador
            VaultHook.addMoney(player, amount);

            sendMessage(player, "%prefix% &aRetiraste " + amount + ". Nueva cantidad del clan: " + newBalance);
        } finally {
            bankLocks.unlock(name);
        }
    }
}