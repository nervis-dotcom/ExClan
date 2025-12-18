package ex.nervisking.commands.clanArguments;

import ex.api.base.command.Arguments;
import ex.api.base.command.CommandArg;
import ex.api.base.command.CommandArgument;
import ex.api.base.command.Sender;
import ex.api.base.model.ParseVariable;
import ex.nervisking.ExClan;
import ex.nervisking.config.SettingConfig;
import ex.nervisking.manager.ClanManager;
import ex.nervisking.models.Clan;

import java.util.UUID;

@CommandArg(name = "chest", permission = true)
public class ChestArgument implements CommandArgument {

    private final SettingConfig settingConfig;
    private final ClanManager clanManager;

    public ChestArgument(ExClan plugin) {
        this.settingConfig = plugin.getSettingConfig();
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        UUID uuid = sender.getUniqueId();
        Clan clan = clanManager.getClan(uuid);
        if (clan == null) {
            sender.sendLang("no-clan");
            return;
        }

        if (!settingConfig.isChestEnable()) {
            sender.sendLang("chest.disabled");
            return;
        }

        if (!clan.isManager(uuid)) {
            sender.sendLang("not-leader");
            return;
        }

        try {
            clan.getChest().openSharedChest(sender.getPlayer());
        } catch (Exception e) {
            sender.sendLang("error", ParseVariable.adD("%error%", e.getMessage()));
        }
    }
}