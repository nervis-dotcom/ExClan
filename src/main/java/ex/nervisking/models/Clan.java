package ex.nervisking.models;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Clan {

    private String leaderName;
    private UUID laderUuid;
    private final String clanName;
    private final List<Member> members;
    private final Set<UUID> bannedMembers;
    private String clanTag;
    private int points;
    private String description;
    private String discordWebhooks;

    public Clan(String clanName, String clanTag, String leaderName, UUID laderUuid, List<Member> members, Set<UUID> bannedMembers, int points, String description, String discordWebhooks) {
        this.clanName = clanName;
        this.leaderName = leaderName;
        this.laderUuid = laderUuid;
        this.members = members;
        this.bannedMembers = bannedMembers;
        this.clanTag = clanTag;
        this.points = points;
        this.description = description;
        this.discordWebhooks = discordWebhooks;

    }

    public void setDelegate(UUID uuid, String name) {
        this.leaderName = name;
        this.laderUuid = uuid;
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

    public String getLeaderName() {
        return leaderName;
    }


    public UUID getLaderUuid() {
        return laderUuid;
    }

    public boolean isLader(UUID uuid) {
        return this.laderUuid.equals(uuid);
    }

    public boolean isManager(UUID uuid) {
        return this.isLader(uuid) || this.hasMemberSubLider(uuid);
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

    public List<Member> getMembers() {
        return members;
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

    public void addMember(Member member) {
        this.members.add(member);
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
        return this.members.stream().filter(m -> m.getUuid().equals(uuid)).findFirst().map(Member::getRank).orElse(Rank.MEMBER);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Player> getOnlineAll() {
        List<Player> members = new ArrayList<>();
        for (var member : this.members) {
            Player player = member.getPlayer();
            if (player != null && player.isOnline()) {
                members.add(player);
            }
        }
        Player player = Bukkit.getPlayer(this.laderUuid);
        if (player != null && player.isOnline()) {
            members.add(player);
        }
        return members;
    }

    public String getDiscordWebhooks() {
        return discordWebhooks;
    }

    public void setDiscordWebhooks(String discordWebhooks) {
        this.discordWebhooks = discordWebhooks;
    }


    public boolean isPvp() {
        return false;
    }
}