package ex.nervisking.models;

import ex.api.base.model.Coordinate;

public class Homes {

    private final String name;
    private String icon;
    private final Coordinate coordinate;

    public Homes(String name, String icon, Coordinate coordinate) {
        this.name = name;
        this.icon = icon;
        this.coordinate = coordinate;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}