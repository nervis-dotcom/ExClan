package ex.nervisking.models;

import ex.nervisking.models.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Member {

    private final UUID uuid;
    private Rank rank;
    private boolean pvp;
    private Chat chat;

    public Member(UUID uuid, Rank rank, Chat chat, boolean pvp) {
        this.uuid = uuid;
        this.rank = rank;
        this.chat = chat;
        this.pvp = pvp;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public @Nullable String getName() {
        OfflinePlayer player = getOfflinePlayer();
        return player.hasPlayedBefore() ? player.getName() : null;
    }

    public @Nullable Player getPlayer() {
        OfflinePlayer player = getOfflinePlayer();
        return (player.hasPlayedBefore() && player.isOnline()) ? player.getPlayer() : null;
    }

    private @NotNull OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}