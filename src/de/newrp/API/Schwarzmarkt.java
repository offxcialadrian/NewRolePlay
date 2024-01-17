package de.newrp.API;

import de.newrp.Organisationen.SchwarzmarktListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

public enum Schwarzmarkt {
    RUINE(0, "Ruine", new Location(Script.WORLD, 743, 69, 316, 180F, 0F));

    public static final String PREFIX = "§8[§cSchwarzmarkt§8]§c " + Messages.ARROW + " §7";
    public static Schwarzmarkt CURRENT_LOCATION = null;
    public static Villager SCHWARZMARKT_VILLAGER = null;
    private final int id;
    private final String name;
    private final Location loc;
    private Schwarzmarkt.TradeItem tradeItem;
    private int[] amounts;

    Schwarzmarkt(int id, String name, Location loc) {
        this.id = id;
        this.name = name;
        this.loc = loc;
    }

    public static void spawn(Schwarzmarkt smarkt) {
        CURRENT_LOCATION = smarkt;

        if (SCHWARZMARKT_VILLAGER != null) {
            SCHWARZMARKT_VILLAGER.getLocation().getChunk().load();
            SCHWARZMARKT_VILLAGER.remove();
        }
        smarkt.getLocation().getChunk().load();

        Villager v = (Villager) Script.WORLD.spawnEntity(smarkt.getLocation(), EntityType.VILLAGER);
        v.setAdult();
        v.setAI(false);
        v.setCanPickupItems(false);
        v.setCollidable(false);
        v.setProfession(Villager.Profession.TOOLSMITH);
        v.setCustomName("Schwarzmarkt");
        v.setCustomNameVisible(false);
        v.setRemoveWhenFarAway(false);
        v.setGravity(true);
        v.setInvulnerable(true);
        v.teleport(smarkt.getLocation());

        SCHWARZMARKT_VILLAGER = v;
        CURRENT_LOCATION.setTradeItem(Schwarzmarkt.TradeItem.values()[Script.getRandom(1, Schwarzmarkt.TradeItem.values().length - 1)].setAmount(Script.getRandom(3, 20)));

        CURRENT_LOCATION.amounts = new int[]{Script.getRandom(3, 7), Script.getRandom(3, 5), (Script.getRandom(1, 5) == 2 ? 1 : 0), 1, 1};

        SchwarzmarktListener.VALID_PLAYER.clear();
    }

    public static void spawnRandom() {
        if (SCHWARZMARKT_VILLAGER == null) {
            cleanUp();
        } else {
            SCHWARZMARKT_VILLAGER.remove();
        }
        spawn(Schwarzmarkt.values()[Script.getRandom(0, Schwarzmarkt.values().length - 1)]);
    }

    public static void cleanUp() {
        for (Entity ent : Script.WORLD.getEntitiesByClass(Villager.class)) {
            ent.getLocation().getChunk().load();
            ent.remove();
        }
    }

    public static Schwarzmarkt getSchwarzmarkt() {
        return CURRENT_LOCATION;
    }

    public static Schwarzmarkt getSchwarzmarktByID(int id) {
        for (Schwarzmarkt all : values()) {
            if (all.getID() == id) return all;
        }
        return null;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Location getLocation() {
        return this.loc;
    }

    public Schwarzmarkt.TradeItem getTradeItem() {
        return this.tradeItem;
    }

    public void setTradeItem(Schwarzmarkt.TradeItem tradeItem) {
        this.tradeItem = tradeItem;
    }

    public int[] getItemAmounts() {
        return this.amounts;
    }

    public enum TradeItem {
        LOTTOSCHEIN(0, new ItemStack(Material.PAPER), "Lottoschein", 3),
        SONNENBLUMENKERNE(1, new ItemStack(Material.PUMPKIN_SEEDS), "Sonnenblumenkerne", 64),
        BASEBALLSCHLAEGER(2, Baseballschlaeger.getItem(), "Baseballschläger", 1);

        private final int id;
        private final ItemStack item;
        private final String name;
        private final int maxAmount;
        private int amount;

        TradeItem(int id, ItemStack item, String name, int maxAmount) {
            this.id = id;
            this.item = item;
            this.amount = 1;
            this.name = name;
            this.maxAmount = maxAmount;
        }

        public static TradeItem getItemByID(int id) {
            for (TradeItem all : values()) {
                if (all.getID() == id) return all;
            }
            return null;
        }

        public int getID() {
            return this.id;
        }

        public ItemStack getItemStack() {
            return this.item;
        }

        public int getAmount() {
            return this.amount;
        }

        public TradeItem setAmount(int amount) {
            this.amount = (Math.min(amount, maxAmount));
            return this;
        }

        public int getMaxAmount() {
            return this.maxAmount;
        }

        public String getName() {
            return this.name;
        }
    }
}
