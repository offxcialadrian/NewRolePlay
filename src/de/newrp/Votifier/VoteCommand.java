package de.newrp.Votifier;

import de.newrp.API.Script;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

public class VoteCommand implements CommandExecutor {
    public static final HashMap<String, String> votes = new HashMap<>();

    public static int getCachedAmount(Player p) {
        int i = 0;
        if (votes.containsKey(p.getName().toLowerCase() + ".minecraft-server_eu")) {
            i++;
        }
        if (votes.containsKey(p.getName().toLowerCase() + ".minecraft-serverlist_net")) {
            i++;
        }
        return i;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        p.sendMessage("§8===== §6Offizielle Voting-Seiten §8=====");
        TextComponent vote2 = new TextComponent("  §8» §6Minecraft-Serverlist.net");
        vote2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Klicke hier um zu §6Minecraft-Serverlist.net §7zu gelangen!").create()));
        vote2.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, "https://minecraft-serverlist.net/vote/1011"));
        p.spigot().sendMessage(vote2);
        p.sendMessage("§8===== §6Einfach drauf klicken §8=====");
        int dayOfTheWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        boolean weekend = dayOfTheWeek == Calendar.FRIDAY || dayOfTheWeek == Calendar.SATURDAY || dayOfTheWeek == Calendar.SUNDAY;
        int votes = VoteListener.getTotalVotesToday();
        String percentage = new DecimalFormat("#.##").format(Script.getPercentage(votes, (weekend ? de.newrp.API.Vote.VOTE_AMOUNT_WEEKEND : de.newrp.API.Vote.VOTE_AMOUNT_WEEK)));
        TextComponent vote1 = new TextComponent("  §8» §6Vote Event §c" + percentage + "%");
        vote1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§c" + votes + "§7/§c" + (weekend ? de.newrp.API.Vote.VOTE_AMOUNT_WEEKEND : de.newrp.API.Vote.VOTE_AMOUNT_WEEK)).create()));
        p.spigot().sendMessage(vote1);
        return true;
    }
}