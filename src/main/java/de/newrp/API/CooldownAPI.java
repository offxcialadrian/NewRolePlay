package de.newrp.API;

import de.newrp.main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class CooldownAPI implements Listener {

    public static final HashMap<String, HashMap<String, CooldownMeter>> cooldown = new HashMap<>();
    private final int max_level = 10;
    private final Player player;
    private final CooldownAPI.CooldownTime time;
    private final boolean resetOnFinish;
    private final int time_next_level_max;
    private final int time_next_level_min;

    public CooldownAPI(Player player, CooldownAPI.CooldownTime time, boolean resetOnFinish) {
        this.player = player;
        this.time = time;
        this.time_next_level_max = time.getMax();
        this.time_next_level_min = time.getMin();
        this.resetOnFinish = resetOnFinish;
    }

    public CooldownAPI(Player player, CooldownAPI.CooldownTime time) {
        this.player = player;
        this.time = time;
        this.time_next_level_max = time.getMax();
        this.time_next_level_min = time.getMin();
        this.resetOnFinish = true;
    }

    public static void sendAsyncMeter(Player p, int seconds) {
        if (seconds > 10) seconds = 10;
        if (seconds < 0) seconds = 1;
        new BukkitRunnable() {
            int level = 0;

            @Override
            public void run() {
                if (level > 10) this.cancel();
                double current_progress = level;
                double progress_percentage = current_progress / 10;
                StringBuilder sb = new StringBuilder();
                int bar_length = 10;
                for (int i = 0; i < bar_length; i++) {
                    if (i < bar_length * progress_percentage) {
                        sb.append("§c▉");
                    } else {
                        sb.append("§8▉");
                    }
                }
                Script.sendActionBar(p, "§cWarte... §8» §a" + sb);
                level++;
            }
        }.runTaskTimerAsynchronously(main.getInstance(), 0L, (seconds / 10) * 20L);
    }

    public Player getPlayer() {
        return this.player;
    }

    public CooldownAPI.CooldownTime getCooldownTime() {
        return this.time;
    }

    public boolean resetOnFinish() {
        return this.resetOnFinish;
    }

    public boolean checkInput(Class<?> c) {
        HashMap<String, CooldownMeter> map = cooldown.get(getPlayer().getName());

        if (map == null || !map.containsKey(c.getSimpleName())) {
            map = new HashMap<>();
            map.put(c.getSimpleName(), new CooldownMeter(1, System.currentTimeMillis()));
            cooldown.put(getPlayer().getName(), map);
            return false;
        }
        CooldownMeter meter = map.get(c.getSimpleName());
        int level = meter.getLevel();
        long lastUsage = meter.getLastUsage();

        long current = System.currentTimeMillis();

        long l = (current - lastUsage);

        if (level == max_level && !resetOnFinish) {
            if (l > time_next_level_max) {
                meter.setLevel(0);
                map.remove(c.getSimpleName());
                return false;
            }
            meter.setLastUsage(current);
            return true;
        }

        if (l < time_next_level_min && l > time_next_level_max) {
            if (level + 1 >= max_level) {
                if (resetOnFinish()) {
                    map.remove(c.getSimpleName());
                    meter.setLevel(max_level);
                } else {
                    meter.setLevel(max_level);
                    meter.setLastUsage(current);
                }
                sendMeter(meter);
                return true;
            } else {
                meter.setLevel(level + 1);
                meter.setLastUsage(current);
                sendMeter(meter);
                return false;
            }
        } else {
            if (l > time_next_level_max) {
                meter.setLevel(0);
                map.remove(c.getSimpleName());
            }
        }
        cooldown.put(getPlayer().getName(), map);
        if (cooldown.get(getPlayer().getName()).isEmpty()) {
            cooldown.remove(getPlayer().getName());
        }
        sendMeter(meter);
        return false;
    }

    public void sendMeter(CooldownMeter meter) {
        int level = meter.getLevel();
        long lastUsage = meter.getLastUsage();
        long current = System.currentTimeMillis();
        long l = (current - lastUsage);
        int seconds = (int) (l / 1000);
        if (level == max_level) {
            sendAsyncMeter(getPlayer(), seconds);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        cooldown.remove(e.getPlayer().getName());
    }

    public enum CooldownTime {
        FAST(400, 900), //ca. 5s
        MEDIUM(800, 1300), //ca. 8s
        LONG(1000, 1500); //ca. 10s

        private final int max;
        private final int min;

        CooldownTime(int max, int min) {
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return this.min;
        }

        public int getMax() {
            return this.max;
        }
    }

    public class CooldownMeter {

        private int level;
        private long lastUsage;

        public CooldownMeter(int level, long lastUsage) {
            this.level = level;
            this.lastUsage = lastUsage;
        }

        public int getLevel() {
            return (Math.min(this.level, max_level));
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public long getLastUsage() {
            return this.lastUsage;
        }

        public void setLastUsage(long lastUsage) {
            this.lastUsage = lastUsage;
        }
    }
}
