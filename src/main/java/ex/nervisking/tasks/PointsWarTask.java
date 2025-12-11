package ex.nervisking.tasks;

import ex.api.base.task.ExRunnable;
import ex.api.base.task.Task;
import ex.nervisking.ExClan;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class PointsWarTask extends ExRunnable<ExClan> {

    private final BossBar bossBar;
    private Long timeLeft;
    private Task handle;

    public PointsWarTask(long time) {
        this.timeLeft = time;
        this.bossBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SEGMENTED_20);
    }

    public void attach(Task task) {
        this.handle = task;
    }

    @Override
    public void run() {
        if (timeLeft <= 0) {
            bossBar.removeAll();
            handle.cancel();
            return;
        }

        timeLeft -= 1000;

        bossBar.setTitle(utilsManagers.setColoredMessage("&fGuerra de puntos: %time% | %point-lose% / %point-win%"
                .replace("%time%", utilsManagers.formatTime(timeLeft, true))
                .replace("%point-win%", "s")
                .replace("%point-lose%", "a"))
        );

        online.forEach(bossBar::addPlayer);
    }

    public void stop() {
        if (handle != null) handle.cancel();
        bossBar.removeAll();
    }
}