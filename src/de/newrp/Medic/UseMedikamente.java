package de.newrp.Medic;

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

    @EventHandler
    public void onConsume(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = e.getPlayer();
            if (p.getInventory().getItemInMainHand().getType() != Material.PAPER) return;
            Medikamente m = Medikamente.getMedikamentByItemStack(p.getInventory().getItemInMainHand());
            if(m == null) return;

            if(m == Medikamente.SCHMERZMITTEL) {
                Me.sendMessage(p, "nimmt ein Schmerzmittel ein.");
                Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);
                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 1));
                Drogen.addToAdiction(p);
                return;
            }

            Krankheit k = m.getKrankheit();
            if (k == null) return;
            if (use.containsKey(p.getName() + "." + m.getName())) {
                int i = (!use.containsKey(p.getName() + "." + m.getName()) ? use.get(p.getName()) : 0);
                if (i == m.getNeeded()) {
                    Me.sendMessage(p, "nimmt ein Medikament ein.");
                    Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);
                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                    k.remove(Script.getNRPID(p));
                    use.remove(p.getName());
                    return;
                }
                if(!use.containsKey(p.getName() + "." + m.getName())) {
                    use.put(p.getName() + "." + m.getName(), 1);
                    Me.sendMessage(p, "nimmt ein Medikament ein.");
                    Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);
                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                    return;
                }
                use.replace(p.getName() + "." + m.getName(), i + 1);
                Me.sendMessage(p, "nimmt ein Medikament ein.");
                Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);
                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                return;
            }
        }
    }

}
