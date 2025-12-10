package ex.nervisking.gui.homes;

import ex.api.base.gui.ItemDataMenu;
import ex.api.base.gui.MenuEvent;
import ex.api.base.gui.MenuPages;
import ex.api.base.gui.Row;
import ex.api.base.item.ItemBuilder;
import ex.api.base.utils.PlayerTeleport;
import ex.api.base.utils.teleport.TPAnimation;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import ex.nervisking.models.Homes;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class HomeMenu extends MenuPages<ExClan> {

    private final Clan clan;
    private final NamespacedKey key;

    public HomeMenu(Player player, Clan clan) {
        super(player);
        this.clan = clan;
        this.key = new NamespacedKey(plugin, "homes");
    }

    @Override
    public List<ItemStack> addDataItems() {
        List<ItemStack> items = new ArrayList<>();
        for (var entry : clan.getHomes()) {
            ItemStack itemStack = new ItemBuilder(entry.getIcon())
                    .setName("&fHome: &e" + entry.getName())
                    .setLore("",
                            "&8&m                                                        &r",
                            "&7Click izquierdo para cambiar el icono del home.",
                            "&7Click derecho para ir a ese home.",
                            "&7Click-SHIFT izquierdo para borrar este home.",
                            "&8&m                                                        &r",
                            "")
                    .setHideAll()
                    .setPersistentData(key, PersistentDataType.STRING, entry.getName())
                    .build();
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


        this.fill(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setHideTooltip());

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
        return "&fHomes";
    }

    @Override
    public Row setRows() {
        return Row.CHESTS_54;
    }

    @Override
    public void handleMenu(MenuEvent event) {
        Player player = event.getMenuUser().getPlayer();
        int slot = event.getSlot();

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
            ItemDataMenu clickedItem = event.getCurrentItem(key);

            if (clickedItem == null) return;

            String name = clickedItem.getData();
            if (name == null) {
                return;
            }
            Homes home = clan.getHome(name);
            if (home == null) {
                return;
            }

            if (event.getClick() == ClickType.RIGHT) {
                Location loc = home.getCoordinate().getLocation();
                if (loc == null) {
                    sendMessage(player, "%prefix% &cError: La ubicación del home no está configurada.");
                    return;
                }

                PlayerTeleport tm = new PlayerTeleport(player, loc)
                        .setMessage("%prefix% &aHas sido teletransportado al home %name%!".replace("%name%", name))
                        .setSound(Sound.ENTITY_ENDERMAN_TELEPORT)
                        .setParticle(Particle.FLAME)
                        .setTeleportAnimation(TPAnimation.DOUBLE_SPIRAL)
                        .setDelayTicks(3)
                        .setNoDelayPermission("home.instant")
                        .setMessageInTeleport("Teletransporte en %time% segundos...", "&aTeletransportado...")
                        .setSoundInTeleport(Sound.ENTITY_PLAYER_LEVELUP);
                tm.teleportOf(
                        player::closeInventory,
                        () -> {
                            sendMessage(player, "%prefix% &cError: " + tm.getErrorMessage());
                            player.closeInventory();
                        }
                );
            } else if (event.getClick() == ClickType.LEFT) {
                if (!clan.isLader(player.getUniqueId())) {
                    sendMessage(player, "%prefix% &cNo eres el líder del clan.");
                    return;
                }
                openMenu(new HomeIconMenu(player, home));
            } else if (event.getClick() == ClickType.SHIFT_LEFT) {
                if (!clan.isLader(player.getUniqueId())) {
                    sendMessage(player, "%prefix% &cNo eres el líder del clan.");
                    return;
                }
                clan.removeHome(home);
                sendMessage(player, "%prefix% &a¡Has eliminado el home %name%!".replace("%name%", name));
                refreshData();
            }
        }
    }
}