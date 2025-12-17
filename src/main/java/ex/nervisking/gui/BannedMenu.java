package ex.nervisking.gui;

import ex.api.base.gui.*;
import ex.api.base.item.ItemBuilder;
import ex.api.base.model.ParseVariable;
import ex.nervisking.ExClan;
import ex.nervisking.config.gui.ConfigBanned;
import ex.nervisking.models.Clan;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class BannedMenu extends MenuPages<ExClan> {

    private final ConfigBanned configAlly;
    private final Clan clan;
    private final NamespacedKey key;

    public BannedMenu(Player player, Clan clan) {
        super(player);
        this.configAlly = plugin.getConfigBanned();
        this.clan = clan;
        this.key = new NamespacedKey(plugin, "banned");
    }

    @Override
    public String setSection() {
        return "clan";
    }

    @Override
    public String setName() {
        return configAlly.getTitle();
    }

    @Override
    public Row setRows() {
        return Row.fromSize(configAlly.getRows());
    }

    @Override
    public List<Integer> setSlots() {
        return configAlly.getSlots();
    }

    @Override
    public List<ItemStack> addDataItems() {
        List<ItemStack> items = new ArrayList<>();
        for (var banned : clan.getBannedMembers()) {
            var player = Bukkit.getOfflinePlayer(banned);
            if (!player.hasPlayedBefore()) {
                continue;
            }
            ItemStack itemStack = configAlly.getItem(ConfigBanned.DataItem.ALLYS).getItemBuilder(Material.PLAYER_HEAD, ParseVariable
                            .adD("%name%", player.getName())
                    )
                    .setSkull(banned)
                    .setPersistentData(key, PersistentDataType.STRING, banned.toString())
                    .build();
            items.add(itemStack);
        }

        return items;
    }

    @Override
    public void buildItems() {
        for (var itemData : configAlly.getOtherItems()) {
            this.setItem(itemData.getSlot(), itemData.getItemBuilder(player));
        }
        for (var entry : configAlly.getDefaultItems().entrySet()) {
            ItemBuilder itemBuilder = entry.getValue().getItemBuilder(player);

            if (entry.getKey() == ConfigBanned.DataItem.PREVIOUS_PAGE) {
                if (this.hasBack()) {
                    this.setItem(entry.getValue().getSlot(), itemBuilder);
                }
            } else if (entry.getKey() == ConfigBanned.DataItem.NEXT_PAGE) {
                if (this.hasNext()) {
                    this.setItem(entry.getValue().getSlot(), itemBuilder);
                }
            } else if (entry.getKey() == ConfigBanned.DataItem.HIDE) {
                this.fill(itemBuilder);
            } else {
                this.setItem(entry.getValue().getSlot(), itemBuilder);
            }
        }
    }

    @Override
    public void handleMenu(MenuEvent event) {
        MenuUser sender = event.getMenuUser();
        Player player = event.getPlayer();
        int slot = event.getSlot();
        if (getSlotIndex(slot)) {
            ItemDataMenu clickedItem = event.getCurrentItem(key);

            if (clickedItem == null) return;

            var name = clickedItem.getDataAsOfflinePlayer();
            if (name == null || !name.hasPlayedBefore()) {
                return;
            }

            if (!clan.isManager(player.getUniqueId())) {
                sender.sendLang("not-leader");
                return;
            }

            if (!clan.isBanned(name.getUniqueId())) {
                sender.sendLang("unban.not-banned");
                return;
            }

            clan.unbanMember(name.getUniqueId());
            sender.sendLang("unban.success", ParseVariable.adD("%player%", name.getName()).add("%clan%", clan.getClanName()));
            refreshData();
        } else if (get(slot) != null) {
            switch (get(slot)) {
                case MAIN -> openMenu(new MainClan(player, clan));
                case CLOSE -> this.closeInventory();
                case NEXT_PAGE -> this.nextPage();
                case PREVIOUS_PAGE -> this.prevPage();
                default -> {}
            }
        } else {
            for (var itemData : configAlly.getOtherItems()) {
                if (itemData.getSlot(slot) && itemData.hasActions()) {
                    executeActions(player, itemData.getActions());
                    break;
                }
            }
        }
    }

    public ConfigBanned.DataItem get(int slot) {
        for (var entry : configAlly.getDefaultItems().entrySet()) {
            if (entry.getValue().getSlot(slot)) {
                return entry.getKey();
            }
        }
        return null;
    }
}