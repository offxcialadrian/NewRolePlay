package de.newrp.Administrator;

import com.google.common.collect.Lists;
import de.newrp.API.Messages;
import de.newrp.API.Team;
import de.newrp.NewRoleplayMain;
import de.newrp.config.MainConfig;
import de.newrp.dependencies.DependencyContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;

public class SQLCommand implements CommandExecutor {

    private final MainConfig mainConfig = DependencyContainer.getContainer().getDependency(MainConfig.class);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;
        final Team.Teams playerTeam = Team.getTeam(player);

        if(playerTeam != Team.Teams.ENTWICKLUNG) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        if(args.length == 0) {
            player.sendMessage(Messages.ERROR + "/sql [tables, table, run]");
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            switch (args[0].toLowerCase()) {
                case "tables":
                    player.sendMessage(getPrefix() + " §6Folgende SQL Tabellen existieren:");
                    for (final String table : getTableWithSchema(this.mainConfig.getMainConnection().getDatabase(), "*")) {
                        player.sendMessage(getPrefix() + " §a" + table);
                    }
                    break;
                case "table":
                    if(args.length < 2) {
                        player.sendMessage(Messages.ERROR + "/sql table [Table Name] ([WHERE])");
                        return;
                    }

                    final String tableName = args[1];
                    final String whereClause = args.length >= 3 ? " WHERE " + args[2] : "";
                    Bukkit.getLogger().info(whereClause);
                    final Optional<String> schema = this.getTableWithSchema(this.mainConfig.getMainConnection().getDatabase(), tableName).stream().findFirst();
                    if(!schema.isPresent()) {
                        player.sendMessage(Messages.ERROR + "Es existiert keine Tabelle mit dem Namen!");
                        return;
                    }

                    player.sendMessage(getPrefix() + schema.get());
                    Bukkit.getLogger().info("SELECT * FROM " + tableName + whereClause);
                    try(final PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement("SELECT * FROM " + tableName + whereClause)) {
                        try(final ResultSet resultSet = statement.executeQuery()) {
                            final ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                            final int columnCount = resultSetMetaData.getColumnCount();

                            while (resultSet.next()) {
                                final StringBuilder stringBuilder = new StringBuilder();
                                for(int i = 1; i <= columnCount; i++) {
                                    if(i > 1) stringBuilder.append(", ");
                                    try {
                                        stringBuilder.append(resultSet.getString(i));
                                    } catch(final Exception exception) {
                                        stringBuilder.append("COULD'NT LOAD " + resultSetMetaData.getColumnName(i));
                                    }
                                }

                                player.sendMessage(getPrefix() + Messages.ARROW + " " + stringBuilder);
                            }
                        }
                    } catch(final Exception exception) {
                        player.sendMessage(exception.getMessage());
                    }

                    break;
                case "run":
                    if(!NewRoleplayMain.isTest()) {
                        player.sendMessage(Messages.ERROR + "Dieser Befehl ist nur im Testmodus ausführbar!");
                        return;
                    }

                    if(args.length < 2) {
                        player.sendMessage(Messages.ERROR + "/sql run [Query]");
                        return;
                    }

                    final String query = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    Bukkit.getLogger().info(query);

                    try(final PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(query)) {
                        player.sendMessage(getPrefix() + "§aErfolgreich ausgeführt!");
                    } catch(final Exception exception) {
                        player.sendMessage(exception.getMessage());
                    }
                    break;
            }
        });

        return false;
    }

    private List<String> getTableWithSchema(final String schemaName, final String table) {
        final Map<String, List<String>> tableSchema = new HashMap<>();
        try(final Statement statement = NewRoleplayMain.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT tbl.TABLE_NAME, cols.COLUMN_NAME FROM information_schema.tables tbl LEFT JOIN information_schema.columns cols ON " +
                    "    cols.TABLE_NAME = tbl.TABLE_NAME WHERE tbl.TABLE_SCHEMA = '" + schemaName + "'" + (table.equalsIgnoreCase("*") ? "" : " AND tbl.TABLE_NAME='" + table + "'"))) {
            while (resultSet.next()) {
                final String tableName = resultSet.getString(1);
                final String columnName = resultSet.getString(2);

                if(tableSchema.containsKey(tableName)) {
                    tableSchema.get(tableName).add(columnName);
                } else {
                    tableSchema.put(tableName, Lists.newArrayList(columnName));
                }
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
        }

        final List<String> list = new ArrayList<>();
        for (Map.Entry<String, List<String>> tableSchemas : tableSchema.entrySet()) {
            final StringBuilder stringBuilder = new StringBuilder(tableSchemas.getKey() + " (");
            for (String s : tableSchemas.getValue()) {
                stringBuilder.append(s).append(", ");
            }
            stringBuilder.setLength(stringBuilder.length() - 2);
            stringBuilder.append(")");
            list.add(stringBuilder.toString());
        }

        return list;
    }

    private String getPrefix() {
        return "§8[§eSQL§8] §e" + Messages.ARROW + " §7";
    }
}
