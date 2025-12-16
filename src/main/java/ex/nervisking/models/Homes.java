package ex.nervisking.models;

import ex.api.base.model.Coordinate;
import org.bukkit.Material;

public class Homes {

    private final String name;
    private Material icon;
    private final Coordinate coordinate;

    public Homes(String name, Material icon, Coordinate coordinate) {
        this.name = name;
        this.icon = icon;
        this.coordinate = coordinate;
    }

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}