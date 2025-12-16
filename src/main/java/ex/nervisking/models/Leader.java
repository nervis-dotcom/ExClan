package ex.nervisking.models;

import ex.nervisking.models.chat.Chat;

import java.util.UUID;

public class Leader extends Member {

    private String name;

    public Leader(UUID uuid, String name, Chat chat, boolean pvp) {
        super(uuid, Rank.LEADER, chat, pvp);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}