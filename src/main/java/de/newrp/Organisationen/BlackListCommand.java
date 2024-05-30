package de.newrp.Organisationen;

import de.newrp.API.*;
import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.GoTo;
import de.newrp.Administrator.SDuty;
import de.newrp.Player.AFK;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.StringUtil;

import java.lang.reflect.Array;
import java.util.*;

public class BlackListCommand implements CommandExecutor, Listener, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("bl") || cmd.getName().equalsIgnoreCase("blacklist")) {
            if (!Organisation.hasOrganisation(p)) return Collections.EMPTY_LIST;
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();
            for (Reasons reason : Reasons.getReasons(Organisation.getOrganisation(p))) {
                oneArgList.add(reason.getName().replace(" ", "-"));
            }

            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[args.length - 1], Arrays.asList("add", "remove", "list", "info"), completions);
            }

            if (args.length == 2) {
                return null;
            }

            if (args.length >= 3) {
                StringUtil.copyPartialMatches(args[args.length - 1], oneArgList, completions);
            }

            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }

    public enum Reasons {
        GANGZONE("Gangzones", 500, 50, new Organisation[] {Organisation.CORLEONE, Organisation.KARTELL}),
        ORGASCHÄDIGUNG("Organisationsschädigung", 800, 60, new Organisation[] {Organisation.CORLEONE, Organisation.KARTELL}),
        BLUTRACHE("Blutrache", 300, 25, new Organisation[] {Organisation.CORLEONE}),
        VOGELFREI("Vogelfrei", 1, 0, new Organisation[] {Organisation.CORLEONE, Organisation.KARTELL, Organisation.FALCONE, Organisation.SINALOA}),
        LEICHENBEWACHUNG("Leichenbewachung", 600, 20, new Organisation[] {Organisation.CORLEONE, Organisation.KARTELL}),
        LEADERMORD_KARTELL("Leadermord", 1000, 50, new Organisation[] {Organisation.CORLEONE, Organisation.KARTELL}),
        PROVOKATION_KARTELL("Provokation", 250, 10, new Organisation[] {Organisation.KARTELL}),
        Vendetta_FALCONE("Vendetta", 600, 50, new Organisation[] {Organisation.FALCONE}),
        Tradimento_FALCONE("Tradimento", 500, 50, new Organisation[] {Organisation.FALCONE}),
        Diffamazione_FALCONE("Diffamazione", 200, 20, new Organisation[] {Organisation.FALCONE}),
        Invasione_FALCONE("Invasione", 300, 30, new Organisation[] {Organisation.FALCONE}),
        Vergogna_FALCONE("Vergogna", 1, 1, new Organisation[] {Organisation.FALCONE}),
        Inganno_FALCONE("Inganno", 200, 20, new Organisation[] {Organisation.FALCONE}),
        ADM("Asesinato de Miembros", 1000, 3, new Organisation[] {Organisation.SINALOA}),
        FDR("Falta de Respeto", 1000, 1, new Organisation[] {Organisation.SINALOA}),
        FDRG("Falta de Respeto Grave", 1000, 5, new Organisation[] {Organisation.SINALOA}),
        TRAICION("Traición", 2000, 15, new Organisation[] {Organisation.SINALOA}),
        ESPIONAJE("Espionaje", 3000, 15, new Organisation[] {Organisation.SINALOA});

        private final String name;
        private final int price;
        private final int kills;
        private final Organisation[] organisations;

        Reasons(String name, int price, int kills, Organisation[] organisations) {
            this.name = name;
            this.price = price;
            this.kills = kills;
            this.organisations = organisations;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }

        public int getKills() {
            return kills;
        }

        public Organisation[] getOrganisations() {
            return organisations;
        }

        public boolean hasReason(Organisation o) {
            for(Organisation f : organisations) {
                if(f == o) return true;
            }
            return false;
        }

        public static Reasons getReason(String name) {
            for(Reasons r : values()) {
                if(r.getName().equalsIgnoreCase(name.replace("-"," "))) return r;
            }
            return null;
        }

        public static ArrayList<Reasons> getReasons(Organisation o) {
            ArrayList<Reasons> reasons = new ArrayList<>();
            for(Reasons r : values()) {
                if(r.hasReason(o)) reasons.add(r);
            }
            return reasons;
        }
    }

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
            p.sendMessage(Blacklist.PREFIX + "Deine Organisation hat keine Blacklist. Ihr schaltet die Blacklist mit Level-2 frei.");
            return true;
        }

        if(args[0].equalsIgnoreCase("list")) {
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
                            .append(" §7|§a ").append(bl.getKills()).append(" Kills §7|§a ").append(bl.getPrice()).append("€").append((AFK.isAFK(p1) ? " (AFK seit " + AFK.getAFKTime(p1) +  " Uhr)\n" : "\n"));
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

        if (args.length > 1 && args[0].equalsIgnoreCase("info")) {
            OfflinePlayer tg = Script.getOfflinePlayer(args[1]);
            if(Script.getNRPID(tg) == 0) {
                p.sendMessage(Blacklist.PREFIX + "Der Spieler wurde nicht gefunden.");
                return true;
            }

            if (!Blacklist.isOnBlacklist(tg, o)) {
                p.sendMessage(Blacklist.PREFIX + "Der Spieler ist nicht auf der Blacklist.");
                return true;
            }

            List<Blacklist> blacklist = Blacklist.getBlacklist(o);
            StringBuilder sb = new StringBuilder("§8==== §eBlacklist ").append(o.getName()).append(" §7(§6").append(blacklist.size()).append("§7)§e §8====\n");
            for (Blacklist bl : blacklist) {
                if (Objects.equals(bl.getUserName(), tg.getName())) {
                    String time = getTime(bl);
                    sb.append(" §7» §6").append(Script.getName(tg)).append(" §7|§6 ").append(bl.getReason()).append(" §7|§6 ").append(time)
                            .append(" §7|§a ").append(bl.getKills()).append(" Kills §7|§6 ").append(bl.getPrice()).append("€");
                    if (tg.isOnline()) {
                        sb.append((AFK.isAFK(Objects.requireNonNull(tg.getPlayer())) ? " (AFK seit " + AFK.getAFKTime(tg.getPlayer()) + " Uhr)\n" : "\n"));
                    }
                }
            }
            p.sendMessage(sb.toString());
            return true;
        }

        if (args.length > 1 && args[0].equalsIgnoreCase("remove")) {
            OfflinePlayer tg = Script.getOfflinePlayer(args[1]);
            if (Script.getNRPID(tg) == 0) {
                p.sendMessage(Blacklist.PREFIX + "Der Spieler wurde nicht gefunden.");
                return true;
            }
            if (!Blacklist.isOnBlacklist(tg, o)) {
                p.sendMessage(Blacklist.PREFIX + "Der Spieler ist nicht auf der Blacklist.");
                return true;
            }

            if (Organisation.getRank(p) < 4) {
                p.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            Blacklist.remove(Script.getNRPID(tg), o);
            p.sendMessage(Blacklist.PREFIX + "Der Spieler wurde von der Blacklist entfernt.");
            if(tg.getPlayer() != null) tg.getPlayer().sendMessage(Blacklist.PREFIX + "Du wurdest von der Blacklist der " + o.getName() + " entfernt.");
            o.sendMessage(Blacklist.PREFIX + Script.getName(p) + " hat " + Script.getName(tg) + " von der Blacklist entfernt.");
            if(tg.getPlayer()!=null) Script.updateBlackListSubtitle(tg.getPlayer());
            return true;
        }

        if(args.length > 2 &&args[0].equalsIgnoreCase("add")) {
            Player tg = Script.getPlayer(args[1]);
            if(tg == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }
            if(Blacklist.isOnBlacklist(tg, o)) {
                p.sendMessage(Blacklist.PREFIX + "Der Spieler ist bereits auf der Blacklist.");
                return true;
            }

            if(tg == p) {
                p.sendMessage(Blacklist.PREFIX + "Du kannst dich nicht selbst auf die Blacklist setzen.");
                return true;
            }

            if(Organisation.hasOrganisation(tg) && Organisation.getOrganisation(tg) == o) {
                p.sendMessage(Blacklist.PREFIX + "Du kannst keine Mitglieder deiner Organisation auf die Blacklist setzen.");
                return true;
            }

            if(Organisation.getRank(p) < 3) {
                p.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if(Organisation.getOrganisation(tg) == o) {
                p.sendMessage(Blacklist.PREFIX + "Du kannst keine Mitglieder deiner Organisation auf die Blacklist setzen.");
                return true;
            }

            if(SDuty.isSDuty(tg)) {
                p.sendMessage(Blacklist.PREFIX + "Der Spieler ist im Supporter-Dienst.");
                return true;
            }

            ArrayList<Reasons> reason = new ArrayList<>();

            for(int i = 2; i < args.length; i++) {
                if(Reasons.getReason(args[i]) == null) {
                    p.sendMessage(Blacklist.PREFIX + "Der Grund " + args[i] + " existiert nicht.");
                    continue;
                }

                if(!Reasons.getReason(args[i]).hasReason(o)) {
                    p.sendMessage(Blacklist.PREFIX + "Der Grund " + args[i] + " ist nicht für deine Organisation verfügbar.");
                    continue;
                }

                reason.add(Reasons.getReason(args[i].replace("-"," ")));
            }

            if(reason.isEmpty()) {
                p.sendMessage(Blacklist.PREFIX + "Es wurde kein gültiger Grund gefunden.");
                return true;
            }

            StringBuilder reasons = new StringBuilder();
            int price = 0;
            int kills = 0;
            for(Reasons r : reason) {
                price += r.getPrice();
                kills += r.getKills();
                if(reason.indexOf(r) != reason.size()-1) {
                    reasons.append(r.getName()).append(", ");
                } else {
                    reasons.append(r.getName());
                }
            }

            int orglevel = o.getLevel();
            if(orglevel == 1) {
                price = Math.min(5000, price);
                kills = Math.min(50, kills);
            } else {
                price = Math.min(5000, price*(Math.min(1, o.getLevel()/2)));
                kills = Math.min(50, kills*(Math.min(1, o.getLevel()/2)));
            }


            Blacklist.add(tg, o, reasons.toString(), kills, price);
            p.sendMessage(Blacklist.PREFIX + "Der Spieler wurde auf die Blacklist gesetzt.");
            tg.sendMessage(Blacklist.PREFIX + "Du wurdest auf die Blacklist der " + o.getName() + " gesetzt (Grund: " + reasons.toString() + " | Preis: " + price + "€)");
            o.sendMessage(Blacklist.PREFIX + Script.getName(p) + " hat " + Script.getName(tg) + " auf die Blacklist gesetzt.");
            o.sendMessage(Blacklist.PREFIX + "Grund: " + reasons.toString() + " | Preis: " + price + "€");
            Script.updateBlackListSubtitle(tg);
            return true;
        }

        sendPossabilities(p);
        return true;

    }

    private static String getTime(Blacklist bl) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(bl.getTime());

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        int mHour = calendar.get(Calendar.HOUR);
        int mMinute = calendar.get(Calendar.MINUTE);

        String time = mDay + "." + mMonth + "." + mYear + " " + mHour + ":" + mMinute;
        return time;
    }

    public static void sendPossabilities(Player p) {
        p.sendMessage("§8=== §6Blacklist §8===");
        p.sendMessage("§8» §6/blacklist add [Spieler] [Grund]");
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
        f.addExp(Script.getRandom(5, 15));
        f.sendMessage(Blacklist.PREFIX + Script.getName(killer) + " hat " + Script.getName(killed) + " getötet. (" + (bl.getKills()-1) + "/" + bl.getKills() + " Kills)");
        if (kills == 1) {
            f.sendMessage(Blacklist.PREFIX + Script.getName(killed) + " wurde automatisch von der Blacklist entfernt.");
            killed.sendMessage(Blacklist.PREFIX + "Du wurdest automatisch von der Blacklist der " + f.getName() + " entfernt.");
            Blacklist.remove(killed, f);
        } else {
            Organisation f1 = Organisation.getOrganisation(killer);
            killed.sendMessage(Blacklist.PREFIX + "Du wurdest getötet weil du auf der Blacklist der " + f1.getName() + " bist.");
            Activity.grantActivity(Script.getNRPID(killer), Activities.BLKILL);
            bl.setKills(--kills);
        }
    }

}
