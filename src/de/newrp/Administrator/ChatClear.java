package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChatClear implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if (!Script.hasRank(p, Rank.MODERATOR, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!Script.isInTestMode()) if (Script.hasRank(all, Rank.SUPPORTER, false)) continue;
            Bukkit.broadcastMessage("§c§lChatClear wird eingeleitet...");
            for (int i = 0; i < 1000; i++) {
                all.sendMessage("§c§l" + generateRandomString(5));
            }
            Bukkit.broadcastMessage("§c§lChatClear beendet.");
        }

        p.sendMessage(Script.PREFIX + "Du hast den Chat geleert.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat den Chat geleert.", true);

        return false;
    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt((int) (Math.random() * characters.length())));
        }
        return result.toString();
    }

}
