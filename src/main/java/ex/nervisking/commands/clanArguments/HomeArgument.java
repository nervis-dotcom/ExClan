package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.model.Coordinate;
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
            sender.sendMessage("%prefix% &cNo estás en un clan.");
            return;
        }

        if (args.isEmpty()) {
            openMenu(new HomeMenu(sender.asPlayer(), clan));
            return;
        }

        if (args.lacksMinArgs(2)) {
            sender.help("Usa /clan <set | delete | tp> <home>");
            return;
        }
        String clanName = args.get(1);
        switch (args.get(0).toLowerCase()) {
            case "set" -> {
                if (!clan.isLader(uuid)) {
                    sender.sendMessage("%prefix% &cNo eres el líder del clan.");
                    return;
                }

                if (utilsManagers.isValidText(clanName) || utilsManagers.hasColorCodes(clanName)) {
                    sender.sendMessage("%prefix% &cEl nombre no es valido.");
                    return;
                }
                Location loc = sender.getLocation();
                Coordinate coordinate = Coordinate.of(sender.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                clan.addHome(clanName, coordinate);
                sender.sendMessage("%prefix% &aHome establecido.");
            }
            case "delete" -> {
                if (!clan.isLader(uuid)) {
                    sender.sendMessage("%prefix% &cNo eres el líder del clan.");
                    return;
                }

                Homes homes = clan.getHome(clanName);
                if (homes == null) {
                    sender.sendMessage("%prefix% &fNo se encontró la casa &e" + clanName + "&f.");
                    return;
                }
                clan.removeHome(homes);
                sender.sendMessage("%prefix% &aHome eliminado.");
            }
            case "tp" -> {
                Homes homes = clan.getHome(clanName);
                if (homes == null) {
                    sender.sendMessage("%prefix% &fNo se encontró la casa &e" + clanName + "&f.");
                    return;
                }

                Location location;
                try {
                    location = homes.getCoordinate().getLocation();
                } catch (Exception e) {
                    sender.sendMessage("%prefix% &cError: La ubicación del home no está configurada.");
                    return;
                }

                if (location == null) {
                    sender.sendMessage("%prefix% &cError: La ubicación del home no está configurada.");
                    return;
                }

                PlayerTeleport.andRun(sender.asPlayer(), location, tm -> tm
                        .setMessage("%prefix% &aHas sido teletransportado al home %name%!".replace("%name%", clanName))
                        .setSound(Sound.ENTITY_ENDERMAN_TELEPORT)
                        .setParticle(Particle.FLAME)
                        .setTeleportAnimation(TPAnimation.DOUBLE_SPIRAL)
                        .setDelayTicks(3)
                        .setNoDelayPermission("home.instant")
                        .setMessageInTeleport("Teletransporte en %time% segundos...", "&aTeletransportado...")
                        .setSoundInTeleport(Sound.ENTITY_PLAYER_LEVELUP)
                        .teleportOf(() -> sender.sendMessage("%prefix% &cError: " + tm.getErrorMessage())));
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