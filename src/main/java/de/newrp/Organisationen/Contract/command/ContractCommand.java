package de.newrp.Organisationen.Contract.command;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Organisationen.Contract.model.Contract;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.AFK;
import de.newrp.Player.Annehmen;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

public class ContractCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!Organisation.hasOrganisation(player)) {
                player.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if (Organisation.getOrganisation(player) != Organisation.HITMEN) {
                player.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if (args.length == 0) {
                sendPossabilities(player);
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "add":
                    if (args.length > 3) {
                        OfflinePlayer target = Script.getOfflinePlayer(args[1]);
                        int id = Script.getNRPID(target);
                        if (id == 0) {
                            player.sendMessage(Messages.PLAYER_NOT_FOUND);
                            return true;
                        }
                        if (Contract.hasContract(target)) {
                            player.sendMessage(Messages.ERROR + "Dieser Spieler hat bereits ein offenes Kopfgeld.");
                            return true;
                        }

                        int price;
                        try {
                            price = Integer.parseInt(args[2]);
                        } catch (Exception e) {
                            player.sendMessage(Messages.ERROR + "Ungültiger Preis.");
                            return true;
                        }

                        if (price < 2000) {
                            player.sendMessage(Messages.ERROR + "Der Mindestpreis für Kopfgeld beträgt 2000€.");
                            return true;
                        }

                        Player customer = Script.getPlayer(args[3]);
                        if (customer == null) {
                            player.sendMessage(Messages.PLAYER_NOT_FOUND);
                            return true;
                        }
                        if (Contract.wasCustomer(customer)) {
                            player.sendMessage(Contract.PREFIX + "Der Spieler hat heute schon ein Kopfgeld ausgesetzt.");
                            return true;
                        }
                        Contract ct = Contract.create(target, price);
                        Contract.addOffer(customer, ct);
                        Annehmen.offer.put(customer.getName() + ".contract", player.getName());
                        player.sendMessage(Contract.PREFIX + "Du hast " + customer.getName() + " eine Anfrage zum Eintragen eines Kopfgeldes für §6" + target.getName() + " §7in Höhe von §6" + price + "€ §7gemacht.");
                        customer.sendMessage(Contract.PREFIX + player.getName() + " hat dir eine Anfrage zum Eintragen eines Kopfgeldes für §6" + target.getName() + " §7in Höhe von §6" + price + "€ §7gemacht.");
                        Script.sendAcceptMessage(customer);
                        return true;
                    }
                    break;
                case "remove":
                    if (args.length > 1) {
                        if (Organisation.getRank(player) < 4) {
                            player.sendMessage(Messages.NO_PERMISSION);
                            return true;
                        }
                        OfflinePlayer target = Script.getOfflinePlayer(args[1]);
                        if (Script.getNRPID(target) == 0) {
                            player.sendMessage(Messages.PLAYER_NOT_FOUND);
                            return true;
                        }
                        if (!Contract.hasContract(target)) {
                            player.sendMessage(Messages.ERROR + "Dieser Spieler hat kein offenes Kopfgeld.");
                            return true;
                        }
                        Contract.remove(Objects.requireNonNull(Contract.getContract(target)));
                        Organisation.HITMEN.sendMessage(Contract.PREFIX + player.getName() + " hat das Kopfgeld von §6" + target.getName() + " §7gelöscht.");
                        return true;
                    }
                    break;
                case "info":
                    if (args.length > 1) {
                        OfflinePlayer info = Script.getOfflinePlayer(args[1]);
                        int ids = Script.getNRPID(info);
                        if (ids == 0) {
                            player.sendMessage(Messages.PLAYER_NOT_FOUND);
                            return true;
                        }
                        if (!Contract.hasContract(info)) {
                            player.sendMessage(Messages.ERROR + "Dieser Spieler hat kein offenes Kopfgeld.");
                            return true;
                        }
                        Contract ct = Contract.getContract(info);
                        assert ct != null;
                        StringBuilder sb = new StringBuilder("§8==== §eContracts").append(" §7(§6").append(Contract.getAmount()).append("§7)§e §8====\n");
                        String time = getTime(ct);
                        sb.append(" §7» §6").append(Script.getName(info)).append(" §7|§6 ").append(time)
                                .append(" §7|§6 ").append(ct.getPrice()).append("€");
                        if (info.isOnline()) {
                            sb.append((AFK.isAFK(Objects.requireNonNull(info.getPlayer())) ? " (AFK seit " + AFK.getAFKTime(info.getPlayer()) + " Uhr)\n" : "\n"));
                        }
                        player.sendMessage(sb.toString());
                        return true;
                    }
                    break;
                case "list":
                    StringBuilder sbs = new StringBuilder("§8==== §eContracts").append(" §7(§6").append(Contract.getAmount()).append("§7)§e §8====\n");
                    for (Contract contract : Contract.getContacts()) {
                        if (!contract.isActive()) continue;
                        String times = getTime(contract);
                        OfflinePlayer targets = Script.getOfflinePlayer(contract.getUserID());
                        if (targets == null) continue;
                        if (targets.isOnline()) {
                            sbs.append(" §7» §a").append(Script.getName(targets)).append(" §7|§a ").append(times)
                                    .append(" §7|§a ").append(contract.getPrice()).append("€");
                            sbs.append((AFK.isAFK(Objects.requireNonNull(targets.getPlayer())) ? " (AFK seit " + AFK.getAFKTime(targets.getPlayer()) + " Uhr)\n" : "\n"));
                        } else {
                            if (args.length > 1) {
                                sbs.append(" §7» §c").append(Script.getName(targets)).append(" §7|§c ").append(times)
                                        .append(" §7|§c ").append(contract.getPrice()).append("€\n");
                            }
                        }
                    }
                    player.sendMessage(sbs.toString());
                    return true;
                default:
                    sendPossabilities(player);
            }
            sendPossabilities(player);
        }
        return true;
    }

    public static void sendPossabilities(Player player) {
        player.sendMessage("§8=== §6Contracts §8===");
        player.sendMessage("§8» §6/contract add [Spieler] [Preis] [Käufer]");
        player.sendMessage("§8» §6/contract remove [Spieler]");
        player.sendMessage("§8» §6/contract info [Spieler]");
        player.sendMessage("§8» §6/contract list");
    }

    private static String getTime(Contract ct) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");
        Date date = new Date(ct.getTime());
        return format.format(date);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        Player player = (Player) sender;
        if (!Organisation.hasOrganisation(player)) return Collections.EMPTY_LIST;
        if (Organisation.getOrganisation(player) != Organisation.HITMEN) return Collections.EMPTY_LIST;
        final List<String> completions = new ArrayList<>();

        if (args.length == 1) StringUtil.copyPartialMatches(args[args.length - 1], Arrays.asList("add", "remove", "list", "info"), completions);
        if (args.length == 2) return null;
        if (args.length == 3) StringUtil.copyPartialMatches(args[args.length - 1], Arrays.asList("2000", "5000"), completions);
        if (args.length == 4) return null;

        Collections.sort(completions);
        return completions;
    }
}
