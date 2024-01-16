package de.newrp.Organisationen;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Player.AFK;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class BlackListCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length == 0) {
            StringBuilder sb = new StringBuilder();
            for (Organisation f : Organisation.values()) {
                if(!f.hasBlacklist()) continue;
                if (Blacklist.BLACKLIST.containsKey(f)) {
                    Iterator<Blacklist> it = Blacklist.BLACKLIST.get(f).iterator();
                    boolean b = true;
                    while (it.hasNext()) {
                        Blacklist bl = it.next();
                        if (bl.getUserName().equals(p.getName())) {
                            sb.append("  §8×§6 ").append(f.getName()).append("§8: §cJa §7(§c").append(bl.getPrice()).append("$§8, ").append(bl.getKills()).append(" §cKills§8, Grund§8: §c").append(bl.getReason()).append("§7)\n");
                            b = false;
                            break;
                        }
                    }
                }
            }

            if(sb.length() == 0) {
                p.sendMessage(Blacklist.PREFIX + "Du bist auf keiner Blacklist.");
                return true;
            }

            p.sendMessage("§8=== §6Blacklists §8===");
            p.sendMessage(sb.toString());
            return true;
        }

        if(!Organisation.hasOrganisation(p)) {
            p.sendMessage(Blacklist.PREFIX + "Du bist in keiner Organisation.");
            return true;
        }

        Organisation o = Organisation.getOrganisation(p);

        if(!o.hasBlacklist()) {
            p.sendMessage(Blacklist.PREFIX + "Deine Organisation hat keine Blacklist. Ihr schaltet die Blacklist mit Level 2 frei.");
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("list")) {
            p.sendMessage("");
            List<Blacklist> blacklist = Blacklist.getBlacklist(o);
            StringBuilder sb = new StringBuilder("§8==== §eBlacklist ").append(o.getName()).append(" §7(§6").append(blacklist.size()).append("§7)§e §8====\n");
            List<Blacklist> offline = new ArrayList<>();
            for (Blacklist bl : blacklist) {
                Player p1 = Script.getPlayer(bl.getUserName());
                if (p1 == null) {
                    offline.add(bl);
                } else {
                    String time = getTime(bl);
                    sb.append(" §7» §a").append(Script.getName(p1)).append(" §7|§a ").append(bl.getReason()).append(" §7|§a ").append(time)
                            .append(" §7|§a ").append(bl.getKills()).append(" Kills §7|§a ").append(bl.getPrice()).append("€").append((AFK.isAFK(p1) ? " (AFK seit " + AFK.getAFKTime(p) +  " Uhr)\n" : "\n"));
                }
            }
            for (Blacklist bl : offline) {
                String time = getTime(bl);
                sb.append(" §7» §c").append(bl.getUserName()).append(" §7|§c ").append(bl.getReason()).append(" §7|§c ").append(time)
                        .append(" §7|§c ").append(bl.getKills()).append(" Kills §7|§c ").append(bl.getPrice()).append("€\n");
            }
            p.sendMessage(sb.toString());
            return true;
        }

        if(args.length == 1) {
            sendPossabilities(p);
            return true;
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            Player tg = Script.getPlayer(args[1]);
            if(tg == null) {
                p.sendMessage(Blacklist.PREFIX + "Der Spieler ist nicht online.");
                return true;
            }
            if(!Blacklist.isOnBlacklist(tg, o)) {
                p.sendMessage(Blacklist.PREFIX + "Der Spieler ist nicht auf der Blacklist.");
                return true;
            }
            Blacklist.remove(tg, o);
            p.sendMessage(Blacklist.PREFIX + "Der Spieler wurde von der Blacklist entfernt.");
            tg.sendMessage(Blacklist.PREFIX + "Du wurdest von der Blacklist der " + o.getName() + " entfernt.");
            o.sendMessage(Blacklist.PREFIX + Script.getName(p) + " hat " + Script.getName(tg) + " von der Blacklist entfernt.");
            return true;
        }

        if(args.length == 2) {
            sendPossabilities(p);
            return true;
        }

        if(args.length >= 5 && args[0].equalsIgnoreCase("add")) {
            Player tg = Script.getPlayer(args[1]);
            if(tg == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }
            if(Blacklist.isOnBlacklist(tg, o)) {
                p.sendMessage(Blacklist.PREFIX + "Der Spieler ist bereits auf der Blacklist.");
                return true;
            }
            int kills;
            try {
                kills = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                p.sendMessage(Blacklist.PREFIX + "Bitte gebe eine Zahl an.");
                return true;
            }
            int price;
            try {
                price = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                p.sendMessage(Blacklist.PREFIX + "Bitte gebe eine Zahl an.");
                return true;
            }
            StringBuilder sb = new StringBuilder();
            for(int i = 4; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            String reason = sb.toString().trim();
            Blacklist.add(tg, o, reason, kills, price);
            p.sendMessage(Blacklist.PREFIX + "Der Spieler wurde auf die Blacklist gesetzt.");
            tg.sendMessage(Blacklist.PREFIX + "Du wurdest auf die Blacklist der " + o.getName() + " gesetzt.");
            o.sendMessage(Blacklist.PREFIX + Script.getName(p) + " hat " + Script.getName(tg) + " auf die Blacklist gesetzt.");
            return true;
        }

        sendPossabilities(p);

        return false;
    }

    private static String getTime(Blacklist bl) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(bl.getTime());

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);

        String time = mDay + "." + mMonth + "." + mYear + " " + mHour + ":" + mMinute;
        return time;
    }

    public static void sendPossabilities(Player p) {
        p.sendMessage("§8=== §6Blacklist §8===");
        p.sendMessage("§8» §6/blacklist add [Spieler] [Kills] [Preis] [Grund]");
        p.sendMessage("§8» §6/blacklist remove [Spieler]");
        p.sendMessage("§8» §6/blacklist list");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player killed = e.getEntity();
        Player killer = killed.getKiller();
        if (killer == null) return;

        Organisation f = Organisation.getOrganisation(killer);
        if (!Blacklist.isOnBlacklist(killed, f)) return;

        Blacklist bl = Blacklist.getBlacklistObject(Script.getNRPID(killed), f);
        int kills = bl.getKills();
        Script.addEXP(killer, Script.getRandom(3, 7));
        if (kills == 1) {
            f.sendMessage(Blacklist.PREFIX + Script.getName(killed) + " wurde automatisch von der Blacklist entfernt.");
            killed.sendMessage(Blacklist.PREFIX + "Du wurdest automatisch von der Blacklist der " + f.getName() + " entfernt.");
            Blacklist.remove(killed, f);
        } else {
            Organisation f1 = Organisation.getOrganisation(killer);
            killed.sendMessage(Blacklist.PREFIX + "Du wurdest getötet weil du auf der Blacklist der " + f1.getName() + " bist.");
            bl.setKills(--kills);
        }
    }

}
