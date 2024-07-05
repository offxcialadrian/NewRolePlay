package de.newrp.Berufe;

import de.newrp.API.*;
import de.newrp.Gangwar.GangwarCommand;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.Mobile;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Reinforcement implements CommandExecutor {

    private final HashMap<String, Types> reinf_type = new HashMap<>();
    private static final HashMap<String, Location> new_reinforcement = new HashMap<>();

    enum Types {
        NORMAL(0, "Unterstützung benötigt!", "", false),
        BUENDNIS(1, "Unterstützung benötigt!", "-d", true),
        DRINGEND_NORMAL(2, "DRINGEND!", "-e", false),
        DRINGEND_BUENDNIS(3, "DRINGEND!", "-ed", true),
        MEDIC(4, "Arzt benötigt!", "-m", true),
        LEICHENBEWACHUNG(5, "Leichenbewachung!", "-lb", true),
        PFANDNAHME(6, "Pfandnahme!", "-gn", true),
        RAMMEN(7, "Rammen!", "-r", false),
        PLANT(8, "Plant!", "-p", false),
        TRAINING_NORMAL(9, "Training!", "-t", false),
        TRAINING_BUENDNIS(10, "Training!", "-td", true),
        RAMMEN_BUENDNIS(11, "Rammen!", "-rd", true);

        int id;
        String name;
        String shorten;
        boolean d_chat;

        Types(int id, String name, String shorten, boolean d_chat) {
            this.id = id;
            this.name = name;
            this.shorten = shorten;
            this.d_chat = d_chat;
        }

        public int getID() {
            return id;
        }

        public String getShorten() {
            return shorten;
        }

        public String getName() {
            return name;
        }

        public boolean isDChat() {
            return d_chat;
        }

        public static Types getType(String s) {
            for (Types types : Types.values()) {
                if (types.getShorten().equals(s)) return types;
            }
            return null;
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {

        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p) && !Organisation.hasOrganisation(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf!");
            return true;
        }

        if (Sperre.MUTE.isActive(Script.getNRPID(p))) {
            p.sendMessage(Messages.ERROR + "Du bist gemuted.");
            return false;
        }

        Beruf.Berufe beruf = Beruf.getBeruf(p);

        if (args.length > 2) {
            p.sendMessage(Messages.ERROR + "/reinforcement <Kürzel>");
            return true;
        }

        if (Beruf.hasBeruf(p)) {
            if (!Duty.isInDuty(p)) {
                p.sendMessage(Messages.ERROR + "Du musst im Dienst sein.");
                return true;
            }
        }

        if(!GangwarCommand.isInGangwar(p)) {
            if (!Mobile.hasPhone(p)) {
                p.sendMessage(Messages.ERROR + "Du benötigst ein Handy.");
                return true;
            }

            if (!Mobile.mobileIsOn(p)) {
                p.sendMessage(Messages.ERROR + "Dein Handy ist ausgeschaltet.");
                return true;
            }
        }

        if (args.length == 2) {
            if (args[0].equals("omw")) {
                Player tg = Script.getPlayer(args[1]);
                if (tg == null) {
                    p.sendMessage(Messages.PLAYER_NOT_FOUND);
                    return true;
                }

                if (!new_reinforcement.containsKey(tg.getName())) {
                    p.sendMessage(Messages.ERROR + "Es wurde kein Verstärkungsruf von " + Script.getName(tg) + " gesendet.");
                    return true;
                }

                new Route(p.getName(), Script.getNRPID(p), p.getLocation(), new_reinforcement.get(tg.getName())).start();
                if (Organisation.hasOrganisation(tg)) {
                    Organisation org = Organisation.getOrganisation(tg);
                    if (org.getMember().contains(tg.getUniqueId())) {
                        for (UUID member : org.getMember()) {
                            Bukkit.getPlayer(member).sendMessage("§7➲ §a" + Organisation.getRankName(p) + " " + Script.getName(p) + " kommt zum Verstärkungsruf von " + Script.getName(tg) + "! §a" + "(ETA: " + calcETA(p.getLocation().distance(new_reinforcement.get(tg.getName()))) + " Sekunden & " + (int) p.getLocation().distance(new_reinforcement.get(tg.getName())) + "m)");
                        }
                    }
                    return true;
                }

                if (reinf_type.get(tg.getName()).isDChat()) {

                    for (Beruf.Berufe berufe : Beruf.Berufe.values()) {
                        if (berufe != Beruf.Berufe.RETTUNGSDIENST && berufe != Beruf.Berufe.POLICE && berufe != Beruf.Berufe.GOVERNMENT)
                            continue;
                        berufe.sendMessage("§7➲ §a" + Beruf.getBeruf(p).getName() + " " + Script.getName(p) + " kommt zum Verstärkungsruf von " + Script.getName(tg) + "! §7" + "(ETA: " + calcETA(p.getLocation().distance(new_reinforcement.get(tg.getName()))) + " Sekunden & " + (int) p.getLocation().distance(new_reinforcement.get(tg.getName())) + "m)");
                    }
                } else {
                    beruf.sendMessage("§7➲ §a" + Beruf.getAbteilung(p, true).getName() + " " + Script.getName(p) + " kommt zum Verstärkungsruf von " + Script.getName(tg) + "! §7" + "(ETA: " + calcETA(p.getLocation().distance(new_reinforcement.get(tg.getName()))) + " Sekunden & " + (int) p.getLocation().distance(new_reinforcement.get(tg.getName())) + "m)");
                }
            }
            return true;
        }

        if (new_reinforcement.containsKey(p.getName()) || reinf_type.containsKey(p.getName())) {
            new_reinforcement.remove(p.getName());
            reinf_type.remove(p.getName());
        }

        if (args.length == 0) {
            Types type = Types.NORMAL;
            new_reinforcement.put(p.getName(), p.getLocation());
            reinf_type.put(p.getName(), type);
            if (Organisation.hasOrganisation(p)) {
                Organisation org = Organisation.getOrganisation(p);
                if (org.getMember().contains(p.getUniqueId())) {
                    for (UUID member : org.getMember()) {
                        Bukkit.getPlayer(member).sendMessage("§c§l" + type.getName() + " §a" + Organisation.getRankName(p) + " " + Script.getName(p) + " benötigt Unterstützung! §8➥ §7" + Navi.getNextNaviLocation(p.getLocation()).getName() + " §7(" + (int) Bukkit.getPlayer(member).getLocation().distance(p.getLocation()) + "m)");
                        OnMyWayLink(Bukkit.getPlayer(member), p);
                        showRoute(Bukkit.getPlayer(member), p);
                    }
                    return true;
                }
            }
            for (UUID member : Objects.requireNonNull(beruf.getMember())) {
                Objects.requireNonNull(Bukkit.getPlayer(member)).sendMessage("§c§l" + type.getName() + " §a" + Beruf.getAbteilung(p, true).getName() + " " + Script.getName(p) + " benötigt Unterstützung! §8➥ §7" + Navi.getNextNaviLocation(p.getLocation()).getName() + " §7(" + (int) Objects.requireNonNull(Bukkit.getPlayer(member)).getLocation().distance(p.getLocation()) + "m)");
                OnMyWayLink(Objects.requireNonNull(Bukkit.getPlayer(member)), p);
                showRoute(Objects.requireNonNull(Bukkit.getPlayer(member)), p);
            }
            return true;
        }

        Types type = Types.getType(args[0]);

        if (type == null) {
            p.sendMessage(Messages.ERROR + "Kürzel nicht gefunden!");
            return true;
        }

        new_reinforcement.put(p.getName(), p.getLocation());
        reinf_type.put(p.getName(), type);

        if (Organisation.hasOrganisation(p)) {
            Organisation org = Organisation.getOrganisation(p);
            if (org.getMember().contains(p.getUniqueId())) {
                for (UUID member : org.getMember()) {
                    Bukkit.getPlayer(member).sendMessage("§c§l" + type.getName() + " §a" + org.getName() + " " + Script.getName(p) + " benötigt Unterstützung! §8➥ §7" + Navi.getNextNaviLocation(p.getLocation()).getName() + " §7(" + (int) Bukkit.getPlayer(member).getLocation().distance(p.getLocation()) + "m)");
                    OnMyWayLink(Bukkit.getPlayer(member), p);
                    showRoute(Bukkit.getPlayer(member), p);
                }
                return true;
            }
        }

        if (type.isDChat()) {
            if (beruf != Beruf.Berufe.RETTUNGSDIENST && beruf != Beruf.Berufe.POLICE && beruf != Beruf.Berufe.GOVERNMENT) {
                p.sendMessage(Messages.ERROR + "Du bist nicht im Rettungsdienst, Polizei oder Regierung.");
                return true;
            }

            Set<Player> staatler = new HashSet<>();
            for (UUID uuid : Objects.requireNonNull(Beruf.Berufe.RETTUNGSDIENST.getMember())) staatler.add(Bukkit.getPlayer(uuid));
            for (UUID uuid : Objects.requireNonNull(Beruf.Berufe.POLICE.getMember())) staatler.add(Bukkit.getPlayer(uuid));
            for (UUID uuid : Objects.requireNonNull(Beruf.Berufe.GOVERNMENT.getMember())) staatler.add(Bukkit.getPlayer(uuid));
            for (Player member : staatler) {
                member.sendMessage("§c§l" + type.getName() + " §a" + Beruf.getBeruf(p).getName() + " " + Script.getName(p) + " benötigt Unterstützung! §8➥ §7" + Navi.getNextNaviLocation(p.getLocation()).getName() + " §7(" + (int) member.getLocation().distance(p.getLocation()) + "m)");
                OnMyWayLink(member, p);
                showRoute(member, p);
            }
        } else {
            for (UUID member : Objects.requireNonNull(beruf.getMember())) {
                Objects.requireNonNull(Bukkit.getPlayer(member)).sendMessage("§c§l" + type.getName() + " §a" + Beruf.getAbteilung(p, true).getName() + " " + Script.getName(p) + " benötigt Unterstützung! §8➥ §7" + Navi.getNextNaviLocation(p.getLocation()).getName() + " §7(" + (int) Objects.requireNonNull(Bukkit.getPlayer(member)).getLocation().distance(p.getLocation()) + "m)");
                OnMyWayLink(Objects.requireNonNull(Bukkit.getPlayer(member)), p);
                showRoute(Objects.requireNonNull(Bukkit.getPlayer(member)), p);
            }
        }
        return true;
    }

    private static void OnMyWayLink(Player p, Player tg) {
        TextComponent x = new TextComponent("Kommen");
        x.setText("   §9§l➤ Zum Verstärkungsruf kommen!");
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reinforcement omw " + tg.getName());
        x.setClickEvent(clickEvent);
        p.spigot().sendMessage(x);
    }

    private static void showRoute(Player p, Player tg) {
        TextComponent x = new TextComponent("Route anzeigen!");
        x.setText("   §9§l➤ Route anzeigen!");
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/navi " + new_reinforcement.get(tg.getName()).getBlockX() + "/" + new_reinforcement.get(tg.getName()).getBlockY() + "/" + new_reinforcement.get(tg.getName()).getBlockZ());
        x.setClickEvent(clickEvent);
        p.spigot().sendMessage(x);
    }


    private static int calcETA(double meter) {
        return (int) (meter / 6.0);
    }
}