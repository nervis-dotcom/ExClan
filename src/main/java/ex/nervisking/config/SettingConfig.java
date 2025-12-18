package ex.nervisking.config;

import ex.api.base.config.ConfigInfo;
import ex.api.base.config.Json;

@ConfigInfo(name = "Setting", register = false)
public class SettingConfig extends Json {

    private boolean playerLang;
    private boolean chestEnable;
    private boolean bankEnable;

    public SettingConfig() {
        this.load();
    }

    @Override
    protected void load() {
        this.playerLang = jsonConfig.getBoolean("player-lang", false);
        this.chestEnable = jsonConfig.getBoolean("chest-enable", true);
        this.bankEnable = jsonConfig.getBoolean("bank-enable", true);
    }

    public boolean isPlayerLang() {
        return playerLang;
    }

    public boolean isChestEnable() {
        return chestEnable;
    }

    public boolean isBankEnable() {
        return bankEnable;
    }

    public void setPlayerLang(boolean playerLang) {
        this.playerLang = playerLang;
        jsonConfig.set("player-lang", playerLang);
        jsonConfig.save();
    }

    public void setChestEnable(boolean chestEnable) {
        this.chestEnable = chestEnable;
        jsonConfig.set("chest-enable", chestEnable);
        jsonConfig.save();
    }

    public void setBankEnable(boolean bankEnable) {
        this.bankEnable = bankEnable;
        jsonConfig.set("bank-enable", bankEnable);
        jsonConfig.save();
    }
}