package de.newrp.Organisationen;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Chat.Me;
import de.newrp.Government.Stadtkasse;
import de.newrp.NewRoleplayMain;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BreakinCommand implements CommandExecutor {

    public static String PREFIX = "§8[§dBreak-in§8] §d" + Messages.ARROW + " §7";

    HashMap<Organisation, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();

            assert player != null;
            if (Organisation.hasOrganisation(player)) {
                if (Organisation.getRank(player) >= 2) {
                    Organisation orga = Organisation.getOrganisation(player);
                    if (RobLocation.getLocations(orga).isEmpty()) {
                        player.sendMessage(Messages.ERROR + "Du kannst nichts ausrauben!");
                        return true;
                    }

                    RobLocation rob = RobLocation.getRob(orga, player.getLocation());
                    if (rob != null) {
                        if (cooldowns.containsKey(orga)) {
                            if (cooldowns.get(orga) > System.currentTimeMillis()) {
                                long left = cooldowns.get(orga) - System.currentTimeMillis();
                                player.sendMessage(PREFIX + "Ihr könnt einen Raub erst in " + TimeUnit.MILLISECONDS.toMinutes(left) + " Minuten wieder versuchen!");
                                return true;
                            }
                        }

                        if (player.getInventory().getItemInMainHand().getType() != Material.BLAZE_ROD) {
                            player.sendMessage(PREFIX + "Du benötigst eine Brechstange zum Aufbrechen.");
                            return true;
                        }

                        cooldowns.put(orga, System.currentTimeMillis() + 2 * 60 * 60 * 1000);

                        orga.sendMessage(PREFIX + player.getName() + " beginnt bei " + rob.getName() + " aufzubrechen.");
                        Me.sendMessage(player, "startet einen Einbruch.");

                        LockpickHandler.c.put(player, 1);
                        LockpickHandler.a.put(player, 1);
                        LockpickHandler.f.put(player, 0);
                        LockpickHandler.value.put(player, 0);
                        LockpickHandler.pulver.put(player, 0);
                        LockpickHandler.kraeuter.put(player, 0);
                        LockpickHandler.next(player, rob.getType());

                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
                            if (player.getOpenInventory().title() instanceof TextComponent) {
                                if (((TextComponent) player.getOpenInventory().title()).content().contains("Schloss")) {
                                    player.closeInventory();

                                    if (Objects.equals(rob.getType(), "Kasse")) {
                                        if (LockpickHandler.value.get(player) > 0) {
                                            orga.sendMessage(PREFIX + player.getName() + " hat erfolgreich " + LockpickHandler.value.get(player) + "€ aus der Kasse gestohlen.");
                                            Script.addMoney(player, PaymentType.CASH, LockpickHandler.value.get(player));
                                            Stadtkasse.removeStadtkasse(LockpickHandler.value.get(player), "Raub bei " + rob.getName() + " von " + player.getName());
                                            orga.addExp(LockpickHandler.value.get(player) / 100);
                                            Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + "Die Stadtkasse ist für den Raub bei " + rob.getName() + " aufgekommen.");
                                            Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Raub bei " + rob.getName() + " konnte nicht verhindert werden.");
                                            Beruf.Berufe.POLICE.sendMessage(PREFIX + "Aufnahmen zeigen, dass " + player.getName() + " verantwortlich ist."); // Kein Bug, zur Fairness nur bei Geldraub Aufnahme;
                                        } else {
                                            orga.sendMessage(PREFIX + player.getName() + " hat es nicht geschafft Geld aus der Kasse zu stehlen.");
                                            Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Raub bei " + rob.getName() + " wurde verhindert.");
                                        }
                                    } else if (Objects.equals(rob.getType(), "Lager")) {
                                        if (LockpickHandler.pulver.get(player) > 0 || LockpickHandler.kraeuter.get(player) > 0) {
                                            for (int i = 0; i < LockpickHandler.pulver.get(player); i++) {
                                                player.getInventory().addItem(new ItemBuilder(Material.SUGAR).setName(Drogen.PULVER.getName()).setLore("§7Reinheitsgrad: " + Drogen.DrugPurity.HIGH.getText()).build());
                                            }
                                            for (int i = 0; i < LockpickHandler.kraeuter.get(player); i++) {
                                                player.getInventory().addItem(new ItemBuilder(Material.GREEN_DYE).setName(Drogen.KRÄUTER.getName()).setLore("§7Reinheitsgrad: " + Drogen.DrugPurity.HIGH.getText()).build());
                                            }
                                            if (LockpickHandler.pulver.get(player) > 0 && LockpickHandler.kraeuter.get(player) > 0) {
                                                orga.sendMessage(PREFIX + player.getName() + " hat " + LockpickHandler.pulver.get(player) + "g Pulver und " + LockpickHandler.kraeuter.get(player) + "g Kräuter aus dem Lager gestohlen.");
                                            } else if (LockpickHandler.pulver.get(player) > 0) {
                                                orga.sendMessage(PREFIX + player.getName() + " hat " + LockpickHandler.pulver.get(player) + "g Pulver aus dem Lager gestohlen.");
                                            } else {
                                                orga.sendMessage(PREFIX + player.getName() + " hat " + LockpickHandler.kraeuter.get(player) + "g Kräuter aus dem Lager gestohlen.");
                                            }
                                            orga.addExp((LockpickHandler.pulver.get(player) + LockpickHandler.kraeuter.get(player)) / 4);
                                            Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Raub bei " + rob.getName() + " konnte nicht verhindert werden.");
                                        } else {
                                            orga.sendMessage(PREFIX + player.getName() + " hat es nicht geschafft Drogen aus dem Lager zu stehlen.");
                                            Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Raub bei " + rob.getName() + " wurde verhindert.");
                                        }
                                    }
                                }
                            }

                            if (LockpickHandler.c.containsKey(player)) {
                                ItemStack item = player.getInventory().getItemInMainHand();
                                if (item.getType() == Material.BLAZE_ROD) {
                                    player.getInventory().getItemInMainHand().setAmount(item.getAmount() - 1);
                                } else {
                                    for (ItemStack items : player.getInventory().getContents()) {
                                        if (items != null && items.getType() == Material.BLAZE_ROD) {
                                            items.setAmount(items.getAmount() - 1);
                                        }
                                    }
                                }

                                LockpickHandler.c.remove(player);
                            }
                        }, 40 * 20L);
                    } else {
                        player.sendMessage(PREFIX + "Du bist nicht in der Nähe einer Kasse oder eines Lagers!");
                    }
                } else {
                    player.sendMessage(Messages.ERROR + "Du musst mindestens Rang-2 sein um einzubrechen!");
                }
            } else {
                player.sendMessage(Messages.ERROR + "Du bist in keiner Organisation!");
            }
        }
        return true;
    }
}
