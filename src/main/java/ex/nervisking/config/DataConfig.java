package ex.nervisking.config;

import ex.api.base.annotations.KeyAlphaNum;
import ex.api.base.config.FolderConfig;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import ex.nervisking.models.Member;
import ex.nervisking.models.Rank;
import ex.nervisking.models.Symbols;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class DataConfig extends FolderConfig<ExClan> {

    @Override
    public @KeyAlphaNum String folderName() {
        return "Clans";
    }

    @Override
    public void loadConfigs() {
        for (var  configFile : configFiles) {
            FileConfiguration config = configFile.getConfig();

            String clanPath = "data";
            if (config.isConfigurationSection(clanPath)) {
                String clanId = config.getString(clanPath + ".name");
                String clanTag = config.getString(clanPath + ".tag");
                String leaderName = config.getString(clanPath + ".leader-name");
                UUID leaderUuid = UUID.fromString(config.getString(clanPath + ".leader-uuid", ""));
                int points = config.getInt(clanPath + ".points", 0);
                String description = config.getString(clanPath + ".description", "");
                String discordWebhooks = config.getString(clanPath + ".discord-webhooks");
                boolean pvp = config.getBoolean(clanPath + ".pvp");
                boolean pvpAlly = config.getBoolean(clanPath + ".pvp-ally");

                // 🔹 Miembros
                List<Member> members = new ArrayList<>();
                ConfigurationSection membersSection = config.getConfigurationSection(clanPath + ".members");
                if (membersSection != null) {
                    for (String key : membersSection.getKeys(false)) {
                        try {
                            UUID memberUuid = UUID.fromString(key);
                            Rank rank = Rank.fromString(config.getString(clanPath + ".members." + key + ".rank", "member").toUpperCase());
                            members.add(new Member(memberUuid, rank != null ? rank : Rank.MEMBER));
                        } catch (IllegalArgumentException e) {
                            logger.warn("Invalid UUID format for member: " + key);
                        }
                    }
                }

                // 🔹 Baneados
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

                // 🔹 Crear clan
                plugin.getClanManager().addClan(clanId, new Clan(clanId, clanTag, leaderName, leaderUuid, members, bannedMembers, allys, points, description, discordWebhooks, pvp, pvpAlly, symbols));
            }
        }
    }


    @Override
    public void saveConfigs() {
        for (var clan : plugin.getClanManager().getClanData().values()) {

            var customConfig = getConfigFileOrCreate(clan.getClanName());
            FileConfiguration config = customConfig.getConfig();

            // 🔹 Resetear sección
            config.set("data", null);

            String clanPath = "data.";
            config.set(clanPath + "name", clan.getClanName());
            config.set(clanPath + "tag", clan.getClanTag());
            config.set(clanPath + "leader-name", clan.getLeaderName());
            config.set(clanPath + "leader-uuid", clan.getLaderUuid().toString());
            config.set(clanPath + "points", clan.getPoints());
            config.set(clanPath + "description", clan.getDescription());
            config.set(clanPath + "discord-webhooks", clan.getDiscord());

            // 🔹 Miembros
            if (clan.getMembers() != null && !clan.getMembers().isEmpty()) {
                for (Member member : clan.getMembers()) {
                    if (member != null && member.getUuid() != null) {
                        String memberPath = clanPath + "members." + member.getUuid();
                        config.set(memberPath + ".rank", member.getRank() != null ? member.getRank().name() : Rank.MEMBER.name());
                    }
                }
            }

            // 🔹 Baneados
            if (clan.getBannedMembers() != null && !clan.getBannedMembers().isEmpty()) {
                List<String> bannedList = clan.getBannedMembers().stream().filter(Objects::nonNull).map(UUID::toString).toList();
                config.set(clanPath + "banned-members", new ArrayList<>(bannedList));
            }

            // 🔹 Allies
            if (clan.getAllys() != null && !clan.getAllys().isEmpty()) {
                List<String> allyList = clan.getAllys().stream().filter(Objects::nonNull).toList();
                config.set(clanPath + "allys", new ArrayList<>(allyList));
            }

            // 🔹 Símbolos
            if (clan.getSymbols() != null && !clan.getSymbols().isEmpty()) {
                for (var entry : clan.getSymbols().entrySet()) {
                    config.set(clanPath + "symbols." + entry.getKey().name(), entry.getValue().name());
                }
            }
        }

        for (var delete : plugin.getClanManager().getDeleteClanData()) {
            this.removeFile(delete);
        }
        saveConfigFiles();
    }
}