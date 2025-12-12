package ex.nervisking.events;

import ex.api.base.event.Event;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.config.MainConfig;
import ex.nervisking.manager.WarManager;
import ex.nervisking.models.Clan;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatEvent extends Event<ExClan> {

    private final MainConfig config;
    private final ClanManager clanManager;
    private final WarManager warManager;
    private final Map<UUID, Map<UUID, Long>> lastKills;

    public CombatEvent() {
        this.clanManager = plugin.getClanManager();
        this.warManager = plugin.getPointsWarManager();
        this.config = plugin.getMainConfig();
        this.lastKills = new HashMap<>();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Player damaged)) return;

        Clan clan = clanManager.getClan(damaged.getUniqueId());
        if (clan == null) return;


        if (clan.isPvpAlly()) {
            switch (event.getDamager()) {
                case Player player when !damaged.equals(player) && clanManager.hasAllyPlayer(clan, player.getUniqueId()) -> event.setCancelled(true); // Cuerpo a cuerpo
                case ThrownPotion potion -> { // Daño por pociones
                    if (potion.getShooter() instanceof Player player && !damaged.equals(player) && clanManager.hasAllyPlayer(clan, player.getUniqueId())) {
                        event.setCancelled(true);
                    }
                }
                case Projectile projectile -> { // Daño por proyectiles
                    if (projectile.getShooter() instanceof Player player && !damaged.equals(player) && clanManager.hasAllyPlayer(clan, player.getUniqueId())) {
                        event.setCancelled(true);
                    }
                }
                default -> {}
            }
            return;
        }

        if (clan.isPvp()) {
            switch (event.getDamager()) {
                case Player player when !damaged.equals(player) && clan.hasMemberOf(player.getUniqueId()) -> event.setCancelled(true); // Cuerpo a cuerpo
                case ThrownPotion potion -> { // Daño por pociones
                    if (potion.getShooter() instanceof Player player && !damaged.equals(player) && clan.hasMemberOf(player.getUniqueId())) {
                        event.setCancelled(true);
                    }
                }
                case Projectile projectile -> { // Daño por proyectiles
                    if (projectile.getShooter() instanceof Player player && !damaged.equals(player) && clan.hasMemberOf(player.getUniqueId())) {
                        event.setCancelled(true);
                    }
                }
                default -> {}
            }
        }
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null || killer.equals(victim)) return;

        Clan killerClan = clanManager.getClan(killer.getUniqueId());
        Clan victimClan = clanManager.getClan(victim.getUniqueId());

        if (killerClan == null || victimClan == null) return;

        if (killerClan == victimClan) return;

        long now = System.currentTimeMillis();
        long cooldown = parseTime(config.getCooldownTime());

        Map<UUID, Long> victimMap = lastKills.getOrDefault(killer.getUniqueId(), new HashMap<>());
        Long lastKillTime = victimMap.get(victim.getUniqueId());

        if (lastKillTime != null && (now - lastKillTime) < cooldown) {
            if (config.hasCooldownMessage()) {
                sendMessage(killer, config.getCooldownMessage().replace("%player%", victim.getName()));
            }
            return;
        }

        victimMap.put(victim.getUniqueId(), now);
        lastKills.put(killer.getUniqueId(), victimMap);

        int pointsPerKill = config.getPointEarned();
        int pointsPerDeath = config.getPointLosing();
        if (warManager.enabledPoint()) {
            pointsPerKill = 20;
            pointsPerDeath = 10;
        }

        killerClan.addPoints(pointsPerKill);
        victimClan.removePoints(pointsPerDeath);
        victimClan.addKills(1);

        if (config.hasKillerMessage()) {
            sendMessage(killer, config.getKillerMessage()
                    .replace("%points%", String.valueOf(pointsPerKill))
                    .replace("%target%", victim.getName())
                    .replace("%target_clan%", victimClan.getClanName()));
        }

        if (config.hasVictimMessage()) {
            sendMessage(victim, config.getVictimMessage()
                    .replace("%points%", String.valueOf(pointsPerDeath)));
        }

        if (config.hasBroadcastPointsMessage()) {
            sendBroadcastMessage(config.getBroadcastPointsMessage()
                    .replace("%killer_clan%", killerClan.getClanName())
                    .replace("%victim_clan%", victimClan.getClanName())
                    .replace("%points_earned%", String.valueOf(pointsPerKill))
                    .replace("%points_lost%", String.valueOf(pointsPerDeath)));
        }
    }
}