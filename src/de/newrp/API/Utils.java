package de.newrp.API;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.Notications;
import de.newrp.Administrator.SDuty;
import de.newrp.Government.Wahlen;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.help.HelpTopic;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

public class Utils implements Listener {

    private static final Material[] DROP_BLACKLIST = new Material[]{};
    private static final String[] BLOCKED_COMMANDS = new String[]{
            "/minecraft", "/spi", "/pl", "/protocol", "/rl", "/restart", "/bukkit", "/stop", "/time", "/ver", "/icanhasbukkit", "/xp", "/tell",
            "/toggledownfall", "/testfor", "/recipe", "/help", "/give", "/effect", "/enchant", "/deop", "/defaultgamemode", "/ban-ip",
            "/banlist", "/advancement", "/?", "/gamemode", "/gamerule", "/give", "/help", "/kill", "/list", "/about",
            "/ability", "/advancement", "/alwaysday", "/attribute", "/ban-ip", "/banlist", "/bossbar", "/camera", "/camerashake",
            "/changesetting", "/clear", "/clearspawnpoint", "/clone", "/connect", "/damage", "/data", "/datapack", "/daylock",
            "/dedicatedwsserver", "/defaultgamemode", "/deop", "/dialogue", "/difficulty", "/effect", "/enchant", "/event", "/execute",
            "/experience", "/fill", "/fillbiome", "/fog", "/forceload", "/function", "/gamemode", "/gamerule", "/gametest", "/give", "/help",
            "/immutableworld", "/item", "/jfr", "/kill", "/list", "/locate", "/loot", "/mobevent", "/music", "/op",
            "/ops", "/pardon", "/pardon-ip", "/particle", "/perf", "/permission", "/place", "/playanimation", "/playsound", "/publish",
            "/random", "/recipe", "/reload", "/replaceitem", "/return", "/ride", "/save", "/save-all", "/save-off", "/save-on",
            "/say", "/schedule", "/scoreboard", "/script", "/scriptevent", "/seed", "/setblock", "/setidletimeout", "/setmaxplayers",
            "/setworldspawn", "/spawnpoint", "/spreadplayers", "/stop", "/stopsound", "/structure", "/summon", "/tag",
            "/teammsg", "/tell", "/tellraw", "/testfor", "/testforblock", "/testforblocks", "/tickingarea", "/time", "/title",
            "/titleraw", "/tm", "/toggledownfall", "/trigger", "/volumearea", "/wb", "/whitelist", "/worldborder",
            "/worldbuilder", "/wsserver", "/xp"
    };


    public static final HashMap<String, Long> cooldowns = new HashMap<>();
    public static final int WORLD_BORDER_MIN_X = 180;
    public static final int WORLD_BORDER_MAX_X = 1055;
    public static final int WORLD_BORDER_MIN_Z = 500;
    public static final int WORLD_BORDER_MAX_Z = 1300;


    @EventHandler
    public void blockExplode(BlockExplodeEvent e) {
        double x = e.getBlock().getLocation().getX();
        double y = e.getBlock().getLocation().getY();
        double z = e.getBlock().getLocation().getZ();
        Script.WORLD.createExplosion(x, y, z, 25F, false, false);
        e.setCancelled(true);
    }

