package ex.nervisking.models;

import net.kyori.adventure.key.KeyPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public enum Symbols {

    CROWN("crown", "👑"),
    COMET("comet", "☄"),
    STAR("star", "⭐"),
    AGORAT("agorat", "₪"),
    SWORD("sword", "⚔"),
    SHIELD("shield", "🛡"),
    SKULL("skull", "☠"),
    FIRE("fire", "🔥"),
    HEART("heart", "❤"),
    DIAMOND("diamond", "💎"),
    FLAG("flag", "🚩"),
    CHECK("check", "✔"),
    CROSS("cross", "❌"),
    WARNING("warning", "⚠"),
    LIGHTNING("lightning", "⚡"),
    MAGIC("magic", "✨"),
    TARGET("target", "🎯"),
    BOOK("book", "📘"),
    MAP("map", "🗺"),
    GEM("gem", "🔮"),
    KING("king", "🤴"),
    QUEEN("queen", "👸"),
    DRAGON("dragon", "🐉"),
    BOW("bow", "🏹"),
    HAMMER("hammer", "🔨");

    private final @KeyPattern String name;
    private final String symbol;

    Symbols(@KeyPattern String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public @KeyPattern String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public static @NotNull @Unmodifiable List<Symbols> getSymbols() {
        return List.of(Symbols.values());
    }

    public static @Nullable Symbols fromString(String name) {
        for (Symbols symbol : Symbols.values()) {
            if (symbol.name().equalsIgnoreCase(name)) {
                return symbol;
            }
        }
        return null;
    }

    public static @Nullable Symbols froSimbol(String name) {
        for (Symbols symbol : Symbols.values()) {
            if (symbol.getSymbol().equalsIgnoreCase(name)) {
                return symbol;
            }
        }
        return null;
    }
}