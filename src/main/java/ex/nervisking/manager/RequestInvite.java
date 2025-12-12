package ex.nervisking.manager;

import ex.api.base.service.Service;
import ex.api.base.task.Scheduler;
import ex.api.base.task.Task;
import ex.nervisking.ExClan;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class RequestInvite extends Service<ExClan> {

    private final List<Request> invites;
    private Task taskRemoveInvite;

    public RequestInvite() {
        this.invites = new ArrayList<>();
        this.start();
    }

    public void addInvite(String clanName, UUID sender, UUID receiver) {
        this.invites.add(new Request(clanName, sender, receiver, System.currentTimeMillis() + 60000)); // 1 minuto
        this.start();
    }

    public Request getInvite(String clanName, UUID receiver) {
        for (var request : invites) {
            if (request.clanName.equalsIgnoreCase(clanName) && request.receiver.equals(receiver)) {
                return request;
            }
        }
        return null;
    }

    public List<String> getInvite(UUID receiver) {
        List<String> clanNames = new ArrayList<>();
        for (var request : invites) {
            if (request.receiver.equals(receiver)) {
                clanNames.add(request.clanName);
            }
        }
        return clanNames;
    }

    public boolean hasInvite(String clanName, UUID receiver) {
        return getInvite(clanName, receiver) != null;
    }

    public void removeInvite(String clanName, UUID receiver) {
        this.invites.removeIf(request -> request.clanName.equalsIgnoreCase(clanName) && request.receiver.equals(receiver));
    }

    private void start() {
        if (this.taskRemoveInvite != null && this.taskRemoveInvite.isRunning()) {
            return;
        }
        this.taskRemoveInvite = Scheduler.runTimer(() -> {
            if (invites.isEmpty()) {
                this.taskRemoveInvite.cancel();
            }
            for (var invites : new ArrayList<>(invites)) {
                if (invites.isExpired()) {
                    Player request = Bukkit.getPlayer(invites.sender());
                    if (request != null && request.isOnline()) {
                        sendMessage(request, language.getString("clan", "invitation-expired"));
                    }

                    Player receiver = Bukkit.getPlayer(invites.receiver());
                    if (receiver != null && receiver.isOnline()) {
                        sendMessage(receiver, language.getString("clan", "invitation-expired"));
                    }
                    this.invites.remove(invites);
                }
            }
        }, 0, 20);
    }

    public record Request(String clanName, UUID sender, UUID receiver, long expirationTime) {
        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}