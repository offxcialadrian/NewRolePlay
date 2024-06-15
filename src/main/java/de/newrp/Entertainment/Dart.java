package de.newrp.Entertainment;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import de.newrp.Shop.Shop;
import de.newrp.Shop.ShopType;
import de.newrp.Shop.Shops;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class Dart implements Listener, CommandExecutor {

    private final String PREFIX = "§8[§cDart§8] §c" + Messages.ARROW + " §7";

    public static HashMap<String, Integer> progress = new HashMap<>();

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof SpectralArrow) {
            SpectralArrow arrow = (SpectralArrow) event.getEntity();
            Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), arrow::remove, 30 * 20L);
            if (event.getHitBlock() == null) return;
            if (event.getHitBlock().getBlockData().getMaterial().equals(Material.TARGET)) {
                ProjectileSource shooter = event.getEntity().getShooter();
                if (shooter instanceof Player) {
                    Player player = (Player) shooter;

                    Location loc = arrow.getLocation();
                    Vector direction = loc.getDirection();
                    loc.add(direction);

                    double x = loc.getX();
                    double y = loc.getY() - (double) 1/16;
                    double z = loc.getZ();

                    double p;
                    if (Math.floor(y) != event.getHitBlock().getY()) return;
                    y = Math.pow((-Math.abs(2 * (y % 1 - 0.5))) + 1, 2);
                    if (event.getHitBlockFace() == BlockFace.SOUTH || event.getHitBlockFace() == BlockFace.NORTH) {
                        if (Math.floor(x) != event.getHitBlock().getX()) return;
                        x = Math.pow((-Math.abs(2 * (x % 1 - 0.5))) + 1, 2);
                        p = (x + y) / 2;
                    }
                    else if (event.getHitBlockFace() == BlockFace.EAST || event.getHitBlockFace() == BlockFace.WEST) {
                        if (Math.floor(z) != event.getHitBlock().getZ()) return;
                        z = Math.pow((-Math.abs(2 * (z % 1 - 0.5))) + 1, 2);
                        p = (y + z) / 2;
                    }
                    else return;

                    int points = (int) Math.ceil(p * 10);
                    Script.sendLocalMessage(6, player, PREFIX + Script.getName(player) + " hat " + points + " Punkt" + (points > 1 ? "e" : "") + " geworfen.");
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.SPECTRAL_ARROW) {
            progress.putIfAbsent(player.getName(), 0);
            progress.put(player.getName(), progress.get(player.getName()) + 1);
            progressBar(player);
            if (progress.get(player.getName()) >= 10) shootDart(player);
        }
    }

    private void shootDart(Player player) {
        progress.put(player.getName(), 0);
        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
        SpectralArrow arrow = Script.WORLD.spawnArrow(player.getEyeLocation(), player.getEyeLocation().getDirection(), 1, 0, SpectralArrow.class);
        arrow.setShooter(player);
        double r = 0.2D * Math.pow(2 * (Script.getRandomFloat(0, 1) - 0.5), 3);
        arrow.setVelocity(arrow.getVelocity().multiply(0.9D + r));
    }

    private void progressBar(Player player) {
        double progress_percentage = progress.get(player.getName()) / (double) 10;
        StringBuilder sb = new StringBuilder();
        int bar_length = 10;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) sb.append("§c▉");
            else sb.append("§8▉");
        }
        Script.sendActionBar(player, "§cWerfen.. §8» §a" + sb);
    }

    public static void clear() {
        for (Entity entity : Script.WORLD.getEntities()) {
            if (entity instanceof SpectralArrow) entity.remove();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof SpectralArrow) {
            event.setDamage(0);
            event.setCancelled(true);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            Shops shop = Shops.getShopByLocation(player.getLocation(), 8.0F);
            if (shop == null || shop.getType() != ShopType.BAR) {
                player.sendMessage(Messages.ERROR + "Du befindest dich nicht in einer Bar.");
                return true;
            }

            if (Script.removeMoney(player, PaymentType.CASH, 20)) {
                player.getInventory().addItem(Script.setName(new ItemStack(Material.SPECTRAL_ARROW, 10), "§eDart-Pfeile"));
                player.sendMessage(PREFIX + "Du hast dir 10 Dart-Pfeile gekauft.");
                player.sendMessage(Messages.INFO + "Der Spieler, der am nächsten an 69 Punkten ist, hat gewonnen.");

                shop.addKasse(25);
                if (shop.getOwner() > 0)
                    Script.sendActionBar(Objects.requireNonNull(Script.getPlayer(shop.getOwner())), Shop.PREFIX + "Dein Shop §6" + shop.getPublicName() + " §7hat §620€ §7Gewinn gemacht aus dem Verkauf von §610x Dart-Pfeile §7(§620€§7)");
            } else {
                player.sendMessage(PREFIX + "Du benötigst 20€ um dir Pfeile zu kaufen.");
            }
        }

        return true;
    }
}