    @EventHandler
    public void blockEntityExplode(EntityExplodeEvent e) {
        Location bombLocation = e.getLocation();
        Navi navi = Navi.getNextNaviLocation(bombLocation);
        bombLocation.add(0, -1, 0);
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
                    double damage = ((100D - (distance * 1.5D)) * .8D);
                    online.damage(damage);

                }
            }
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.setJoinMessage(null);
        Script.sendOfflineMessages(p);
        if (e.getPlayer().hasPlayedBefore()) {
            e.getPlayer().sendMessage(Script.PREFIX + "Willkommen zurück auf §eNewRP§7!");
            if(Script.hasRank(p, Rank.MODERATOR, false)) e.getPlayer().sendMessage(Messages.INFO + "Aufgrund deines Status als " + Script.getRank(p).getName(p) + " hast du automatisch einen Premium-Account.");
            Script.sendActionBar(e.getPlayer(), "§7Willkommen zurück auf §eNewRP§7!");
        } else {
            if (!Script.getCountry(p).contains("Germany") && !Script.getCountry(p).contains("Austria") && !Script.getCountry(p).contains("Switzerland")) {
                p.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cAccess denied §8« \n\n§8§m------------------------------\n\n§7We are sorry to inform you that only players inside DE, AT & CH can join the server.\n\n§7If you think this is a mistake, please contact our support.\n\n§8§m------------------------------");
                return;
            }
            Script.registerPlayer(e.getPlayer());
            Script.sendActionBar(e.getPlayer(), "§7Willkommen auf §eNewRP§7!");
            e.getPlayer().sendMessage("§eNew RolePlay" + "§rWillkommen auf §eNewRP§7!");
            Achievement.FIRST_JOIN.grant(p);
        }
        e.getPlayer().setPlayerListName(Script.getName(e.getPlayer()));
        Script.checkPlayerName(p);
        Script.sendTabTitle(e.getPlayer());
        Script.resetHealth(p);
        new BukkitRunnable() {
            @Override
            public void run() {
                Script.resetHealth(p);
                SDuty.updateScoreboard();
            }
        }.runTaskLater(main.getInstance(), 20L);
        p.setFlySpeed(0.1f);
        if(Wahlen.wahlenActive()) p.sendMessage(Messages.INFO + "Die Wahlen sind aktiv! Du kannst mit §8/§6wahlen §rdeine Stimme abgeben.");
        Notications.sendMessage(Notications.NotificationType.LEAVE, "§e" + Script.getName(e.getPlayer()) + " §7hat den Server betreten.");
        Script.executeAsyncUpdate("INSERT INTO last_disconnect (nrp_id, time) VALUES (" + Script.getNRPID(p)  + ", " + System.currentTimeMillis() + ")");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void itemFrameItemRemoval(HangingBreakByEntityEvent e) {
        if (e.getEntity().getType().equals(EntityType.ITEM_FRAME) || e.getEntity().getType().equals(EntityType.PAINTING)) {
            if (e.getRemover().getType().equals(EntityType.PLAYER)) {
                Player p = (Player) e.getRemover();
                e.setCancelled(!BuildMode.isInBuildMode(p));
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        if(e.getRegainReason() == EntityRegainHealthEvent.RegainReason.EATING || e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED && ((Player) e.getEntity()).getHealth() >= 19)
            e.setCancelled(true);
    }

    @EventHandler
    public void FrameEntity(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof ArmorStand) {
            if(!BuildMode.isInBuildMode((Player) e.getDamager())) e.setCancelled(true);
        }
        if (e.getEntity() instanceof ItemFrame) {
            if (e.getDamager() instanceof Player) {
                if (!BuildMode.isInBuildMode((Player) e.getDamager())) {
                    e.setCancelled(true);
                }
            }
            if (e.getDamager() instanceof Projectile) {
                if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
                    if (!BuildMode.isInBuildMode((Player) ((Projectile) e
                            .getDamager()).getShooter())) {
                        e.getDamager().remove();
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void FrameRotate(PlayerInteractEntityEvent e) {
        if (e.getRightClicked().getType().equals(EntityType.ITEM_FRAME)) {
            if (!BuildMode.isInBuildMode(e.getPlayer())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e) {
        if (Script.isInTestMode() && !Script.isNRPTeam(e.getPlayer()) && !e.getPlayer().isWhitelisted()) {
            e.getPlayer().kickPlayer("§eDer Server ist momentan im Wartungsmodus.");
            Debug.debug("Der Spieler " + e.getPlayer().getName() + " wurde gekickt, da der Server im Wartungsmodus ist.");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if(e.getClickedBlock().getType() == Material.TNT && !Script.hasRank(e.getPlayer(), Rank.ADMINISTRATOR, false)) {
                e.setCancelled(true);
                return;
            }

            if(e.getClickedBlock().getType() == Material.CHEST && Script.hasRank(e.getPlayer(), Rank.ADMINISTRATOR, false)) {
                e.setCancelled(false);
                return;
            }

            if((e.getClickedBlock().getType() == Material.CRAFTING_TABLE || e.getClickedBlock().getType() == Material.FURNACE || e.getClickedBlock().getType() == Material.LOOM) && BuildMode.isInBuildMode(e.getPlayer())) {
                e.setCancelled(false);
                return;
            }

            if (e.getClickedBlock().getType() == Material.CHEST ||
                    e.getClickedBlock().getType() == Material.TRAPPED_CHEST ||
                    e.getClickedBlock().getType() == Material.ENDER_CHEST ||
                    e.getClickedBlock().getType() == Material.SHULKER_BOX ||
                    e.getClickedBlock().getType() == Material.BARREL ||
                    e.getClickedBlock().getType() == Material.DISPENSER ||
                    e.getClickedBlock().getType() == Material.DROPPER ||
                    e.getClickedBlock().getType() == Material.HOPPER ||
                    e.getClickedBlock().getType() == Material.FURNACE ||
                    e.getClickedBlock().getType() == Material.BLAST_FURNACE ||
                    e.getClickedBlock().getType() == Material.SMOKER ||
                    e.getClickedBlock().getType() == Material.BREWING_STAND ||
                    e.getClickedBlock().getType() == Material.ANVIL ||
                    e.getClickedBlock().getType() == Material.COMMAND_BLOCK ||
                    e.getClickedBlock().getType() == Material.COMMAND_BLOCK_MINECART ||
                    e.getClickedBlock().getType() == Material.CHIPPED_ANVIL ||
                    e.getClickedBlock().getType() == Material.DAMAGED_ANVIL ||
                    e.getClickedBlock().getType() == Material.ENCHANTING_TABLE ||
                    e.getClickedBlock().getType() == Material.CRAFTING_TABLE ||
                    e.getClickedBlock().getType() == Material.BEACON ||
                    e.getClickedBlock().getType() == Material.DAYLIGHT_DETECTOR ||
                    e.getClickedBlock().getType() == Material.GRINDSTONE ||
                    e.getClickedBlock().getType() == Material.LOOM) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        long time = System.currentTimeMillis();
        Long lastUsage = cooldowns.get(e.getPlayer().getName());
        if (cooldowns.containsKey(e.getPlayer().getName())) {
            if (lastUsage + 0.8 * 1000 > time) {
                return;
            }
        }

        int move_x = e.getTo().getBlockX();
        int move_z = e.getTo().getBlockZ();

        if (move_x > WORLD_BORDER_MAX_X || move_x < WORLD_BORDER_MIN_X || move_z > WORLD_BORDER_MAX_Z || move_z < WORLD_BORDER_MIN_Z) {
            e.setCancelled(!BuildMode.isInBuildMode(e.getPlayer()));
            if(BuildMode.isInBuildMode(e.getPlayer())) {
                Script.sendActionBar(e.getPlayer(), Messages.INFO + "Du kannst dieses Gebiet nur betreten, da du im BuildMode bist.");
                return;
            }
            Location playerCenterLocation = e.getTo();
            Location playerToThrowLocation = e.getPlayer().getEyeLocation();
            double x = playerToThrowLocation.getX() - playerCenterLocation.getX();
            double y = playerToThrowLocation.getY() - playerCenterLocation.getY();
            double z = playerToThrowLocation.getZ() - playerCenterLocation.getZ();
            Vector throwVector = new Vector(x, y, z);
            throwVector.normalize();
            throwVector.multiply(2.1D);
            throwVector.setY(.2D);
            e.getPlayer().setVelocity(throwVector);
            cooldowns.put(e.getPlayer().getName(), time);
            e.getPlayer().damage(2D);
            e.getPlayer().sendMessage("§4§lVorsicht! §c§lDu hast das Ende der Welt erreicht.");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (SDuty.isSDuty(p)) {
            SDuty.removeSDuty(p);
        }

        if(BuildMode.isInBuildMode(p) && !Script.isInTestMode()) {
            BuildMode.removeBuildMode(p);
        }
        e.setQuitMessage(null);
        new BukkitRunnable() {

            @Override
            public void run() {
                SDuty.updateScoreboard();
            }
        }.runTaskLater(main.getInstance(), 20L);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        boolean block = false;
        for (Material material : DROP_BLACKLIST) {
            block = true;
        }
        if (block)
            e.setCancelled(true);
    }


    @EventHandler
    public void onBlocKMinecraftCommands(PlayerCommandPreprocessEvent e) {
        for (String s : BLOCKED_COMMANDS) {
            if (e.getMessage().toLowerCase().startsWith(s)) {
                if (!Script.hasRank(e.getPlayer(), Rank.OWNER, false)) {
                    e.setCancelled(true);
                    Script.sendActionBar(e.getPlayer(), Messages.ERROR + "Der Befehl wurde nicht gefunden.");
                    return;
                } else {
                    e.getPlayer().sendMessage(Messages.INFO + "Du konntest diesen Befehl nur ausführen, da du Server-Owner bist.");
                    return;
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (!e.isCancelled()) {
            String command = e.getMessage().split(" ")[0];
            HelpTopic htopic = Bukkit.getServer().getHelpMap().getHelpTopic(command);
            if (htopic == null) {
                Script.sendActionBar(e.getPlayer(), Messages.ERROR + "Der Befehl wurde nicht gefunden.");
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onBlockTabComplete(TabCompleteEvent e) {
        List<String> NEW_COMPLETEION = e.getCompletions();
        List<String> chache = new ArrayList<>();
        for (String nc : NEW_COMPLETEION) {
            for (String s : BLOCKED_COMMANDS) {
                if (nc.startsWith(s)) {
                    chache.add(nc);
                }
            }
        }
        for (String s : chache) {
            NEW_COMPLETEION.remove(s);
        }

        e.setCompletions(NEW_COMPLETEION);

    }


}
