package ex.nervisking.models;

public enum Permissions {

    CLAN_CREATE("clan.create"),
    CLAN_JOIN("clan.join"),
    CLAN_LEAVE("clan.leave"),
    CLAN_INVITE("clan.invite"),
    CLAN_KICK("clan.kick"),
    CLAN_PROMOTE("clan.promote"),
    CLAN_DEMOTE("clan.demote"),
    CLAN_SET_TAG("clan.settag"),
    CLAN_SET_DESCRIPTION("clan.setdescription"),
    CLAN_SET_POINTS("clan.setpoints"),
    CLAN_VIEW_MEMBERS("clan.viewmembers");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
