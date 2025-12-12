package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.model.ParseVariable;
import ex.api.base.utils.PlayerTeleport;
import ex.api.base.utils.teleport.TPAnimation;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.gui.homes.HomeMenu;
import ex.nervisking.models.Clan;
import ex.nervisking.models.Homes;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.UUID;

@CommandArg(name = "home", permission = true)
public class HomeArgument implements CommandArgument {

    public final ClanManager clanManager;

    public HomeArgument(ExClan plugin) {
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
        String clanName = args.get(1);
        switch (args.get(0).toLowerCase()) {
            case "set" -> {
                if (!clan.isLader(uuid)) {
                    sender.sendLang("not-leader");
                    return;
                }

                if (utilsManagers.isValidText(clanName) || utilsManagers.hasColorCodes(clanName)) {
                    sender.sendLang("invalid-name");
                    return;
                }
                clan.addHome(clanName, sender.getCoordinate());
                sender.sendLang("home.set.success");
            }
            case "delete" -> {
                if (!clan.isLader(uuid)) {
                    sender.sendLang("not-leader");
                    return;
                }

                Homes homes = clan.getHome(clanName);
                if (homes == null) {
                    sender.sendLang("home.not-found", ParseVariable.adD("%home%", clanName));
                    return;
                }
                clan.removeHome(homes);
                sender.sendLang("home.delete.success");
            }
            case "tp" -> {
                Homes homes = clan.getHome(clanName);
                if (homes == null) {
                    sender.sendLang("home.not-found", ParseVariable.adD("%home%", clanName));
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

                PlayerTeleport teleport = PlayerTeleport.of(sender.getPlayer(), location)
                        .setMessage(language.getString("clan","home.tp.success").replace("%home%", clanName))
                        .setSound(Sound.ENTITY_ENDERMAN_TELEPORT)
                        .setParticle(Particle.FLAME)
                        .setTeleportAnimation(TPAnimation.DOUBLE_SPIRAL)
                        .setDelayTicks(3)
                        .setNoDelayPermission("home.instant")
                        .setMessageInTeleport(language.getString("clan", "home.tp.teleporting"), language.getString("clan", "home.tp.teleported"))
                        .setSoundInTeleport(Sound.ENTITY_PLAYER_LEVELUP);

                teleport.teleportOf(() -> sender.sendMessage(language.getString("clan", "home.tp.error") + teleport.getErrorMessage()));
            }
        }
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.add("set", "delete", "tp");
        }

        if (args.has(2) && args.equalsIgnoreCase(0, "set")) {
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