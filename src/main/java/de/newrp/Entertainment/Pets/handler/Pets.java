package de.newrp.Entertainment.Pets.handler;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Premium;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Chat.Chat;
import de.newrp.Chat.Me;
import de.newrp.Entertainment.Pets.model.Pet;
import de.newrp.Entertainment.Pets.types.PetType;
import de.newrp.Gangwar.GangwarCommand;
import de.newrp.Government.Stadtkasse;
import de.newrp.NewRoleplayMain;
import de.newrp.Shop.Shop;
import de.newrp.Shop.Shops;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.bizwar.IBizWarService;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class Pets implements Listener, CommandExecutor, TabCompleter {

    public static final String PREFIX = "§8[§2Pet§8] §2" + Messages.ARROW + " §7";
    public static final HashMap<UUID, Integer> amount = new HashMap<>();
    private static final HashMap<Pet, UUID> pets = new HashMap<>();

    @EventHandler
    public static void onJoin(PlayerJoinEvent event) {
        spawn(event.getPlayer());
    }

    @EventHandler
    public static void onQuit(PlayerQuitEvent event) {
        despawn(event.getPlayer());
    }

    @EventHandler
    public static void onInteract(NPCRightClickEvent event) {
        if (isPet(event.getNPC())) {
            Pet pet = getPet(event.getNPC());
            if (pet != null) {
                if (event.getClicker().isSneaking()) {
                    if (Beruf.hasBeruf(event.getClicker())) {
                        if (Beruf.getBeruf(event.getClicker()) == Beruf.Berufe.RETTUNGSDIENST) {
                            if (event.getClicker().getInventory().getItemInMainHand().getType() == Material.END_ROD) {
                                if (event.getClicker().getInventory().getItemInMainHand().hasItemMeta()) {
                                    if (event.getClicker().getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("§7Spritze")) {
                                        if (Bukkit.getPlayer(pet.getOwner()) != null) {
                                            /*
                                            Annehmen.offer.put(Bukkit.getPlayer(pet.getOwner()).getName() + ".petkill", event.getClicker().getName());

                                            Bukkit.getPlayer(pet.getOwner()).sendMessage(PREFIX + "Dein Haustier wurde von " + event.getClicker().getName() + " eingeschläfert.");
                                            event.getClicker().sendMessage(PREFIX + "Du hast das Haustier von " + Bukkit.getOfflinePlayer(pet.getOwner()).getName() + " eingeschläfert.");
                                            event.getClicker().getInventory().getItemInMainHand().setAmount(event.getClicker().getInventory().getItemInMainHand().getAmount() - 1);
                                            removePet(Script.getNRPID(Bukkit.getOfflinePlayer(pet.getOwner())), pet.getName());
                                            pets.remove(pet);
                                            pet.getNpc().despawn();
                                            pet.getNpc().destroy();
                                            return
                                             */
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (isPet(event.getClicker().getUniqueId(), event.getNPC())) {
                    if (event.getClicker().isSneaking()) {
                        Inventory inv = Bukkit.createInventory(event.getClicker(), 18, "§8» §7Haustier");
                        inv.setItem(2, Script.setNameAndLore(new ItemStack(Material.APPLE), "§cGesundheit:", "§6" + pet.getHealth() + "%"));
                        inv.setItem(4, Script.setNameAndLore(new ItemStack(Material.NAME_TAG), "§aName:", "§6" + pet.getName()));
                        inv.setItem(6, Script.setNameAndLore(new ItemStack(Material.CAT_SPAWN_EGG), "§bVariante:", "§6" + StringUtils.capitalize(pet.getVariant().replace("_", " ").toLowerCase()).replace("Default", "Standard")));
                        inv.setItem(13, Script.setName(new ItemStack(Material.BARRIER), "§7Schließen"));
                        event.getClicker().openInventory(inv);
                    } else {
                        pet.setSitting(!pet.isSitting());
                        pet.getNpc().getNavigator().cancelNavigation();
                        if (pet.getNpc().getEntity() instanceof Tameable) {
                            ((Tameable) pet.getNpc().getEntity()).setTamed(!pet.isSitting());
                            if (!pet.isSitting())
                                ((Tameable) pet.getNpc().getEntity()).setOwner(event.getClicker());
                        }
                        if (pet.getNpc().getEntity() instanceof Sittable)
                            ((Sittable) pet.getNpc().getEntity()).setSitting(pet.isSitting());
                        if (pet.getNpc().getEntity() instanceof Fox)
                            pet.getNpc().setUseMinecraftAI(!pet.isSitting());
                        if (pet.isSitting())
                            event.getClicker().sendMessage(PREFIX + "Du hast dein Haustier hingesetzt.");
                    }
                } else {
                    if (event.getClicker().isSneaking()) {
                        UUID uuid = event.getClicker().getUniqueId();
                        long millis = System.currentTimeMillis();
                        if(!cuddleCooldown.containsKey(uuid) || cuddleCooldown.get(uuid)+3000 < millis) {
                            Me.sendMessage(event.getClicker(), "streichelt " + pet.getUncoloredName() + ".");
                            cuddleCooldown.put(event.getClicker().getUniqueId(), millis);
                        } else {
                            Script.sendActionBar(event.getClicker(), PREFIX + "§cDu kannst das Haustier gerade nicht streicheln.");
                        }
                    }
                }
            }
        }
    }

    public static HashMap<UUID, Long> cuddleCooldown = new HashMap<>();
    public static HashMap<UUID, Pet> renaming = new HashMap<>();
    public static HashMap<UUID, Pet> revarianting = new HashMap<>();

    @EventHandler
    public static void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getOpenInventory().title() instanceof TextComponent) {
            if (event.getView().getTitle().contains("Haustier")) {
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
                if (event.getCurrentItem().getType() == Material.NAME_TAG) {
                    String old = event.getView().getItem(4).getLore().get(0).replace("§6", "");
                    renaming.put(event.getWhoClicked().getUniqueId(), getPet(event.getWhoClicked().getUniqueId(), old));
                    event.getWhoClicked().sendMessage(Messages.INFO + "Verwende nun §6/pet [Name] §rum dein Haustier umzubenennen, dies kostet 2000€.");
                }
                if (event.getCurrentItem().getType() == Material.CAT_SPAWN_EGG) {
                    String old = event.getView().getItem(4).getLore().get(0).replace("§6", "");
                    if (getPet(event.getWhoClicked().getUniqueId(), old).getVariant().equalsIgnoreCase("DEFAULT")) {
                        event.getWhoClicked().sendMessage(Messages.ERROR + "Dieses Haustier hat nur eine Variante.");
                        return;
                    }
                    revarianting.put(event.getWhoClicked().getUniqueId(), getPet(event.getWhoClicked().getUniqueId(), old));
                    event.getWhoClicked().sendMessage(Messages.INFO + "Verwende nun §6/pet [Variante] §rum dein Haustier anzupassen, dies kostet 5000€.");
                }
            }
        }
    }

    public static void reset() {
        for (LivingEntity entity : Script.WORLD.getLivingEntities()) entity.remove();
    }

    public static void refresh() {
        for (LivingEntity entity : Script.WORLD.getLivingEntities()) {
            if (!(entity instanceof Player)) if (!(entity instanceof ArmorStand)) if (!CitizensAPI.getNPCRegistry().isNPC(entity)) {
                if (entity instanceof Ageable)
                    if (!((Ageable) entity).isAdult()) continue;
                entity.remove();
            };
        }
    }

    public static void spawn(Player player) {
        refresh();
        enabled.put(player.getUniqueId(), false);
        amount.put(player.getUniqueId(), 0);
        for (Pet pet : getPets(Script.getNRPID(player))) {
            pet.getNpc().spawn(player.getLocation().clone().add(Script.getRandom(-2, 2), 0, Script.getRandom(-2, 2)));
            pet.getNpc().setUseMinecraftAI(true);
            pet.getNpc().getNavigator().getDefaultParameters().baseSpeed(1.4F);
            pet.getNpc().data().set(NPC.Metadata.DAMAGE_OTHERS, false);
            pet.getNpc().data().set(NPC.Metadata.AGGRESSIVE, false);
            Entity entity = pet.getNpc().getEntity();
            entity.setInvulnerable(true);
            if (pet.getType().isBaby()) {
                if (pet instanceof Ageable)
                    ((Ageable) entity).setBaby();
            }
            ((LivingEntity) entity).setCollidable(true);
            if (!pet.getVariant().equalsIgnoreCase("DEFAULT")) setVariant(pet, entity);
            if (entity instanceof Tameable) {
                ((Tameable) entity).setTamed(true);
                ((Tameable) entity).setOwner(player);
            }
            if (entity instanceof Fox) {
                ((Fox) entity).setFirstTrustedPlayer(player);
            }
            if (entity instanceof Sittable)
                ((Sittable) entity).setSitting(false);
            pet.setSitting(false);
            pets.put(pet, player.getUniqueId());
            enabled.put(player.getUniqueId(), true);
            amount.put(player.getUniqueId(), amount.get(player.getUniqueId()) + 1);
        }
    }

    private static void setVariant(Pet pet, Entity entity) {
        if (entity instanceof Cat)
            ((Cat) entity).setCatType(Cat.Type.valueOf(pet.getVariant()));
        if (entity instanceof Parrot)
            ((Parrot) entity).setVariant(Parrot.Variant.valueOf(pet.getVariant()));
        if (entity instanceof Fox)
            ((Fox) entity).setFoxType(Fox.Type.valueOf(pet.getVariant()));
    }

    private static List<String> getVariants(EntityType entity) {
        List<String> variants = new ArrayList<>();
        if (entity == EntityType.CAT)
            for (Cat.Type type : Cat.Type.values()) variants.add(type.name());
        if (entity == EntityType.PARROT)
            for (Parrot.Variant type : Parrot.Variant.values()) variants.add(type.name());
        if (entity == EntityType.FOX)
            for (Fox.Type type : Fox.Type.values()) variants.add(type.name());
        return variants;
    }

    public static void despawn(Player player) {
        for (Pet pet : getPets(player.getUniqueId())) {
            pets.remove(pet);
            pet.getTask().cancel();
            pet.getNpc().despawn();
            pet.getNpc().destroy();
        }
        for (Pet pet : pets.keySet()) {
            if (pets.get(pet) == player.getUniqueId())
                pets.remove(pet);
        }
    }

    private static List<Pet> getPets(UUID uuid) {
        List<Pet> petList = new ArrayList<>();
        for (Pet pet : pets.keySet())
            if (pets.get(pet) == uuid) petList.add(pet);
        return petList;
    }

    private static Pet getPet(UUID uuid, String name) {
        for (Pet pet : getPets(uuid))
            if (Objects.equals(pet.getName(), name)) return pet;
        return null;
    }

    private static boolean isPet(UUID uuid, NPC npc) {
        for (Pet pet : getPets(uuid))
            if (pet.getNpc() == npc) return true;
        return false;
    }

    private static boolean isPet(NPC npc) {
        for (Pet pet : getPets())
            if (pet.getNpc().getEntity() == npc.getEntity()) return true;
        return false;
    }

    private static Set<Pet> getPets() {
        return pets.keySet();
    }

    private static Pet getPet(NPC npc) {
        for (Pet pet : getPets())
            if (pet.getNpc().getId() == npc.getId()) return pet;
        return null;
    }

    private static List<Pet> getPets(int id) {
        List<Pet> pets = new ArrayList<>();
        UUID uuid = Script.getOfflinePlayer(id).getUniqueId();
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM pets WHERE id=" + id)) {
            while (rs.next()) {
                PetType type = PetType.getType(rs.getInt("type"));
                if (type != null) {
                    String name = rs.getString("name");
                    String variant = rs.getString("variant");
                    int health = rs.getInt("health");
                    NPC npc = createNPC(type, "§f§o" + name);
                    BukkitTask task = runnable(uuid, npc, name).runTaskTimer(NewRoleplayMain.getInstance(), 2 * 20L, 10L);
                    pets.add(new Pet(uuid, type, variant, name, npc, task, health, false));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pets;
    }

    private static BukkitRunnable runnable(UUID uuid, NPC npc, String name) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (npc.isSpawned()) {
                    Pet pet = getPet(uuid, name);
                    if (pet == null) {
                        cancel();
                        return;
                    }

                    if (!pet.isSitting()) {
                        Player owner = Bukkit.getPlayer(pet.getOwner());
                        if (owner == null || !owner.isOnline()) {
                            cancel();
                            return;
                        }

                        if (owner.isOnline()) {
                            if (owner.getGameMode() == GameMode.SURVIVAL) {
                                double distance = pet.getNpc().getEntity().getLocation().clone().distance(owner.getLocation().clone());
                                if (distance > 4) {
                                    if (distance > 20) {
                                        pet.getNpc().teleport((pet.getNpc().getEntity() instanceof Parrot ? owner.getLocation().clone().add(0, 1, 0) : owner.getLocation().clone()), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                    } else {
                                        pet.getNpc().getNavigator().setTarget(owner, false);
                                    }
                                } else {
                                    pet.getNpc().getNavigator().cancelNavigation();
                                }
                            }
                        }
                    } else {
                        pet.getNpc().getNavigator().cancelNavigation();
                    }
                }
            }
        };
    }

    private static NPC createNPC(PetType type, String name) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(type.getType(), name);
        npc.setName(name);
        return npc;
    }

    public static void addPet(int id, PetType type, String variant, String name) {
        Script.executeAsyncUpdate("INSERT INTO pets VALUES (" + id + ", " + type.getId() + ", '" + variant + "', '" + name + "', 100)");
    }

    public static void setHealth(int id, String name, int health) {
        Script.executeAsyncUpdate("UPDATE pets SET health=" + health + " WHERE name='" + name + "' AND id=" + id);
    }

    public static void setName(int id, String old, String name) {
        Script.executeAsyncUpdate("UPDATE pets SET name='" + name + "' WHERE name='" + old + "' AND id=" + id);
    }

    public static void setVariant(int id, String old, String variant) {
        Script.executeAsyncUpdate("UPDATE pets SET variant='" + variant + "' WHERE name='" + old + "' AND id=" + id);
    }

    public static void removePet(int id, String name) {
        Script.executeAsyncUpdate("DELETE FROM pets WHERE id=" + id + " AND name='" + name + "'");
    }

    public static boolean hasNamed(int id, String name) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM pets WHERE id=" + id + " AND name='" + name + "'")) {
            if (rs.next()) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static HashMap<UUID, Boolean> enabled = new HashMap<>();

    private static final List<String> banned = Arrays.asList("dinnerbone", "grumm", "jeb_");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                if (renaming.containsKey(player.getUniqueId())) {
                    int l = args[0].length();
                    if (l > 22) {
                        player.sendMessage(Messages.ERROR + "Dieser Name ist zu lang.");
                        return true;
                    }
                    for (String b : banned) {
                        if (args[0].toLowerCase().contains(b)) {
                            player.sendMessage(Messages.ERROR + "Der Name ist nicht verfügbar.");
                            return true;
                        }
                    }
                    for (String b : Chat.Filter) {
                        if (args[0].toLowerCase().contains(b)) {
                            player.sendMessage(Messages.ERROR + "Der Name ist nicht verfügbar.");
                            return true;
                        }
                    }
                    if (!Premium.hasPremium(player)) {
                        if (args[0].contains("&")) {
                            player.sendMessage(Messages.ERROR + "Der Name ist nur mit Premium verfügbar.");
                            return true;
                        }
                    } else {
                        int a = (int) args[0].chars().filter(ch -> ch == '&').count() * 2;
                        if (a > 14) {
                            player.sendMessage(Messages.ERROR + "Dieser Name ist zu lang.");
                            return true;
                        }
                        l -= a;
                    }
                    if (l > 16) {
                        player.sendMessage(Messages.ERROR + "Dieser Name ist zu lang.");
                        return true;
                    }
                    if (Pets.hasNamed(Script.getNRPID(player), args[0])) {
                        player.sendMessage(Messages.ERROR + "Du hast bereits ein Haustier mit diesem Namen.");
                        return true;
                    }
                    if (Script.removeMoney(player, PaymentType.BANK, 2000)) {
                        setName(Script.getNRPID(player), renaming.get(player.getUniqueId()).getName(), args[0]);
                        player.sendMessage(PREFIX + "Du hast dein Haustier zu " + args[0] + " umbenannt.");
                        //player.sendMessage(Messages.INFO + "Verwende §6/pets §rum deine Haustiere neu zu laden.");
                        player.sendMessage(Messages.INFO + "Deine Haustiere wurden neu geladen.");
                        renaming.remove(player.getUniqueId());

                        despawn(player);
                        spawn(player);

                        Shops shop = Shops.PET;
                        shop.addKasse(1000);
                        Stadtkasse.addStadtkasse(1000, "Tier-Namensänderung", null);
                        if (shop.getOwner() > 0)
                            if (Script.getOfflinePlayer(shop.getOwner()).isOnline())
                                Script.sendActionBar(Objects.requireNonNull(Script.getPlayer(shop.getOwner())), Shop.PREFIX + "Dein Shop §6" + shop.getPublicName() + " §7hat §61000€ §7Gewinn gemacht aus dem Verkauf von §6Namensänderung §7(§62000€§7)");
                    } else {
                        player.sendMessage(Messages.ERROR + "Du benötigst 2000€ um dein Haustier umzubenennen.");
                    }
                    return true;
                }
                if (revarianting.containsKey(player.getUniqueId())) {
                    if (!getVariants(revarianting.get(player.getUniqueId()).getType().getType()).contains(args[0])) {
                        player.sendMessage(Messages.ERROR + args[0] + " ist keine verfügbare Variante.");
                        return true;
                    }
                    if (Script.removeMoney(player, PaymentType.BANK, 5000)) {
                        setVariant(Script.getNRPID(player), revarianting.get(player.getUniqueId()).getName(), args[0]);
                        player.sendMessage(PREFIX + "Du hast dein Haustier zu " + args[0] + " umgeändert.");
                        player.sendMessage(Messages.INFO + "Verwende §6/pets §rum deine Haustiere neu zu laden.");
                        revarianting.remove(player.getUniqueId());

                        Shops shop = Shops.PET;
                        shop.addKasse(3000);
                        Stadtkasse.addStadtkasse(2000, "Tier-Variantenänderung", null);
                        if (shop.getOwner() > 0)
                            if (Objects.requireNonNull(Script.getOfflinePlayer(shop.getOwner())).isOnline())
                                Script.sendActionBar(Objects.requireNonNull(Script.getPlayer(shop.getOwner())), Shop.PREFIX + "Dein Shop §6" + shop.getPublicName() + " §7hat §63000€ §7Gewinn gemacht aus dem Verkauf von §6Variantenänderung §7(§65000€§7)");
                    } else {
                        player.sendMessage(Messages.ERROR + "Du benötigst 5000€ um dein Haustier anzupassen.");
                    }
                    return true;
                }
            }






            if (enabled.get(player.getUniqueId())) {
                despawn(player);
                enabled.put(player.getUniqueId(), false);
            }
            else {
                final IBizWarService bizWarService = DependencyContainer.getContainer().getDependency(IBizWarService.class);
                if (bizWarService.isMemberOfBizWar(player)) {
                    player.sendMessage(Messages.ERROR + "Du kannst dein Haustier während eines BizWars nicht nutzen.");
                    return true;
                }

                if(GangwarCommand.isInGangwar(player)) {
                    player.sendMessage(Messages.ERROR + "Du kannst dein Haustier während eines Gangwars nicht nutzen.");
                    return true;
                }
                spawn(player);
                enabled.put(player.getUniqueId(), true);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> arg = new ArrayList<>();
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (revarianting.containsKey(((Player) sender).getUniqueId()))
                arg.addAll(getVariants(revarianting.get(((Player) sender).getUniqueId()).getType().getType()));
            for (String string : arg) if (string.toLowerCase().startsWith(args[0].toLowerCase())) completions.add(string);
        }
        return completions;
    }
}
