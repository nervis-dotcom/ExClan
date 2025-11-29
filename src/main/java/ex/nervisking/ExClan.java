package ex.nervisking;

import ex.api.base.ExPlugin;
import ex.api.base.data.MenuInfo;
import ex.api.base.data.PluginInfo;
import ex.api.base.model.CustomColor;
import ex.api.base.task.Scheduler;
import ex.api.base.task.Task;
import ex.nervisking.manager.RequestInvite;
import ex.nervisking.commands.ClanCommand;
import ex.nervisking.commands.CommandMain;
import ex.nervisking.config.DataConfig;
import ex.nervisking.config.MainConfig;
import ex.nervisking.events.CombatEvent;
import ex.nervisking.events.ExEvents;
import ex.nervisking.events.JoinAndLeaveEvent;
import ex.nervisking.Test.BalloonManager;

@PluginInfo(pyfiglet = true, menu = @MenuInfo(enable = true))
public class ExClan extends ExPlugin {

    private MainConfig mainConfig;
    private DataConfig dataConfig;

    private Task saveDataTask;

    private ClanManager clanManager;
    private RequestInvite requestInvite;

    private BalloonManager balloonManager;

    @Override
    protected void enable() {
        this.pyfigletMessage.setPyfiglet(" ",
                        "                           %plugin% Se Ha %status%",
                        "            Version » %version% | Autores » %autor%",
                        "      ,----.          ,-.--,  _,.----.              ,---.      .-._         ",
                        "   ,-.--` , \\.--.-.  /=/, .'.' .' -   \\   _.-.    .--.'  \\    /==/ \\  .-._  ",
                        "  |==|-  _.-`\\==\\ -\\/=/- / /==/  ,  ,-' .-,.'|    \\==\\-/\\ \\   |==|, \\/ /, / ",
                        "  |==|   `.-. \\==\\ `-' ,/  |==|-   |  .|==|, |    /==/-|_\\ |  |==|-  \\|  |  ",
                        " /==/_ ,    /  |==|,  - |  |==|_   `-' \\==|- |    \\==\\,   - \\ |==| ,  | -|  ",
                        " |==|    .-'  /==/   ,   \\ |==|   _  , |==|, |    /==/ -   ,| |==| -   _ |  ",
                        " |==|_  ,`-._/==/, .--, - \\==\\.       /==|- `-._/==/-  /\\ - \\|==|  /\\ , |  ",
                        " /==/ ,     /\\==\\- \\/=/ , / `-.`.___.-'/==/ - , ,|==\\ _.\\=\\.-'/==/, | |- |  ",
                        " `--`-----``  `--`-'  `--`             `--`-----' `--`        `--`./  `--`  ",
                        "                         Server version » %server%")
                .setStartColor(CustomColor.GRAY)
                .setEndColor(CustomColor.GOLD);

        this.clanManager = new ClanManager();

        this.mainConfig = new MainConfig();
        this.dataConfig = new DataConfig();

        this.requestInvite = new RequestInvite();

        balloonManager = new BalloonManager();
        balloonManager.start();

        // Comando
        this.commandManager.registerCommand(new CommandMain(this));
        this.commandManager.registerCommand(new ClanCommand(this));

        //Evento
        this.eventsManager.registerEvents(new CombatEvent());
        this.eventsManager.registerEvents(new ExEvents());
        this.eventsManager.registerEvents(new JoinAndLeaveEvent());

        for (var p : getServer().getOnlinePlayers()) {
            balloonManager.addPlayer(p);
        }

        this.eventsManager.registerEvents(balloonManager);

        saveDataTask = Scheduler.runTimer(() -> dataConfig.saveConfigs(), 10 * 60 * 20, 30 * 60 * 20);
    }

    @Override
    protected void disable() {
        if (saveDataTask != null) {
            saveDataTask.cancel();
        }

        if (dataConfig != null) {
            dataConfig.saveConfigs();
        }

        balloonManager.stop();
    }

    @Override
    protected void onReload() {

    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public RequestInvite getRequestInvite() {
        return requestInvite;
    }
}