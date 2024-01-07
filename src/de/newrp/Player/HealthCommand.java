package de.newrp.Player;

import de.newrp.API.Health;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;

public class HealthCommand implements CommandExecutor, Listener {

    public static String PREFIX = "§8[§6Gesundheit§8] §6" + Messages.ARROW + " ";

    @SuppressWarnings("deprecation")
    public static TextComponent health(Player p) {
        TextComponent msg = new TextComponent(PREFIX + " §c» Gesundheit§7: ");
        double heal = p.getHealth() / 2;
        int max_heal = (int) p.getMaxHealth() / 2;
        int percentage = ((int) getPercent((int) heal, max_heal)) / 10;
        StringBuilder s = new StringBuilder("§c");
        int x = 0;
        for (int i = 0; i < percentage; i++) {
            s.append("▉");
            x++;
        }
        if (x != 10) {
            s.append("§7");
            while (x < 10) {
                s.append("▉");
                x++;
            }
        }
        TextComponent bar = new TextComponent(s.toString());
        bar.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§c" + new DecimalFormat("#.#").format(heal) + "§8/§c" + max_heal).create()));
        msg.addExtra(bar);
        return msg;
    }

    public static TextComponent blood(Player p) {
        TextComponent msg = new TextComponent(PREFIX + " §4» Blut §7: ");
        double blood = Health.getBloodAmount(Script.getNRPID(p));
        int max_heal = 6;
        int percentage = ((int) getPercent((int) blood, max_heal)) / 10;
        StringBuilder s = new StringBuilder("§4");
        int x = 0;
        for (int i = 0; i < percentage; i++) {
            s.append("▉");
            x++;
        }
        if (x != 10) {
            s.append("§7");
            while (x < 10) {
                s.append("▉");
                x++;
            }
        }
        TextComponent bar = new TextComponent(s.toString());
        bar.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§4" + new DecimalFormat("#.#").format(blood) + "L§8/§4" + max_heal + "L").create()));
        msg.addExtra(bar);
        return msg;
    }

    public static TextComponent hunger(Player p) {
        TextComponent msg = new TextComponent(PREFIX + " §a» Hunger§7: ");
        double hunger = p.getFoodLevel();
        if (hunger > 20) hunger = 20D;
        int max_hunger = 20;
        int percentage = ((int) getPercent((int) hunger, max_hunger)) / 10;
        StringBuilder s = new StringBuilder("§a");
        int x = 0;
        for (int i = 0; i < percentage; i++) {
            s.append("▉");
            x++;
        }
        if (x != 10) {
            s.append("§7");
            while (x < 10) {
                s.append("▉");
                x++;
            }
        }
        if (s.length() > 20) s = new StringBuilder("§7▉▉▉▉▉▉▉▉▉▉");
        TextComponent bar = new TextComponent(s.toString());
        bar.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§a" + new DecimalFormat("#.#").format(hunger) + "§8/§a" + max_hunger).create()));
        msg.addExtra(bar);
        return msg;
    }

    public static TextComponent thirst(Player p) {
        TextComponent msg = new TextComponent(PREFIX + " §b» Durst§7: ");
        float thirst = Health.THIRST.get(Script.getNRPID(p));
        float max_thirst = Health.THIRST.getMax();
        int percentage = ((int) getPercent((int) thirst, (int) max_thirst)) / 10;
        StringBuilder s = new StringBuilder("§b");
        int x = 0;
        for (int i = 0; i < percentage; i++) {
            s.append("▉");
            x++;
        }
        if (x != 10) {
            s.append("§7");
            while (x < 10) {
                s.append("▉");
                x++;
            }
        }
        TextComponent bar = new TextComponent(s.toString());
        bar.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§b" + new DecimalFormat("#.#").format(thirst) + "§8/§b" + max_thirst).create()));
        msg.addExtra(bar);
        return msg;
    }

    public static TextComponent fat(Player p) {
        TextComponent msg = new TextComponent(PREFIX + " §e» Fett§7: ");
        float fat = Health.FAT.get(Script.getNRPID(p));
        float max_fat = Health.FAT.getMax();
        int percentage = ((int) getPercent((int) fat, (int) max_fat)) / 10;
        StringBuilder s = new StringBuilder("§e");
        int x = 0;
        for (int i = 0; i < percentage; i++) {
            s.append("▉");
            x++;
        }
        if (x != 10) {
            s.append("§7");
            while (x < 10) {
                s.append("▉");
                x++;
            }
        }
        TextComponent bar = new TextComponent(s.toString());
        bar.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§e" + new DecimalFormat("#.#").format(fat) + "§8/§e" + max_fat).create()));
        msg.addExtra(bar);
        return msg;
    }

    public static TextComponent muscle(Player p) {
        TextComponent msg = new TextComponent(PREFIX + " §2» Muskeln§7: ");
        float m = Health.MUSCLES.get(Script.getNRPID(p));
        float max_m = Health.MUSCLES.getMax();
        int percentage = ((int) getPercent((int) m, (int) max_m)) / 10;
        StringBuilder s = new StringBuilder("§2");
        int x = 0;
        for (int i = 0; i < percentage; i++) {
            s.append("▉");
            x++;
        }
        if (x != 10) {
            s.append("§7");
            while (x < 10) {
                s.append("▉");
                x++;
            }
        }
        TextComponent bar = new TextComponent(s.toString());
        bar.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§2" + new DecimalFormat("#.###").format(m) + "§8/§2" + max_m).create()));
        msg.addExtra(bar);
        return msg;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        p.sendMessage(PREFIX + "Dein Zustand:");
        p.spigot().sendMessage(health(p));
        p.spigot().sendMessage(blood(p));
        p.spigot().sendMessage(hunger(p));
        p.spigot().sendMessage(thirst(p));
        p.spigot().sendMessage(fat(p));
        p.spigot().sendMessage(muscle(p));
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        int id = Script.getNRPID(p);
        if (id != 0) Health.setup(id);
        float fat = Health.FAT.get(id);
        p.removePotionEffect(PotionEffectType.JUMP);
        if (fat >= 20) {
            p.setWalkSpeed(.12F);
        } else if (fat >= 15) {
            p.setWalkSpeed(.14F);
        } else if (fat >= 10) {
            p.setWalkSpeed(.16F);
        } else if (fat >= 5) {
            p.setWalkSpeed(.18F);
        } else {
            p.setWalkSpeed(.2F);
        }
    }

    public static double getPercent(int percent, int total) {
        if (total == 0) return 0;
        return (percent * 100D) / total;
    }
}