package ex.nervisking;

import ex.api.base.ExPlugin;
import ex.api.base.data.MenuInfo;
import ex.api.base.data.PluginInfo;
import ex.api.base.hook.VaultHook;
import ex.api.base.language.Lang;
import ex.api.base.language.Language;
import ex.api.base.language.LanguageService;
import ex.api.base.model.CustomColor;
import ex.api.base.task.Scheduler;
import ex.api.base.task.Task;
import ex.nervisking.config.gui.ConfigHome;
import ex.nervisking.config.gui.ConfigHomeIcon;
import ex.nervisking.events.ChatEvent;
import ex.nervisking.hook.Papi;
import ex.nervisking.manager.BankManager;
import ex.nervisking.manager.ClanManager;
import ex.nervisking.manager.WarManager;
import ex.nervisking.manager.RequestInvite;
import ex.nervisking.commands.ClanCommand;
import ex.nervisking.commands.CommandMain;
import ex.nervisking.config.DataConfig;
import ex.nervisking.config.MainConfig;
import ex.nervisking.events.CombatEvent;
import ex.nervisking.events.JoinAndLeaveEvent;

@PluginInfo(pyfiglet = true, menu = @MenuInfo(enable = true))
public class ExClan extends ExPlugin {

    private MainConfig mainConfig;
    private DataConfig dataConfig;

    private Task saveDataTask;
    private WarManager warManager;

    private ClanManager clanManager;
    private RequestInvite requestInvite;
    private BankManager bankManager;

    private Language language;

    private ConfigHome configHome;
    private ConfigHomeIcon configHomeIcon;

    @Override
    public String setPrefix() {
        return mainConfig.getPrefix();
    }

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

        VaultHook.setupEconomy();

        // Idioma
        this.language = LanguageService.register(Lang.ES, "Lang").addFiles(Lang.ES);
        this.language.load();

        this.clanManager = new ClanManager();

        this.mainConfig = new MainConfig();
        this.dataConfig = new DataConfig();

        this.requestInvite = new RequestInvite();
        this.bankManager = new BankManager();
        this.warManager = new WarManager();

        // Configs Gui
        this.configHome = new ConfigHome();
        this.configHomeIcon = new ConfigHomeIcon();

        // Comando
        this.register(new CommandMain(this));
        this.register(new ClanCommand(this));

        // Evento
        this.register(new CombatEvent());
        this.register(new JoinAndLeaveEvent());
        this.register(new ChatEvent());

        // Placeholder
        this.register(new Papi(this));

        this.saveDataTask = Scheduler.runTimer(() -> dataConfig.saveConfigs(), 10 * 60 * 20, 30 * 60 * 20);
    }

    @Override
    protected void disable() {
        if (saveDataTask != null) {
            saveDataTask.cancel();
        }

        if (dataConfig != null) {
            dataConfig.saveConfigs();
        }

        if (warManager != null) {
            warManager.stopAll();
        }
    }

    @Override
    protected void onReload() {
        this.mainConfig.reload();
        this.language.reload();
        this.configHome.reload();
        this.configHomeIcon.reload();
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

    public BankManager getBankManager() {
        return bankManager;
    }

    public WarManager getPointsWarManager() {
        return warManager;
    }

    public ConfigHome getConfigHome() {
        return configHome;
    }

    public ConfigHomeIcon getConfigHomeIcon() {
        return configHomeIcon;
    }
}