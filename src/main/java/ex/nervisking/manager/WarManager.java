package ex.nervisking.manager;

import ex.api.base.task.Scheduler;
import ex.api.base.task.Task;
import ex.nervisking.tasks.BankWarTask;
import ex.nervisking.tasks.PointsWarTask;

public class WarManager {

    private Task taskPoint;
    private Task taskBank;
    private PointsWarTask pointsWarTask;
    private BankWarTask bankWarTask;

    public boolean startPoint(long time) {
        if (taskPoint != null && taskPoint.isRunning()) return false;

        pointsWarTask = new PointsWarTask(time);
        taskPoint = Scheduler.runTimer(pointsWarTask, 20);

        pointsWarTask.attach(taskPoint);
        return true;
    }

    public boolean stopPoint() {
        if (taskPoint != null) {
            pointsWarTask.stop();
            taskPoint = null;
            pointsWarTask = null;
            return true;
        }
        return false;
    }

    public boolean startBank(long time) {
        if (taskBank != null && taskBank.isRunning()) {
            return false;
        }

        bankWarTask = new BankWarTask(time);
        taskBank = Scheduler.runTimer(bankWarTask, 20);
        bankWarTask.attach(taskBank);
        return true;
    }

    public boolean stopBank() {
        if (taskBank != null) {
            bankWarTask.stop();
            bankWarTask = null;
            taskBank = null;
            return true;
        }
        return false;
    }

    public boolean enabledPoint() {
        return taskPoint != null && taskPoint.isRunning();
    }

    public boolean enabledBank() {
        return taskBank != null && taskBank.isRunning();
    }

    public void stopAll() {
        stopPoint();
        stopBank();
    }
}