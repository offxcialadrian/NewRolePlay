package de.newrp.Organisationen;

import de.newrp.API.*;
import de.newrp.Administrator.Notifications;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Chat.Me;
import de.newrp.NewRoleplayMain;
import de.newrp.Player.AFK;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryView;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RobCommand implements CommandExecutor {

    public static String PREFIX = "§8[§4Rob§8] §4" + Messages.ARROW + " §7";

    public static HashMap<UUID, Long> cooldownsP = new HashMap<>();
    public static HashMap<UUID, Long> cooldownsV = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (Organisation.hasOrganisation(player)) {
                if (cooldownsP.containsKey(player.getUniqueId())) {
                    if (cooldownsP.get(player.getUniqueId()) > System.currentTimeMillis()) {
                        long left = cooldownsP.get(player.getUniqueId()) - System.currentTimeMillis();
                        player.sendMessage(PREFIX + "Du kannst erst in " + TimeUnit.MILLISECONDS.toMinutes(left) + " Minuten wieder eine Person ausrauben!");
                        return true;
                    }
                }

                Player victim = null;
                for (Player v : Bukkit.getServer().getOnlinePlayers()) {
                    if (v != player) {
                        if (!AFK.isAFK(v)) {
                            if (!SDuty.isSDuty(v)) {
                                double d = v.getLocation().distance(player.getLocation());
                                if (d <= 1) {
                                    if (victim == null) victim = v;
                                    else if (d < victim.getLocation().distance(player.getLocation())) victim = v;
                                }
                            }
                        }
                    }
                }

                if (victim == null) {
                    player.sendMessage(PREFIX + "Es gibt keine Person zum Ausrauben in deiner Nähe!");
                } else {
                    if (Organisation.hasOrganisation(victim)) {
                        if (Organisation.getOrganisation(player) == Organisation.getOrganisation(victim)) {
                            player.sendMessage(PREFIX + "Du kannst deine eigenen Mitglieder nicht ausrauben!");
                            return true;
                        }
                    }
                    if (Beruf.hasBeruf(victim)) {
                        Beruf.Berufe beruf = Beruf.getBeruf(victim);
                        if (beruf == Beruf.Berufe.GOVERNMENT || beruf == Beruf.Berufe.POLICE || beruf == Beruf.Berufe.RETTUNGSDIENST) {
                            player.sendMessage(PREFIX + "Du kannst Mitglieder des Staates nicht ausrauben!");
                            return true;
                        }
                    }
                    if (Script.getLevel(victim) < 3) {
                        player.sendMessage(PREFIX + "Du kannst nur Spieler über Level-3 ausrauben!");
                        return true;
                    }
                    if (cooldownsV.containsKey(victim.getUniqueId())) {
                        if (cooldownsV.get(victim.getUniqueId()) > System.currentTimeMillis()) {
                            player.sendMessage(PREFIX + "Diese Person kann gerade nicht ausgeraubt werden!");
                            return true;
                        }
                    }

                    if (!(victim.getOpenInventory() instanceof CraftInventoryView)) {
                        player.sendMessage(PREFIX + "Diese Person kann gerade nicht ausgeraubt werden!");
                        return true;
                    }

                    cooldownsP.put(player.getUniqueId(), System.currentTimeMillis() + 10 * 60 * 1000);

                    int money = Script.getMoney(victim, PaymentType.CASH);
                    if (money > 500) money = 500;
                    money = Math.round(money * (0.4F + (new Random().nextFloat() / 3)));
                    Me.sendMessage(player, "greift in die Brieftasche von " + victim.getName() + ".");

                    Player finalVictim = victim;
                    Location finalLocation = victim.getLocation();
                    int finalMoney = money;
                    Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
                        if (player.getLocation().distance(finalVictim.getLocation()) <= 2) {
                            if (finalLocation.distance(finalVictim.getLocation()) <= 2) {
                                Me.sendMessage(player, "nimmt etwas aus der Brieftasche von " + finalVictim.getName() + " heraus.");
                                player.sendMessage(PREFIX + "Du hast " + finalMoney + "€ von " + finalVictim.getName() + " gestohlen.");
                                int exp = finalMoney / 5;
                                if (exp > 50) exp = 50;
                                Script.addEXP(player, exp, true);
                                Script.removeMoney(finalVictim, PaymentType.CASH, finalMoney);
                                Script.addMoney(player, PaymentType.CASH, finalMoney);
                                cooldownsV.put(finalVictim.getUniqueId(), System.currentTimeMillis() + 2 * 60 * 60 * 1000);
                                Log.LOW.write(finalVictim, "bekommt von " + Script.getName(player) + " " + finalMoney + "€ gestohlen.");
                                Log.LOW.write(player, "stiehlt von " + Script.getName(finalVictim) + " " + finalMoney + "€.");
                                Notifications.sendMessage(Notifications.NotificationType.PAYMENT, Script.getName(player) + " hat " + Script.getName(finalVictim) + " " + finalMoney + "€ gestohlen. (CASH)");
                                Activity.grantActivity(Script.getNRPID(player), Activities.ROB);
                            } else {
                                player.sendMessage(PREFIX + "Die Person hat sich zu viel bewegt!");
                            }
                        } else {
                            player.sendMessage(PREFIX + "Du hast dich zu weit von der Person entfernt!");
                        }
                    }, 10 * 20L);
                }
            } else {
                player.sendMessage(Messages.ERROR + "Du musst in einer Organisation sein um andere Personen auszurauben.");
            }
        }
        return true;
    }
}
