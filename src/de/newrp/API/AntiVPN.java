package de.newrp.API;

import de.newrp.Administrator.AntiCheatSystem;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class AntiVPN implements Listener {
    public static final HashMap<String, Boolean> IP_CACHE = new HashMap<>();
    private static String PREFIX = "§8[§cAntiVPN§8] §c" + Messages.ARROW + " §7";
    public static boolean activated = true;

    @EventHandler
    public void onLogin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String ip = p.getAddress().getAddress().getHostAddress();
        if (!activated) return;

        if (IP_CACHE.containsKey(ip)) {
            boolean isVPN = IP_CACHE.get(ip);
            if (isVPN) {
                Script.sendTeamMessage(AntiCheatSystem.PREFIX + "Bei " + Script.getName(p) + " wurde eine VPN entdeckt.");
            }
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            if (ip.equals("127.0.0.1")) return;
            try {
                URLConnection conn = new URL("https://blackbox.ipinfo.app/lookup/" + ip).openConnection();
                if (conn == null) return;
                String lookup = IOUtils.toString(conn.getInputStream());
                boolean isVPN = lookup.equals("Y");
                if (isVPN) {
                    Bukkit.getScheduler().runTask(main.getInstance(), () -> {
                        Script.sendTeamMessage(AntiCheatSystem.PREFIX + "Bei " + Script.getName(p) + " wurde eine VPN entdeckt.");
                    });
                }
                IP_CACHE.put(ip, isVPN);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }
}
