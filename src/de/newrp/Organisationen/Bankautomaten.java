package de.newrp.Organisationen;

import de.newrp.API.*;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Government.Stadtkasse;
import de.newrp.Player.AFK;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Bankautomaten implements Listener {

    public static String PREFIX = "§8[§6Bankautomat§8] §6" + Messages.ARROW + " §7";
    public static HashMap<Organisation, Long> cooldown = new HashMap<>();
    public static HashMap<Location, Block> atmBlocks = new HashMap<>();
    public static HashMap<ATM, Long> cooldownATM = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!Organisation.hasOrganisation(p)) return;
        if(e.getClickedBlock() == null) return;
        if (p.getInventory().getItemInMainHand().getType() != Material.TNT) return;
        ATM atm = ATM.getNearATM(p);
        if (atm == null) return;
        Organisation o = Organisation.getOrganisation(p);

        if (cooldown.containsKey(o) && cooldown.get(o) > System.currentTimeMillis()) {
            p.sendMessage(Messages.ERROR + "Du kannst erst in " + Script.getRemainingTime(cooldown.get(o)) + " wieder einen Bankautomaten zerstören.");
            return;
        }

        List<Player> cops = Beruf.Berufe.POLICE.getMembers().stream()
                .filter(Beruf::hasBeruf)
                .filter(nearbyPlayer -> Beruf.getBeruf(nearbyPlayer).equals(Beruf.Berufe.POLICE))
                .filter(Duty::isInDuty)
                .filter(nearbyPlayer -> !AFK.isAFK(nearbyPlayer)).collect(Collectors.toList());

        if (cops.size() < 3 && !Script.isInTestMode()) {
            p.sendMessage(Messages.ERROR + "Es braucht mindestens 3 Beamte um einen Bankautomaten zu zerstören.");
            return;
        }


        cooldown.put(o, System.currentTimeMillis() + 10800000);
        cooldownATM.put(atm, System.currentTimeMillis() + 3600000);
        atmBlocks.put(e.getClickedBlock().getLocation(), e.getClickedBlock());
        p.getInventory().remove(Material.TNT);
        p.sendMessage(PREFIX + "Der Bankautomat wird in 180 Sekunden zerstört.");
        Beruf.Berufe.POLICE.sendMessage(PREFIX + "ACHTUNG! ES WURDE SPRENGSTOFF AN ATM " + atm.getID() + " GEFUNDEN!");
        Beruf.Berufe.POLICE.sendMessage(Messages.INFO + "In der Nähe von " + Navi.getNextNaviLocation(p.getLocation()).getName());
        Location bombLocation = atm.getLocation();
        e.getClickedBlock().setType(Material.TNT);
        p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                if(p.getLocation().distance(bombLocation) > 10) {
                    p.sendMessage(PREFIX + "Du bist zu weit entfernt.");
                    return;
                }
                for (Location l : Script.getBlocksAroundLocation(bombLocation, 5, 5, false, false, -1)) {
                    Block b = l.getBlock();
                    if (b.getType().hasGravity()) continue;
                    if (b.getType() == Material.AIR) continue;

                    FallingBlock block = b.getWorld().spawnFallingBlock(bombLocation, b.getType(), b.getData());
                    float x = (float) -0.2 + (float) (Math.random() * ((0.2 + 0.3) + 0.3));
                    float y = (float) -0.5 + (float) (Math.random() * ((0.3 + 0.3) + 1));
                    float z = (float) -0.2 + (float) (Math.random() * ((0.2 + 0.3) + 0.3));
                    block.setVelocity(new Vector(x, y, z));
                    block.setDropItem(false);
                    block.setGlowing(false);
                    block.setHurtEntities(false);
                }
                Script.WORLD.createExplosion(bombLocation.getX(), bombLocation.getY(), bombLocation.getZ(), 4F, false, false);

                for (Player online : Bukkit.getOnlinePlayers()) {
                    double distance = online.getLocation().distance(bombLocation);
                    if (distance < 30D) {
                        online.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 0, 0, false));
                        online.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 1, false));

                        if (distance < 10D) {
                            double damage = ((100D - (distance * 1.5D)) * .2D);
                            online.damage(damage);

                        }
                    }
                }
                e.getClickedBlock().setType(atmBlocks.get(e.getClickedBlock().getLocation()).getType());
                atmBlocks.remove(bombLocation);
                int remove = (int) Math.min(Script.getRandom(3000,6000),Script.getPercent(20, atm.getCash()));
                atm.removeCash(remove);
                o.sendMessage(PREFIX + Script.getName(p) + " hat einen Bankautomaten zerstört und " + remove + "€ gestohlen.");
                Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Bankautomat " + atm.getID() + " wurde zerstört. Es wurden " + remove + "€ gestohlen.");
                Script.addMoney(p, PaymentType.CASH, remove);
                o.addExp(remove/100);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Stadtkasse.removeStadtkasse(1000, "Wiederherstellung Bankautomat");
                    }
                }.runTaskLater(main.getInstance(), 20L * 60 * 90);
            }
        }.runTaskLater(main.getInstance(), 20L * 60);

    }
}
