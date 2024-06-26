package de.newrp.News;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.Checkpoints;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
import de.newrp.Police.Jail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// 1Minify - Automatic News System
public class ANewsCommand implements CommandExecutor {

    private final ArrayList<Abteilung.Abteilungen> abteilungen = new ArrayList<>(Arrays.asList(
            Abteilung.Abteilungen.CHEFREDAKTION,
            Abteilung.Abteilungen.JOURNALIST
    ));
    private final Map<Integer, ANewsMessage> messages = new ConcurrentHashMap<>();
    public static String prefix = "§8[§6ANews§8] §8» §7";
    private int lastId = 1;
    private BukkitRunnable runnable = null;

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
        if(args.length < 1) {
            if(messages.isEmpty()) {
                player.sendMessage(prefix + "Aktuell gibt es keine ANews.");
                return true;
            }
            player.sendMessage(prefix + "Liste aller ANews:");
            messages.forEach((id, message) -> {
                if(message.isAccepted()) {
                    String text = prefix + "§6#" + id + " §8× §6" + message.getPlayer().getName() + " §8× §6" + message.getTimeAsString() + " §8× §aAkzeptiert §8× §6" + message.getText();
                    player.sendMessage(text);
                } else {
                    String text = prefix + "§6#" + id + " §8× §6" + message.getPlayer().getName() + " §8× §6" + message.getTimeAsString() + " §8× §7Offen §8× §6" + message.getText();
                    Script.sendClickableMessage(player, text, "/anews accept " + id, "§6News Annehmen");
                }
            });
            return true;
        }
        if(Beruf.isLeader(player, false)) {
            if(args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny")) {
                int id = Integer.parseInt(args[1]);
                if(!messages.containsKey(id)) {
                    player.sendMessage(Messages.ERROR + "Die ANews #" + id + " §cwurde nicht gefunden!");
                    return true;
                }
                ANewsMessage message = messages.get(id);
                if(args[0].equalsIgnoreCase("accept")) {
                    if(message.isAccepted()) {
                        player.sendMessage(Messages.ERROR + "Die ANews #" + id + " wurde bereits angenommen!");
                        return true;
                    }
                    message.accepted = true;
                    player.sendMessage(prefix + "Du hast die ANews §6#" + id + " §7akzeptiert.");
                }
                if(args[0].equalsIgnoreCase("deny")) {
                    messages.remove(id);
                    player.sendMessage(prefix + "Du hast die ANews §6#" + id + " §7abgelehnt und gelöscht.");
                }
                return true;
            }
        }
        if(args.length < 3) {
            player.sendMessage(prefix + "Nutze: §6/anews create [Zeit] [Text]");
            player.sendMessage(Messages.INFO + "Die Zeit muss im Format HH:mm angegeben werden.");
            return true;
        }
        if(args[0].equalsIgnoreCase("create")) {
            String[] split = args[1].split(":");
            if(split.length != 2) {
                player.sendMessage(Messages.ERROR + "Du hast das Zeitformat falsch angegeben.");
                return true;
            }
            String text = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(split[1]));
            calendar.set(Calendar.SECOND, 0);
            long targetTimeMillis = calendar.getTimeInMillis();
            if(System.currentTimeMillis() >= targetTimeMillis) {
                player.sendMessage(Messages.ERROR + "Die angegebene Zeit liegt in der vergangenheit.");
                return true;
            }
            messages.put(lastId, new ANewsMessage(player, false, text, targetTimeMillis));
            player.sendMessage(prefix + "Du hast die ANews '§6" + text + "§7' erstellt. §6#" + lastId);
            for (UUID uuid : Beruf.Berufe.NEWS.getBeruf().keySet()) {
                if(!player.getUniqueId().equals(uuid)) {
                    Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(NewsCommand.NEWS + player.getName() + " §7hat die ANews '§6" + text + "§7' erstellt. §6#" + lastId);
                }
            }
            lastId++;
            updateRunnable();
        }
        return true;
    }

    private void updateRunnable() {
        if(runnable != null) {
            return;
        }
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis();
                messages.forEach((id, message) -> {
                    if(millis >= message.getMillis()) {
                        message.execute(id);
                    }
                });
                if(messages.isEmpty()) {
                    runnable.cancel();
                    runnable = null;
                }
            }
        };
        runnable.runTaskTimer(NewRoleplayMain.getInstance(), 0, (20*30));
    }

    @Getter
    @AllArgsConstructor
    public class ANewsMessage {

        private Player player;
        private boolean accepted;
        private String text;
        private long millis;

        public void execute(int id) {
            messages.remove(id);
            if(accepted) {
                NewsCommand.sendNewsMessage(this.player, NewsCommand.NEWS + this.text);
            }
        }

        public String getTimeAsString() {
            return new SimpleDateFormat("HH:mm").format(this.millis);
        }

    }

}
