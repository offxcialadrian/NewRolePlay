package de.newrp.API;

import de.newrp.NewRoleplayMain;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class ParticleManager {

    public static final String PREFIX = "§8[§aPartikel§8]§3 ";

    public static HashMap<ParticleType, ParticleWrapper> getDefaultCacheMap() {
        HashMap<ParticleType, ParticleWrapper> map = new HashMap<>();
        for (ParticleType type : ParticleType.values()) map.put(type, type.getDefaultParticle());
        return map;
    }


    public static HashMap<ParticleManager.ParticleType, ParticleManager.ParticleWrapper> getParticles(int userID) {
        HashMap<ParticleManager.ParticleType, ParticleManager.ParticleWrapper> map = ParticleManager.getDefaultCacheMap();
        if(!Premium.hasPremium(userID)) return map;
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT routeID, spotID FROM particles WHERE userID=" + userID)) {
            if (rs.next()) {
                map.put(ParticleManager.ParticleType.ROUTE, ParticleManager.ParticleWrapper.getParticleWrapperByID(rs.getInt("routeID")));
                map.put(ParticleManager.ParticleType.SPOT, ParticleManager.ParticleWrapper.getParticleWrapperByID(rs.getInt("spotID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static ParticleWrapper getParticle(int userID, ParticleType type) {
        HashMap<ParticleType, ParticleWrapper> cache = getParticles(userID);
        return cache.get(type);
    }

    public static void setParticle(int userID, ParticleType type, ParticleWrapper particle) {
        switch (type) {
            case ROUTE:
                Script.executeAsyncUpdate("INSERT INTO particles (userID, routeID) VALUES (" + userID + ", " + particle.getID() + ") ON DUPLICATE KEY UPDATE routeID = " + particle.getID());
                break;
            case SPOT:
                Script.executeAsyncUpdate("INSERT INTO particles (userID, spotID) VALUES (" + userID + ", " + particle.getID() + ") ON DUPLICATE KEY UPDATE spotID = " + particle.getID());
                break;
        }
    }

    public enum ParticleType {
        ROUTE(0, ParticleWrapper.END_ROD),
        SPOT(1, ParticleWrapper.FIREWORKS_SPARK);

        private final int id;
        private final ParticleWrapper default_particle;

        ParticleType(int id, ParticleWrapper default_particle) {
            this.id = id;
            this.default_particle = default_particle;
        }

        public int getID() {
            return this.id;
        }

        public ParticleWrapper getDefaultParticle() {
            return this.default_particle;
        }
    }

    public enum ParticleWrapper {
        WATER_WAKE(1, org.bukkit.Particle.WATER_WAKE, new ItemStack(Material.INK_SAC, 1, (byte) 4), "Wasser", ChatColor.BLUE),
        SUSPENDED_DEPTH(2, org.bukkit.Particle.SUSPENDED_DEPTH, new ItemStack(Material.FLINT), "Tiefen-Effekt", ChatColor.BLACK),
        CRIT(3, org.bukkit.Particle.CRIT, new ItemStack(Material.EGG), "Volltreffer-Effekt", ChatColor.WHITE),
        CRIT_MAGIC(4, org.bukkit.Particle.CRIT_MAGIC, new ItemStack(Material.INK_SAC, 1, (byte) 6), "Magie-Volltreffer", ChatColor.DARK_AQUA),
        SMOKE_NORMAL(5, org.bukkit.Particle.SMOKE_NORMAL, new ItemStack(Material.COBWEB), "Rauch", ChatColor.WHITE),
        SPELL(6, org.bukkit.Particle.SPELL, new ItemStack(Material.ENCHANTING_TABLE), "Zauber", ChatColor.GRAY),
        SPELL_WITCH(7, org.bukkit.Particle.SPELL_WITCH, new ItemStack(Material.INK_SAC, 1, (byte) 5), "Verzauberung (Hexe)", ChatColor.DARK_PURPLE),
        VILLAGER_HAPPY(8, org.bukkit.Particle.VILLAGER_HAPPY, new ItemStack(Material.EMERALD), "Emerald", ChatColor.DARK_GREEN),
        ENCHANTMENT_TABLE(9, org.bukkit.Particle.ENCHANTMENT_TABLE, new ItemStack(Material.ENCHANTING_TABLE), "Verzauberungstisch", ChatColor.GRAY),
        FLAME(10, org.bukkit.Particle.FLAME, new ItemStack(Material.MAGMA_BLOCK), "Flamme", ChatColor.RED),
        CLOUD(11, org.bukkit.Particle.CLOUD, new ItemStack(Material.SNOWBALL), "Wolken", ChatColor.WHITE),
        DRAGON_BREATH(12, org.bukkit.Particle.DRAGON_BREATH, new ItemStack(Material.SKELETON_SKULL, 1), "Drachenatem", ChatColor.DARK_PURPLE),
        END_ROD(13, org.bukkit.Particle.END_ROD, new ItemStack(Material.END_ROD), "Endstab", ChatColor.WHITE),
        TOTEM(14, org.bukkit.Particle.TOTEM, new ItemStack(Material.TOTEM_OF_UNDYING), "Totem", ChatColor.GREEN),
        FIREWORKS_SPARK(15, org.bukkit.Particle.FIREWORKS_SPARK, new ItemStack(Material.FIREWORK_ROCKET), "Feuerwerk", ChatColor.WHITE),
        SMOKE_LARGE(16, org.bukkit.Particle.SMOKE_LARGE, new ItemStack(Material.COBWEB), "Viel Rauch", ChatColor.WHITE),
        SPELL_INSTANT(17, org.bukkit.Particle.SPELL_INSTANT, new ItemStack(Material.STICK), "Zauber", ChatColor.WHITE),
        SPELL_MOB(18, org.bukkit.Particle.SPELL_MOB, new ItemStack(Material.ENDER_EYE), "Monsterverzauberung", ChatColor.WHITE),
        PORTAL(19, org.bukkit.Particle.PORTAL, new ItemStack(Material.PURPLE_GLAZED_TERRACOTTA), "Portal", ChatColor.DARK_PURPLE),
        HEART(20, org.bukkit.Particle.HEART, new ItemStack(Material.HEART_OF_THE_SEA), "Herzen", ChatColor.DARK_RED);

        private final int id;
        private final org.bukkit.Particle enumparticle;
        private final ItemStack itemstack;
        private final String name;
        private final ChatColor colorcode;

        ParticleWrapper(int id, org.bukkit.Particle enumparticle, ItemStack itemstack, String name, ChatColor colorcode) {
            this.id = id;
            this.enumparticle = enumparticle;
            this.itemstack = itemstack;
            this.name = name;
            this.colorcode = colorcode;
        }

        public static ParticleWrapper getParticleWrapperByID(int id) {
            for (ParticleWrapper pw : values()) {
                if (pw.getID() == id) return pw;
            }
            return null;
        }

        public int getID() {
            return this.id;
        }

        public org.bukkit.Particle getParticle() {
            return this.enumparticle;
        }

        public ItemStack getItemStack() {
            return this.itemstack;
        }

        public String getName() {
            return this.name;
        }

        public ChatColor getColorCode() {
            return this.colorcode;
        }
    }
}