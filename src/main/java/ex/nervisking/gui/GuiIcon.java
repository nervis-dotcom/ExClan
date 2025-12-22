package ex.nervisking.gui;

import ex.api.base.gui.Menu;
import ex.api.base.gui.MenuEvent;
import ex.api.base.gui.MenuUser;
import ex.api.base.gui.Row;
import ex.api.base.item.ItemBuilder;
import ex.api.base.task.Scheduler;
import ex.api.base.utils.Text;
import ex.nervisking.ExClan;
import ex.nervisking.config.gui.ConfigIcon;
import ex.nervisking.models.Clan;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class GuiIcon extends Menu<ExClan> {

    private final ConfigIcon configIcon;
    private final Clan clan;
    private final int icon;

    public GuiIcon(@NotNull Player player, @NotNull Clan clan) {
        super(player);
        this.configIcon = plugin.getConfigIcon();
        this.clan = clan;
        this.icon = configIcon.getIcon();
    }

    @Override
    public String setSection() {
        return "clan";
    }

    @Override
    public Text setName() {
        return Text.of(configIcon.getTitle());
    }

    @Override
    public Row setRows() {
        return Row.fromSize(configIcon.getRows());
    }

    @Override
    public boolean setCancelClicks() {
        return false;
    }

    @Override
    public boolean setCancelSlap() {
        return true;
    }

    @Override
    public void addItems() {
        for (var itemData : configIcon.getOtherItems()) {
            this.setItem(itemData.getSlot(), itemData.getItemBuilder(player));
        }
        for (var entry : configIcon.getDefaultItems().entrySet()) {
            this.setItem(entry.getValue().getSlot(), entry.getValue().getItemBuilder(player));
        }

        if (clan.hasIcon()) {
            this.setItem(icon, ItemBuilder.of(clan.getIcon().clone()).setName(clan.getClanTag()));
        } else {
            this.put(icon, null);
        }
    }

    @Override
    public void handleMenu(MenuEvent event) {
        MenuUser user = event.getMenuUser();
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        int slot = event.getSlot();
        if (event.getSlot(icon)) {
            if (sendCooldown(clan.getClanName())) {
                event.setCancelled(true);
                return;
            }

            if (clan.hasIcon() && current != null) {
                giveItem(user.getPlayer(), clearNameAndLore(current));
                user.sendLang("icon.removed");
                put(icon, null);
                clan.setIcon(null);
                event.setCancelled(true);
                return;
            }

            if (cursor != null && !clan.hasIcon()) {
                if (cursor.getType().isAir()) {
                    event.setCancelled(true);
                    return;
                }
                if (!isBANNER(cursor)) {
                    user.sendLang("icon.invalid-item");
                    event.setCancelled(true);
                    return;
                }

                var item = clearNameAndLore(cursor);
                if (item == null) {
                    event.setCancelled(true);
                    return;
                }

                item.setAmount(1);
                clan.setIcon(item.clone());
                user.sendLang("icon.set");
                Scheduler.runLater(()-> this.setItem(icon, ItemBuilder.of(clan.getIcon().clone()).setName(clan.getClanTag())), 1);
            }
        } else {
            event.setCancelled();
            if (get(slot) != null) {
                switch (get(slot)) {
                    case CLOSE -> this.closeInventory();
                    case MAIN -> openMenu(new MainClan(player, clan));
                }
            } else {
                for (var itemData : configIcon.getOtherItems()) {
                    if (itemData.getSlot(slot) && itemData.hasActions()) {
                        executeActions(player, itemData.getActions());
                        break;
                    }
                }
            }
        }
    }

    public ConfigIcon.DataItem get(int slot) {
        for (var entry : configIcon.getDefaultItems().entrySet()) {
            if (entry.getValue().getSlot(slot)) {
                return entry.getKey();
            }
        }
        return null;
    }
    private ItemStack clearNameAndLore(ItemStack item) {
        if (item == null) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.displayName(null);
        meta.lore(null);

        item.setItemMeta(meta);
        return item;
    }

    private boolean isBANNER(ItemStack itemStack) {
        return itemStack != null && itemStack.getType().name().endsWith("_BANNER");
    }
}