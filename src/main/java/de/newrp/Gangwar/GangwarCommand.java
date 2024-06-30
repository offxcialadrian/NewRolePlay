package de.newrp.Gangwar;

import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import de.newrp.Organisationen.Drogen;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.AFK;
import de.newrp.Waffen.Waffen;
import de.newrp.Waffen.Weapon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GangwarCommand implements CommandExecutor, Listener {

    public static String PREFIX = "§8[§cGangwar§8] §c» §7";
    public static HashMap<GangwarZones, Organisation[]> gangwar = new HashMap<>();
    public static HashMap<Organisation, Integer> points = new HashMap<>();
    public static HashMap<Organisation, ArrayList<Location>> captures = new HashMap<>();
    public static HashMap<Location, Long> cooldown = new HashMap<>();

    private static final int MAX = 3;

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if (args.length > 0) {
            if (SDuty.isSDuty(p) && args[0].equalsIgnoreCase("standings")) {
                for (GangwarZones zone : gangwar.keySet()) {
                    Organisation[] orgs = gangwar.get(zone);
                    Organisation org1 = orgs[0];
                    Organisation org2 = orgs[1];


                    p.sendMessage(PREFIX + "Zone: " + zone.getName());
                    p.sendMessage(PREFIX + org1.getName() + " §8» §c" + points.get(org1) + " §8| §c" + points.get(org2) + " §8« §7" + org2.getName());
                }
                return true;
            }
        }

        if(!Organisation.hasOrganisation(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keiner Organisation.");
            return true;
        }

        if(Organisation.getOrganisation(p) == Organisation.HITMEN) {
            p.sendMessage(Messages.ERROR + "Deine Organisation kann nicht am Gangwar teilnehmen.");
            return true;
        }

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/gangwar");
            return true;
        }

        if(!Organisation.isLeader(p, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        GangwarZones zone = GangwarZones.getZoneByLocation(p.getLocation());
        if(zone == null) {
            p.sendMessage(Messages.ERROR + "Du befindest dich nicht in einer Gangwar-Zone.");
            return true;
        }

        if(zone.getOwner() == Organisation.getOrganisation(p)) {
            p.sendMessage(Messages.ERROR + "Diese Zone gehört bereits deiner Organisation.");
            return true;
        }

        if (GangwarZones.getGangwarZoneAmount(Organisation.getOrganisation(p)) >= MAX) {
            p.sendMessage(Messages.ERROR + "Deine Organisation hat das Maximum an Gangzones erreicht.");
            return true;
        }

        if(zone.getOwner() == null) {
            p.sendMessage(Messages.ERROR + "Diese Zone gehört keiner Organisation.");
            return true;
        }

        if(zone.getOwner().getMembers().isEmpty()) {
            p.sendMessage(Messages.ERROR + "Die Organisation, die diese Zone besitzt, hat zu wenig Mitglieder.");
            return true;
        }

        if (!Script.hasRank(p, Rank.FRAKTIONSMANAGER, false) || !SDuty.isSDuty(p)) {
            if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                p.sendMessage(Messages.ERROR + "Der Gangwar kann nur Sonntags stattfinden.");
                return true;
            }

            if ((Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 20 || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 21) && !Script.isInTestMode()) {
                p.sendMessage(Messages.ERROR + "Der Gangwar kann nur zwischen 20 und 21 Uhr stattfinden.");
                return true;
            }
        }

        if(isInGangwar(p)) {
            p.sendMessage(Messages.ERROR + "Du bist bereits im Gangwar.");
            return true;
        }

        if(gangwarIsActive(zone)) {
            p.sendMessage(Messages.ERROR + "In dieser Zone findet bereits ein Gangwar statt.");
            return true;
        }

        if(isInGangwar(Organisation.getOrganisation(p))) {
            p.sendMessage(Messages.ERROR + "Deine Organisation ist bereits im Gangwar.");
            return true;
        }

        if(isInGangwar(zone.getOwner())) {
            p.sendMessage(Messages.ERROR + "Die Organisation, die diese Zone besitzt, ist bereits im Gangwar.");
            return true;
        }

        Organisation org = Organisation.getOrganisation(p);
        Organisation enemy = zone.getOwner();

        gangwar.put(zone, new Organisation[]{org, enemy});
        points.put(org, 0);
        points.put(enemy, 0);
        captures.put(org, new ArrayList<>());
        captures.put(enemy, new ArrayList<>());

        org.sendMessage(PREFIX + "Deine Organisation hat den Gangwar in der Zone " + zone.getName() + " gestartet.");
        enemy.sendMessage(PREFIX + "Deine Organisation wurde von " + org.getName() + " zum Gangwar in der Zone " + zone.getName() + " herausgefordert.");
        for(Player org1member: org.getMembers()) {
            Title.sendTitle(org1member, 2, 50, 2, "§cDer Gangwar hat begonnen!", "§6" + zone.getName());
        }
        for(Player org2member: enemy.getMembers()) {
            Title.sendTitle(org2member, 2, 50, 2, "§cDer Gangwar hat begonnen!", "§6" + zone.getName());
        }

        for(Player member : getMember(zone)) {
            Cache.saveInventory(member);
            giveGangwarEquip(member);
        }


        return false;
    }

    public static boolean isInGangwar(Organisation org) {
        for(Organisation[] orgs : gangwar.values()) {
            for(Organisation o : orgs) {
                if(o == org) return true;
            }
        }
        return false;
    }


    public static boolean gangwarIsActive() {
        return !gangwar.isEmpty();
    }

    public static Organisation getOpponent(Organisation org) {
        for(GangwarZones zone : gangwar.keySet()) {
            Organisation[] orgs = gangwar.get(zone);
            for(Organisation o : orgs) {
                if(o != org) return o;
            }
        }
        return null;
    }

    public static boolean gangwarIsActive(GangwarZones zone) {
        return gangwar.containsKey(zone);
    }

    public static boolean isInGangwar(Player p) {
        Organisation org = Organisation.getOrganisation(p);
        for(Organisation[] orgs : gangwar.values()) {
            for(Organisation o : orgs) {
                if(o == org) return true;
            }
        }
        return false;
    }

    public static void processGangwar() {
        for(GangwarZones zone : gangwar.keySet()) {
            Organisation[] orgs = gangwar.get(zone);
            Organisation org1 = orgs[0];
            Organisation org2 = orgs[1];

            int capturedByOrg1 = 0;
            int capturedByOrg2 = 0;

            for(Location loc : captures.get(org1)) {
                capturedByOrg1++;
            }

            for(Location loc : captures.get(org2)) {
                capturedByOrg2++;
            }

            points.replace(org1, points.get(org1) + capturedByOrg1);
            points.replace(org2, points.get(org2) + capturedByOrg2);

            for(Organisation org : orgs) {
                org.sendMessage(PREFIX + "Punktestand: " + org1.getName() + " §8» §c" + points.get(org1) + " §8| §c" + points.get(org2) + " §8« §7" + org2.getName());
            }

            if(points.get(org1) >= zone.getPointsToWin()) {
                win(org1, zone);
            } else if(points.get(org2) >= zone.getPointsToWin()) {
                win(org2, zone);
            }

        }
    }

    public static void win(Organisation o, GangwarZones zone) {
        Organisation other = null;

        for(Player member : getMember(zone)) {
            Cache.loadInventory(member);
        }

        for(Organisation org : gangwar.get(zone)) {
            if(org != o) {
                other = org;
                break;
            }
        }

        o.sendMessage(PREFIX + "Deine Organisation hat den Gangwar in der Zone " + zone.getName() + " gewonnen.");
        o.addExp(points.get(o), true);
        for (UUID m : o.getMember()) if (Bukkit.getOfflinePlayer(m).isOnline()) if (!AFK.isAFK(m))
            Activity.grantActivity(Script.getNRPID(Bukkit.getPlayer(m)), Activities.GANGWAR);
        other.sendMessage(PREFIX + "Deine Organisation hat den Gangwar in der Zone " + zone.getName() + " verloren.");
        for (UUID m : other.getMember()) if (Bukkit.getOfflinePlayer(m).isOnline()) if (!AFK.isAFK(m))
            Activity.grantActivity(Script.getNRPID(Bukkit.getPlayer(m)), Activities.GANGWAR);
        other.addExp(points.get(other) / 2, true);

        if(zone.getOwner() != o) {
            zone.setOwner(o);
        }

        points.remove(o);
        points.remove(o);
        captures.remove(other);
        captures.remove(other);
        gangwar.remove(zone);

    }

    public static void endGangwar() {
        for(GangwarZones zone : gangwar.keySet()) {
            Organisation[] orgs = gangwar.get(zone);
            Organisation org1 = orgs[0];
            Organisation org2 = orgs[1];

            int capturedByOrg1 = 0;
            int capturedByOrg2 = 0;

            for(Location loc : captures.get(org1)) {
                capturedByOrg1++;
            }

            for(Location loc : captures.get(org2)) {
                capturedByOrg2++;
            }

            if(capturedByOrg1 > capturedByOrg2) {
                win(org1, zone);
            } else if(capturedByOrg2 > capturedByOrg1) {
                win(org2, zone);
            } else {
                for(Organisation org : orgs) {
                    org.sendMessage(PREFIX + "Der Gangwar in der Zone " + zone.getName() + " endete unentschieden.");
                }
            }

            points.remove(org1);
            points.remove(org2);
            captures.remove(org1);
            captures.remove(org2);
            gangwar.remove(zone);

        }
    }

    public static Location getRandomSpawnLocFromGangwar(Player p) {
        Organisation org = Organisation.getOrganisation(p);
        GangwarZones zone = null;
        for(GangwarZones z : gangwar.keySet()) {
            Organisation[] orgs = gangwar.get(z);
            for(Organisation o : orgs) {
                if(o == org) {
                    zone = z;
                    break;
                }
            }
        }

        if(zone == null) return null;

        Location[] spawns = zone.getSpawn();
        return spawns[(int) (Math.random() * spawns.length)];
    }

    public static void giveGangwarEquip(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        for(Weapon w : Weapon.values()) {
            if(w == Weapon.SNIPER) continue;
            if(w == Weapon.MP7) continue;

            p.getInventory().addItem(new ItemBuilder(Waffen.setAmmo(w.getWeapon(), w.getMagazineSize(), 500)).setNoDrop().build());
        }
        for (final Drogen drug : Drogen.values()) {
            if(drug == Drogen.ANTIBIOTIKA) {
                continue;
            }

            if(drug == Drogen.ECSTASY) {
                continue;
            }

            if(drug == Drogen.KRISTALLE) {
                continue;
            }

            p.getInventory().addItem(new ItemBuilder(drug.getMaterial()).setNoDrop().setName(drug.getName()).setLore("§7Reinheitsgrad: " + Drogen.DrugPurity.HIGH.getText()).setAmount(10).build());
        }
        p.getInventory().addItem(new ItemBuilder(Material.BREAD).setNoDrop().setAmount(32).build());
        //p.getInventory().addItem(new ItemBuilder(Material.POTION).setAmount(1).build());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Organisation o = Organisation.getOrganisation(p);
        if (o == null) return;
        if (!isInGangwar(p)) return;
        giveGangwarEquip(p);
        p.sendMessage(PREFIX + "Deine Organisation befindet sich im Gangwar!");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Cache.loadInventory(e.getPlayer());
    }

    public static ArrayList<Player> getMember(GangwarZones zone) {
        Organisation[] orgs = gangwar.get(zone);
        ArrayList<Player> players = new ArrayList<>();
        for(Organisation org : orgs) {
            players.addAll(org.getMembers());
        }
        return players;
    }

}
