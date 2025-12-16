package ex.nervisking.gui.homes;

import ex.api.base.gui.*;
import ex.api.base.item.ItemBuilder;
import ex.api.base.model.ParseVariable;
import ex.api.base.utils.PlayerTeleport;
import ex.nervisking.ExClan;
import ex.nervisking.config.MainConfig;
import ex.nervisking.config.gui.ConfigHome;
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

    private final ConfigHome configHome;
    private final MainConfig config;
    private final Clan clan;
    private final NamespacedKey key;

    public HomeMenu(Player player, Clan clan) {
        super(player);
        this.configHome = plugin.getConfigHome();
        this.config = plugin.getMainConfig();
        this.clan = clan;
        this.key = new NamespacedKey(plugin, "homes");
    }

    @Override
    public String setSection() {
        return "clan";
    }

    @Override
    public String setName() {
        return configHome.getTitle();
    }

    @Override
    public Row setRows() {
        return Row.fromSize(configHome.getRows());
    }

    @Override
    public List<Integer> setSlots() {
        return configHome.getSlots();
    }

    @Override
    public List<ItemStack> addDataItems() {
        List<ItemStack> items = new ArrayList<>();
        for (var entry : clan.getHomes()) {
            ItemStack itemStack = configHome.getItem(ConfigHome.DataItem.HOMES).getItemBuilder(entry.getIcon(), ParseVariable.adD("%name%", entry.getName()))
                    .setPersistentData(key, PersistentDataType.STRING, entry.getName())
                    .build();
            items.add(itemStack);
        }

        return items;
    }

    @Override
    public void buildItems() {
        for (var itemData : configHome.getOtherItems()) {
            this.setItem(itemData.getSlot(), itemData.getItemBuilder(player));
        }
        for (var entry : configHome.getDefaultItems().entrySet()) {
            ItemBuilder itemBuilder = entry.getValue().getItemBuilder(player);

            if (entry.getKey() == ConfigHome.DataItem.PREVIOUS_PAGE) {
                if (this.hasBack()) {
                    this.setItem(entry.getValue().getSlot(), itemBuilder);
                }
            } else if (entry.getKey() == ConfigHome.DataItem.NEXT_PAGE) {
                if (this.hasNext()) {
                    this.setItem(entry.getValue().getSlot(), itemBuilder);
                }
            } else if (entry.getKey() == ConfigHome.DataItem.HIDE) {
                this.fill(itemBuilder);
            } else {
                this.setItem(entry.getValue().getSlot(), itemBuilder);
            }
        }
    }

    @Override
    public void handleMenu(MenuEvent event) {
        Player player = event.getPlayer();
        MenuUser menuUser = event.getMenuUser();
        int slot = event.getSlot();

        if (slot == 48) {
            this.prevPage();
        } else if (slot == 49) {
            player.closeInventory();
        } else if (slot == 50) {
            this.nextPage();
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
                    menuUser.sendLang("home.tp.invalid-location");
                    return;
                }

                PlayerTeleport teleport = PlayerTeleport.of(player, loc)
                        .setMessage(language.getString("clan","home.tp.success").replace("%home%", home.getName()))
                        .setSound(config.getSound())
                        .setParticle(config.getParticle())
                        .setTeleportAnimation(config.getAnimation())
                        .setDelayTicks(config.getDelayTeleport())
                        .setNoDelayPermission(config.getPermissionBypass())
                        .setMessageInTeleport(language.getString("clan", "home.tp.teleporting"), language.getString("clan", "home.tp.teleported"))
                        .setSoundInTeleport(config.getSoundInTeleport());

                teleport.teleportOf(this::closeInventory, () -> {this.closeInventory();menuUser.sendLang("home.tp.error", ParseVariable.adD("%error%", teleport.getErrorMessage()));});

            } else if (event.getClick() == ClickType.LEFT) {
                if (!clan.isLader(player.getUniqueId())) {
                    menuUser.sendLang("not-leader");
                    return;
                }
                openMenu(new HomeIconMenu(player, home));
            } else if (event.getClick() == ClickType.SHIFT_LEFT) {
                if (!clan.isLader(player.getUniqueId())) {
                    menuUser.sendLang("not-leader");
                    return;
                }
                clan.removeHome(home);
                menuUser.sendLang("home.delete.success", ParseVariable.adD("%name%", name));
                refreshData();
            }
        } else if (get(slot) != null) {
            switch (get(slot)) {
                case CLOSE -> this.closeInventory();
                case NEXT_PAGE -> this.nextPage();
                case PREVIOUS_PAGE -> this.prevPage();
                default -> {}
            }
        } else {
            for (var itemData : configHome.getOtherItems()) {
                if (itemData.getSlot(slot) && itemData.hasActions()) {
                    executeActions(player, itemData.getActions());
                    break;
                }
            }
        }
    }

    public ConfigHome.DataItem get(int slot) {
        for (var entry : configHome.getDefaultItems().entrySet()) {
            if (entry.getValue().getSlot(slot)) {
                return entry.getKey();
            }
        }
        return null;
    }
}