package ex.nervisking.hook;

import ex.api.base.hook.PlaceholderApiHook;
import ex.api.base.placeholder.Identifier;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.models.Clan;
import ex.nervisking.models.Rank;
import ex.nervisking.models.Symbols;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Papi extends PlaceholderApiHook {

    private final ClanManager clanManager;

    public Papi(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public Identifier register(@Nullable Player player, @NotNull String string, @NotNull Identifier identifier) {
        String[] parts = string.toLowerCase().split("_");

        // EJEMPLOS:
        // player_clan
        // clan_excalibur_points
        // top_points_excalibur

        if (parts.length < 1) {
            return identifier.nulL();
        }

        String type = parts[0];
        String arg = (parts.length >= 2 ? parts[1] : null);
        String extra = (parts.length >= 3 ? parts[2] : null);

        switch (type) {

            // %exclan_player_[args]%
            case "player" -> {
                if (player == null) return identifier.nulL();

                Clan clan = clanManager.getClan(player.getUniqueId());
                if (clan == null) return identifier.set("sin clan");

                if (arg == null) return identifier.nulL();

                return switch (arg) {
                    case "symbol-icon" -> identifier.set(clan.getSymbol(player.getUniqueId()).getSymbol());
                    case "symbol-name" -> identifier.set(clan.getSymbol(player.getUniqueId()).getName());

                    case "clan" -> identifier.set(clan.getClanName());
                    case "tag" -> identifier.set(clan.getClanTag());
                    case "discord" -> identifier.set(clan.getDiscord());
                    case "rank" -> identifier.set(clan.getMemberRank(player.getUniqueId()).name());

                    case "points" -> identifier.set(clan.getPoints());
                    case "kills" -> identifier.set(clan.getKills());
                    case "bank" -> identifier.set(clan.getBankDouble());

                    case "top-kills" -> {
                        int pos = clanManager.getClanKillsPosition(clan.getClanName());
                        yield pos == -1 ? identifier.empty() : identifier.set(pos);
                    }

                    case "top-points" -> {
                        int pos = clanManager.getClanPosition(clan.getClanName());
                        yield pos == -1 ? identifier.empty() : identifier.set(pos);
                    }

                    case "top-bank" -> {
                        int pos = clanManager.getClanBankPosition(clan.getClanName());
                        yield pos == -1 ? identifier.empty() : identifier.set(pos);
                    }

                    default -> identifier.nulL();
                };
            }

            // %exclan_clan_[name]_[args]%
            case "clan" -> {

                if (arg == null) return identifier.nulL(); // nombre del clan

                Clan clan = clanManager.getClan(arg);
                if (clan == null) return identifier.NT();

                if (parts.length < 3) return identifier.nulL();

                String field = parts[2];

                return switch (field) {
                    case "clan" -> identifier.set(clan.getClanName());
                    case "tag" -> identifier.set(clan.getClanTag());
                    case "discord" -> identifier.set(clan.getDiscord());

                    case "points" -> identifier.set(clan.getPoints());
                    case "kills" -> identifier.set(clan.getKills());
                    case "bank" -> identifier.set(clan.getBankDouble());
                    case "description" -> identifier.set(clan.getDescription());

                    case "leader" -> identifier.set(clan.getLeaderName());
                    case "leader-uuid" -> identifier.set(clan.getLaderUuid().toString());

                    case "members-amount" -> identifier.set(clan.getMembers().size());
                    case "allys-amount" -> identifier.set(clan.getAllys().size());
                    case "banned-amount" -> identifier.set(clan.getBannedMembers().size());

                    case "pvp" -> identifier.set(clan.isPvp());
                    case "pvp-ally" -> identifier.set(clan.isPvpAlly());

                    case "members" -> {
                        StringBuilder sb = new StringBuilder();
                        clan.getMembers().forEach(m -> sb.append(m.getName()).append(", "));
                        yield identifier.set(sb.toString());
                    }

                    case "allys" -> {
                        StringBuilder sb = new StringBuilder();
                        clan.getAllys().forEach(a -> sb.append(a).append(", "));
                        yield identifier.set(sb.toString());
                    }

                    case "banned" -> {
                        StringBuilder sb = new StringBuilder();
                        for (var uuid : clan.getBannedMembers()) {
                            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                            if (op.hasPlayedBefore()) {
                                sb.append(op.getName()).append(", ");
                            }
                        }
                        yield identifier.set(sb.toString());
                    }

                    case "symbols-icon", "symbols-name" -> {
                        if (extra == null) yield identifier.NT();
                        Rank rank = Rank.fromString(extra);
                        if (rank == null) yield identifier.NT();

                        Symbols symbol = clan.getSymbol(rank);
                        yield field.equals("symbols-icon")
                                ? identifier.set(symbol.getSymbol())
                                : identifier.set(symbol.getName());
                    }

                    case "top-kills" -> {
                        int pos = clanManager.getClanKillsPosition(clan.getClanName());
                        yield pos == -1 ? identifier.empty() : identifier.set(pos);
                    }

                    case "top-points" -> {
                        int pos = clanManager.getClanPosition(clan.getClanName());
                        yield pos == -1 ? identifier.empty() : identifier.set(pos);
                    }

                    case "top-bank" -> {
                        int pos = clanManager.getClanBankPosition(clan.getClanName());
                        yield pos == -1 ? identifier.empty() : identifier.set(pos);
                    }

                    default -> identifier.nulL();
                };
            }

            // ============================================================
            // TOP PLACEHOLDERS
            // ============================================================
            case "top" -> {

                if (arg == null || extra == null) return identifier.nulL();

                try {
                    int position = Integer.parseInt(extra);
                    return switch (arg) {
                        case "points" -> {
                            var pos = clanManager.getClanByPosition(position);
                            yield pos == null ? identifier.empty() : identifier.set(pos.getClanName());
                        }
                        case "kills" -> {
                            var pos = clanManager.getClanByKillsPosition(position);
                            yield pos == null ? identifier.empty() : identifier.set(pos.getClanName());
                        }
                        case "bank" -> {
                            var pos = clanManager.getClanByBankPosition(position);
                            yield pos == null ? identifier.empty() : identifier.set(pos.getClanName());
                        }
                        default -> identifier.NT();
                    };
                } catch (NumberFormatException e) {
                    return switch (arg) {
                        case "points" -> {
                            int pos = clanManager.getClanPosition(extra);
                            yield pos == -1 ? identifier.NT() : identifier.set(pos);
                        }
                        case "kills" -> {
                            int pos = clanManager.getClanKillsPosition(extra);
                            yield pos == -1 ? identifier.NT() : identifier.set(pos);
                        }
                        case "bank" -> {
                            int pos = clanManager.getClanBankPosition(extra);
                            yield pos == -1 ? identifier.NT() : identifier.set(pos);
                        }
                        default -> identifier.NT();
                    };
                }
            }
        }

        return identifier.nulL();
    }
}
