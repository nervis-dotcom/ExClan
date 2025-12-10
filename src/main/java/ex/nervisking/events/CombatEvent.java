package ex.nervisking.events;

import ex.api.base.event.Event;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.manager.WarManager;
import ex.nervisking.models.Clan;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CombatEvent extends Event<ExClan> {

    private final ClanManager clanManager;
    private final WarManager warManager;

    public CombatEvent() {
        this.clanManager = plugin.getClanManager();
        this.warManager = plugin.getPointsWarManager();
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

        if (killer == null || killer.equals(victim)) return; // evitar suicidios

        // Obtener clanes
        Clan killerClan = clanManager.getClan(killer.getUniqueId());
        Clan victimClan = clanManager.getClan(victim.getUniqueId());

        if (killerClan == null || victimClan == null) return; // alguno no tiene clan

        if (killerClan == victimClan) return; //

        // Configurable
        int pointsPerKill = 10;
        int pointsPerDeath = 5;
        if (warManager.enabledPoint()) {
            pointsPerKill = 20;
            pointsPerDeath = 10;
        }

        // Dar puntos al clan del killer
        killerClan.addPoints(pointsPerKill);

        // Quitar puntos al clan de la víctima
        victimClan.removePoints(pointsPerDeath);
        victimClan.addKills(1);

        // Mensajes
        sendMessage(killer,"%prefix% &a¡Tu clan ganó +" + pointsPerKill + " puntos por matar a " + victim.getName() + "!");
        sendMessage(victim,"%prefix% &cTu clan perdió -" + pointsPerDeath + " puntos por tu muerte...");

        // Broadcast opcional
        sendBroadcastMessage("%prefix% &e" + killerClan.getClanName() + " &aganó +" + pointsPerKill + " puntos y &e" + victimClan.getClanName() + " &cPerdió -" + pointsPerDeath + " puntos.");
    }
}