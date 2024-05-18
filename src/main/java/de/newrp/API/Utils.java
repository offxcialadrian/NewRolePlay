package de.newrp.API;

import com.google.gson.JsonObject;
import de.newrp.Administrator.*;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Houseban;
import de.newrp.Government.Wahlen;
import de.newrp.Organisationen.Blacklist;
import de.newrp.Organisationen.MaskHandler;
import de.newrp.Organisationen.Organisation;
import de.newrp.Police.Fahndung;
import de.newrp.NewRoleplayMain;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.discord.IJdaService;
import de.newrp.discord.impl.JdaService;
import de.newrp.features.recommendation.IRecommendationService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.help.HelpTopic;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static de.newrp.API.Rank.SUPPORTER;

public class Utils implements Listener {

    private static final Material[] DROP_BLACKLIST = new Material[]{Material.WOODEN_HOE, Material.LEAD, Material.ANDESITE_SLAB, Material.SHIELD, Material.LEATHER_CHESTPLATE, Material.WITHER_SKELETON_SKULL, Material.LEVER};
    private static final String[] BLOCKED_COMMANDS = new String[]{
            "/minecraft", "/spi", "/protocol", "/rl", "/restart", "/bukkit", "/version", "/icanhasbukkit", "/xp",
            "/toggledownfall", "/testfor", "/recipe", "/effect", "/enchant", "/deop", "/defaultgamemode", "/ban-ip",
            "/banlist", "/advancement", "/?", "/gamemode", "/gamerule", "/kill", "/list", "/about",
            "/ability", "/advancement", "/alwaysday", "/attribute", "/ban-ip", "/banlist", "/bossbar", "/camera", "/camerashake",
            "/changesetting", "/clear", "/clearspawnpoint", "/clone", "/connect", "/damage", "/data", "/datapack", "/daylock",
            "/dedicatedwsserver", "/defaultgamemode", "/deop", "/dialogue", "/difficulty", "/effect", "/enchant", "/event", "/execute",
            "/experience", "/fill", "/fillbiome", "/fog", "/forceload", "/function", "/gamemode", "/gamerule", "/gametest",
            "/immutableworld", "/item", "/jfr", "/kill", "/list", "/locate", "/loot", "/mobevent", "/music", "/op",
            "/ops", "/pardon", "/pardon-ip", "/particle", "/perf", "/permission", "/place", "/playanimation", "/playsound", "/publish",
            "/random", "/recipe", "/reload", "/replaceitem", "/return", "/ride", "/save", "/save-all", "/save-off", "/save-on",
            "/say", "/schedule", "/scoreboard", "/script", "/scriptevent", "/seed", "/setblock", "/setidletimeout", "/setmaxplayers",
            "/setworldspawn", "/spawnpoint", "/spreadplayers", "/stop", "/stopsound", "/structure", "/summon", "/tag",
            "/teammsg", "/tellraw", "/testfor", "/testforblock", "/testforblocks", "/tickingarea", "/title",
            "/titleraw", "/tm", "/toggledownfall", "/trigger", "/volumearea", "/wb", "/worldborder",
            "/worldborder", "/wsserver", "/xp", "/citizens", "/npc", "/vehicle", "/garage", "/tebex", "/buycraft", "/paper", "/addpremiumtoplayer", "/dynmap"
    };

    private static final String[] BLOCKED_COMMANDS_SPECIFIC = new String[]{
            "/pl", "/plugins", "/give", "/whitelist", "/ver", "/version", "/time", "/tell"
    };


    public static final HashMap<String, Long> cooldowns = new HashMap<>();
    public static final int WORLD_BORDER_MIN_X = 0;
    public static final int WORLD_BORDER_MAX_X = 1100;
    public static final int WORLD_BORDER_MIN_Z = 420;
    public static final int WORLD_BORDER_MAX_Z = 1362;
    private IJdaService jdaService =  DependencyContainer.getContainer().getDependency(IJdaService.class);
    public static HashMap<String, Long> fishCooldown = new HashMap<>();
    public static HashMap<String, Integer> fishCount = new HashMap<>();


