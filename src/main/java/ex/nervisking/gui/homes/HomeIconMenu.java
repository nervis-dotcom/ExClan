package ex.nervisking.gui.homes;

import ex.api.base.gui.MenuEvent;
import ex.api.base.gui.MenuPages;
import ex.api.base.gui.Row;
import ex.api.base.item.ItemBuilder;
import ex.api.base.item.RDMaterial;
import ex.nervisking.ExClan;
import ex.nervisking.models.Homes;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HomeIconMenu extends MenuPages<ExClan> {

    private final Homes homes;

    public HomeIconMenu(Player player, Homes homes) {
        super(player);
        this.homes = homes;
    }

    @Override
    public List<ItemStack> addDataItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Material entry : RDMaterial.DYE.getMaterials()) {
            ItemStack itemStack = new ItemBuilder(entry).setHideTooltip().build();
            if (itemStack.getType().name().equals(homes.getIcon())) {
                itemStack = new ItemBuilder(entry)
                        .setGlintOverride()
                        .setHideTooltip()
                        .build();
            }
            items.add(itemStack);
        }

        return items;
    }

    @Override
    public List<Integer> setSlots() {
        return List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);
    }

    @Override
    public void buildItems() {
        this.setItemFilter(new ItemBuilder(ItemBuilder.GRAY).setHideTooltip());
        this.setItem(new ItemBuilder(ItemBuilder.BLACK).setHideTooltip(), List.of(45, 46, 47, 48, 49, 50, 51, 52, 53));

        this.fill(new ItemBuilder(ItemBuilder.WHITE).setHideTooltip());

        if (this.hasBack()) {
            this.setItem(48, new ItemBuilder(ItemBuilder.BACK).setName("&fPagina Anterior"));
        }

        this.setItem(49, new ItemBuilder(ItemBuilder.CLOSE).setName("&fCerrar"));

        if (this.hasNext()) {
            this.setItem(50, new ItemBuilder(ItemBuilder.AFTER).setName("&fSiguiente Pagina"));
        }
    }

    @Override
    public String setName() {
        return "&fIconos";
    }

    @Override
    public Row setRows() {
        return Row.CHESTS_54;
    }

    @Override
    public void handleMenu(MenuEvent event) {
        Player player = event.getPlayer();
        int slot = event.getSlot();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null) return;

        if (slot == 47) {
            this.firstPage();
        } else if (slot == 48) {
            this.prevPage();
        } else if (slot == 49) {
            player.closeInventory();
        } else if (slot == 50) {
            this.nextPage();
        } else if (slot == 51) {
            this.lastPage();
            return;
        }

        if (getSlotIndex(slot)) {
            homes.setIcon(clickedItem.getType().name());
            back();
        }
    }
}