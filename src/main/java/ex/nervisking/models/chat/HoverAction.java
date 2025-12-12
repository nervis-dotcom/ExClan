package ex.nervisking.models.chat;

import java.util.List;

public record HoverAction(boolean enabled, List<String> hoverText, boolean clickEnabled, String clickAction, String clickTag) {}