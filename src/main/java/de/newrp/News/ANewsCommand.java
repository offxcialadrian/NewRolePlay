package de.newrp.News;

import de.newrp.API.Messages;
import de.newrp.Administrator.Checkpoints;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Police.Jail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

// 1Minify - Automatic News System
public class ANewsCommand implements CommandExecutor {

    private final ArrayList<Abteilung.Abteilungen> abteilungen = new ArrayList<>(Arrays.asList(
            Abteilung.Abteilungen.CHEFREDAKTION,
            Abteilung.Abteilungen.JOURNALIST
    ));
    private ArrayList<ANewsMessage> messages = new ArrayList<>();
    public static String prefix = "§8[§6A-News§8] §8» §6";
    private int lastId = 1;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        if(!Beruf.hasBeruf(player) || Beruf.getBeruf(player) != Beruf.Berufe.NEWS) {
            player.sendMessage(Messages.ERROR + "Nur Mitglieder der News können diesen Befehl benutzen.");
            return true;
        }
        if(Checkpoints.hasCheckpoints(player) || Jail.isInJail(player)) {
            player.sendMessage(Messages.ERROR + "Du kannst diesen Befehl gerade nicht benutzen.");
            return true;
        }
        if(!abteilungen.contains(Beruf.getAbteilung(player)) || !Beruf.isLeader(player, false)) {
            player.sendMessage(Messages.ERROR + "Deine Abteilung hat keine Befugnis hierzu.");
            return true;
        }
        if(args.length < 2) {
            player.sendMessage(prefix + "§7Nutze: §7/anews [Zeit] [Text]");
            player.sendMessage(Messages.INFO + "Die Zeit musst du im Format HH:mm angeben.");
            return true;
        }
        if(args[0].equalsIgnoreCase("accept")) {

        }

        return false;
    }

    @Getter
    @AllArgsConstructor
    public class ANewsMessage {

        private String name;
        private int id;
        private boolean accepted;
        private String text;
        private long millis;

    }

}
