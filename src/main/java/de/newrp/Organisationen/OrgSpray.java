package de.newrp.Organisationen;

import de.newrp.API.*;
import de.newrp.NewRoleplayMain;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OrgSpray implements Listener {

    public static final String PREFIX = "§8[§eGraffiti§8] §e" + Messages.ARROW + " §7";
    public static HashMap<Location, Organisation> C_SPRAY = new HashMap<>();
    private static final Map<String, Long> LAST_CLICK = new HashMap<>();
    public static HashMap<String, Integer> LEVEL = new HashMap<>();
    public final HashMap<Location, Long> spray_cooldown = new HashMap<>();
    public final HashMap<String, Long> small_cooldown = new HashMap<>();
    public final HashMap<String, HashMap<Location, Integer>> m = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getHand() == EquipmentSlot.OFF_HAND) return;
            Player p = e.getPlayer();
            Block b = e.getClickedBlock();
            if (!p.getInventory().getItemInMainHand().equals(new ItemBuilder(Material.LEVER).setName("§eGraffiti").build()))
                return;
            assert b != null;
            if (b.getType().equals(Material.WHITE_WALL_BANNER)) {
                boolean valid = C_SPRAY.containsKey(b.getLocation());
                if (small_cooldown.containsKey(p.getName())) {
                    if (small_cooldown.get(p.getName()) > System.currentTimeMillis()) {
                        return;
                    }
                }
                if (valid) {
                    if (Organisation.hasOrganisation(p)) {
                        long time = System.currentTimeMillis();
                        if (spray_cooldown.containsKey(b.getLocation())) {
                            long lastSprayUsage = spray_cooldown.get(b.getLocation());
                            if (lastSprayUsage + 600 * 1000 > time) {
                                long left = (lastSprayUsage + 600 * 1000) - time;
                                int min = (int) TimeUnit.MILLISECONDS.toMinutes(left);
                                if (min == 0) {
                                    int sec = (int) TimeUnit.MILLISECONDS.toSeconds(left);
                                    p.sendMessage(PREFIX + "Du kannst das Graffiti erst in " + sec + " Sekunden übersprayen.");
                                } else {
                                    p.sendMessage(PREFIX + "Du kannst das Graffiti erst in " + min + " Minuten übersprayen.");
                                }
                                return;
                            }
                        }
                        int level = LEVEL.computeIfAbsent(p.getName(), k -> 0);
                        LAST_CLICK.put(p.getName(), time);
                        LEVEL.replace(p.getName(), level + 1);
                        progressBar(8, p);
                        addSpray(p, b);
                        small_cooldown.put(p.getName(), System.currentTimeMillis() + 1500);
                        if (level >= 6) {
                            LAST_CLICK.remove(p.getName());
                            LEVEL.remove(p.getName());
                        }
                    }
                }
            }
        }
    }

    private static void progressBar(double required_progress, Player p) {
        double current_progress = LEVEL.get(p.getName());
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
        Script.sendActionBar(p, "§eGraffiti sprühen.. §8» §a" + sb.toString());
    }

    public void removeSpray(Player p, Block b) {
        if (!m.containsKey(p.getName())) {
            HashMap<Location, Integer> map = new HashMap<>();
            map.put(b.getLocation(), 0);
            m.put(p.getName(), map);
        }
        HashMap<Location, Integer> map = m.get(p.getName());
        if (!map.containsKey(b.getLocation())) {
            map.put(b.getLocation(), 0);
            m.put(p.getName(), map);
        }
        int i = map.get(b.getLocation());
        map.put(b.getLocation(), ++i);
        Banner banner = (Banner) b.getState();
        if (i == 1) {
            List<Pattern> patterns = banner.getPatterns();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.GRADIENT));
            banner.setPatterns(patterns);
        } else if (i == 2) {
            List<Pattern> patterns = banner.getPatterns();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.GRADIENT_UP));
            banner.setPatterns(patterns);
        } else if (i == 3) {
            List<Pattern> patterns = banner.getPatterns();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.BASE));
            banner.setPatterns(patterns);
        } else if (i == 4) {
            Organisation f = Organisation.getOrganisation(p);
            BannerMeta meta = (BannerMeta) f.getFraktionSpray().getBanner().getItemMeta();
            List<Pattern> new_patterns = meta.getPatterns();
            new_patterns.add(new Pattern(DyeColor.WHITE, PatternType.GRADIENT_UP));
            new_patterns.add(new Pattern(DyeColor.WHITE, PatternType.GRADIENT));
            new_patterns.add(new Pattern(DyeColor.WHITE, PatternType.BASE));
            meta.setPatterns(new_patterns);
            banner.setPatterns(new_patterns);
        } else if (i == 5) {
            List<Pattern> patterns = banner.getPatterns();
            patterns.remove(patterns.size() - 1);
            banner.setPatterns(patterns);
        } else if (i == 6) {
            List<Pattern> patterns = banner.getPatterns();
            patterns.remove(patterns.size() - 1);
            banner.setPatterns(patterns);
        } else if (i == 7) {
            Organisation f = Organisation.getOrganisation(p);
            banner.setPatterns(f.getFraktionSpray().getPattern());
            map.put(b.getLocation(), 0);
            spray_cooldown.put(b.getLocation(), System.currentTimeMillis());
        }
        p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, .5F, 6F);
        Location loc = p.getEyeLocation();
        for (double d = 0; d <= 2; d += 0.1) {
            loc.add(loc.getDirection().multiply(.1D));
            new de.newrp.API.Particle(org.bukkit.Particle.CLOUD, loc, false, .05F, .05F, .05F, 0.01F, 4).sendAll();
        }
        if (i == 7) {
            List<Pattern> patterns = banner.getPatterns();
            patterns.clear();
            banner.setPatterns(patterns);
            banner.setPatterns(Organisation.getOrganisation(p).getFraktionSpray().getPattern());
            m.remove(p.getName());
            p.sendMessage(PREFIX + "Du hast das Graffiti entfernt.");
            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
            Script.addEXP(p, Script.getRandom(3, 5), true);
        } else {
            m.put(p.getName(), map);
        }
        banner.update();

        Script.executeAsyncUpdate("UPDATE graffiti SET org = " + null + " WHERE x = " + b.getLocation().getBlockX() + " AND y = " + b.getLocation().getBlockY() + " AND z = " + b.getLocation().getBlockZ());
    }

    public void addSpray(Player p, Block b) {
        if (!m.containsKey(p.getName())) {
            HashMap<Location, Integer> map = new HashMap<>();
            map.put(b.getLocation(), 0);
            m.put(p.getName(), map);
        }
        HashMap<Location, Integer> map = m.get(p.getName());
        if (!map.containsKey(b.getLocation())) {
            map.put(b.getLocation(), 0);
            m.put(p.getName(), map);
        }
        int i = map.get(b.getLocation());
        map.put(b.getLocation(), ++i);
        Banner banner = (Banner) b.getState();
        if (i == 1) {
            List<Pattern> patterns = banner.getPatterns();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.GRADIENT));
            banner.setPatterns(patterns);
        } else if (i == 2) {
            List<Pattern> patterns = banner.getPatterns();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.GRADIENT_UP));
            banner.setPatterns(patterns);
        } else if (i == 3) {
            List<Pattern> patterns = banner.getPatterns();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.BASE));
            banner.setPatterns(patterns);
        } else if (i == 4) {
            Organisation f = Organisation.getOrganisation(p);
            BannerMeta meta = (BannerMeta) f.getFraktionSpray().getBanner().getItemMeta();
            List<Pattern> new_patterns = meta.getPatterns();
            new_patterns.add(new Pattern(DyeColor.WHITE, PatternType.GRADIENT_UP));
            new_patterns.add(new Pattern(DyeColor.WHITE, PatternType.GRADIENT));
            new_patterns.add(new Pattern(DyeColor.WHITE, PatternType.BASE));
            meta.setPatterns(new_patterns);
            banner.setPatterns(new_patterns);
        } else if (i == 5) {
            List<Pattern> patterns = banner.getPatterns();
            patterns.remove(patterns.size() - 1);
            banner.setPatterns(patterns);
        } else if (i == 6) {
            List<Pattern> patterns = banner.getPatterns();
            patterns.remove(patterns.size() - 1);
            banner.setPatterns(patterns);
        } else if (i == 7) {
            Organisation f = Organisation.getOrganisation(p);
            banner.setPatterns(f.getFraktionSpray().getPattern());
            map.put(b.getLocation(), 0);
            spray_cooldown.put(b.getLocation(), System.currentTimeMillis());
        }
        p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, .5F, 6F);
        Location loc = p.getEyeLocation();
        Organisation f = Organisation.getOrganisation(p);
        for (double d = 0; d <= 2; d += 0.1) {
            loc.add(loc.getDirection().multiply(.1D));
            new de.newrp.API.Particle(org.bukkit.Particle.CLOUD, loc, false, .05F, .05F, .05F, 0.01F, 4).sendAll();
        }
        if (i == 7) {
            List<Pattern> patterns = banner.getPatterns();
            patterns.clear();
            banner.setPatterns(patterns);
            banner.setPatterns(Organisation.getOrganisation(p).getFraktionSpray().getPattern());
            m.remove(p.getName());
            p.sendMessage(PREFIX + "Du hast das Graffiti mit deiner Organisationsflagge übersprayt.");
            f.sendMessage(PREFIX + p.getName() + " hat ein Graffiti mit der Flagge der Organisation übersprayt.");
            if (p.getInventory().getItemInMainHand().getType().equals(Material.LEVER))
                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
            f.addExp(Script.getRandom(5, 10));
            Activity.grantActivity(Script.getNRPID(p), Activities.GRAFFITI);
            Script.addEXP(p, Script.getRandom(3, 5), true);
        } else {
            m.put(p.getName(), map);
        }
        banner.update();


        Script.executeAsyncUpdate("UPDATE graffiti SET org = " + f.getID() + " WHERE x = " + b.getLocation().getBlockX() + " AND y = " + b.getLocation().getBlockY() + " AND z = " + b.getLocation().getBlockZ());
        C_SPRAY.put(b.getLocation(), f);
    }

    public enum FraktionSpray {
        GROVE(0),
        BRATERSTWO(1),
        TRIORLA(2),
        FALCONE(3),
        CORLEONE(4),
        MIAMI_VIPERS(6),
        POLICE(5);

        private final int id;

        FraktionSpray(int id) {
            this.id = id;
        }

        public static void init() {
            World w = Script.WORLD;
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT x, y, z, org FROM graffiti")) {
                while (rs.next()) {
                    Location loc = new Location(w, rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                    int fid = rs.getInt("org");
                    Organisation organisation = Organisation.getOrganisation(fid);
                    if (fid != 0) {
                        for (Organisation f : Organisation.values()) {
                            if (fid == f.getID()) {
                                organisation = f;
                                break;
                            }
                        }
                    }
                    C_SPRAY.put(loc, organisation);
                    if (!loc.getBlock().getType().equals(Material.WHITE_WALL_BANNER)) {
                        System.err.println("Wrong block material for Type: FraktionSpray.java! Expected: WALL_BANNER at " + loc);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public int getID() {
            return this.id;
        }

        public List<Pattern> getPattern() {
            if (this == GROVE) {
                List<Pattern> patterns = new ArrayList<>();
                patterns.add(new Pattern(DyeColor.GREEN, PatternType.BASE));
                patterns.add(new Pattern(DyeColor.BLACK, PatternType.GRADIENT));
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.FLOWER));
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.CROSS)); //Bitte überprüfen, ob es das diagonale ist
                patterns.add(new Pattern(DyeColor.LIME, PatternType.RHOMBUS_MIDDLE));
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.GLOBE));
                return patterns;
            } else if (this == CORLEONE) {
                List<Pattern> patterns = new ArrayList<>();
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.BASE));
                patterns.add(new Pattern(DyeColor.LIGHT_GRAY, PatternType.FLOWER));
                patterns.add(new Pattern(DyeColor.BLACK, PatternType.GRADIENT));
                patterns.add(new Pattern(DyeColor.RED, PatternType.RHOMBUS_MIDDLE));
                patterns.add(new Pattern(DyeColor.BLACK, PatternType.SKULL));
                return patterns;
            } else if (this == FALCONE) {
                List<Pattern> patterns = new ArrayList<>();
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.BASE));
                patterns.add(new Pattern(DyeColor.BLACK, PatternType.RHOMBUS_MIDDLE));
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.HALF_HORIZONTAL_MIRROR));
                patterns.add(new Pattern(DyeColor.BLACK, PatternType.CIRCLE_MIDDLE));
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.TRIANGLE_BOTTOM));
                patterns.add(new Pattern(DyeColor.BLACK, PatternType.TRIANGLE_BOTTOM));
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
                patterns.add(new Pattern(DyeColor.BLACK, PatternType.BORDER));
                return patterns;
            } else if (this == TRIORLA) {
                List<Pattern> patterns = new ArrayList<>();
                patterns.add(new Pattern(DyeColor.BLUE, PatternType.BASE));
                patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_BOTTOM));
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
                patterns.add(new Pattern(DyeColor.BLACK, PatternType.FLOWER));
                return patterns;
            } else if (this == BRATERSTWO) {
                List<Pattern> patterns = new ArrayList<>();
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.BASE));
                patterns.add(new Pattern(DyeColor.RED, PatternType.CURLY_BORDER));
                patterns.add(new Pattern(DyeColor.YELLOW, PatternType.CIRCLE_MIDDLE));
                patterns.add(new Pattern(DyeColor.RED, PatternType.GRADIENT));
                patterns.add(new Pattern(DyeColor.RED, PatternType.CROSS)); //Bitte überprüfen, ob es das diagonale ist
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.FLOWER));
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.TRIANGLE_TOP));
                return patterns;
            } else if (this == POLICE) {
                List<Pattern> patterns = new ArrayList<>();
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.BASE));
                return patterns;
            } else if(this == MIAMI_VIPERS) {
                // To-Do: Change
                List<Pattern> patterns = new ArrayList<>();
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.BASE));
                patterns.add(new Pattern(DyeColor.RED, PatternType.CURLY_BORDER));
                patterns.add(new Pattern(DyeColor.YELLOW, PatternType.CIRCLE_MIDDLE));
                patterns.add(new Pattern(DyeColor.RED, PatternType.GRADIENT));
                patterns.add(new Pattern(DyeColor.RED, PatternType.CROSS)); //Bitte überprüfen, ob es das diagonale ist
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.FLOWER));
                patterns.add(new Pattern(DyeColor.WHITE, PatternType.TRIANGLE_TOP));
                return patterns;
            }
            return new ArrayList<>();
        }

        public ItemStack getBanner() {
            ItemStack banner = new ItemStack(Material.WHITE_WALL_BANNER);
            BannerMeta meta = (BannerMeta) banner.getItemMeta();
            if (this == GROVE) {
                meta.setDisplayName("Grove");
                meta.setPatterns(getPattern());
            } else if (this == FALCONE) {
                meta.setDisplayName("Falcone");
                meta.setPatterns(getPattern());
            } else if (this == CORLEONE) {
                meta.setDisplayName("Corleone");
                meta.setPatterns(getPattern());
            } else if (this == TRIORLA) {
                meta.setDisplayName("Kartell");
                meta.setPatterns(getPattern());
            } else if (this == BRATERSTWO) {
                meta.setDisplayName("Braterstwo");
                meta.setPatterns(getPattern());
            } else if (this == POLICE) {
                meta.setDisplayName("Polizei");
                meta.setPatterns(getPattern());
            }
            banner.setItemMeta(meta);
            return banner;
        }
    }
}
