package ex.nervisking.config;

import ex.api.base.config.CustomConfig;
import org.bukkit.configuration.file.FileConfiguration;

public class MainConfig {

    private final CustomConfig configFile;

    public MainConfig(){
        this.configFile = CustomConfig.of("Config");
        this.loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = configFile.getConfig();
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }
}