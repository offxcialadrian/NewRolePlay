package de.newrp.Votifier;

import de.newrp.API.Baseballschlaeger;
import de.newrp.API.Premium;
import de.newrp.API.Script;
import de.newrp.API.Token;
import de.newrp.Waffen.Waffen;
import de.newrp.Waffen.Weapon;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VoteShopListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§l§6Voteshop")) {
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                ItemStack is = e.getCurrentItem();
                Player p = (Player) e.getWhoClicked();
                e.setCancelled(true);
                e.getView().close();
                if (is.getType().equals(Material.NETHER_STAR)) {
                    p.sendMessage(VoteShop.PREFIX + "Du hast " + VoteListener.getVotepoints(Script.getNRPID(p)) + " Votepunkte.");
                } else if (is.getType().equals(Material.CHEST)) {
                    switch (is.getItemMeta().getDisplayName()) {
                        case "§6Votekiste §7[§3§lNormal§7]":
                            if (VoteListener.getVotepoints(Script.getNRPID(p)) >= Votekiste.NORMAL.getPrice()) {
                                p.closeInventory();
                                Votekiste.NORMAL.open(p);
                            } else {
                                p.sendMessage(VoteShop.PREFIX + "Diese Votekiste kostet " + Votekiste.NORMAL.getPrice() + " Votepunkte!");
                            }
                            break;
                        case "§6Votekiste §7[§3§lSpecial§7]":
                            if (VoteListener.getVotepoints(Script.getNRPID(p)) >= Votekiste.SPECIAL.getPrice()) {
                                p.closeInventory();
                                Votekiste.SPECIAL.open(p);
                            } else {
                                p.sendMessage(VoteShop.PREFIX + "Diese Votekiste kostet " + Votekiste.SPECIAL.getPrice() + " Votepunkte!");
                            }
                            break;
                        case "§6Votekiste §7[§3§lUltimate§7]":
                            if (VoteListener.getVotepoints(Script.getNRPID(p)) >= Votekiste.ULTIMATE.getPrice()) {
                                p.closeInventory();
                                Votekiste.ULTIMATE.open(p);
                            } else {
                                p.sendMessage(VoteShop.PREFIX + "Diese Votekiste kostet " + Votekiste.ULTIMATE.getPrice() + " Votepunkte!");
                            }
                            break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().equals("§6Votekiste")) {
            Player p = (Player) e.getPlayer();
            if (Votekiste.tasks.containsKey(p.getName())) {
                Integer[] tasks = Votekiste.tasks.get(p.getName());
                Bukkit.getScheduler().cancelTask(tasks[0]);
                Bukkit.getScheduler().cancelTask(tasks[1]);
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                Votekiste.tasks.remove(p.getName());

                Inventory view = p.getOpenInventory().getTopInventory();
                ItemStack price = view.getItem(13);
                Votekiste.Items i = null;
                for (Votekiste.Items items1 : Votekiste.Items.values()) {
                    if (items1.getItem().isSimilar(price) || items1.getName().equals(ChatColor.stripColor(price.getItemMeta().getDisplayName()))) {
                        i = items1;
                        break;
                    }
                }
                if (i != null) {
                    int id = Script.getNRPID(p);
                    switch (i) {
                        case PREMIUM_1:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 1 Tag Premium gewonnen!");
                            Premium.addPremiumStorage(p, TimeUnit.DAYS.toMillis(1), true);
                            break;
                        case PREMIUM_3:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 3 Tage Premium gewonnen!");
                            Premium.addPremiumStorage(p, TimeUnit.DAYS.toMillis(3), true);
                            break;
                        case PREMIUM_7:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 7 Tage Premium gewonnen!");
                            Premium.addPremiumStorage(p, TimeUnit.DAYS.toMillis(7), true);
                            break;
                        case PREMIUM_14:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 14 Tage Premium gewonnen!");
                            Premium.addPremiumStorage(p, TimeUnit.DAYS.toMillis(14), true);
                            break;
                        case CHANGE_TOKEN_PERSO:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 1 ChangeToken (Personalausweis) gewonnen!");
                            Token.PERSONALAUSWEIS.add(Script.getNRPID(p), 1);
                            break;
                        case BASEBALLSCHLAEGER:
                            p.sendMessage(VoteShop.PREFIX + "Du hast einen Baseballschläger gewonnen!");
                            p.getInventory().addItem(Waffen.setAmmo(Baseballschlaeger.getItem(), 500, 500));
                            break;
                        case EXP_500:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 500 Exp gewonnen!");
                            Script.addEXP(p, 500);
                            break;
                        case EXP_750:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 750 Exp gewonnen!");
                            Script.addEXP(p, 750);
                            break;
                        case EXP_1000:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 1000 Exp gewonnen!");
                            Script.addEXP(p, 1000);
                            break;
                        case EXP_1250:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 1250 Exp gewonnen!");
                            Script.addEXP(p, 1250);
                            break;
                        case EXP_1500:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 1500 Exp gewonnen!");
                            Script.addEXP(p, 1500);
                            break;
                        case EXP_2000:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 2000 Exp gewonnen!");
                            Script.addEXP(p, 2000);
                            break;
                        case EXP_2500:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 2500 Exp gewonnen!");
                            Script.addEXP(p, 2500);
                            break;
                        case KEVLAR_LEICHT:
                            p.sendMessage(VoteShop.PREFIX + "Du hast eine leichte Kevlar gewonnen!");
                            p.getInventory().addItem(Script.kevlar(1));
                            break;
                        case KEVLAR_SCHWER:
                            p.sendMessage(VoteShop.PREFIX + "Du hast eine schwere Kevlar gewonnen!");
                            p.getInventory().addItem(Script.kevlar(2));
                            break;
                        case MP5_MUNITION_50:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 50 Munition für die Peacekeeper gewonnen!");
                            Weapon.AK47.addMunition(id, 50);
                            break;
                        case MP5_MUNITION_75:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 75 Munition für die Peacekeeper gewonnen!");
                            Weapon.AK47.addMunition(id, 75);
                            break;
                        case MP5_MUNITION_100:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 100 Munition für die Peacekeeper gewonnen!");
                            Weapon.AK47.addMunition(id, 100);
                            break;
                        case PISTOLE_MUNITION_50:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 50 Munition für die Glory gewonnen!");
                            Weapon.PISTOLE.addMunition(id, 50);
                            break;
                        case PISTOLE_MUNITION_75:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 75 Munition für die Glory gewonnen!");
                            Weapon.PISTOLE.addMunition(id, 75);
                            break;
                        case PISTOLE_MUNITION_100:
                            p.sendMessage(VoteShop.PREFIX + "Du hast 100 Munition für die Glory gewonnen!");
                            Weapon.PISTOLE.addMunition(id, 100);
                            break;
                        case FEUERWERK:
                            ItemStack item = new ItemStack(Material.FIREWORK_ROCKET, Script.getRandom(3, 10));
                            FireworkMeta fm = (FireworkMeta) item.getItemMeta();
                            List<Color> c = Arrays.asList(Color.BLUE, Color.LIME);
                            FireworkEffect effect = FireworkEffect.builder()
                                    .flicker(true)
                                    .withColor(c)
                                    .withFade(c)
                                    .with(FireworkEffect.Type.BALL_LARGE)
                                    .trail(true)
                                    .build();
                            int r = Script.getRandom(0, 9);
                            switch (r) {
                                case 0:
                                    c = Arrays.asList(Color.BLUE, Color.LIME);
                                    effect = FireworkEffect.builder()
                                            .flicker(true)
                                            .withColor(c)
                                            .withFade(c)
                                            .with(FireworkEffect.Type.BALL_LARGE)
                                            .trail(true)
                                            .build();
                                    fm.setPower(2);
                                    break;
                                case 1:
                                    c = Collections.singletonList(Color.RED);
                                    effect = FireworkEffect.builder()
                                            .withColor(c)
                                            .withFade(c)
                                            .with(FireworkEffect.Type.STAR)
                                            .trail(true)
                                            .build();
                                    fm.setPower(3);
                                    break;
                                case 2:
                                    c = Collections.singletonList(Color.GREEN);
                                    effect = FireworkEffect.builder()
                                            .withColor(c)
                                            .withFade(c)
                                            .with(FireworkEffect.Type.CREEPER)
                                            .trail(true)
                                            .build();
                                    fm.setPower(2);
                                    break;
                                case 3:
                                    c = Arrays.asList(Color.BLUE, Color.WHITE);
                                    effect = FireworkEffect.builder()
                                            .withColor(c)
                                            .withFade(c)
                                            .with(FireworkEffect.Type.BALL)
                                            .trail(true)
                                            .build();
                                    fm.setPower(3);
                                    break;
                                case 4:
                                    c = Arrays.asList(Color.BLUE, Color.NAVY);
                                    effect = FireworkEffect.builder()
                                            .flicker(true)
                                            .withColor(c)
                                            .withFade(c)
                                            .with(FireworkEffect.Type.STAR)
                                            .trail(true)
                                            .build();
                                    fm.setPower(3);
                                    break;
                                case 5:
                                    c = Collections.singletonList(Color.YELLOW);
                                    effect = FireworkEffect.builder()
                                            .flicker(true)
                                            .withColor(c)
                                            .withFade(c)
                                            .with(FireworkEffect.Type.STAR)
                                            .trail(true)
                                            .build();
                                    fm.setPower(3);
                                    break;
                                case 6:
                                    c = Collections.singletonList(Color.ORANGE);
                                    effect = FireworkEffect.builder()
                                            .flicker(true)
                                            .withColor(c)
                                            .withFade(c)
                                            .with(FireworkEffect.Type.BALL_LARGE)
                                            .trail(true)
                                            .build();
                                    fm.setPower(3);
                                    break;
                                case 7:
                                    c = Collections.singletonList(Color.RED);
                                    effect = FireworkEffect.builder()
                                            .flicker(true)
                                            .withColor(c)
                                            .withFade(c)
                                            .with(FireworkEffect.Type.BALL_LARGE)
                                            .trail(true)
                                            .build();
                                    fm.setPower(2);
                                    break;
                                case 8:
                                    c = Arrays.asList(Color.PURPLE, Color.RED, Color.LIME);
                                    effect = FireworkEffect.builder()
                                            .flicker(true)
                                            .withColor(c)
                                            .withFade(c)
                                            .with(FireworkEffect.Type.BALL_LARGE)
                                            .trail(true)
                                            .build();
                                    fm.setPower(3);
                                    break;
                            }
                            fm.addEffect(effect);
                            item.setItemMeta(fm);
                            p.sendMessage(VoteShop.PREFIX + "Du hast Feuerwerkskörper gewonnen!");
                            p.getInventory().addItem(item);
                            p.closeInventory();
                            break;
                        default:
                            break;
                    }
                } else {
                    if (price.getType().equals(Material.COOKED_BEEF) || price.getType().equals(Material.COOKED_PORKCHOP) || price.getType().equals(Material.COOKED_CHICKEN)) {
                        p.getInventory().addItem(new ItemStack(price.getType(), price.getAmount()));
                        p.sendMessage(VoteShop.PREFIX + "Du hast etwas zu Essen gewonnen!");
                    } else if (price.getType().equals(Material.POTION)) {
                        p.getInventory().addItem(price);
                        p.sendMessage(VoteShop.PREFIX + "Du hast Trinkwasser gewonnen!");
                    }
                }
                p.closeInventory();
            }
        }
    }

    @EventHandler
    public void onClickKiste(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        if (!e.getView().getTitle().equals("§6Votekiste")) return;
        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType().equals(Material.AIR) || !item.hasItemMeta()) return;

        if (!Votekiste.tasks.containsKey(e.getWhoClicked().getName())) {
            e.getView().close();
        }
    }
}
