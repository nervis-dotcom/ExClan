package ex.nervisking.models;

public enum Rank {

    MEMBER("Miembro", 0),
    CAPTAIN("capitán", 1),
    SUB_LEADER("sublíder", 1);

    private final String displayName;
    private final int level;

    Rank(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Rank fromString(String name) {
        for (Rank rank : Rank.values()) {
            if (rank.name().equalsIgnoreCase(name)) {
                return rank;
            }
        }
        return null;
    }

    public static boolean isValidRank(String name) {
        for (Rank rank : Rank.values()) {
            if (rank.name().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static Rank getRankByDisplayName(String displayName) {
        for (Rank rank : Rank.values()) {
            if (rank.getDisplayName().equalsIgnoreCase(displayName)) {
                return rank;
            }
        }
        return null; // or throw an exception if preferred
    }
}
