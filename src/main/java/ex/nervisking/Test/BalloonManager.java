package ex.nervisking.Test;

import ex.api.base.item.ItemBuilder;
import ex.api.base.task.Scheduler;
import ex.api.base.task.Task;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BalloonManager implements Listener {

    private final Map<UUID, ArmorStand> balloons;
    private final Map<UUID, Location> targetPositions;
    private final Map<UUID, Double> phase;
    private final Map<UUID, Float> spinDeg;
    private Task task;

    private final double baseOffsetY;
    private final double baseOffsetX;
    private final boolean rightSide;

    public BalloonManager() {
        this.balloons = new HashMap<>();
        this.targetPositions = new HashMap<>();
        this.phase = new HashMap<>();
        this.spinDeg = new HashMap<>();
        this.rightSide = false; // false = derecha, true = izquierda
        this.baseOffsetY = 2.0;
        this.baseOffsetX = 1.2;
    }

    // Evento que actualiza la posición lateral y altura del globo
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (!balloons.containsKey(p.getUniqueId())) return;

        double yawRad = Math.toRadians(p.getLocation().getYaw());
        double dx = Math.cos(yawRad) * baseOffsetX * (rightSide ? 1 : -1);
        double dz = Math.sin(yawRad) * baseOffsetX * (rightSide ? 1 : -1);

        // ExtraY solo se aplica en el task para la animación
        Location target = p.getLocation().clone().add(dx, baseOffsetY, dz);
        targetPositions.put(p.getUniqueId(), target);

        ArmorStand balloon = balloons.get(p.getUniqueId());
        if (balloon != null) {
            balloon.teleport(target);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        removePlayer(e.getPlayer());
    }

    // Task solo para animación (bob y rotación)
    public void start() {
        task = Scheduler.runTimer(() -> {
            for (UUID uuid : balloons.keySet()) {
                ArmorStand balloon = balloons.get(uuid);
                Player p = Bukkit.getPlayer(uuid);

                if (p == null || !p.isOnline() || balloon.isDead()) {
                    if (balloon != null) balloon.remove();
                    phase.remove(uuid);
                    spinDeg.remove(uuid);
                    targetPositions.remove(uuid);
                    continue;
                }

                boolean moving = p.getVelocity().lengthSquared() > 0.01;

                double bobAmp, bobSpeed, extraY;
                float spinSpeed;

                if (p.isSneaking()) {
                    extraY = -0.45;
                    bobAmp = 0.05;
                    bobSpeed = 0.15;
                    spinSpeed = 2f;
                } else if (p.isSprinting()) {
                    extraY = 0.35;
                    bobAmp = 0.18;
                    bobSpeed = 0.28;
                    spinSpeed = 12f;
                } else if (moving) {
                    extraY = 0.18;
                    bobAmp = 0.12;
                    bobSpeed = 0.22;
                    spinSpeed = 7f;
                } else {
                    extraY = 0.0;
                    bobAmp = 0.10;
                    bobSpeed = 0.18;
                    spinSpeed = 3f;
                }

                // Animación senoidal
                double ph = phase.getOrDefault(uuid, 0.0);
                ph += bobSpeed;
                phase.put(uuid, ph);
                double bob = Math.sin(ph) * bobAmp;

                // Solo se añade bob y extraY a la posición que ya calculó PlayerMoveEvent
                Location baseTarget = targetPositions.get(uuid);
                if (baseTarget != null) {
                    Location animPos = baseTarget.clone().add(0, extraY + bob, 0);
                    balloon.teleport(animPos);
                }

                // Rotación del globo
                float rot = spinDeg.getOrDefault(uuid, 0f);
                rot += spinSpeed;
                if (rot >= 360f) rot -= 360f;
                spinDeg.put(uuid, rot);

                double pitchDeg = Math.sin(ph) * 10.0;
                double rollDeg = Math.cos(ph) * 5.0;
                balloon.setHeadPose(new EulerAngle(Math.toRadians(pitchDeg), Math.toRadians(rot), Math.toRadians(rollDeg)));
            }
        }, 0L, 2L);
    }

    public void stop() {
        if (task != null) task.cancel();
        balloons.values().forEach(ArmorStand::remove);
        balloons.clear();
        phase.clear();
        spinDeg.clear();
        targetPositions.clear();
    }

    public void addPlayer(@NotNull Player p) {
        if (balloons.containsKey(p.getUniqueId())) return;

        Location loc = p.getLocation().clone().add(0, baseOffsetY, 0);
        ArmorStand as = p.getWorld().spawn(loc, ArmorStand.class, ent -> {
            ent.setInvisible(true);
            ent.setMarker(true);
            ent.setSmall(true);
            ent.setGravity(false);
            ent.setInvulnerable(true);
            ent.getEquipment().setHelmet(ItemBuilder.of(ItemBuilder.CLOSE).build());
        });

        balloons.put(p.getUniqueId(), as);
        targetPositions.put(p.getUniqueId(), loc);
        phase.put(p.getUniqueId(), 0.0);
        spinDeg.put(p.getUniqueId(), 0f);
    }

    public void removePlayer(@NotNull Player p) {
        ArmorStand balloon = balloons.remove(p.getUniqueId());
        if (balloon != null) balloon.remove();
        phase.remove(p.getUniqueId());
        spinDeg.remove(p.getUniqueId());
        targetPositions.remove(p.getUniqueId());
    }
}
