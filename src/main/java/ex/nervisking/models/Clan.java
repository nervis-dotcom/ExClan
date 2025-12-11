package ex.nervisking.models;

import ex.api.base.gui.Row;
import ex.api.base.gui.trunk.VirtualChest;
import ex.api.base.model.Coordinate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class Clan {

    private Lader lader;
    private final String clanName;
    private final List<Member> members;
    private final Set<UUID> bannedMembers;
    private final Set<String> allys;
    private final Map<Rank, Symbols> symbols;
    private final List<Homes> homes;
    private final VirtualChest chest;
    private String clanTag;
    private int points;
    private int kills;
    private final AtomicLong bank = new AtomicLong(0);
    private String description;
    private String discord;
    private ItemStack icon;
    private boolean pvp;
    private boolean pvpAlly;

    public Clan(String clanName, String clanTag, String leaderName, UUID laderUuid, List<Member> members, Set<UUID> bannedMembers, Set<String> allys, List<Homes> homes, int points, int kills, String description, String discord, boolean pvp, boolean pvpAlly, Map<Rank, Symbols> symbols, Map<Integer, ItemStack> chestItems, ItemStack icon) {
        this.clanName = clanName;
        this.lader = new Lader(laderUuid, leaderName);
        this.members = members;
        this.bannedMembers = bannedMembers;
        this.allys = allys;
        this.homes = homes;
        this.clanTag = clanTag;
        this.points = points;
        this.kills = kills;
        this.description = description;
        this.discord = discord;
        this.pvp = pvp;
        this.pvpAlly = pvpAlly;
        this.symbols = symbols;
        this.chest = VirtualChest.of("Clan Chest", Row.CHESTS_54, new HashMap<>(chestItems));
        this.icon = icon;
    }

    public Clan(String clanName, String leaderName, UUID laderUuid) {
        this(clanName, clanName, leaderName, laderUuid, new ArrayList<>(), new HashSet<>(), new HashSet<>(), new ArrayList<>(), 0, 0, null, null, true, true, Rank.getSymbols(), new HashMap<>(), null);
    }

    public void setDelegate(UUID uuid, String name) {
        this.lader = new Lader(uuid, name);
        this.removeMember(uuid);
        this.addMember(uuid, Rank.SUB_LEADER);
    }

    public String getClanName() {
        return clanName;
    }

    public String getClanTag() {
        return clanTag;
    }

    public void setClanTag(String clanTag) {
        this.clanTag = clanTag;
    }

    public Lader getLader() {
        return lader;
    }

    public String getLeaderName() {
        return getLader().getName();
    }

    public UUID getLaderUuid() {
        return getLader().getUuid();
    }

    public void upateName(String name) {
        this.lader.setName(name);
    }

    public boolean isLader(UUID uuid) {
        return this.lader.getUuid().equals(uuid);
    }

    public boolean isManager(UUID uuid) {
        return this.isLader(uuid) || this.hasMemberSubLider(uuid);
    }

    public boolean isManagerOnline() {
        if (lader.isOnline()) {
            return true;
        }
        for (var member : this.members) {
            if (member.isOnline()) {
                return true;
            }
        }
        return false;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void removePoints(int amount) {
        this.points = Math.max(0, this.points - amount); // evitar negativos
    }

    public int getKills() {
        return kills;
    }

    public void addKills(int kills) {
        this.kills += kills;
    }

    public List<Member> getMembers() {
        return members;
    }

    public Member getMember(UUID uuid) {
        Member rank = null;
        if (this.isLader(uuid)) {
            return lader;
        }
        for (var member : this.members) {
            if (member.getUuid().equals(uuid)) {
                rank = member;
                break;
            }
        }
        return rank;
    }

    public void setMemberRank(UUID member, Rank rank) {
        this.members.stream().filter(m -> m.getUuid().equals(member)).findFirst().ifPresent(m -> m.setRank(rank));
    }

    public void addMember(UUID member, Rank rank) {
        this.members.add(new Member(member, rank));
    }

    public void removeMember(UUID member) {
        this.members.removeIf(m -> m.getUuid().equals(member));
    }

    public boolean hasMember(UUID uuid) {
        return this.members.stream().anyMatch(m -> m.getUuid().equals(uuid));
    }

    public boolean hasMemberOf(UUID uuid) {
      return hasMember(uuid) || isLader(uuid);
    }

    public boolean hasMemberSubLider(UUID uuid) {
        return this.members.stream().anyMatch(m -> m.getUuid().equals(uuid) && m.getRank() == Rank.SUB_LEADER);
    }

    public Rank getMemberRank(UUID uuid) {
        Rank rank = null;
        for (Member member : this.members) {
            if (member.getUuid().equals(uuid)) {
                rank = member.getRank();
                break;
            }
        }
        return rank;
    }

    public Set<UUID> getBannedMembers() {
        return bannedMembers;
    }

    public boolean isBanned(UUID member) {
        return this.bannedMembers.contains(member);
    }

    public void banMember(UUID member) {
        this.bannedMembers.add(member);
    }

    public void unbanMember(UUID member) {
        this.bannedMembers.remove(member);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<? extends Player> getOnlineAll() {
        List<Player> members = new ArrayList<>();
        for (var member : this.members) {
            Player player = member.getPlayer();
            if (player != null && player.isOnline()) {
                members.add(player);
            }
        }
        Player player = Bukkit.getPlayer(this.lader.getUuid());
        if (player != null && player.isOnline()) {
            members.add(player);
        }
        return members;
    }

    public String getDiscord() {
        return discord;
    }

    public void setDiscord(String discord) {
        this.discord = discord;
    }

    public boolean hasDiscord() {
        return this.discord != null;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public boolean isPvp() {
        return pvp;
    }

    public Set<String> getAllys() {
        return allys;
    }

    public void addAlly(String ally) {
        this.allys.add(ally);
    }

    public void removeAlly(String ally) {
        this.allys.remove(ally);
    }

    public boolean isAlly(String ally) {
        return this.allys.contains(ally);
    }

    public boolean isPvpAlly() {
        return pvpAlly;
    }

    public void setPvpAlly(boolean pvpAlly) {
        this.pvpAlly = pvpAlly;
    }

    public Map<Rank, Symbols> getSymbols() {
        return symbols;
    }

    public void setSymbols(Rank rank, Symbols symbol) {
        this.symbols.put(rank, symbol);
    }

    public Symbols getSymbol(UUID uuid) {
        if (isLader(uuid)) {
            return symbols.get(Rank.LEADER);
        }
        for (var member : this.members) {
            if (member.getUuid().equals(uuid)) {
                return symbols.get(member.getRank());
            }
        }
        return null;
    }

    public Symbols getSymbol(Rank rank) {
        return symbols.get(rank);
    }

    public VirtualChest getChest() {
        return chest;
    }

    /** Obtiene el banco en centavos */
    public long getBank() {
        return bank.get();
    }

    /** Obtiene el banco como double */
    public double getBankDouble() {
        return bank.get() / 100.0;
    }

    /** Set directo del banco en centavos (uso interno) */
    public void setBank(long amount) {
        bank.set(Math.max(amount, 0));
    }

    /** Depositar en centavos (seguro y atómico) */
    public long deposit(long amountCents) {
        if (amountCents <= 0) return bank.get();
        return bank.addAndGet(amountCents);
    }

    /** Retirar en centavos (seguro y atómico) */
    public long withdraw(long amountCents) {
        if (amountCents <= 0) return bank.get();

        long current, updated;

        do {
            current = bank.get();
            if (current < amountCents) return current; // No hay suficiente

            updated = current - amountCents;

            // Intentar cambiar (compare-and-set)
        } while (!bank.compareAndSet(current, updated));

        return updated;
    }

    /** Depositar usando double */
    public double depositDouble(double amount) {
        long cents = toCents(amount);
        return deposit(cents) / 100.0;
    }

    /** Retirar usando double */
    public double withdrawDouble(double amount) {
        long cents = toCents(amount);
        return withdraw(cents) / 100.0;
    }

    /** Conversión segura */
    private long toCents(double amount) {
        if (amount <= 0) return 0;
        return Math.round(amount * 100.0); // redondeo seguro
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public boolean hasIcon() {
        return this.icon != null;
    }

    public void addHome(String name, Coordinate coordinate) {
        this.homes.add(new Homes(name, "FIREWORK_STAR", coordinate));
    }

    public void removeHome(Homes home) {
        this.homes.remove(home);
    }

    public List<Homes> getHomes() {
        return homes;
    }

    public Homes getHome(String s) {
        for (Homes h : homes) {
            if (h.getName().equals(s)) {
                return h;
            }
        }
        return null;
    }
}