package ex.nervisking.gui;

import ex.api.base.gui.*;
import ex.api.base.item.ItemBuilder;
import ex.api.base.model.ParseVariable;
import ex.nervisking.ExClan;
import ex.nervisking.config.gui.ConfigMember;
import ex.nervisking.models.Clan;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class MembersMenu extends MenuPages<ExClan> {

    private final ConfigMember configMember;
    private final Clan clan;
    private final NamespacedKey key;

    public MembersMenu(Player player, Clan clan) {
        super(player);
        this.configMember = plugin.getConfigMember();
        this.clan = clan;
        this.key = new NamespacedKey(plugin, "members");
    }

    @Override
    public String setSection() {
        return "clan";
    }

    @Override
    public String setName() {
        return configMember.getTitle();
    }

    @Override
    public Row setRows() {
        return Row.fromSize(configMember.getRows());
    }

    @Override
    public List<Integer> setSlots() {
        return configMember.getSlots();
    }

    @Override
    public List<ItemStack> addDataItems() {
        List<ItemStack> items = new ArrayList<>();
        for (var entry : clan.getMembers()) {
            ItemStack itemStack = configMember.getItem(ConfigMember.DataItem.MEMBERS).getItemBuilder(Material.PLAYER_HEAD, ParseVariable
                            .adD("%name%", entry.getName())
                            .add("%rank%", entry.getRank().name())
                    )
                    .setSkull(entry.getUuid())
                    .setPersistentData(key, PersistentDataType.STRING, entry.getName())
                    .build();
            items.add(itemStack);
        }

        return items;
    }

    @Override
    public void buildItems() {
        for (var itemData : configMember.getOtherItems()) {
            this.setItem(itemData.getSlot(), itemData.getItemBuilder(player));
        }
        for (var entry : configMember.getDefaultItems().entrySet()) {
            ItemBuilder itemBuilder = entry.getValue().getItemBuilder(player);

            if (entry.getKey() == ConfigMember.DataItem.PREVIOUS_PAGE) {
                if (this.hasBack()) {
                    this.setItem(entry.getValue().getSlot(), itemBuilder);
                }
            } else if (entry.getKey() == ConfigMember.DataItem.NEXT_PAGE) {
                if (this.hasNext()) {
                    this.setItem(entry.getValue().getSlot(), itemBuilder);
                }
            } else if (entry.getKey() == ConfigMember.DataItem.HIDE) {
                this.fill(itemBuilder);
            } else {
                this.setItem(entry.getValue().getSlot(), itemBuilder);
            }
        }
    }

    @Override
    public void handleMenu(MenuEvent event) {
        Player player = event.getPlayer();
        int slot = event.getSlot();
        if (getSlotIndex(slot)) {
            ItemDataMenu clickedItem = event.getCurrentItem(key);

            if (clickedItem == null) return;

            String name = clickedItem.getData();
            if (name == null) {
                return;
            }

        } else if (get(slot) != null) {
            switch (get(slot)) {
                case MAIN -> openMenu(new MainClan(player, clan));
                case CLOSE -> this.closeInventory();
                case NEXT_PAGE -> this.nextPage();
                case PREVIOUS_PAGE -> this.prevPage();
                default -> {}
            }
        } else {
            for (var itemData : configMember.getOtherItems()) {
                if (itemData.getSlot(slot) && itemData.hasActions()) {
                    executeActions(player, itemData.getActions());
                    break;
                }
            }
        }
    }

    public ConfigMember.DataItem get(int slot) {
        for (var entry : configMember.getDefaultItems().entrySet()) {
            if (entry.getValue().getSlot(slot)) {
                return entry.getKey();
            }
        }
        return null;
    }
}