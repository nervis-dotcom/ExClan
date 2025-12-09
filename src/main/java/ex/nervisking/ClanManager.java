package ex.nervisking;

import ex.nervisking.models.Clan;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ClanManager {

    private final Map<String, Clan> clanData;
    private final List<String> deleteClanData;

    public ClanManager() {
        this.clanData = new HashMap<>();
        this.deleteClanData = new ArrayList<>();
    }

    public Map<String, Clan> getClanData() {
        return clanData;
    }

    public void addClan(String clanName, Clan clan) {
        clanData.put(clanName, clan);
        deleteClanData.remove(clanName);
    }

    public void removeClan(String clanName) {
        clanData.remove(clanName);
        deleteClanData.add(clanName);
    }

    public List<String> getDeleteClanData() {
        return deleteClanData;
    }

    public boolean hasClan(String clanName) {
        return clanData.containsKey(clanName);
    }

    public @Nullable Clan getClan(UUID uuid) {
        for (var clan : clanData.values()) {
            if (clan.hasMemberOf(uuid)) {
                return clan;
            }
        }
        return null;
    }

    public @Nullable Clan getClan(String name) {
        return clanData.get(name);
    }

    public boolean isInClan(UUID uuid) {
        return getClan(uuid) != null;
    }

    public Collection<? extends Player> getOnlineAll(@NotNull Clan clan) {
        List<Player> members = new ArrayList<>(clan.getOnlineAll());
        for (var allys : clan.getAllys()) {
            var ally = getClan(allys);
            if (ally == null) {
                continue;
            }
            members.addAll(ally.getOnlineAll());
        }
        return members;
    }

    public boolean hasAllyPlayer(@NotNull Clan clan, UUID uuid) {
        for (var allys : clan.getAllys()) {
            var ally = getClan(allys);
            return ally != null && ally.hasMemberOf(uuid);
        }
        return false;
    }
}