package de.newrp.Organisationen;

import de.newrp.API.*;
import de.newrp.Berufe.Beruf;
import de.newrp.Chat.Me;
import de.newrp.Government.Stadtkasse;
import de.newrp.NewRoleplayMain;
import de.newrp.Player.AFK;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.takemoney.ITakeMoneyService;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BreakinCommand implements CommandExecutor {

    public static String PREFIX = "§8[§dBreak-in§8] §d" + Messages.ARROW + " §7";

    public static HashMap<Organisation, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();

            assert player != null;
            if (Organisation.hasOrganisation(player)) {
                if (Organisation.getRank(player) >= 2 || Organisation.isLeader(player, true)) {
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

                        cooldowns.put(orga, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2) + TimeUnit.MINUTES.toMillis(30));

                        Beruf.Berufe.POLICE.sendMessage(PREFIX + "Ein Einbruch bei " + rob.getName() + " wurde gemeldet.");
                        orga.sendMessage(PREFIX + player.getName() + " beginnt bei " + rob.getName() + " aufzubrechen.");
                        Me.sendMessage(player, "startet einen Einbruch.");
                        for (UUID m : orga.getMember()) if (Bukkit.getOfflinePlayer(m).isOnline()) if (!AFK.isAFK(m)) if (Objects.requireNonNull(Bukkit.getPlayer(m)).getLocation().distance(player.getLocation()) <= 30)
                            Activity.grantActivity(Script.getNRPID(Bukkit.getPlayer(m)), Activities.BREAKIN);

                        LockpickHandler.c.put(player, 1);
                        LockpickHandler.a.put(player, 1);
                        LockpickHandler.f.put(player, 0);
                        LockpickHandler.value.put(player, 0);
                        LockpickHandler.pulver.put(player, 0);
                        LockpickHandler.kraeuter.put(player, 0);
                        LockpickHandler.next(player, rob.getType());

                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
                            boolean done = false;
                            if (player.getOpenInventory().title() instanceof TextComponent)
                                done = ((TextComponent) player.getOpenInventory().title()).content().contains("Schloss");
                            if (done) player.closeInventory();
                            else done = (LockpickHandler.value.get(player) > 0 || LockpickHandler.pulver.get(player) > 0 || LockpickHandler.kraeuter.get(player) > 0);

                            if (done) {
                                if (Objects.equals(rob.getType(), "Kasse")) {
                                    if (LockpickHandler.value.get(player) > 0) {
                                        orga.sendMessage(PREFIX + player.getName() + " hat erfolgreich " + LockpickHandler.value.get(player) + "€ aus der Kasse gestohlen.");
                                        Script.addMoney(player, PaymentType.CASH, LockpickHandler.value.get(player));
                                        Stadtkasse.removeStadtkasse((LockpickHandler.value.get(player)/2), "Raub bei " + rob.getName() + " von " + player.getName());
                                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
                                            DependencyContainer.getContainer().getDependency(ITakeMoneyService.class).deleteMoney(player);
                                        }, (20 * 60) * 7);
                                        orga.addExp(LockpickHandler.value.get(player) / 60, true);
                                        DependencyContainer.getContainer().getDependency(ITakeMoneyService.class).addIllegalObtainedMoneyToPlayer(player, LockpickHandler.value.get(player));
                                        Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + "Die Stadtkasse ist für den Raub bei " + rob.getName() + " aufgekommen und hat " + (LockpickHandler.value.get(player)/2) + "€ verloren.");
                                        Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Raub bei " + rob.getName() + " konnte nicht verhindert werden.");
                                        if(MaskHandler.masks.containsKey(player.getUniqueId())) {
                                            Beruf.Berufe.POLICE.sendMessage(PREFIX + "Eine maskierte Person ist für den Raub verantwortlich.");
                                            Beruf.Berufe.BUNDESKRIMINALAMT.sendMessage(PREFIX + "Eine maskierte Person ist für den Raub bei " + rob.getName() + " verantwortlich.");
                                        } else {
                                            Beruf.Berufe.POLICE.sendMessage(PREFIX + "Aufnahmen zeigen, dass " + player.getName() + " verantwortlich ist.");
                                            Beruf.Berufe.BUNDESKRIMINALAMT.sendMessage(PREFIX + "Aufnahmen zeigen, dass " + player.getName() + " für den Raub bei " + rob.getName() + " verantwortlich ist.");
                                        }
                                    } else {
                                        orga.sendMessage(PREFIX + player.getName() + " hat es nicht geschafft Geld aus der Kasse zu stehlen.");
                                        Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Raub bei " + rob.getName() + " wurde verhindert.");
                                        Beruf.Berufe.BUNDESKRIMINALAMT.sendMessage(PREFIX + "Der Raub bei " + rob.getName() + " wurde verhindert.");
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
                                        orga.addExp((LockpickHandler.pulver.get(player) + LockpickHandler.kraeuter.get(player)) / 4, true);
                                        Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Raub bei " + rob.getName() + " konnte nicht verhindert werden.");
                                    } else {
                                        orga.sendMessage(PREFIX + player.getName() + " hat es nicht geschafft Drogen aus dem Lager zu stehlen.");
                                        Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Raub bei " + rob.getName() + " wurde verhindert.");
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
