package de.newrp.API;

import de.newrp.Organisationen.SchwarzmarktListener;
import de.newrp.Shop.ShopItem;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public enum Schwarzmarkt {
    PFAND(0, "Pfandleihhaus", new Location(Script.WORLD, 556, 65, 1280, 270.3405f, 1.5001209f)),
    GARAGE(1, "Garage", new Location(Script.WORLD, 708, 65, 1188, 138.8761f, 5.286323f)),
    HAFEN(2, "Hafen", new Location(Script.WORLD, 940, 67, 1066, 358.9071f, 3.0365276f)),
    FUNPARK(3, "Funpark", new Location(Script.WORLD, 843, 67, 739, 359.80933f, 1.5366807f)),
    ARCADE(4, "Arcade", new Location(Script.WORLD, 424, 76, 735, 92.05975f, 2.5869339f)),
    ALTSTADT(5, "Altstadt", new Location(Script.WORLD, 291, 67, 1005, -262.5547f, 6.029933f)),
    WALD(6, "Wald", new Location(Script.WORLD, 477, 64, 1182, -0.783306f, 2.5498862f)),
    UBAHN(7, "U-Bahn", new Location(Script.WORLD, 603, 57, 950, -60.532288f, 7.363153f)),
    CASINO(8, "Casino", new Location(Script.WORLD, 802, 110, 849, -180.53113f, 3.600285f)),
    MOTEL(9, "Motel", new Location(Script.WORLD, 808, 64, 1228, -61.73477f, 7.3633313f));

    public static final String PREFIX = "§8[§cSchwarzmarkt§8]§c " + Messages.ARROW + " §7";
    public static Schwarzmarkt CURRENT_LOCATION = null;
    public static int SCHWARZMARKT_ID;
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

        if (SCHWARZMARKT_ID != 0) {
            NPC npc = CitizensAPI.getNPCRegistry().getById(SCHWARZMARKT_ID);
            npc.despawn();
            npc.destroy();
            SCHWARZMARKT_ID = 0;
        }

        net.citizensnpcs.api.npc.NPC npc = net.citizensnpcs.api.CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "§eSchwarzmarkt");
        npc.getOrAddTrait(SkinTrait.class).setSkinName("JesusIsMyLife");
        npc.spawn(smarkt.getLocation());

        SCHWARZMARKT_ID = npc.getId();
        TradeItem item = Schwarzmarkt.TradeItem.values()[Script.getRandom(0, Schwarzmarkt.TradeItem.values().length - 1)];
        CURRENT_LOCATION.setTradeItem(item.setAmount(Script.getRandom((int) Math.ceil((double) item.getMaxAmount() / 2), item.getMaxAmount())));

        CURRENT_LOCATION.amounts = new int[]{Script.getRandom(3, 7), Script.getRandom(3, 5), (Script.getRandom(1, 5) == 2 ? 1 : 0), 1, 1};

        SchwarzmarktListener.VALID_PLAYER.clear();
    }


    public static void spawnRandom() {
        if (SCHWARZMARKT_ID != 0) {
            NPC npc = CitizensAPI.getNPCRegistry().getById(SCHWARZMARKT_ID);
            npc.despawn();
            npc.destroy();
            SCHWARZMARKT_ID = 0;
        }
        spawn(Schwarzmarkt.values()[Script.getRandom(0, Schwarzmarkt.values().length - 1)]);
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
        LOTTOSCHEIN(0, new ItemStack(Material.PAPER), "§7Lottoschein", 1),
        BROT(1, new ItemStack(Material.BREAD), "§7Brot", 20),
        LILIE(2, new ItemStack(Material.WHITE_TULIP), "§7Lilie", 4),
        TULPE(3, new ItemStack(Material.RED_TULIP), "§7Tulpe", 4),
        MARGARITE(4, new ItemStack(Material.OXEYE_DAISY), "§7Margarite", 4),
        DISK_MELLOHI(5, new ItemStack(Material.MUSIC_DISC_MELLOHI), "§7Mellohi-Schallplatte", 1),
        DISK_WAIT(6, new ItemStack(Material.MUSIC_DISC_WAIT), "§7Wait-Schallplatte", 1),
        ZEITUNG(7, new ItemStack(Material.WRITTEN_BOOK), "§7Zeitung", 1),
        HANDY(8, new ItemStack(Material.IRON_INGOT), "§7Hawaii P55", 1),
        SNEAKER(9, new ItemStack(Material.LEATHER_BOOTS), "§7Sneaker", 1),
        KABELBINDER(10, new ItemStack(Material.STRING), "§7Kabelbinder", 1),
        KARTE(11, new ItemStack(Material.MAP), "§7Karte", 1),
        TICKET(12, ShopItem.MONATSFAHRAUSWEIS.getItemStack(), "§7Monatsfahrausweis", 1),
        KAFFEE(13, new ItemStack(Material.FLOWER_POT), "§7Kaffee", 1),
        MEHL(14, new ItemStack(Material.WHITE_DYE), "§7Mehl", 1),
        PLUSH(15, new ItemStack(Material.PLAYER_HEAD), "§7Plüschtier", 1);

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
