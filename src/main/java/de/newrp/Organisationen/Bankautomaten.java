package de.newrp.Organisationen;

import com.destroystokyo.paper.ParticleBuilder;
import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Government.Stadtkasse;
import de.newrp.Player.AFK;
import de.newrp.NewRoleplayMain;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.takemoney.ITakeMoneyService;
import org.bukkit.*;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Bankautomaten implements Listener {

    public static String PREFIX = "§8[§6Bankautomat§8] §6" + Messages.ARROW + " §7";
    public static HashMap<Organisation, Long> cooldown = new HashMap<>();
    public static HashMap<Location, Block> atmBlocks = new HashMap<>();
    public static HashMap<ATM, Long> cooldownATM = new HashMap<>();
    public static HashMap<String, Integer> progress = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getClickedBlock() == null) return;
        if (p.getInventory().getItemInMainHand().getType() != Material.TNT) return;
        Organisation o = Organisation.getOrganisation(p);
        if (o == null) return;
        ATM atm = ATM.getNearATM(p);
        if (atm == null) return;

        if (cooldown.containsKey(o) && cooldown.get(o) > System.currentTimeMillis()) {
            p.sendMessage(Messages.ERROR + "Du kannst erst in " + Script.getRemainingTime(cooldown.get(o)) + " wieder einen Bankautomaten zerstören.");
            return;
        }

        List<UUID> cops = Beruf.Berufe.POLICE.getMember().stream()
                .filter(Duty::isInDuty)
                .filter((nearbyPlayer) -> !SDuty.isSDuty(nearbyPlayer))
                .filter(nearbyPlayer -> !AFK.isAFK(nearbyPlayer)).collect(Collectors.toList());

        if (cops.size() < 3 && !Script.isInTestMode()) {
            p.sendMessage(Messages.ERROR + "Es braucht mindestens 3 Beamte um einen Bankautomaten zu zerstören.");
            return;
        }

        cooldown.put(o, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2) + TimeUnit.MINUTES.toMillis(30));
        cooldownATM.put(atm, System.currentTimeMillis() + 3600000);
        p.getInventory().remove(Material.TNT);
        p.sendMessage(PREFIX + "Der Bankautomat wird in 90 Sekunden zerstört.");
        Beruf.Berufe.POLICE.sendMessage(PREFIX + "ACHTUNG! ES WURDE SPRENGSTOFF AN ATM " + atm.getID() + " GEFUNDEN!");
        Beruf.Berufe.POLICE.sendMessage(Messages.INFO + "In der Nähe von " + Navi.getNextNaviLocation(p.getLocation()).getName());
        e.getClickedBlock().getLocation().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1f, 0.5f);
        for (LivingEntity nearbyLivingEntity : e.getClickedBlock().getLocation().getNearbyLivingEntities(5)) {
            nearbyLivingEntity.sendMessage(PREFIX + "§r§lEine Bombe wurde an einem Bankautomaten in deiner Nähe platziert, verschwinde!");
        }
        Location bombLocation = atm.getLocation();
        progress.put(p.getName(), 0);
        p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
        final List<Location> particle = Script.getPointsOfCircle(bombLocation, 2);
        AtomicInteger y = new AtomicInteger(atm.getLocation().getBlockY());
        final int yMax = atm.getLocation().getBlockY() + 3;
        AtomicInteger particleIndex = new AtomicInteger(0);
        new BukkitRunnable() {
            @Override
            public void run() {
                bombLocation.getWorld().playSound(bombLocation, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.2f);
                if (p.getLocation().distance(bombLocation) > 10) {
                    p.sendMessage(PREFIX + "Du bist zu weit entfernt.");
                    Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Bankautomat " + atm.getID() + " wurde nicht zerstört. Der Täter ist geflohen.");
                    cancel();
                    return;
                }

                if(y.get() > yMax) {
                    y.set(atm.getLocation().getBlockY());
                }

                final Location particleLocation = particle.get(particleIndex.getAndIncrement());
                final Location clonedLocation = particleLocation.clone();
                clonedLocation.setY(y.getAndIncrement());
                Script.WORLD.spawnParticle(Particle.REDSTONE, clonedLocation, 3, new Particle.DustOptions(Color.ORANGE, 2));

                if (particleIndex.get() == particle.size()) {
                    particleIndex.set(0);
                }


                if (progress.get(p.getName()) >= 180) {
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
                    int remove = (int) Math.min(Script.getRandom(3000, 6000), Script.getPercent(20, atm.getCash()));
                    atm.removeCash(remove);
                    o.sendMessage(PREFIX + Script.getName(p) + " hat einen Bankautomaten zerstört und " + remove + "€ gestohlen.");
                    Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Bankautomat " + atm.getID() + " wurde zerstört. Es wurden " + remove + "€ gestohlen.");
                    Script.addMoney(p, PaymentType.CASH, remove);
                    o.addExp(remove / 100);
                    DependencyContainer.getContainer().getDependency(ITakeMoneyService.class).addIllegalObtainedMoneyToPlayer(p, remove);
                    progress.remove(p.getName());
                    cancel();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            DependencyContainer.getContainer().getDependency(ITakeMoneyService.class).deleteMoney(p);
                            Stadtkasse.removeStadtkasse(1000, "Wiederherstellung Bankautomat");
                        }
                    }.runTaskLater(NewRoleplayMain.getInstance(), 20L * 60 * 60);
                } else {
                    progressBar(180, p);
                    progress.replace(p.getName(), progress.get(p.getName()) + 1);
                }
            }
        }.runTaskTimer(NewRoleplayMain.getInstance(), 0L, 10L);
    }

    private static void progressBar(double required_progress, Player p) {
        double current_progress = progress.get(p.getName());
        double progress_percentage = current_progress / required_progress;
        StringBuilder sb = new StringBuilder();
        int bar_length = 10;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) {
                sb.append("§c▉");
            } else {
                sb.append("§8▉");
            }
        }
        Script.sendActionBar(p, "§cBankautomat sprengen.. §8» §a" + sb.toString());
    }

}
