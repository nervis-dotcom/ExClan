package ex.nervisking.models;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Member {

    private final UUID uuid;
    private Rank rank;

    public Member(UUID uuid, Rank rank) {
        this.uuid = uuid;
        this.rank = rank;
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

    public String getName() {
        OfflinePlayer player = getOfflinePlayer();
        return player.hasPlayedBefore() ? player.getName() : "Unknown Player";
    }

    public Player getPlayer() {
        OfflinePlayer player = getOfflinePlayer();
        return (player.hasPlayedBefore() && player.isOnline()) ? player.getPlayer() : null;
    }

    private @NotNull OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }
}