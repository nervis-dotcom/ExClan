package ex.nervisking.gui;

import ex.api.base.gui.*;
import ex.api.base.item.ItemBuilder;
import ex.api.base.model.ParseVariable;
import ex.nervisking.ExClan;
import ex.nervisking.config.gui.ConfigAlly;
import ex.nervisking.manager.ClanManager;
import ex.nervisking.models.Clan;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class AllysMenu extends MenuPages<ExClan> {

    private final ClanManager clanManager;
    private final ConfigAlly configAlly;
    private final Clan clan;
    private final NamespacedKey key;

    public AllysMenu(Player player, Clan clan) {
        super(player);
        this.clanManager = plugin.getClanManager();
        this.configAlly = plugin.getConfigAlly();
        this.clan = clan;
        this.key = new NamespacedKey(plugin, "allys");
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
        for (var allys : clan.getAllys()) {
            var ally = clanManager.getClan(allys);
            if (ally == null) {
                continue;
            }
            ItemStack itemStack = configAlly.getItem(ConfigAlly.DataItem.ALLYS).getItemBuilder(Material.PLAYER_HEAD, ParseVariable
                            .adD("%name%", allys)
                    )
                    .setSkull(ally.getLaderUuid())
                    .setPersistentData(key, PersistentDataType.STRING, allys)
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

            if (entry.getKey() == ConfigAlly.DataItem.PREVIOUS_PAGE) {
                if (this.hasBack()) {
                    this.setItem(entry.getValue().getSlot(), itemBuilder);
                }
            } else if (entry.getKey() == ConfigAlly.DataItem.NEXT_PAGE) {
                if (this.hasNext()) {
                    this.setItem(entry.getValue().getSlot(), itemBuilder);
                }
            } else if (entry.getKey() == ConfigAlly.DataItem.HIDE) {
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

            String name = clickedItem.getData();
            if (name == null) {
                return;
            }

            var clanTarget = clanManager.getClan(name);
            if (clanTarget == null) {
                sender.sendLang("not-exist");
                refreshData();
                return;
            }

            if (!clan.isAlly(clanTarget.getClanName())) {
                sender.sendLang("ally.unally.not-ally");
                refreshData();
                return;
            }

            clan.removeAlly(clanTarget.getClanName());
            clanTarget.removeAlly(clan.getClanName());
            sender.sendLang("ally.unally.removed", ParseVariable.adD("%clan%", clanTarget.getClanName()));
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

    public ConfigAlly.DataItem get(int slot) {
        for (var entry : configAlly.getDefaultItems().entrySet()) {
            if (entry.getValue().getSlot(slot)) {
                return entry.getKey();
            }
        }
        return null;
    }
}