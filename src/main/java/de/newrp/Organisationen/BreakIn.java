package de.newrp.Organisationen;

import de.newrp.API.*;
import de.newrp.Berufe.Beruf;
import de.newrp.House.House;
import de.newrp.House.HouseAddon;
import de.newrp.Player.AFK;
import de.newrp.Police.Handschellen;
import de.newrp.NewRoleplayMain;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.emergencycall.IEmergencyCallService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BreakIn implements Listener {
    private static final String PREFIX = "§8[§cEinbruch§8]§6 ";
    private static final Map<String, Long> COOLDOWNS = new HashMap<>();
    private static final Map<String, Long> TOTAL_COOLDOWN = new HashMap<>();
    private static final Map<String, House> HOUSES = new HashMap<>();
    private static final HashMap<String, Double> progress = new HashMap<>();
    private final IEmergencyCallService emergencyCallService = DependencyContainer.getContainer().getDependency(IEmergencyCallService.class);

    Location[] labor = new Location[] { new Location(Script.WORLD, 374, 76, 1312), new Location(Script.WORLD, 374, 75, 1312), new Location(Script.WORLD, 375, 76, 1312), new Location(Script.WORLD, 375, 75, 1312)};

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(!p.getInventory().getItemInMainHand().equals(Script.brechstange())) return;

        long time = System.currentTimeMillis();
        Long lastUsage = TOTAL_COOLDOWN.get(p.getName());
        if (HOUSES.containsKey(p.getName()) || TOTAL_COOLDOWN.containsKey(p.getName()) && lastUsage + 9000000 > time) {
            if (Objects.requireNonNull(e.getClickedBlock()).getType() == Material.OAK_DOOR) {
                p.sendMessage(Messages.ERROR + "Du kannst nur alle 3 Stunden in ein Haus einbrechen.");
            }
            return;
        }

        lastUsage = COOLDOWNS.get(p.getName());
        if (COOLDOWNS.containsKey(p.getName())) {
            if (lastUsage + 90 * 1000 > time) {
                int left = (int) ((lastUsage + (90 * 1000)) - System.currentTimeMillis());
                p.sendMessage(PREFIX + "Du hast die Beute in " + (left / 1000) + " Sekunden...");
            } else {
                House house = HOUSES.get(p.getName());
                int geld = (house.getKasse() / 3);
                p.sendMessage(PREFIX + "Du hast alles. Verschwinde nun bevor die Polizei eintrifft!");
                Script.addMoney(p, PaymentType.CASH, (int) (geld * 0.8));
                house.setKasse(house.getKasse() - (int) (geld * 0.8));
                COOLDOWNS.remove(p.getName());
                HOUSES.remove(p.getName());
                TOTAL_COOLDOWN.put(p.getName(), System.currentTimeMillis());
            }
            return;
        }

        ItemStack brechstange = Script.setName(Material.BLAZE_ROD, "§7Brechstange");
        if (!p.getInventory().getItemInMainHand().equals(brechstange))  return;

        House house = House.getNearHouse(p.getLocation(), 3);
        if (house == null) {
            if (e.getClickedBlock() != null) {
                if (Objects.requireNonNull(e.getClickedBlock()).getType() == Material.OAK_DOOR) {
                    Script.sendActionBar(p, PREFIX + "Du kannst hier nicht einbrechen.");
                }
                return;
            }
        }

        if (house.hasAddon(HouseAddon.ALARM)) {
            Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> Beruf.Berufe.POLICE.sendMessage(this.emergencyCallService.getPrefix() + "Es wurde ein Einbruch bei Haus " + house.getID() + " gemeldet."), 10 * 20L);
        }

        COOLDOWNS.put(p.getName(), System.currentTimeMillis());
        HOUSES.put(p.getName(), house);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 90 * 20, 2));
        p.sendMessage(PREFIX + "Du hast begonnen in das Haus " + house.getID() + " einzubrechen.");
        if (Organisation.hasOrganisation(p)) {
            for (UUID m : Organisation.getOrganisation(p).getMember()) if (Bukkit.getOfflinePlayer(m).isOnline()) if (!AFK.isAFK(m)) if (Objects.requireNonNull(Bukkit.getPlayer(m)).getLocation().distance(p.getLocation()) <= 20)
                Activity.grantActivity(Script.getNRPID(Bukkit.getPlayer(m)), Activities.EINBRUCH);
        }
        progress.put(p.getName(), 0.0);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (p.getLocation().distance(house.getSignLocation()) > 3) {
                    resetBreakIn(p);
                    this.cancel();
                    return;
                }

                if (!p.getInventory().getItemInMainHand().equals(brechstange)) {
                    resetBreakIn(p);
                    this.cancel();
                    return;
                }

                if (Handschellen.isCuffed(p)) {
                    resetBreakIn(p);
                    this.cancel();
                    return;
                }

                if (!COOLDOWNS.containsKey(p.getName())) {
                    resetBreakIn(p);
                    this.cancel();
                    return;
                }

                if (progress.get(p.getName()) < 60) {
                    progress.put(p.getName(), progress.get(p.getName()) + 1);
                    progressBar(61, p);
                    return;
                }


                House house = HOUSES.get(p.getName());
                if (house.hasAddon(HouseAddon.SICHERHEITSTUER)) {
                    if (new Random().nextInt(4) > 0) {
                        p.sendMessage(PREFIX + "Der Einbruch ist fehlgeschlagen.");
                        TOTAL_COOLDOWN.put(p.getName(), System.currentTimeMillis());
                        ItemStack item = p.getInventory().getItemInMainHand();
                        if (item.getType() == Material.BLAZE_ROD) item.setAmount(item.getAmount() - 1);
                        return;
                    }
                }

                int geld = (house.getKasse() / 3);
                p.sendMessage(PREFIX + "Du hast alles. Verschwinde nun bevor die Polizei eintrifft!");
                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                Script.addMoney(p, PaymentType.CASH, geld);

                house.setKasse(house.getKasse() - geld);
                COOLDOWNS.remove(p.getName());
                HOUSES.remove(p.getName());
                TOTAL_COOLDOWN.put(p.getName(), System.currentTimeMillis());
                progress.remove(p.getName());
                if (house.hasAddon(HouseAddon.ALARM)) {
                    Beruf.Berufe.POLICE.sendMessage(emergencyCallService.getPrefix() + "Ein Einbruch bei Haus " + house.getID() + " wurde gemeldet.");
                }
                if (Organisation.hasOrganisation(p)) {
                    Organisation.getOrganisation(p).addExp(Script.getRandom(10, 20), false);
                }
                this.cancel();

            }
        }.runTaskTimer(NewRoleplayMain.getInstance(), 20L, 20L);

    }

    private void resetBreakIn(Player player) {
        player.sendMessage(PREFIX + "Du hast den Einbruch abgebrochen.");
        if(player.hasPotionEffect(PotionEffectType.SLOW)) {
            player.removePotionEffect(PotionEffectType.SLOW);
        }
        HOUSES.remove(player.getName());
        COOLDOWNS.remove(player.getName());
        progress.remove(player.getName());
    }

    private static void progressBar(double required_progress, Player p) {
        double current_progress = progress.get(p.getName());
        double progress_percentage = current_progress / required_progress;
        StringBuilder sb = new StringBuilder();
        int bar_length = 10;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) {
                sb.append("§a▉");
            } else {
                sb.append("§8▉");
            }
        }
        Script.sendActionBar(p, "§cEinbrechen.. §8» §a" + sb);
    }

}
