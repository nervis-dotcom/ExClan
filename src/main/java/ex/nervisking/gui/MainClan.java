package ex.nervisking.gui;

import ex.api.base.gui.Menu;
import ex.api.base.gui.MenuEvent;
import ex.api.base.gui.MenuUser;
import ex.api.base.gui.Row;
import ex.api.base.model.ParseVariable;
import ex.nervisking.ExClan;
import ex.nervisking.config.gui.ConfigMainGui;
import ex.nervisking.gui.home.HomeMenu;
import ex.nervisking.models.Clan;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MainClan extends Menu<ExClan> {

    private final ConfigMainGui configMainGui;
    private final Clan clan;

    public MainClan(@NotNull Player player, @NotNull Clan clan) {
        super(player);
        this.clan = clan;
        this.configMainGui = plugin.getConfigMainGui();
    }

    @Override
    public String setName() {
        return configMainGui.getTitle();
    }

    @Override
    public Row setRows() {
        return Row.fromSize(configMainGui.getRows());
    }

    @Override
    public void addItems() {
        for (var itemData : configMainGui.getOtherItems()) {
            this.setItem(itemData.getSlot(), itemData.getItemBuilder(player));
        }
        for (var entry : configMainGui.getDefaultItems().entrySet()) {
            if (entry.getKey() == ConfigMainGui.DataItem.LEADER) {
                this.setItem(entry.getValue().getSlot(), entry.getValue().getItemBuilder(player, Material.PLAYER_HEAD, ParseVariable.adD("%leader%", clan.getLeaderName()))
                        .setSkull(clan.getLaderUuid())
                );
            } else {
                this.setItem(entry.getValue().getSlot(), entry.getValue().getItemBuilder(player));
            }
        }
    }

    @Override
    public void handleMenu(MenuEvent event) {
        Player player = event.getPlayer();
        MenuUser menuUser = event.getMenuUser();
        int slot = event.getSlot();
        if (get(slot) != null) {
            switch (get(slot)) {
                case CLOSE -> this.closeInventory();
                case HOME -> openMenu(new HomeMenu(player, clan));
                case ICON -> {
                    if (!clan.isLader(player.getUniqueId())) {
                        menuUser.sendLang("not-leader");
                        return;
                    }
                    openMenu(new GuiIcon(player, clan));
                }
                case VAULT -> {
                    if (!clan.isManager(player.getUniqueId())) {
                        menuUser.sendLang("not-leader");
                        return;
                    }
                    clan.getChest().openSharedChest(player);
                }
                case MEMBER -> openMenu(new MembersMenu(player, clan));
                case ALLY -> openMenu(new AllysMenu(player, clan));
                case BANNED -> openMenu(new BannedMenu(player, clan));

            }
        } else {
            for (var itemData : configMainGui.getOtherItems()) {
                if (itemData.getSlot(slot) && itemData.hasActions()) {
                    executeActions(player, itemData.getActions());
                    break;
                }
            }
        }
    }

    public ConfigMainGui.DataItem get(int slot) {
        for (var entry : configMainGui.getDefaultItems().entrySet()) {
            if (entry.getValue().getSlot(slot)) {
                return entry.getKey();
            }
        }
        return null;
    }
}