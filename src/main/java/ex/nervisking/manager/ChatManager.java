package ex.nervisking.manager;

import ex.nervisking.models.Chat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager {

    private final Map<UUID, Chat> playerChat;

    public ChatManager() {
        this.playerChat = new HashMap<>();
    }

    public void setChat(UUID uuid, Chat chat) {
        this.playerChat.put(uuid, chat);
    }

    public Chat getChat(UUID uuid) {
        return this.playerChat.get(uuid);
    }

    public void removeChat(UUID uuid) {
        this.playerChat.remove(uuid);
    }
}