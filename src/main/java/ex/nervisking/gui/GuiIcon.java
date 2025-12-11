package ex.nervisking.gui;

import ex.api.base.gui.Menu;
import ex.api.base.gui.MenuEvent;
import ex.api.base.gui.MenuUser;
import ex.api.base.gui.Row;
import ex.api.base.item.ItemBuilder;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class GuiIcon extends Menu<ExClan> {

    private final Clan clan;

    public GuiIcon(@NotNull Player player, @NotNull Clan clan) {
        super(player);
        this.clan = clan;
    }

    @Override
    public String setName() {
        return "&fIcono del clan";
    }

    @Override
    public Row setRows() {
        return Row.CHESTS_36;
    }

    @Override
    public boolean setCancelClicks() {
        return false;
    }

    @Override
    public void addItems() {
        this.setFilter();
        this.setBorder(ItemBuilder.of(ItemBuilder.BLACK).setHideTooltip());
        this.put(13, clan.getIcon());
        this.setItem(31, ItemBuilder.of(ItemBuilder.CLOSE).setName("&cCerrar"));
    }

    @Override
    public void handleMenu(MenuEvent event) {
        MenuUser player = event.getMenuUser();
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if (event.getSlot(13)) {
            if (clan.hasIcon() && current != null) {
                giveItem(player.getPlayer(), clearNameAndLore(current));
                player.sendMessage("%prefix% &aHas quitado el icono del clan,");
                put(13, null);
                clan.setIcon(null);
                event.setCancelled(true);
                return;
            }

            if (cursor != null && !clan.hasIcon()) {
                if (cursor.getType().isAir()) {
                    return;
                }
                if (!isBANNER(cursor)) {
                    player.sendMessage("%prefix% &cEl icono debe ser un banner.");
                    event.setCancelled(true);
                    return;
                }

                var item = clearNameAndLore(cursor);
                if (item == null) {
                    return;
                }

                item.setAmount(1);
                clan.setIcon(item.clone());
                player.sendMessage("%prefix% &aHas puesto el icono del clan.");
            }
        } else {
            event.setCancelled(true);
        }
    }

    public static ItemStack clearNameAndLore(ItemStack item) {
        if (item == null) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.displayName(null); // borra el nombre
        meta.lore(null); // borra el lore

        item.setItemMeta(meta);
        return item;
    }

    public boolean isBANNER(ItemStack itemStack) {
        return itemStack != null && itemStack.getType().name().endsWith("_BANNER");
    }
}