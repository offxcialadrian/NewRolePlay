package de.newrp.Shop;

import com.comphenix.protocol.PacketType;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import de.newrp.API.Gender;
import de.newrp.API.Script;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinLayers;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class ShopNPC implements Listener {

    private static final List<String> FEMALE = Arrays.asList("Marie", "Sophie", "Maria", "Sofia", "Emma", "Emilia", "Hannah", "Mia", "Anna", "Lina", "Lea", "Ella", "Lena", "Clara", "Leonie", "Amelie", "Luisa", "Emily", "Charlotte", "Mila", "Lilly", "Johanna", "Nora", "Lara", "Laura", "Greta", "Sarah", "Marlene", "Victoria", "Julia", "Frieda", "Leni", "Helena", "Ida", "Maya", "Paulina", "Mara", "Lotta", "Anni", "Elisa", "Melina", "Valentina", "Isabella", "Lia", "Stella", "Alina", "Eva", "Livia", "Mathilda", "Juna");
    private static final List<String> MALE = Arrays.asList("Paul", "Alexander", "Maximilian", "Elias", "Leon", "Ben", "Noah", "Louis", "Felix", "Lukas", "Finn", "Henry", "Jakob", "Luca", "Emil", "Jonas", "Liam", "David", "Leo", "Julian", "Jannik", "Anton", "Matteo", "Max", "Oskar", "Theo", "Samuel", "Philipp", "Moritz", "Jakob", "Jannis", "Nico", "Erik", "Johannes", "Matthias", "Adam", "Benjamin", "Jonathan", "Vincent", "Luis", "Hannes", "Linus", "Simon", "Jan", "Tom", "Lenny", "Adrian", "Till", "Aaron", "Fabian");
    private static final List<String> FEMALE_SKINS = List.of("Flolady");
    private static final List<String> MALE_SKINS = List.of("CraftBad");

    private static final HashMap<Shops, NPC> npcs = new HashMap<>();

    public static void spawn() {
        for (Shops shop : Shops.values()) {
            boolean gender = new Random().nextInt(2) == 0;
            String name;
            if (gender) name = MALE.get(new Random().nextInt(MALE.size()));
            else name = FEMALE.get(new Random().nextInt(FEMALE.size()));
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "§b" + name);
            if (shop.getOwner() == 0) {
                if (gender) npc.getOrAddTrait(SkinTrait.class).setSkinName(MALE_SKINS.get(new Random().nextInt(MALE_SKINS.size())));
                else npc.getOrAddTrait(SkinTrait.class).setSkinName(FEMALE_SKINS.get(new Random().nextInt(FEMALE_SKINS.size())));
            } else {
                try {
                    if (Script.getOfflinePlayer(shop.getOwner()) != null) {
                        npc.getOrAddTrait(SkinTrait.class).setSkinName(Objects.requireNonNull(Script.getOfflinePlayer(shop.getOwner())).getName());
                        if (Script.getGender(Script.getOfflinePlayer(shop.getOwner())) == Gender.MALE) name = MALE.get(new Random().nextInt(MALE.size()));
                        else name = FEMALE.get(new Random().nextInt(FEMALE.size()));
                        npc.setName("§b" + name);
                    }
                } catch (Exception e) {
                    if (gender) npc.getOrAddTrait(SkinTrait.class).setSkinName(MALE_SKINS.get(new Random().nextInt(MALE_SKINS.size())));
                    else npc.getOrAddTrait(SkinTrait.class).setSkinName(FEMALE_SKINS.get(new Random().nextInt(FEMALE_SKINS.size())));
                }
            }
            npc.getOrAddTrait(SkinLayers.class).hideCape();
            npc.spawn(shop.getNpcLoc().clone());
            addNpc(shop, npc);
        }
    }

    @EventHandler
    public static void onTick(ServerTickEndEvent event) {
        for (NPC npc : npcs.values()) {
            if (npc == null) continue;
            Player closest = null;
            double last = Double.MAX_VALUE;
            for (Player player : Script.WORLD.getNearbyPlayers(npc.getStoredLocation().clone(), 10)) {
                if (CitizensAPI.getNPCRegistry().isNPC(player)) continue;

                double distance = npc.getStoredLocation().clone().distance(player.getLocation());
                if (distance < last) {
                    last = distance;
                    closest = player;
                }
            }

            if (closest != null) npc.faceLocation(closest.getLocation().clone());
        }
    }

    @EventHandler
    public static void onNPC(NPCRightClickEvent event) {
        if (event.getNPC().getEntity().getType() == EntityType.PLAYER) {
            Player player = event.getClicker();
            Shops shop = Shops.getShopByLocationFurther(player.getLocation());
            if (shop == null) return;
            player.performCommand("buy");
        }
    }

    public static NPC getNpc(Shops shop) {
        if (npcs.containsKey(shop)) return npcs.get(shop);
        return null;
    }

    public static void addNpc(Shops shop, NPC npc) {
        npcs.put(shop, npc);
    }

    public static String getNpcName(Shops shop) {
        NPC npc = getNpc(shop);
        if (npc != null) return npc.getName().replace("§b", "");
        else return "Verkäufer";
    }
}
