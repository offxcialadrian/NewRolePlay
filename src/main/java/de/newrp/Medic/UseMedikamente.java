package de.newrp.Medic;

import de.newrp.API.Debug;
import de.newrp.API.Krankheit;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Chat.Me;
import de.newrp.Organisationen.Drogen;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class UseMedikamente implements Listener {

    public static HashMap<String, Integer> use = new HashMap<>();
    public static String PREFIX = "§8[§cMedikamente§8] §c" + Messages.ARROW + " §7";
    private final long lastUsedTimestamp = System.currentTimeMillis();

    @EventHandler
    public void onConsume(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = e.getPlayer();
            if (p.getInventory().getItemInMainHand().getType() != Material.PAPER) return;
            Medikamente m = Medikamente.getMedikamentByItemStack(p.getInventory().getItemInMainHand());
            if (m == null) return;

            if (m == Medikamente.SCHMERZMITTEL) {
                Me.sendMessage(p, "nimmt ein Schmerzmittel ein.");
                Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);
                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                if(!p.hasPotionEffect(PotionEffectType.REGENERATION)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 15, 1));
                }
                return;
            } else if(m == Medikamente.SCHMERZMITTEL_HIGH) {
                Me.sendMessage(p, "nimmt ein Schmerzmittel ein.");
                Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);
                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                if(!p.hasPotionEffect(PotionEffectType.ABSORPTION)) p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 220 * 20, 2, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 2));
                return;
            }

            Krankheit k = m.getKrankheit();
            if (k == null) return;
            if (use.containsKey(p.getName() + "." + m.getName())) {
                int i = use.get(p.getName() + "." + m.getName());
                if (i >= m.getNeeded()) {
                    Me.sendMessage(p, "nimmt ein Medikament ein.");
                    Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);
                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                    k.remove(Script.getNRPID(p));
                    p.sendMessage(Messages.INFO + "Du bist nun wieder gesund.");
                    use.remove(p.getName());
                } else {
                    use.replace(p.getName() + "." + m.getName(), i + 1);
                    Me.sendMessage(p, "nimmt ein Medikament ein.");
                    Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);
                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                }
                return;
            }

                if (!use.containsKey(p.getName() + "." + m.getName())) {
                    use.put(p.getName() + "." + m.getName(), 1);
                    Me.sendMessage(p, "nimmt ein Medikament ein.");
                    Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);
                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                }

            }
        }

    }
