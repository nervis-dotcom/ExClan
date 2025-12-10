package ex.nervisking.config;

import ex.api.base.config.ConfigInfo;
import ex.api.base.config.Yaml;
import org.bukkit.configuration.file.FileConfiguration;

@ConfigInfo(name = "Config", register = false)
public class MainConfig extends Yaml {

    private String prefix;

    public MainConfig(){
        this.load();
    }

    @Override
    protected void load() {
        FileConfiguration config = customConfig.getConfig();
        this.prefix = config.getString("prefix", "&#ff0000&lᴇ&#ff3000&lx&#ff6000&lᴄ&#ff8f00&lʟ&#ffbf00&lᴀ&#ffef00&lɴ");
    }

    public String getPrefix() {
        return prefix;
    }
}