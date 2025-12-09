//package ex.nervisking.hook;
//
//import ex.api.base.hook.PlaceholderApiHook;
//import ex.nervisking.ClanManager;
//import ex.nervisking.ExClan;
//import ex.nervisking.models.Clan;
//import org.bukkit.entity.Player;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//public class Papi extends PlaceholderApiHook {
//
//    public final ClanManager clanManager;
//
//    public Papi(ExClan plugin) {
//        this.clanManager = plugin.getClanManager();
//    }
//
//    @Override
//    public @Nullable String register(@Nullable Player player, @NotNull String identifier) {
//        String lowerCaseIdentifier = identifier.toLowerCase();
//        String[] parts = lowerCaseIdentifier.split("_");
//
//        if (parts.length < 2) return null;
//
//        String part = parts[0];
//        String string = parts[1];
//
//        if (player == null) {
//            return null;
//        }
//
//        Clan clan = clanManager.getClan(player.getUniqueId());
//        if (clan == null) {
//            return null;
//        }
//
//        switch (part.toLowerCase()) {
//            case "player" -> {
//                switch (string.toLowerCase()) {
//                    case "symbol" -> {
//                        return clan.getSymbol(player.getUniqueId());
//                    }
//                    case "tag" -> {
//                        return clan.getClanTag();
//                    }
//                    case "points" -> {
//                        return String.valueOf(clan.getPoints());
//                    }
//                    case "description" -> {
//                        return clan.getDescription();
//                    }
//                    case "discord" -> {
//                        return clan.getDiscord();
//                    }
//                    case "leader" -> {
//                        return clan.getLeaderName();
//                    }
//
//                }
//            }
//        }
//
//        return "";
//    }
//}