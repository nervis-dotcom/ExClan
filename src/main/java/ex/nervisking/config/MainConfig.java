package ex.nervisking.config;

import ex.api.base.config.ConfigInfo;
import ex.api.base.config.Yaml;
import ex.nervisking.models.chat.ChatFormat;
import ex.nervisking.models.chat.HoverAction;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ConfigInfo(name = "Config", register = false)
public class MainConfig extends Yaml {

    private String prefix;
    private final Map<String, ChatFormat> chatFormats;
    private String joinMessage;
    private String leaveMessage;
    private String killerMessage;
    private String victimMessage;
    private String broadcastPointsMessage;
    private int pointEarned;
    private int pointLosing;
    private String cooldownTime;
    private String cooldownMessage;

    public MainConfig() {
        this.chatFormats = new HashMap<>();
        this.load();
    }

    @Override
    protected void load() {
        var config = customConfig.getConfig();

        this.prefix = config.getString("prefix", "&#ff0000&lᴇ&#ff3000&lx&#ff6000&lᴄ&#ff8f00&lʟ&#ffbf00&lᴀ&#ffef00&lɴ");

        this.joinMessage = config.getString("config.join-message");
        this.leaveMessage = config.getString("config.leave-message");
        this.victimMessage = config.getString("config.point-kills.message-victim");
        this.killerMessage = config.getString("config.point-kills.message-killer");
        this.pointEarned = config.getInt("config.point-kills.for-earned");
        this.pointLosing = config.getInt("config.point-kills.for-losing");
        this.broadcastPointsMessage = config.getString("config.point-kills.broadcast-points-message");

        this.cooldownTime = config.getString("config.point-kills.cooldown.time", "2m");
        this.cooldownMessage = config.getString("config.point-kills.cooldown.message");

        chatFormats.clear();
        if (config.contains("chat-format")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("chat-format")).getKeys(false)) {
                String format = config.getString("chat-format." + key + ".format", "%player% > ");

                HoverAction playerHover = loadHoverAction(config, "chat-format." + key + ".hover.player");
                HoverAction messageHover = loadHoverAction(config, "chat-format." + key + ".hover.message");

                chatFormats.put(key, new ChatFormat(format, playerHover, messageHover));
            }
        } else {
            logger.warn("No se encontró la sección 'chat-format' en el archivo de configuración.");
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public ChatFormat getChat(String chat) {
        return chatFormats.get(chat);
    }

    public boolean hasJoinMessage() {
        return joinMessage != null && !joinMessage.isEmpty() && !joinMessage.equals("none");
    }

    public boolean hasLeaveMessage() {
        return leaveMessage != null && !leaveMessage.isEmpty() && !leaveMessage.equals("none");
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public boolean hasKillerMessage() {
        return killerMessage != null && !killerMessage.isEmpty() && !killerMessage.equals("none");
    }

    public boolean hasVictimMessage() {
        return victimMessage != null && !victimMessage.isEmpty() && !victimMessage.equals("none");
    }

    public String getKillerMessage() {
        return killerMessage;
    }

    public String getVictimMessage() {
        return victimMessage;
    }

    public int getPointEarned() {
        return pointEarned;
    }

    public int getPointLosing() {
        return pointLosing;
    }

    public String getBroadcastPointsMessage() {
        return broadcastPointsMessage;
    }

    public boolean hasBroadcastPointsMessage() {
        return broadcastPointsMessage != null && !broadcastPointsMessage.isEmpty() && !broadcastPointsMessage.equals("none");
    }

    public boolean hasCooldownMessage() {
        return cooldownMessage != null && !cooldownMessage.isEmpty() && !cooldownMessage.equals("none");
    }

    public String getCooldownMessage() {
        return cooldownMessage;
    }

    public String getCooldownTime() {
        return cooldownTime;
    }

    private @NotNull HoverAction loadHoverAction(@NotNull FileConfiguration config, String path) {
        boolean enabled = config.getBoolean(path + ".enabled", false);
        List<String> hoverText = config.getStringList(path + ".hover");
        boolean clickEnabled = config.getBoolean(path + ".click.enabled", false);
        String clickTag = config.getString(path + ".click.tag", "");
        String clickAction = config.getString(path + ".click.action", "");
        return new HoverAction(enabled, hoverText, clickEnabled, clickAction, clickTag);
    }
}