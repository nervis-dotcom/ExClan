package ex.nervisking.tasks.competition;

import ex.api.base.task.ExRunnable;
import ex.api.base.task.Task;
import ex.nervisking.ExClan;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class BankWarTask extends ExRunnable<ExClan> {

    private final BossBar bossBar;
    private Long timeLeft;
    private Task handle;

    public BankWarTask(long time) {
        this.timeLeft = time;
        this.bossBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SEGMENTED_20);
    }

    public void attach(Task task) {
        this.handle = task;
    }

    @Override
    public void run() {
        if (timeLeft <= 0) {
            this.bossBar.removeAll();
            handle.cancel();
            return;
        }
        this.timeLeft -= 1000L;

        this.bossBar.setTitle(utilsManagers.setColoredMessage("&fGuerra de dinero: %time% | %bank-lose% / %bank-win%"
                .replace("%time%", utilsManagers.formatTime(timeLeft, true))
                .replace("%bank-win%", "s")
                .replace("%bank-lose%", "a"))
        );
        online.forEach(bossBar::addPlayer);
    }

    public void stop() {
        if (handle != null) handle.cancel();
        bossBar.removeAll();
    }
}