package ex.nervisking.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Rank {

    MEMBER("miembro", 0, Symbols.COMET),
    CAPTAIN("capitán", 1, Symbols.STAR),
    SUB_LEADER("sub-líder", 2, Symbols.AGORAT),
    LEADER("líder", 3, Symbols.CROWN);

    private final String displayName;
    private final int level;
    private final Symbols symbol;

    Rank(String displayName, int level, Symbols symbol) {
        this.displayName = displayName;
        this.level = level;
        this.symbol = symbol;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Symbols getSymbol() {
        return symbol;
    }

    public static @Nullable Rank fromString(String name) {
        for (Rank rank : Rank.values()) {
            if (rank.name().equalsIgnoreCase(name)) {
                return rank;
            }
        }
        return null;
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull @Unmodifiable List<String> getRank() {
        return List.of("MEMBER", "CAPTAIN", "SUB_LEADER");
    }

    public static @NotNull Map<Rank, Symbols> getSymbols() {
        Map<Rank, Symbols> symbols = new HashMap<>();
        for (var rank : Rank.values()) {
            symbols.put(rank, rank.getSymbol());
        }
        return symbols;
    }
}