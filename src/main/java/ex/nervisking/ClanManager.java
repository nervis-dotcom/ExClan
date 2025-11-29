package ex.nervisking;

import ex.nervisking.models.Clan;

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

    public Clan getClan(UUID uuid) {
        for (var clan : clanData.values()) {
            if (clan.hasMemberOf(uuid)) {
                return clan;
            }
        }
        return null;
    }

    public Clan getClan(String name) {
        return clanData.get(name);
    }

    public boolean isInClan(UUID uuid) {
        return getClan(uuid) != null;
    }
}