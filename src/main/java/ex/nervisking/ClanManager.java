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

    public List<Clan> getTopClans() {
        return clanData.values()
                .stream()
                .sorted(Comparator.comparingInt(Clan::getPoints).reversed())
                .toList();
    }

    public @Nullable Clan getClanByPosition(int position) {
        if (position <= 0) return null;

        List<Clan> sorted = getTopClans();

        if (position > sorted.size()) {
            return null; // No existe ese puesto
        }

        return sorted.get(position - 1); // index 0 = top 1
    }

    public List<String> getTopListFormatted(int limit) {
        List<Clan> top = getTopClans();
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < Math.min(limit, top.size()); i++) {
            Clan clan = top.get(i);
            lines.add((i + 1) + ". " + clan.getClanName() + " - " + clan.getPoints() + " puntos");
        }

        return lines;
    }

    public int getClanPosition(String clanName) {
        List<Clan> top = getTopClans();
        for (int i = 0; i < top.size(); i++) {
            if (top.get(i).getClanName().equalsIgnoreCase(clanName)) {
                return i + 1;
            }
        }
        return -1; // no encontrado
    }

    public List<Clan> getTopClansByKills() {
        return clanData.values()
                .stream()
                .sorted(Comparator.comparingInt(Clan::getKills).reversed())
                .toList();
    }

    public @Nullable Clan getClanByKillsPosition(int position) {
        if (position <= 0) return null;

        List<Clan> sorted = getTopClansByKills();

        if (position > sorted.size()) {
            return null;
        }

        return sorted.get(position - 1); // index 0 = top 1
    }

    public int getClanKillsPosition(String clanName) {
        List<Clan> top = getTopClansByKills();
        for (int i = 0; i < top.size(); i++) {
            if (top.get(i).getClanName().equalsIgnoreCase(clanName)) {
                return i + 1;
            }
        }
        return -1; // no encontrado
    }

    /**
     * TOP de clanes ordenado por dinero en el banco.
     */
    public List<Clan> getTopClansByBank() {
        return clanData.values()
                .stream()
                .sorted(Comparator.comparingLong(Clan::getBank).reversed())
                .toList();
    }

    /**
     * Obtener un clan por posición en el TOP de dinero.
     * position = 1 -> Top 1
     */
    public @Nullable Clan getClanByBankPosition(int position) {
        if (position <= 0) return null;

        List<Clan> sorted = getTopClansByBank();

        if (position > sorted.size()) {
            return null; // No existe ese puesto
        }

        return sorted.get(position - 1);
    }

    /**
     * Obtener la posición del clan en el TOP de dinero.
     * Retorna -1 si no existe.
     */
    public int getClanBankPosition(String clanName) {
        List<Clan> top = getTopClansByBank();
        for (int i = 0; i < top.size(); i++) {
            if (top.get(i).getClanName().equalsIgnoreCase(clanName)) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Lista formateada del TOP de dinero.
     */
    public List<String> getTopBankListFormatted(int limit) {
        List<Clan> top = getTopClansByBank();
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < Math.min(limit, top.size()); i++) {
            Clan clan = top.get(i);
            lines.add((i + 1) + ". " + clan.getClanName() + " - $" + clan.getBank());
        }

        return lines;
    }
}