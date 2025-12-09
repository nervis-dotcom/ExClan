package ex.nervisking.models;

import java.util.UUID;

public class Lader extends Member {

    private String name;

    public Lader(UUID uuid, String name) {
        super(uuid, Rank.LEADER);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}