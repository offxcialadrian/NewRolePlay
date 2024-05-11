package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.GoTo;
import de.newrp.Administrator.SDuty;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.AFK;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemberCommand implements CommandExecutor, TabCompleter {

    private static final String PREFIX = "§8[§6Member§8] §6» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length == 0) {

            if(Organisation.hasOrganisation(p)) {
                Organisation org = Organisation.getOrganisation(p);
                p.sendMessage(PREFIX + "Mitglieder von " + org.getName() + ":");
                int i = 0;
                for(OfflinePlayer player : org.getMember()) {
                    p.sendMessage("§8" + Messages.ARROW + " §6" + player.getName() + " §8(§6" + Organisation.getRankName(player) + "§8) "+ (AFK.isAFK(Script.getPlayer(Script.getNRPID(player)))?"§8[§6AFK§8]":"" ));
                    i++;
                }
                p.sendMessage(PREFIX  + "§8" + Messages.ARROW + " §6" + i + " Mitglieder");
                return true;
            }

            if(!Beruf.hasBeruf(p)) {
                p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
                return true;
            }

            Beruf.Berufe beruf = Beruf.getBeruf(p);
            p.sendMessage(PREFIX + "Mitglieder von " + beruf.getName() + ":");
            int i = 0;
            for(OfflinePlayer player : beruf.getMember()) {
                p.sendMessage("§8" + Messages.ARROW + " §6" + player.getName() + " §8(§6" + Beruf.getAbteilung(player).getName()+ "§8)" + (player.isOnline()? " §8[§aOnline§8] " + (AFK.isAFK(Script.getPlayer(Script.getNRPID(player)))?"§8[§6AFK§8]":"" ) : ""));
                i++;
            }
            p.sendMessage(PREFIX  + "§8" + Messages.ARROW + " §6" + i + " Mitglieder");

            return true;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("all")) {
                Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
                    if(Organisation.hasOrganisation(p)) {
                        Organisation org = Organisation.getOrganisation(p);
                        p.sendMessage(PREFIX + "Mitglieder von " + org.getName() + ":");
                        int i = 0;
                        for(OfflinePlayer player : org.getAllMembers()) {
                            p.sendMessage("§8" + Messages.ARROW + " §6" + player.getName() + " §8(§6" + Organisation.getRankName(player) + "§8)" + (player.isOnline()? " §8[§aOnline§8] " + (AFK.isAFK(Script.getPlayer(Script.getNRPID(player)))?"§8[§6AFK§8]":"" ) : ""));
                            i++;
                        }
                        p.sendMessage(PREFIX  + "§8" + Messages.ARROW + " §6" + i + " Mitglieder");
                        return;
                    }

                    if(Beruf.hasBeruf(p)) {
                        Beruf.Berufe beruf = Beruf.getBeruf(p);
                        p.sendMessage(PREFIX + "Mitglieder von " + beruf.getName() + ":");
                        int i = 0;
                        for(OfflinePlayer player : beruf.getAllMembers()) {
                            p.sendMessage("§8" + Messages.ARROW + " §6" + player.getName() + " §8(§6" + Beruf.getAbteilung(player).getName()+ "§8)" + (player.isOnline()? " §8[§aOnline§8] " + (AFK.isAFK(Script.getPlayer(Script.getNRPID(player)))?"§8[§6AFK§8]":"" ) : ""));
                            i++;
                        }
                        p.sendMessage(PREFIX  + "§8" + Messages.ARROW + " §6" + i + " Mitglieder");
                        return;
                    }
                });
            }

            Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
                Beruf.Berufe beruf = Beruf.Berufe.getBeruf(args[0]);
                if(beruf != null) {
                    p.sendMessage(PREFIX + "Mitglieder der " + beruf.getName() + ":");
                    for(OfflinePlayer player : beruf.getAllMembers()) {
                        p.sendMessage("§8" + Messages.ARROW + " §6" + player.getName() + " §8(§6" + Beruf.getAbteilung(player).getName() + "§8)" + (player.isOnline()? " §8[§aOnline§8] " + (AFK.isAFK(Script.getPlayer(Script.getNRPID(player)))?"§8[§6AFK§8]":"" ) : ""));
                    }
                    return;
                }

                Organisation org = Organisation.getOrganisation(args[0]);
                if(org != null) {
                    p.sendMessage(PREFIX + "Mitglieder von " + org.getName() + ":");
                    for(OfflinePlayer player : org.getAllMembers()) {
                        p.sendMessage("§8" + Messages.ARROW + " §6" + player.getName() + " §8(§6" + Organisation.getRankName(player) + "§8)" + (player.isOnline()? " §8[§aOnline§8] " + (AFK.isAFK(Script.getPlayer(Script.getNRPID(player)))?"§8[§6AFK§8]":"" ) : ""));
                    }
                    return;
                }

                OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
                if(Script.getNRPID(tg) == 0) {
                    p.sendMessage(Messages.ERROR + "Spieler oder Beruf nicht gefunden.");
                    return;
                }

                if(!Beruf.hasBeruf(tg) && !Organisation.hasOrganisation(tg)) {
                    p.sendMessage(Messages.ERROR + tg.getName() + " hat keinen Beruf oder ist in keiner Organisation.");
                    return;
                }

                p.sendMessage(PREFIX + tg.getName() + " befindet sich in " + (Beruf.hasBeruf(tg)? Beruf.getBeruf(tg).getName() : Organisation.getOrganisation(tg).getName()) + ".");
            });

            return true;
        }

        p.sendMessage(Messages.ERROR + "/member [Beruf/Organisation/Spieler]");

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("member") || cmd.getName().equalsIgnoreCase("members") || cmd.getName().equalsIgnoreCase("memberinfo")) {
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();
            for (Beruf.Berufe beruf : Beruf.Berufe.values()) {
                oneArgList.add(beruf.getName());
            }
            for (Organisation org : Organisation.values()) {
                oneArgList.add(org.getName());
            }

            for(Player player : Bukkit.getOnlinePlayers()) {
                oneArgList.add(player.getName());
            }

            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], oneArgList, completions);
            }

            if (args.length == 2) {
                return null;
            }
            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }

}
