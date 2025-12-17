package ex.nervisking.config;

import ex.api.base.Ex;
import ex.api.base.annotations.KeyAlphaNum;
import ex.api.base.config.FolderConfig;
import ex.api.base.model.Coordinate;
import ex.nervisking.ExClan;
import ex.nervisking.models.*;
import ex.nervisking.models.chat.Chat;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DataConfig extends FolderConfig<ExClan> {

    @Override
    public @KeyAlphaNum String folderName() {
        return "Clans";
    }

    @Override
    public void loadConfigs() {
        for (var configFile : configFiles) {
            FileConfiguration config = configFile.getConfig();

            String clanPath = "data";
            if (config.isConfigurationSection(clanPath)) {
                String clanId = configFile.getFileName();
                String clanTag = config.getString(clanPath + ".tag");
                String leaderName = config.getString(clanPath + ".leader-name");
                boolean leaderPvp = config.getBoolean(clanPath + ".leader-pvp");
                Chat leaderChat = Chat.fromString(config.getString(clanPath + ".leader-chat"));
                UUID leaderUuid = UUID.fromString(config.getString(clanPath + ".leader-uuid", ""));

                Leader leader = new Leader(leaderUuid, leaderName, leaderChat, leaderPvp);
                int points = config.getInt(clanPath + ".points", 0);
                int kills = config.getInt(clanPath + ".kills", 0);
                String description = config.getString(clanPath + ".description", "");
                String discordWebhooks = config.getString(clanPath + ".discord-webhooks");
                boolean pvp = config.getBoolean(clanPath + ".pvp");
                boolean pvpAlly = config.getBoolean(clanPath + ".pvp-ally");

                List<Member> members = new ArrayList<>();
                ConfigurationSection membersSection = config.getConfigurationSection(clanPath + ".members");
                if (membersSection != null) {
                    for (String key : membersSection.getKeys(false)) {
                        try {
                            UUID memberUuid = UUID.fromString(key);
                            Rank rank = Rank.fromString(config.getString(clanPath + ".members." + key + ".rank", "member").toUpperCase());
                            boolean memberPvp = config.getBoolean(clanPath + ".members." + key + ".pvp");
                            Chat chat = Chat.fromString(config.getString(clanPath + ".members." + key + ".chat"));
                            members.add(new Member(memberUuid, rank != null ? rank : Rank.MEMBER, chat, memberPvp));
                        } catch (IllegalArgumentException e) {
                            logger.warn("Invalid UUID format for member: " + key);
                        }
                    }
                }

                Set<UUID> bannedMembers = new HashSet<>();
                if (config.isList(clanPath + ".banned-members")) {
                    for (String uuidString : config.getStringList(clanPath + ".banned-members")) {
                        try {
                            bannedMembers.add(UUID.fromString(uuidString));
                        } catch (IllegalArgumentException e) {
                            logger.warn("Invalid UUID format in banned members: " + uuidString);
                        }
                    }
                }

                Set<String> allys = new HashSet<>();
                if (config.isList(clanPath + ".allys")) {
                    allys.addAll(config.getStringList(clanPath + ".allys"));
                }

                Map<Rank, Symbols> symbols = new HashMap<>();
                ConfigurationSection symbolsSection = config.getConfigurationSection(clanPath + ".symbols");
                if (symbolsSection != null) {
                    for (String key : symbolsSection.getKeys(false)) {
                        try {
                            Rank rank = Rank.fromString(key.toUpperCase());
                            Symbols symbol = Symbols.fromString(symbolsSection.getString(key));
                            if (symbol == null) continue;
                            symbols.put(rank, symbol);
                        } catch (IllegalArgumentException e) {
                            logger.warn("Invalid rank format for symbol: " + key);
                        }
                    }
                }

                Map<Integer, ItemStack> chestItems = new HashMap<>();
                ConfigurationSection chestItemsSection = config.getConfigurationSection(clanPath + ".chest-items");
                if (chestItemsSection != null) {
                    for (String key : chestItemsSection.getKeys(false)) {
                        try {
                            int slot = Integer.parseInt(key);
                            ItemStack item = chestItemsSection.getItemStack(key);
                            if (item != null) {
                                chestItems.put(slot, item);
                            }
                        } catch (NumberFormatException e) {
                            logger.warn("Invalid slot format for chest item: " + key);
                        }
                    }
                }

                List<Homes> homes = new ArrayList<>();
                ConfigurationSection homesSection = config.getConfigurationSection(clanPath + ".homes");
                if (homesSection != null) {
                    for (String key : homesSection.getKeys(false)) {
                        var icon = Ex.getMaterial(homesSection.getString(key + ".icon"), Material.FIREWORK_STAR);
                        String world = homesSection.getString(key + ".location.world");
                        double x = homesSection.getDouble(key + ".location.x");
                        double y = homesSection.getDouble(key + ".location.y");
                        double z = homesSection.getDouble(key + ".location.z");
                        float yaw = homesSection.getInt(key + ".location.yaw");
                        float pitch = homesSection.getInt(key + ".location.pitch");
                        homes.add(new Homes(key, icon, Coordinate.of(world, x, y, z, yaw, pitch)));
                    }
                }

                ItemStack icon = config.getItemStack(clanPath + ".icon");

                Clan clan = new Clan(clanId, clanTag, leader, members, bannedMembers, allys, homes, points, kills, description, discordWebhooks, pvp, pvpAlly, symbols, chestItems, icon);

                clan.setBank(config.getLong(clanPath + ".bank", 0));
                plugin.getClanManager().addClan(clanId, clan);
            }
        }
    }

    @Override
    public void saveConfigs() {
        for (var clan : plugin.getClanManager().getClanData().values()) {

            var customConfig = getConfigFileOrCreate(clan.getClanName());
            FileConfiguration config = customConfig.getConfig();

            config.set("data", null);

            String clanPath = "data.";
            config.set(clanPath + "tag", clan.getClanTag());
            config.set(clanPath + "leader-name", clan.getLeaderName());
            config.set(clanPath + "leader-uuid", clan.getLaderUuid().toString());
            config.set(clanPath + "leader-pvp", clan.getLader().isPvp());
            config.set(clanPath + "leader-chat", clan.getLader().getChat() != null ? clan.getLader().getChat().name() : Chat.NONE.name());
            config.set(clanPath + "points", clan.getPoints());
            config.set(clanPath + "kills", clan.getKills());
            config.set(clanPath + "bank", clan.getBank());
            config.set(clanPath + "description", clan.getDescription());
            config.set(clanPath + "discord-webhooks", clan.getDiscord());
            config.set(clanPath + "pvp", clan.isPvp());
            config.set(clanPath + "pvp-ally", clan.isPvpAlly());
            config.set(clanPath + "icon", clan.getIcon());

            if (clan.getMembers() != null && !clan.getMembers().isEmpty()) {
                for (Member member : clan.getMembers()) {
                    if (member != null && member.getUuid() != null) {
                        String memberPath = clanPath + "members." + member.getUuid();
                        config.set(memberPath + ".rank", member.getRank() != null ? member.getRank().name() : Rank.MEMBER.name());
                        config.set(memberPath + ".pvp", member.isPvp());
                        config.set(memberPath + ".chat", member.getChat() != null ? member.getChat().name() : Chat.NONE.name());
                    }
                }
            }

            if (clan.getBannedMembers() != null && !clan.getBannedMembers().isEmpty()) {
                List<String> bannedList = clan.getBannedMembers().stream().filter(Objects::nonNull).map(UUID::toString).toList();
                config.set(clanPath + "banned-members", new ArrayList<>(bannedList));
            }

            if (clan.getAllys() != null && !clan.getAllys().isEmpty()) {
                List<String> allyList = clan.getAllys().stream().filter(Objects::nonNull).toList();
                config.set(clanPath + "allys", new ArrayList<>(allyList));
            }

            if (clan.getSymbols() != null && !clan.getSymbols().isEmpty()) {
                for (var entry : clan.getSymbols().entrySet()) {
                    config.set(clanPath + "symbols." + entry.getKey().name(), entry.getValue().name());
                }
            }

            ConfigurationSection homesSection = config.createSection(clanPath + ".homes");
            List<Homes> homes = clan.getHomes();
            if (homes != null && !homes.isEmpty()) {
                for (Homes home : homes) {
                    homesSection.set(home.getName() + ".icon", home.getIcon().name());
                    ConfigurationSection locSec = homesSection.createSection(home.getName() + ".location");
                    Coordinate loc = home.getCoordinate();
                    locSec.set("world", loc.world());
                    locSec.set("x", loc.x());
                    locSec.set("y", loc.y());
                    locSec.set("z", loc.z());
                    locSec.set("yaw", loc.yaw());
                    locSec.set("pitch", loc.pitch());
                }
            }

            if (clan.getChest() != null) {
                var chestItems = clan.getChest().getStorage();
                for (var entry : chestItems.entrySet()) {
                    config.set(clanPath + "chest-items." + entry.getKey(), entry.getValue());
                }
            }
        }

        for (var delete : plugin.getClanManager().getDeleteClanData()) {
            this.removeFile(delete);
        }
        saveConfigFiles();
    }
}