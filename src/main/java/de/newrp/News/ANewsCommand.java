package de.newrp.News;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.Checkpoints;
import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
import de.newrp.Police.Jail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// 1Minify - Automatic News System
public class ANewsCommand implements CommandExecutor, TabCompleter, Listener {

    private static final Map<Integer, ANewsMessage> messages = new ConcurrentHashMap<>();
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
        if(args.length == 0) {
            if(messages.isEmpty()) {
                player.sendMessage(prefix + "Aktuell gibt es keine ANews.");
                return true;
            }
            Inventory inventory = getInventory();
            player.openInventory(inventory);
            return true;
        }
        if(Beruf.isLeader(player, true)) {
            if(args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("delete")) {
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
                if(args[0].equalsIgnoreCase("delete")) {
                    messages.remove(id);
                    player.sendMessage(prefix + "Du hast die ANews §6#" + id + " §7gelöscht.");
                }
                return true;
            }
        }
        if(args[0].equalsIgnoreCase("add")) {
            if(args.length <= 2) {
                player.sendMessage(prefix + "Nutze: §6/anews add [id] [text]");
                return true;
            }
            int id = Integer.parseInt(args[1]);
            if(!messages.containsKey(id)) {
                player.sendMessage(Messages.ERROR + "Die ANews #" + id + " existiert nicht.");
                return true;
            }
            String text = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            ANewsMessage message = messages.get(id);
            message.setText(message.getText() + " " + text);
            message.setAccepted(false);
            player.sendMessage(prefix + "Du hast '§6" + text + "§7' zu §6#" + id + " §7hinzugefügt.");
        }
        if(args[0].equalsIgnoreCase("create")) {
            if(args.length < 3) {
                player.sendMessage(prefix + "Nutze: §6/anews create [Zeit] [Text]");
                player.sendMessage(Messages.INFO + "Die Zeit muss im Format HH:mm angegeben werden.");
                return true;
            }
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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) commandSender;
        if(args.length == 1) {
            if(Beruf.isLeader(player, false)) {
                return new ArrayList<>(Arrays.asList("create","add","accept","delete"));
            }
            return new ArrayList<>(List.of("create","add"));
        }
        return null;
    }

    private Map<Integer, ANewsMessage> getTimeSorted() {
        TreeMap<Integer, ANewsMessage> sortedMap = new TreeMap<>(Comparator.comparingLong(key -> messages.get(key).getMillis()));
        sortedMap.putAll(messages);
        return sortedMap;
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

    private Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null, 45, "§8» §6ANews");
        for(int i = 0; i < 45; i++) {
            if(i > 8 && i < 36) {
                inventory.setItem(i, Script.setName(Material.GRAY_STAINED_GLASS_PANE, " "));
            } else {
                inventory.setItem(i, Script.setName(Material.BLACK_STAINED_GLASS_PANE, " "));
            }
        }
        int i = 9;
        for(Map.Entry<Integer, ANewsMessage> entry : getTimeSorted().entrySet()) {
            ANewsMessage message = entry.getValue();
            String[] words = message.getText().split(" ");
            StringBuilder stringBuilder = new StringBuilder("§7Ersteller: §6" + message.getPlayer().getName() + "\n§7Uhrzeit: §6" + message.getTimeAsString() + "\n\n§7Text:\n§8➥ §6");
            for (int j = 0; j < words.length; j++) {
                if (j % 7 == 0 && j > 0) {
                    stringBuilder.append("\n§6");
                }
                stringBuilder.append(words[j]).append(" ");
            }
            if(entry.getValue().isAccepted()) {
                inventory.setItem(i, Script.setNameAndLore(Material.LIME_DYE, "§a#" + entry.getKey(), stringBuilder.toString().split("\n")));
            } else {
                inventory.setItem(i, Script.setNameAndLore(Material.GRAY_DYE, "§7#" + entry.getKey(), stringBuilder.toString().split("\n")));
            }
            i++;
        }
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
       if(!event.getView().getTitle().equals("§8» §6ANews")) {
           return;
       }
       event.setCancelled(true);
       if(!Beruf.isLeader(player, false)) {
           return;
       }
       ItemStack item = event.getCurrentItem();
       if(item == null || !item.hasItemMeta()) {
           return;
       }
       ItemMeta meta = item.getItemMeta();
       String name = meta.getDisplayName();
       if(!name.contains("#")) {
           return;
       }
       int id = Integer.parseInt(name.replaceAll("§[0-9a-zA-Z]", "").replaceAll("\\D", ""));
       if(!messages.containsKey(id)) {
           player.sendMessage(Messages.ERROR + "Diese ANews existiert nicht mehr!");
           return;
       }
       ANewsMessage message = messages.get(id);
       message.setAccepted(!message.isAccepted());
       player.openInventory(getInventory());
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ANewsMessage {

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
