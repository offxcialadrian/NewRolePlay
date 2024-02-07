package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Schwarzmarkt;
import de.newrp.API.Script;
import de.newrp.Commands.Test;
import de.newrp.main;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GMX implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length < 1) {
            p.sendMessage(Messages.ERROR + "/gmx [Grund]");
            return true;
        }

        StringBuilder msg = new StringBuilder();
        for(int i = 0; i < args.length; i++) {
            msg.append(args[i]).append(" ");
        }

        if(Schwarzmarkt.SCHWARZMARKT_ID != 0) {
            NPC npc = CitizensAPI.getNPCRegistry().getById(Schwarzmarkt.SCHWARZMARKT_ID);
            npc.despawn();
            npc.destroy();
        }

        if(args[0].equals("instant")) {
            Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet jetzt neu!");
            for(Player all : Bukkit.getOnlinePlayers()) {
                all.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cKICK §8« \n\n§8§m------------------------------\n\n§7Der Server startet neu§8.\n\n§7Grund §8× §e" + "Kein Grund angegeben" + "\n\n§8§m------------------------------");
            }
            Bukkit.getServer().shutdown();
            return true;
        }


        Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in einer Minute neu! (erwartete Restart-Dauer: " + Script.getRandom(40, 60) + " Sekunden)");
        Bukkit.broadcastMessage(Script.PREFIX + "§4Grund §8× §c" + msg);
        Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
            Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in 30 Sekunden neu!");
            Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in 10 Sekunden neu!");
                Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                    Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in 5 Sekunden neu!");
                    Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                        Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in 4 Sekunden neu!");
                        Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                            Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in 3 Sekunden neu!");
                            Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                                Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in 2 Sekunden neu!");
                                Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                                    Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in 1 Sekunde neu!");
                                    Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                                        Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet jetzt neu!");
                                        for(Player all : Bukkit.getOnlinePlayers()) {
                                            all.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cKICK §8« \n\n§8§m------------------------------\n\n§7Der Server startet neu§8.\n\n§7Grund §8× §e" + msg + "\n\n§8§m------------------------------");
                                        }
                                        Bukkit.getServer().shutdown();
                                    }, 20);
                                }, 20);
                            }, 20);
                        }, 20);
                    }, 20);
                }, 20*5);
            }, 20*15);
        }, 20*30);

        return false;
    }
}
