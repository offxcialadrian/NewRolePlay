package de.newrp.Commands;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.newrp.API.Achievement;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.NewRoleplayMain;
import de.newrp.TeamSpeak.TeamSpeak;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.discord.Discord;
import de.newrp.discord.IJdaService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static de.newrp.TeamSpeak.TeamSpeak.*;

public class DiscordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (args.length == 0) {
            p.sendMessage("§8[§9Discord§8] §9» §7https://discord.gg/newroleplay");
            return true;
        }
        String argument = args[0];

        if (args.length == 1 && argument.equalsIgnoreCase("delete")) {
            if (!Discord.isVerified(Script.getNRPID(p))) {
                p.sendMessage(Messages.ERROR + "§cDu hast dich noch nicht verifiziert.");
                return true;
            }
            p.sendMessage(TeamSpeak.PREFIX + "§cDu hast deine Discord-Verfikation gelöscht.");
            Discord.deleteVerfify(Script.getNRPID(p));
            return true;
        }

        if (args.length == 1 && argument.equalsIgnoreCase("sync")) {
            if (!Discord.isVerified(Script.getNRPID(p))) {
                p.sendMessage(TeamSpeak.PREFIX + "§cDu hast dich noch nicht verifiziert.");
                return true;
            }
            p.sendMessage(Discord.PREFIX + "§cDu hast deinen Discord Account neu synchronisiert.");
            Discord.sync(Script.getNRPID(p));
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
                int userID = Script.getNRPID(p);
                JDA jda = DependencyContainer.getContainer().getDependency(IJdaService.class).getJda();
                String guildId = "1183386774374981662";

                Member foundMember = Discord.findMemberByName(jda, guildId, args[0]);
                if (foundMember == null) {
                    p.sendMessage(Messages.ERROR + "Der Benutzer konnte nicht gefunden werden.");
                } else if (Discord.getDiscordID(userID) != 0) {
                    p.sendMessage(Messages.ERROR + "§cDu hast dich bereits verifiziert.");
                } else {
                    Discord.verify(Script.getNRPID(p), foundMember);
                    p.sendMessage(Discord.PREFIX + "Du hast deinen Minecraft Account mit deinem Discord-Account verbunden!");
                    //Achievement.TEAMSPEAK.grant(p);
                }
            });
        }
        return true;
    }
}
