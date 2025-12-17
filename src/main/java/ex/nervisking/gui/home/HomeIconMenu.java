package ex.nervisking.gui.home;

import ex.api.base.gui.MenuEvent;
import ex.api.base.gui.MenuPages;
import ex.api.base.gui.Row;
import ex.api.base.item.RDMaterial;
import ex.nervisking.ExClan;
import ex.nervisking.config.gui.ConfigHomeIcon;
import ex.nervisking.models.Homes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HomeIconMenu extends MenuPages<ExClan> {

    private final ConfigHomeIcon configHomeIcon;
    private final Homes homes;

    public HomeIconMenu(Player player, Homes homes) {
        super(player);
        this.configHomeIcon = plugin.getConfigHomeIcon();
        this.homes = homes;
    }

    @Override
    public List<ItemStack> addDataItems() {
        List<ItemStack> items = new ArrayList<>();
        for (var entry : RDMaterial.DYE.getMaterials()) {
            ItemStack itemStack = configHomeIcon.getItem(ConfigHomeIcon.DataItem.ICONS).getItemBuilder(player, entry).build();
            if (itemStack.getType().equals(homes.getIcon())) {
                itemStack = configHomeIcon.getItem(ConfigHomeIcon.DataItem.ICONS).getItemBuilder(player, entry)
                        .setGlintOverride()
                        .build();
            }
            items.add(itemStack);
        }

        return items;
    }

    @Override
    public String setName() {
        return configHomeIcon.getTitle();
    }

    @Override
    public Row setRows() {
        return Row.fromSize(configHomeIcon.getRows());
    }

    @Override
    public List<Integer> setSlots() {
        return configHomeIcon.getSlots();
    }

    @Override
    public void buildItems() {
        for (var itemData : configHomeIcon.getOtherItems()) {
            this.setItem(itemData.getSlot(), itemData.getItemBuilder(player));
        }
        for (var entry : configHomeIcon.getDefaultItems().entrySet()) {
            var itemBuilder = entry.getValue().getItemBuilder(player);
            if (entry.getKey() == ConfigHomeIcon.DataItem.PREVIOUS_PAGE) {
                this.setItem(entry.getValue().getSlot(), itemBuilder);
            } else if (entry.getKey() == ConfigHomeIcon.DataItem.HIDE) {
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
            var clickedItem = event.getCurrentItem();

            if (clickedItem == null) return;
            homes.setIcon(clickedItem.getType());
            back();
        } else if (get(slot) != null) {
            switch (get(slot)) {
                case CLOSE -> this.closeInventory();
                case PREVIOUS_PAGE -> this.back();
                default -> {}
            }
        } else {
            for (var itemData : configHomeIcon.getOtherItems()) {
                if (itemData.getSlot(slot) && itemData.hasActions()) {
                    executeActions(player, itemData.getActions());
                    break;
                }
            }
        }
    }

    public ConfigHomeIcon.DataItem get(int slot) {
        for (var entry : configHomeIcon.getDefaultItems().entrySet()) {
            if (entry.getValue().getSlot(slot)) {
                return entry.getKey();
            }
        }
        return null;
    }
}