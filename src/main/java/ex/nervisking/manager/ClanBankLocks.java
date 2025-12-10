package ex.nervisking.manager;

import java.util.HashMap;
import java.util.Map;

public class ClanBankLocks {

    private final Map<String, Boolean> locks = new HashMap<>();

    public synchronized boolean tryLock(String clan) {
        if (locks.getOrDefault(clan, false)) {
            return false;
        }
        locks.put(clan, true);
        return true;
    }

    public synchronized void unlock(String clan) {
        locks.put(clan, false);
    }

}