package ex.nervisking.config.gui;

import ex.api.base.config.ConfigInfo;
import ex.api.base.config.CustomItem;
import ex.api.base.config.Yaml;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@ConfigInfo(name = "HomeIcon", folder = "Gui", register = false)
public class ConfigHomeIcon extends Yaml {

    private final Map<DataItem, CustomItem> defaultItems = new EnumMap<>(DataItem.class);
    private List<CustomItem> otherItems = new ArrayList<>();
    private String title;
    private int rows;
    private List<Integer> slots;

    public ConfigHomeIcon() {
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
                    this.defaultItems.put(dataItem, customConfig.createItem("items.default." + key));
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

        ICONS("icons"),
        HIDE("hide"),
        PREVIOUS_PAGE("previous-page"),
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