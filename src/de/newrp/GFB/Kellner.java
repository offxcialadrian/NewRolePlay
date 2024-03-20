package de.newrp.GFB;

import de.newrp.API.Messages;
import de.newrp.API.PayDay;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Kellner implements CommandExecutor, Listener {

    private static String PREFIX = "§8[§6Kellner§8] §6" + Messages.ARROW + " §7";

    public enum Tables {
        TISCH1(1, "Tisch 1", new Location(Script.WORLD, 421, 70, 932)),
        TISCH2(2, "Tisch 2",new Location(Script.WORLD, 419, 70, 934)),
        TISCH3(3, "Tisch 3",new Location(Script.WORLD, 420, 70, 934)),
        TISCH4(4, "Tisch 4",new Location(Script.WORLD, 421, 70, 934)),
        TISCH5(5, "Tisch 5",new Location(Script.WORLD, 421, 70, 929)),
        TISCH6(6, "Tisch 6",new Location(Script.WORLD, 414, 69, 930)),
        TISCH7(7, "Tisch 7",new Location(Script.WORLD, 414, 69, 930)),
        TISCH8(8, "Tisch 8",new Location(Script.WORLD, 412, 69, 931)),
        TISCH9(9, "Tisch 9",new Location(Script.WORLD, 414, 69, 937)),
        TISCH10(10, "Tisch 10",new Location(Script.WORLD, 419, 69, 924)),
        TISCH11(11, "Tisch 11",new Location(Script.WORLD, 422, 69, 925)),
        TISCH12(12, "Tisch 12",new Location(Script.WORLD, 425, 69, 924)),
        TISCH13(13, "Tisch 13",new Location(Script.WORLD, 431, 76, 934)),
        TISCH14(14, "Tisch 14",new Location(Script.WORLD, 430, 76, 934)),
        TISCH15(15, "Tisch 15",new Location(Script.WORLD, 429, 76, 934)),
        TISCH16(16, "Tisch 16",new Location(Script.WORLD, 425, 76, 935)),
        TISCH17(17, "Tisch 17",new Location(Script.WORLD, 425, 76, 935)),
        TISCH18(18, "Tisch 18",new Location(Script.WORLD, 421, 76, 934)),
        TISCH19(19, "Tisch 19",new Location(Script.WORLD, 420, 76, 934)),
        TISCH20(20, "Tisch 20",new Location(Script.WORLD, 419, 76, 934)),
        TISCH21(21, "Tisch 21",new Location(Script.WORLD, 419, 76, 930)),
        TISCH22(22, "Tisch 22",new Location(Script.WORLD, 420, 76, 930)),
        TISCH23(23, "Tisch 23",new Location(Script.WORLD, 421, 76, 930));

        private final int id;
        private final String name;
        private final Location loc;

        Tables(int id, String name, Location loc) {
            this.id = id;
            this.name = name;
            this.loc = loc;
        }

        public int getID() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public Location getLocation() {
            return this.loc;
        }

        public static Tables getTableByID(int id) {
            for (Tables table : Tables.values()) {
                if (table.getID() == id) return table;
            }
            return null;
        }

        public static Tables getTableByName(String name) {
            for (Tables table : Tables.values()) {
                if (table.getName().equalsIgnoreCase(name)) return table;
            }
            return null;
        }

        public static Tables getTableByLocation(Location loc) {
            for (Tables table : Tables.values()) {
                if (table.getLocation().equals(loc)) return table;
            }
            return null;
        }

        public static Tables getRandomTable() {
            return Tables.values()[Script.getRandom(0, Tables.values().length - 1)];
        }

    }

    public static HashMap<String, Integer> SCORE = new HashMap<>();
    public static HashMap<String, Long> cooldown = new HashMap<>();
    public static HashMap<String, Tables> CURRENT = new HashMap<>();
    public static HashMap<String, Integer> TOTAL_SCORE = new HashMap<>();
    public static HashMap<String, Long> time = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/kellner");
            return true;
        }

        if(GFB.CURRENT.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du hast bereits einen Job.");
            return true;
        }

        if(p.getLocation().distance(new Location(Script.WORLD, 425, 69, 934, 272.7804f, 6.7590766f)) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Nähe des Restaurants.");
            return true;
        }

        if (cooldown.containsKey(p.getName())) {
            if (cooldown.get(p.getName()) > System.currentTimeMillis()) {
                p.sendMessage(Messages.ERROR + "Du musst noch " + Script.getRemainingTime(cooldown.get(p.getName())) + " warten.");
                return true;
            }
        }

        cooldown.put(p.getName(), System.currentTimeMillis() + 10 * 60 * 2000L);
        GFB.CURRENT.put(p.getName(), GFB.KELLNER);
        int totalscore = GFB.KELLNER.getLevel(p) + Script.getRandom(8, 12);
        SCORE.put(p.getName(), totalscore);
        TOTAL_SCORE.put(p.getName(), totalscore);
        p.sendMessage(PREFIX + "Du hast den Job §6Kellner §7angenommen.");
        p.sendMessage(Messages.INFO + "Klicke nun in der Küche auf das Schild \"Fertige Bestellungen\".");
        return false;
    }

    @EventHandler
    public void onClickOnSign(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(!GFB.CURRENT.containsKey(p.getName())) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getClickedBlock() == null) return;
        if(CURRENT.containsKey(p.getName())) return;
        if(!SCORE.containsKey(p.getName())) return;
        if(e.getClickedBlock().getType() != Material.OAK_SIGN && e.getClickedBlock().getType() != Material.OAK_WALL_SIGN) return;
        Sign sign = (Sign) e.getClickedBlock().getState();
        if(!sign.getLine(2).equalsIgnoreCase("§lBestellungen")) return;
        e.setCancelled(true);

        Tables table = Tables.getRandomTable();
        CURRENT.put(p.getName(), table);
        p.sendMessage(GFB.PREFIX + "Bringe nun die Bestellung zu §6" + table.getName() + "§7.");
        p.sendMessage(Messages.INFO + "Klicke auf den Tisch, um die Bestellung abzugeben.");
        time.put(p.getName(), System.currentTimeMillis());
    }

    @EventHandler
    public void onClickTable(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(!GFB.CURRENT.containsKey(p.getName())) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getClickedBlock() == null) return;
        if(!CURRENT.containsKey(p.getName())) return;
        if(e.getClickedBlock().getLocation().equals(CURRENT.get(p.getName()).getLocation())) {
            p.sendMessage(PREFIX + "Du hast die Bestellung erfolgreich abgegeben.");
            if(System.currentTimeMillis() - time.get(p.getName()) < 20 * 1000) {
                if(Script.getRandom(1,100)<=20) {
                    p.sendMessage(PREFIX + "Du warst sehr schnell und hast ein Trinkgeld erhalten.");
                    Script.addMoney(p, PaymentType.CASH, Script.getRandom(1, 2));
                    time.remove(p.getName());
                }
            } else if(System.currentTimeMillis() - time.get(p.getName()) > 60 * 1000) {
                p.sendMessage(PREFIX + "Du warst zu langsam und der Kunde hat sich beschwert.");
                GFB.KELLNER.removeExp(p, GFB.KELLNER.getLevel(p) * 2);
                time.remove(p.getName());
            }
            CURRENT.remove(p.getName());
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            int left = SCORE.get(p.getName()) - 1;
            if(left == 0) {
                GFB.KELLNER.addExp(p, GFB.KELLNER.getLevel(p) + Script.getRandom(5, 7));
                SCORE.remove(p.getName());
                GFB.CURRENT.remove(p.getName());
                Script.addEXP(p, GFB.KELLNER.getLevel(p) + Script.getRandom(5, 7));
                PayDay.addPayDay(p, GFB.KELLNER.getLevel(p) + TOTAL_SCORE.get(p.getName()));
                TOTAL_SCORE.remove(p.getName());
                return;
            }
            SCORE.put(p.getName(), left);
            p.sendMessage(PREFIX + "Du hast noch " + left + " Bestellungen zu erledigen.");
            p.sendMessage(Messages.INFO + "Klicke nun in der Küche auf das Schild \"Fertige Bestellungen\".");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(!GFB.CURRENT.containsKey(p.getName())) return;
        GFB.CURRENT.remove(p.getName());
        if(SCORE.containsKey(p.getName())) SCORE.remove(p.getName());
        if(cooldown.containsKey(p.getName())) cooldown.remove(p.getName());
    }

}
