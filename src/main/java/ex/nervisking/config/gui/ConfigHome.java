package ex.nervisking.config.gui;

import ex.api.base.config.ConfigInfo;
import ex.api.base.config.CustomItem;
import ex.api.base.config.Yaml;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@ConfigInfo(name = "Home", folder = "Gui", register = false)
public class ConfigHome extends Yaml {

    private final Map<DataItem, CustomItem> defaultItems = new EnumMap<>(DataItem.class);
    private List<CustomItem> otherItems = new ArrayList<>();
    private String title;
    private int rows;
    private List<Integer> slots;

    public ConfigHome() {
        this.load();
    }

    @Override
    protected void load() {
        this.defaultItems.clear();
        this.otherItems.clear();

        var config = customConfig.getConfig();

        this.title = config.getString("title", "");
        this.rows = config.getInt("rows", 6);
        this.slots = customConfig.parseSlotList("slots", 10);

        // Cargar DEFAULT
        ConfigurationSection defaultSection = config.getConfigurationSection("items.default");
        if (defaultSection != null) {
            for (String key : defaultSection.getKeys(false)) {
                ConfigurationSection section = defaultSection.getConfigurationSection(key);
                if (section == null) continue;
                DataItem dataItem = DataItem.getDataItem(key.toLowerCase());
                if (dataItem != null) {
                    CustomItem builder = customConfig.createItem("items.default." + key);
                    this.defaultItems.put(dataItem, builder);
                }
            }
        }

        // Cargar OTROS
        if (config.contains("items.others")) {
            otherItems = customConfig.createItemListOf("items.others");
        }
    }

    public String getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public Map<DataItem, CustomItem> getDefaultItems() {
        return defaultItems;
    }

    public CustomItem getItem(DataItem dataItem) {
        return defaultItems.get(dataItem);
    }

    public List<CustomItem> getOtherItems() {
        return otherItems;
    }

    public enum DataItem {

        HOMES("homes"),
        HIDE("hide"),
        PREVIOUS_PAGE("previous-page"),
        NEXT_PAGE("next-page"),
        CLOSE("close");

        private final String name;

        DataItem(String name) {
            this.name = name;
        }

        public static @Nullable DataItem getDataItem(String name) {
            for (DataItem dataItem : DataItem.values()) {
                if (dataItem.getName().equalsIgnoreCase(name)) {
                    return dataItem;
                }
            }

            return null;
        }

        public String getName() {
            return name;
        }
    }
}