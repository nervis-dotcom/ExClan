package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.model.ParseVariable;
import ex.api.base.utils.PlayerTeleport;
import ex.nervisking.manager.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.config.MainConfig;
import ex.nervisking.gui.home.HomeMenu;
import ex.nervisking.models.Clan;
import ex.nervisking.models.Homes;
import org.bukkit.Location;

import java.util.UUID;

@CommandArg(name = "home", permission = true)
public class HomeArgument implements CommandArgument {

    private final MainConfig config;
    private final ClanManager clanManager;

    public HomeArgument(ExClan plugin) {
        this.config = plugin.getMainConfig();
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        UUID uuid = sender.getUniqueId();
        Clan clan = clanManager.getClan(uuid);
        if (clan == null) {
            sender.sendLang("no-clan");
            return;
        }

        if (args.isEmpty()) {
            openMenu(new HomeMenu(sender.getPlayer(), clan));
            return;
        }

        if (args.lacksMinArgs(2)) {
            sender.helpLang("home.usage");
            return;
        }
        String homeName = args.get(1);
        switch (args.get(0).toLowerCase()) {
            case "add" -> {
                if (!clan.isLader(uuid)) {
                    sender.sendLang("not-leader");
                    return;
                }

                if (clan.hasHome(homeName)) {
                    sender.sendLang("home.add.already-exists");
                    return;
                }

                if (clan.getHomeAmount() >= config.getMaxHomes()) {
                    sender.sendLang("home.max-homes");
                    return;
                }

                if (utilsManagers.isValidText(homeName) || utilsManagers.hasColorCodes(homeName)) {
                    sender.sendLang("invalid-name");
                    return;
                }

                clan.addHome(homeName, sender.getCoordinate());
                sender.sendLang("home.add.success");
            }
            case "delete" -> {
                if (!clan.isLader(uuid)) {
                    sender.sendLang("not-leader");
                    return;
                }

                Homes homes = clan.getHome(homeName);
                if (homes == null) {
                    sender.sendLang("home.not-found", ParseVariable.adD("%home%", homeName));
                    return;
                }
                clan.removeHome(homes);
                sender.sendLang("home.delete.success", ParseVariable.adD("%home%", homeName));
            }
            case "tp" -> {
                Homes homes = clan.getHome(homeName);
                if (homes == null) {
                    sender.sendLang("home.not-found", ParseVariable.adD("%home%", homeName));
                    return;
                }

                Location location;
                try {
                    location = homes.getCoordinate().getLocation();
                } catch (Exception e) {
                    sender.sendLang("home.tp.invalid-location");
                    return;
                }

                if (location == null) {
                    sender.sendLang("home.tp.invalid-location");
                    return;
                }

                PlayerTeleport.andRun(sender.getPlayer(), location, teleport -> teleport
                        .setMessage(language.getString("clan","home.tp.success").replace("%home%", homeName))
                        .setSound(config.getSound())
                        .setParticle(config.getParticle())
                        .setTeleportAnimation(config.getAnimation())
                        .setDelayTicks(config.getDelayTeleport())
                        .setNoDelayPermission(config.getPermissionBypass())
                        .setMessageInTeleport(language.getString("clan", "home.tp.teleporting"), language.getString("clan", "home.tp.teleported"))
                        .setSoundInTeleport(config.getSoundInTeleport())
                        .teleportOf(() -> sender.sendLang( "home.tp.error", ParseVariable.adD("%error%", teleport.getErrorMessage())))
                );
            }

            default -> sender.helpLang("home.usage");
        }
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.add("add", "delete", "tp");
        }

        if (args.has(2) && args.equalsIgnoreCase(0, "add")) {
            completions.add("[name]");
        }

        if (args.has(2) && args.equalsIgnoreCase(0, "delete", "tp")) {
            Clan clan = clanManager.getClan(sender.getUniqueId());
            if (clan == null) {
                return completions;
            }

            completions.add(clan.getHomes().stream().map(Homes::getName).toList());
        }

        return completions;
    }
}