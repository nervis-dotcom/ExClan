package ex.nervisking.models.chat;

import org.jetbrains.annotations.Nullable;

public enum Chat {
    ALLY,
    CLAN,
    NONE;

    public static Chat fromString(@Nullable String value) {
        if (value == null) {
            return NONE;
        }
        try {
            return Chat.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}