    @EventHandler
    public void blockExplode(BlockExplodeEvent e) {
        double x = e.getBlock().getLocation().getX();
        double y = e.getBlock().getLocation().getY();
        double z = e.getBlock().getLocation().getZ();
        Script.WORLD.createExplosion(x, y, z, 25F, false, false);
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock() == null) return;
            if (e.getClickedBlock().getType() == Material.ACACIA_TRAPDOOR || e.getClickedBlock().getType() == Material.BIRCH_TRAPDOOR || e.getClickedBlock().getType() == Material.DARK_OAK_TRAPDOOR || e.getClickedBlock().getType() == Material.JUNGLE_TRAPDOOR || e.getClickedBlock().getType() == Material.SPRUCE_TRAPDOOR) {
                boolean block = !SDuty.isSDuty(e.getPlayer()) && !BuildMode.isInBuildMode(e.getPlayer());
                e.setCancelled(block);
            }
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        /*if(Script.getNRPID(p) == 0) {
            Notifications.sendMessage(Notifications.NotificationType.ADVANCED_ANTI_CHEAT, Script.PREFIX + "Dem Spieler " + p.getName() + " wurde der Zutritt verweigert, da er nicht registriert ist.");
            e.disallow(PlayerLoginEvent.Result.KICK_FULL, "§cDerzeit können nur bereits registrierte Spieler joinen.");
        }*/
        if (e.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST) {
            e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "§8» §cNRP × New RolePlay §8┃ §cKick §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gekickt§8.\n\n§7Grund §8× §e" + "Wartungsarbeiten");
            Notifications.sendMessage(Notifications.NotificationType.ADVANCED_ANTI_CHEAT, Script.PREFIX + "Dem Spieler " + e.getPlayer().getName() + " wurde der Zutritt verweigert, da der Server im Wartungsmodus ist.");
        }
        if (e.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            e.disallow(PlayerLoginEvent.Result.KICK_FULL, "§8» §cNRP × New RolePlay §8┃ §cKick §8« \n\n§8§m------------------------------\n\n§7§cDerzeit können nur bereits registrierte Spieler joinen.\n\n§8§m------------------------------");
        }
    }

    @EventHandler
    public void onMoving(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!Script.FREEZE.contains(p.getName())) return;
        if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) return;
        e.setCancelled(true);
        p.setVelocity(p.getVelocity().zero());
    }

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        if (Script.isInTestMode()) {
            e.setMotd("§5§lNew RolePlay §8┃ §5Reallife §8× §5RolePlay §8┃ §c1.16.5\n§8» §a§l" + "§e§lWartungsarbeiten!");
        } else {
            e.setMotd("§5§lNew RolePlay §8┃ §5Reallife §8× §5RolePlay §8┃ §c1.16.5\n§8» §a§l" + NewRoleplayMain.getInstance().getDescription().getVersion() + " §8- §6§lTHE NEXT BIG THING");
        }
    }

    @EventHandler
    public void blockEntityExplode(EntityExplodeEvent e) {
        Location bombLocation = e.getLocation();
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
    public void onBedJoin(PlayerJoinEvent e) {
        e.getPlayer().setSleepingIgnored(false);
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent e) {
        e.getPlayer().setBedSpawnLocation(null, false);
        e.setCancelled(false);
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent e) {
        e.setSpawnLocation(false);
        e.getPlayer().setBedSpawnLocation(null);
    }

    @EventHandler
    public void onFish(PlayerFishEvent e) {
        e.setExpToDrop(0);

        if (e.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            if (fishCooldown.containsKey(e.getPlayer().getName()) && fishCooldown.get(e.getPlayer().getName()) > System.currentTimeMillis()) {
                fishCount.put(e.getPlayer().getName(), fishCount.computeIfAbsent(e.getPlayer().getName(), k -> 0) + 1);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (fishCount.get(e.getPlayer().getName()) > 2)
                            Notifications.sendMessage(Notifications.NotificationType.ADVANCED_ANTI_CHEAT, "Verdacht auf Angelmissbrauch bei " + e.getPlayer().getName() + " (" + Script.getPercentage(fishCount.get(e.getPlayer().getName()), 6) + "%)");
                    }
                }.runTaskLaterAsynchronously(NewRoleplayMain.getInstance(), 20 * 5);
                e.setCancelled(true);
                return;
            }
            e.setCancelled(true);
            fishCooldown.put(e.getPlayer().getName(), System.currentTimeMillis() + 2000);
            e.getPlayer().getInventory().addItem(getRandomFish());
            Script.addEXP(e.getPlayer(), 1);
            ItemStack rod = e.getPlayer().getInventory().getItemInMainHand();
            if (rod.getType() == Material.FISHING_ROD) {
                if (rod.getDurability() == rod.getType().getMaxDurability()) {
                    e.getPlayer().getInventory().setItemInMainHand(null);
                    e.getPlayer().sendMessage(Messages.INFO + "Deine Angel ist kaputt gegangen.");
                } else {
                    rod.setDurability((short) (rod.getDurability() + 1));
                }
            }
        }
    }

    public static ItemStack getRandomFish() {
        int random = Script.getRandom(1, 100);
        if (random <= 60) return new ItemStack(Material.COD);
        if (random <= 80) return new ItemStack(Material.SALMON);
        if (random <= 90) return new ItemStack(Material.PUFFERFISH);
        return new ItemStack(Material.TROPICAL_FISH);
    }

    public void sendServerBanner(Player player, String imageUrl) {
        JsonObject object = new JsonObject();
        object.addProperty("url", imageUrl); // Url of the image
        LabyModProtocol.sendLabyModMessage(player, "server_banner", object);
    }

    public void sendCurrentPlayingGamemode(Player player, boolean visible, String gamemodeName) {
        JsonObject object = new JsonObject();
        object.addProperty("show_gamemode", visible); // Gamemode visible for everyone
        object.addProperty("gamemode_name", gamemodeName); // Name of the current playing gamemode

        LabyModProtocol.sendLabyModMessage(player, "server_gamemode", object);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.setJoinMessage(null);
        Script.sendOfflineMessages(p);
        Script.updateExpBar(p);
        Corpse.reloadNPC(p);
        sendServerBanner(p, "https://newrp.de/images/-tablist.png");
        sendCurrentPlayingGamemode(p, true, "NRP × New RolePlay " + NewRoleplayMain.getInstance().getDescription().getVersion());
        Script.disableVoiceChat(p);
        try {
            MaskHandler.clearMasksOutOfEnderchest(p);
        } catch(final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }

        /*EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(Script.getName(p), null, "https://minotar.net/helm/"+ p.getName() + "/100.png");
        embed.setDescription("Der Spieler " + p.getName() + " hat den Server betreten.");
        TextChannel channel = this.jdaService.getJda().getTextChannelById("1236441113057824881");
        channel.sendMessageEmbeds(embed.build()).queue();*/


        p.getInventory().remove(Material.PLAYER_HEAD);
        if (Script.hasRank(p, Rank.SUPPORTER, false)) {
            Script.team.add(p);
        }

        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            if(Script.isNRPTeam(p) || Team.getTeam(p) == Team.Teams.ENTWICKLUNG) {
                Notifications.loadNotificationsForPlayer(p);
            }
        });

        if (!Krankheit.GEBROCHENES_BEIN.isInfected(Script.getNRPID(p))) {
            p.setWalkSpeed(0.2f);
            for (PotionEffect effect : p.getActivePotionEffects()) {
                p.removePotionEffect(effect.getType());
            }
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (BuildMode.isInBuildMode(online)) {
                if (Team.getTeam(p) != Team.Teams.BAU && !Script.hasRank(p, Rank.SUPPORTER, false)) {
                    //Debug.debug("hiding " + online.getName() + " from " + p.getName());
                    p.hidePlayer(NewRoleplayMain.getInstance(), online);
                }
            }
        }
        if (Script.getNRPID(p) != 0) {
            e.getPlayer().sendMessage(Script.PREFIX + "§7Willkommen zurück auf §eNewRP§7!");
            if (Script.hasRank(p, Rank.MODERATOR, false))
                e.getPlayer().sendMessage(Messages.INFO + "Aufgrund deines Status als " + Script.getRank(p).getName(p) + " hast du automatisch einen Premium-Account.");
            Script.sendActionBar(e.getPlayer(), "§7Willkommen zurück auf §eNewRP§7!");
            p.sendMessage(TippOfTheDay.PREFIX + TippOfTheDay.getRandomTipp());
            if(Licenses.PERSONALAUSWEIS.hasLicense(Script.getNRPID(p))) {
                if (Script.haveBirthDay(p)) {
                    if (!hasOpenPresent(p)) {
                        p.sendMessage(Messages.INFO + "§lDas Team von New RolePlay wünscht dir alles Gute zum Geburtstag!");
                        p.sendMessage(Messages.INFO + "Als Geschenk erhältst du 500 Exp Premium!");
                        Script.executeAsyncUpdate("UPDATE birthday SET geschenk = 1 WHERE id = " + Script.getNRPID(p));
                        Script.addEXP(p, 500);
                    }
                }

                final IRecommendationService recommendationService = DependencyContainer.getContainer().getDependency(IRecommendationService.class);
                if(!recommendationService.hasRecommendation(p)) {
                    recommendationService.openInventoryForRecommendation(p);
                }

            }

            for (Organisation o : Organisation.values()) {
                if (Blacklist.isOnBlacklist(p, o)) {
                    for (Player members : o.getMembers()) {
                        Script.setSubtitle(members, p.getUniqueId(), "§c" + Blacklist.getBlacklistObject(Script.getNRPID(p), o).getReason());
                    }
                }
            }

            if (Fahndung.isFahnded(p)) {
                for (Player cops : Beruf.Berufe.POLICE.getMembers()) {
                    Script.setSubtitle(cops, p.getUniqueId(), "§cFahndung: " + Fahndung.getWanteds(p) + " Wanted(s)");
                }
            }

            if(Houseban.isHousebanned(p, Beruf.Berufe.RETTUNGSDIENST)) {
                for(Player medics : Beruf.Berufe.RETTUNGSDIENST.getMembers()) {
                    Script.setSubtitle(medics, p.getUniqueId(), "§cHausverbot: " + Houseban.getReason(p, Beruf.Berufe.RETTUNGSDIENST));
                }
            }

            if (Beruf.hasBeruf(p)) {
                if (Beruf.getBeruf(p) == Beruf.Berufe.POLICE) {
                    for (Player fahnded : Fahndung.getList()) {
                        if(fahnded == null) continue;
                        Script.setSubtitle(p, fahnded.getUniqueId(), "§cFahndung: " + Fahndung.getWanteds(fahnded) + " Wanted(s)");
                    }
                } else if(Beruf.getBeruf(p) == Beruf.Berufe.RETTUNGSDIENST) {
                    for(int i : Houseban.getHousebannedNRPIDs(Beruf.Berufe.RETTUNGSDIENST)) {
                        Player housebanned = Script.getPlayer(i);
                        if(housebanned == null) continue;
                        Script.setSubtitle(p, housebanned.getUniqueId(), "§cHausverbot: " + Houseban.getReason(housebanned, Beruf.Berufe.RETTUNGSDIENST));
                    }
                }
            }


            if (Organisation.hasOrganisation(p)) {
                List<Blacklist> blacklist = Blacklist.getBlacklist(Organisation.getOrganisation(p));
                for (Blacklist bl : blacklist) {
                    Player blacklisted = Script.getPlayer(bl.getUserName());
                    if (blacklisted == null) continue;
                    Script.setSubtitle(p, blacklisted.getUniqueId(), "§c" + bl.getReason());
                }
            }


            if (Script.getBackUpCode(p) == null)
                p.sendMessage(Messages.INFO + "Du hast noch keinen BackupCode. Er ist wichtig, um deinen Account wiederherzustellen, falls du ihn verlierst. Nutze §8/§6backupcode §r, um einen BackupCode zu erhalten.");

        } else {
            /*if (!Script.getCountry(p).contains("Germany") && !Script.getCountry(p).contains("Austria") && !Script.getCountry(p).contains("Switzerland") && !Script.isWhitelistedIP(p.getAddress().getAddress().getHostAddress()) && !Script.isWhitelistedName(p.getName())) {
                p.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cAccess denied §8« \n\n§8§m------------------------------\n\n§7We are sorry to inform you that only players inside DE, AT & CH can join the server.\n\n§7If you think this is a mistake, please contact our support.\n\n§8§m------------------------------");
                Script.sendTeamMessage(AntiCheatSystem.PREFIX + Script.getName(p) + " wurde der Zugriff auf den Server verweigert, da er nicht aus Deutschland, Österreich oder der Schweiz kommt.");
                return;
            }*/
            p.teleport(new Location(Script.WORLD, 935, 66, 1198, 179.92924f, 0.32957163f));
            new BukkitRunnable() {
                @Override
                public void run() {
                    p.teleport(new Location(Script.WORLD, 935, 66, 1198, 179.92924f, 0.32957163f));
                }
            }.runTaskLater(NewRoleplayMain.getInstance(), 20L);
            Script.registerPlayer(e.getPlayer());
            Script.sendActionBar(e.getPlayer(), "§7Willkommen auf §eNewRP§7!");
            e.getPlayer().sendMessage("§eNew RolePlay" + " §rWillkommen auf §eNewRP§7!");
            Notifications.sendMessage(Notifications.NotificationType.REGISTRATION, "§e" + Script.getName(e.getPlayer()) + " §7hat sich auf dem Server registriert §8[§e#" + Script.getNRPID(e.getPlayer()) + "§8]");
            Title.sendTitle(p, 20, 100, 20, "§6Willkommen!", "§7auf §eNewRP§7!");
            Achievement.FIRST_JOIN.grant(p);
            new BukkitRunnable() {
                @Override
                public void run() {
                    p.sendMessage(Script.PREFIX + "Du hast die vollständige Kontrolle über das \"Tutorial\". Nutze einfach unser Achievement-System, um den Server zu erkunden.");
                    p.sendMessage(Messages.INFO + "Nutze §8/§6achievement");
                    p.sendMessage(Messages.INFO + "Beachte bitte, dass wir nicht alles erklären können. Einige Dinge musst du selbst herausfinden. Wir sind aber immer unter §8/§6support §rzu erreichen.");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.sendMessage(Messages.INFO + "Die wichtigsten Chatbefehle sind §8/§6s §r(Schreien) und §8/§6w §r(Flüstern).");
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    p.sendMessage(Messages.INFO + "Dein erster Schritt sollte die Beantragung eines Personalausweises sein. Nutze dafür §8/§6navi Stadthalle§r.");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            p.sendMessage(Messages.INFO + "Du kannst bis Level 3 den NeulingsChat mit §8/§6nc nutzen. Dort findest du andere Spieler mit ähnlichem Level und Supporter. Dort kannst du deine Anliegen auch äußern.");
                                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                                        }
                                    }.runTaskLater(NewRoleplayMain.getInstance(), 20L);
                                }
                            }.runTaskLater(NewRoleplayMain.getInstance(), 20L);
                        }
                    }.runTaskLater(NewRoleplayMain.getInstance(), 60 * 20L);
                }
            }.runTaskLaterAsynchronously(NewRoleplayMain.getInstance(), 20L);
        }
        Script.updateListname(p);
        Script.checkPlayerName(p);
        Script.resetHealth(p);
        Log.LOW.write(p, "hat den Server betreten.");
        new

                BukkitRunnable() {
                    @Override
                    public void run() {
                        Script.resetHealth(p);
                        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("player").addEntry(p.getName());
                        SDuty.updateScoreboard();
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            Script.sendTabTitle(all);
                        }
                        if (GetHere.hasOfflineTP(p)) {
                            p.teleport(GetHere.getOfflineTP(p));
                            p.sendMessage(Script.PREFIX + "Du wurdest während du Offline warst teleportiert.");
                            Script.executeUpdate("DELETE FROM offline_tp WHERE nrp_id = " + Script.getNRPID(p));
                        }
                    }
                }.

                runTaskLater(NewRoleplayMain.getInstance(), 20L);
        p.setFlySpeed(0.1f);
        if (Wahlen.wahlenActive())
            p.sendMessage(Messages.INFO + "Die Wahlen sind aktiv! Du kannst mit §8/§6wahlen §rdeine Stimme abgeben.");
        Notifications.sendMessage(Notifications.NotificationType.JOIN, "§e" + Script.getName(e.getPlayer()) + " §7hat den Server betreten.");
        if (Licenses.ERSTE_HILFE.hasLicense(Script.getNRPID(p)) && ersteHilfeExpired(p)) {
            Licenses.ERSTE_HILFE.remove(Script.getNRPID(p));
            p.sendMessage(Messages.INFO + "Dein §eErste-Hilfe-Schein §7ist abgelaufen.");
        }
        Script.updateListname(p);
        Script.sendTabTitle(e.getPlayer());
        if (Friedhof.isDead(p)) Corpse.spawnNPC(p, Friedhof.getDead(p).

                getDeathLocation());
        if (Beruf.hasBeruf(p)) {
            if (Beruf.getBeruf(p).getMOTD() != null) {
                p.sendMessage(Beruf.PREFIX + Beruf.getBeruf(p).getMOTD());
            }
        }

        if (Organisation.hasOrganisation(p)) {
            if (Organisation.getOrganisation(p).getMOTD() != null) {
                p.sendMessage(Organisation.PREFIX + Organisation.getOrganisation(p).getMOTD());
            }
        }

    }

    @EventHandler
    public void onDmg(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (!(e.getEntity() instanceof Player)) return;
        Player damager = (Player) e.getDamager();
        Player victim = (Player) e.getEntity();
        Log.DAMAGE.write(Script.getName(damager) + " hat " + Script.getName(victim) + " " + (int) e.getDamage() + " Herzen Schaden zugefügt (" + damager.getInventory().getItemInMainHand() + ") " + (e.isCancelled() ? "[BLOCKED]" : ""));
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void emptyBucket(PlayerBucketEmptyEvent e) {
        if (!BuildMode.isInBuildMode(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrink(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = e.getPlayer();
            ItemStack is = p.getInventory().getItemInMainHand();
            if (is.getType().equals(Material.WATER_BUCKET) ||
                    is.getType().equals(Material.LAVA_BUCKET) ||
                    is.getType().equals(Material.INK_SAC)) {
                e.setCancelled(!BuildMode.isInBuildMode(p));
            }
            if (p.getInventory().getItemInMainHand().getType().equals(Material.FLOWER_POT)) {
                if (!BuildMode.isInBuildMode(p)) {
                    if (e.getHand().equals(EquipmentSlot.OFF_HAND)) {
                        p.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                    } else {
                        p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    }
                    Health.THIRST.add(Script.getNRPID(p), .1F);
                    Script.playLocalSound(p.getLocation(), Sound.ENTITY_GENERIC_DRINK, 5);
                }
            }
        }
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (((Player) e.getEntity()).hasPotionEffect(PotionEffectType.REGENERATION)) return;
        if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.EATING || e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED && ((Player) e.getEntity()).getHealth() >= (((Player) e.getEntity()).getMaxHealth() * 0.75)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void FrameEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof ArmorStand) {
            if (!BuildMode.isInBuildMode((Player) e.getDamager())) e.setCancelled(true);
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
    public void onCraft(CraftItemEvent e) {
        if (BuildMode.isInBuildMode((Player) e.getWhoClicked())) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoinEvent(PlayerJoinEvent e) {
        if (Script.isInTestMode() && !Script.isNRPTeam(e.getPlayer()) && !e.getPlayer().isWhitelisted()) {
            e.getPlayer().kickPlayer("§eDer Server ist momentan im Wartungsmodus.");
            Debug.debug("Der, Spieler " + e.getPlayer().getName() + " wurde gekickt, da der Server im Wartungsmodus ist.");
        } else if (Script.isInTestMode()) {
            e.getPlayer().sendMessage(Messages.INFO + "Der Server ist momentan im Wartungsmodus.");
            Achievement.BETA_TESTER.grant(e.getPlayer());
        }
    }

    @EventHandler
    public void blockExpBottle(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (p.getItemInHand().getType().equals(Material.EXPERIENCE_BOTTLE)) {
            p.getInventory().remove(Material.EXPERIENCE_BOTTLE);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void stopFlowers(PlayerInteractEvent e) {
        Block blk = e.getClickedBlock();
        if (blk == null) return;
        if (blk.getType().name().startsWith("POTTED_") || blk.getType() == Material.FLOWER_POT) {
            e.setCancelled(!BuildMode.isInBuildMode(e.getPlayer()));
            return;
        }
    }


    //stop destroying farms
    @EventHandler
    public void onDestroy(EntityChangeBlockEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteract2(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() != Action.PHYSICAL) return;
        if (e.getClickedBlock().getType() == Material.FARMLAND || e.getClickedBlock().getType() == Material.TURTLE_EGG) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (e.getClickedBlock().getType() == Material.TNT && !Script.hasRank(e.getPlayer(), Rank.ADMINISTRATOR, false)) {
                e.setCancelled(true);
                return;
            }

            if (e.getClickedBlock().getType() == Material.CHEST && Script.hasRank(e.getPlayer(), Rank.ADMINISTRATOR, false)) {
                e.setCancelled(!SDuty.isSDuty(e.getPlayer()));
                return;
            }

            if ((e.getClickedBlock().getType() == Material.CRAFTING_TABLE || e.getClickedBlock().getType() == Material.DAYLIGHT_DETECTOR ||
                    e.getClickedBlock().getType() == Material.LECTERN || e.getClickedBlock().getType() == Material.FURNACE ||
                    e.getClickedBlock().getType() == Material.LOOM || e.getClickedBlock().getType() == Material.BIRCH_FENCE_GATE ||
                    e.getClickedBlock().getType() == Material.ACACIA_FENCE_GATE || e.getClickedBlock().getType().equals(Material.DARK_OAK_FENCE_GATE) ||
                    e.getClickedBlock().getType().equals(Material.JUNGLE_FENCE_GATE) || e.getClickedBlock().getType().equals(Material.SPRUCE_FENCE_GATE) ||
                    e.getClickedBlock().getType().equals(Material.CRIMSON_FENCE_GATE) || e.getClickedBlock().getType() == Material.WARPED_FENCE_GATE) ||
                    e.getClickedBlock().getType().equals(Material.FLOWER_POT) ||
                    e.getClickedBlock().getType().equals(Material.OAK_FENCE_GATE) || e.getClickedBlock().getType() == Material.COMPOSTER) {
                e.setCancelled(!BuildMode.isInBuildMode(e.getPlayer()));
                return;
            }

            if (e.getClickedBlock().getState() instanceof ShulkerBox) {
                if (!BuildMode.isInBuildMode(e.getPlayer()) && !SDuty.isSDuty(e.getPlayer())) {
                    e.setCancelled(true);
                    return;
                }
            }


            if (e.getClickedBlock().getType() == Material.CHEST ||
                    e.getClickedBlock().getType() == Material.TRAPPED_CHEST ||
                    e.getClickedBlock().getType() == Material.ENDER_CHEST ||
                    e.getClickedBlock().getType() == Material.CAKE ||
                    e.getClickedBlock().getType() == Material.BARREL ||
                    e.getClickedBlock().getType() == Material.DISPENSER ||
                    e.getClickedBlock().getType() == Material.DROPPER ||
                    e.getClickedBlock().getType() == Material.HOPPER ||
                    e.getClickedBlock().getType() == Material.SMITHING_TABLE ||
                    e.getClickedBlock().getType() == Material.FURNACE ||
                    e.getClickedBlock().getType() == Material.BLAST_FURNACE ||
                    e.getClickedBlock().getType() == Material.SMOKER ||
                    e.getClickedBlock().getType() == Material.BREWING_STAND ||
                    e.getClickedBlock().getType() == Material.ANVIL ||
                    e.getClickedBlock().getState() instanceof ShulkerBox ||
                    e.getClickedBlock().getType() == Material.COMMAND_BLOCK ||
                    e.getClickedBlock().getType() == Material.COMMAND_BLOCK_MINECART ||
                    e.getClickedBlock().getType() == Material.CHIPPED_ANVIL ||
                    e.getClickedBlock().getType() == Material.COMPARATOR ||
                    e.getClickedBlock().getType() == Material.DAMAGED_ANVIL ||
                    e.getClickedBlock().getType() == Material.ENCHANTING_TABLE ||
                    e.getClickedBlock().getType() == Material.CRAFTING_TABLE ||
                    e.getClickedBlock().getType() == Material.LECTERN ||
                    e.getClickedBlock().getType() == Material.BEACON ||
                    e.getClickedBlock().getType() == Material.STONECUTTER ||
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
            if (BuildMode.isInBuildMode(e.getPlayer())) {
                return;
            }
            e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().multiply(-8));
            cooldowns.put(e.getPlayer().getName(), time);
            e.getPlayer().damage(1D);
            e.getPlayer().sendMessage(Script.PREFIX + "§c§lDu hast das Ende der Welt erreicht.");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("player").removeEntry(p.getName());
        if (SDuty.isSDuty(p)) {
            SDuty.removeSDuty(p);
        }

        if (Script.hasRank(p, SUPPORTER, false)) {
            Script.team.remove(p);
        }

        if (BuildMode.isInBuildMode(p) && !Script.isInTestMode()) {
            BuildMode.removeBuildMode(p);
        }

        e.setQuitMessage(null);
        /*EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(Script.getName(p), null, "https://minotar.net/helm/"+ p.getName() + "/100.png");
        embed.setDescription("Der Spieler " + p.getName() + " hat den Server verlassen.");
        TextChannel channel = this.jdaService.getJda().getTextChannelById("1236441810885410896");
        channel.sendMessageEmbeds(embed.build()).queue();*/
        Log.LOW.write(p, "hat den Server verlassen.");
        Script.executeAsyncUpdate("INSERT INTO last_disconnect (nrp_id, time) VALUES (" + Script.getNRPID(p) + ", " + System.currentTimeMillis() + ")");
        new BukkitRunnable() {

            @Override
            public void run() {
                SDuty.updateScoreboard();
                for (Player all : Bukkit.getOnlinePlayers()) {
                    Script.sendTabTitle(all);
                }
            }
        }.runTaskLater(NewRoleplayMain.getInstance(), 20L);
    }


    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        boolean block = false;
        for (Material material : DROP_BLACKLIST) {
            if (e.getItemDrop().getItemStack().getType() == material) {
                block = true;
                break;
            }
        }
        if (block)
            e.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void RicochetingArrow(ProjectileHitEvent hit) {
        if (hit.getEntity() instanceof Arrow) {
            hit.getEntity().remove();
        }
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
        for (String s : BLOCKED_COMMANDS_SPECIFIC) {
            for (String cmd : e.getMessage().split(" ")) {
                if (cmd.toLowerCase().equalsIgnoreCase(s)) {
                    Debug.debug("blocked due to " + s);
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

            for (String s : BLOCKED_COMMANDS_SPECIFIC) {
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

    public static boolean hasOpenPresent(Player p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM birthday WHERE id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                return rs.getInt("geschenk") == 1;
            }
        } catch (Exception e) {
            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static boolean ersteHilfeExpired(Player p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM erste_hilfe WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1")) {
            if (rs.next()) {
                return (rs.getLong("awarded") + TimeUnit.DAYS.toMillis(30)) < System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
