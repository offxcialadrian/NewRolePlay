package de.newrp.features.addiction.impl;

import de.newrp.API.Premium;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Drogen;
import de.newrp.features.addiction.IAddictionService;
import de.newrp.features.addiction.data.AddictionData;
import de.newrp.features.addiction.data.AddictionLevel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.swing.text.html.Option;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AddictionService implements IAddictionService {

    private final Map<UUID, Set<AddictionData>> addictionLevelMap = new HashMap<>();

    @Override
    public AddictionData getAddictionLevel(Player player, Drogen drug) {
        if(addictionLevelMap.containsKey(player.getUniqueId())) {
            final AddictionData addictionData = getDataOfDrug(player, drug).orElse(null);
            if(addictionData != null) {
                return addictionData;
            }
        }

        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("SELECT * FROM addiction WHERE nrp_id = ? AND drug_id = ?")) {
            preparedStatement.setInt(1, Script.getNRPID(player));
            preparedStatement.setInt(2, drug.getID());
            try(final ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    final AddictionLevel addictionLevel = AddictionLevel.valueOf(resultSet.getString("addiction_level"));
                    final int heal = resultSet.getInt("heal");
                    final AddictionData addictionData = new AddictionData(Script.getNRPID(player), drug, addictionLevel, 0, heal);
                    if(!addictionLevelMap.containsKey(player.getUniqueId())) {
                        addictionLevelMap.put(player.getUniqueId(), new HashSet<>(){{ add(addictionData); }});
                    } else {
                        addictionLevelMap.get(player.getUniqueId()).add(addictionData);
                    }
                    return addictionData;
                }
            }
        } catch (final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }

        final AddictionData addictionData = new AddictionData(Script.getNRPID(player), drug, AddictionLevel.NOT_ADDICTED, 0, 0);
        if(!addictionLevelMap.containsKey(player.getUniqueId())) {
            addictionLevelMap.put(player.getUniqueId(), new HashSet<>(){{ add(addictionData); }});
        } else {
            addictionLevelMap.get(player.getUniqueId()).add(addictionData);
        }
        return addictionData;
    }

    @Override
    public void setAddictionLevel(Player player, Drogen drug, AddictionLevel addictionLevel) {
        final Optional<AddictionData> addictionData = this.getDataOfDrug(player, drug);
        addictionData.ifPresent(data -> data.setAddictionLevel(addictionLevel));
    }

    @Override
    public int getDrugUsageInTheLastDay(Player player, Drogen drug) {
        final AddictionData addictionData = this.getDataOfDrug(player, drug).orElse(null);
        if(addictionData != null) {
            return addictionData.getUsage();
        }

        return 0;
    }

    @Override
    public boolean evaluteDrugUse(Player player, Drogen drug) {
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            System.out.println("evaluating");
            Optional<AddictionData> dataOptional = this.getDataOfDrug(player, drug);
            if(!dataOptional.isPresent()) {
                final AddictionData addictionData = new AddictionData(Script.getNRPID(player), drug, AddictionLevel.NOT_ADDICTED, 0, 0);
                if(!this.addictionLevelMap.containsKey(player.getUniqueId())) {
                    this.addictionLevelMap.put(player.getUniqueId(), new HashSet<>(){{ add(addictionData); }});
                } else {
                    this.addictionLevelMap.get(player.getUniqueId()).add(addictionData);
                }
                System.out.println("inserted");
                dataOptional = Optional.of(addictionData);
            }

            final AddictionData addictionData = dataOptional.get();
            final int usage = addictionData.getUsage();
            addictionData.setUsage(usage + 1);
            System.out.println("usage is " + usage);
            final int premiumChance = Premium.hasPremium(player) ? 25 : 0;
            if(addictionData.getAddictionLevel() == AddictionLevel.NOT_ADDICTED) {
                if(Script.getRandom(0, (drug.getAddictionChance() + premiumChance) - usage) == 0) {
                    addictionData.setAddictionLevel(AddictionLevel.PARTIALLY_ADDICTED);
                    player.sendMessage("§8[§6Drogen§8] §7Du fängst an dich an §6" + drug.getName() + " §7zu gewöhnen...");
                    addictionData.setUsage(0);
                }
            } else if(addictionData.getAddictionLevel() == AddictionLevel.PARTIALLY_ADDICTED) {
                if(Script.getRandom(0, (drug.getAddictionChance() + premiumChance) - usage - 30) <= 0) {
                    addictionData.setAddictionLevel(AddictionLevel.FULLY_ADDICTED);
                    player.sendMessage("§8[§6Drogen§8] §7Du hast eine Abhängigkeit zu §6" + drug.getName() + " §7entwickelt!");
                }
            }
        });
        return false;
    }

    @Override
    public void flushData(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            for (final AddictionData addictionData : this.addictionLevelMap.get(player.getUniqueId())) {
                try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("REPLACE INTO addiction(nrp_id, drug_id, addiction_level, heal) VALUES(?, ?, ?, ?)")) {
                    preparedStatement.setInt(1, addictionData.getNrpId());
                    preparedStatement.setInt(2, addictionData.getDrug().getID());
                    preparedStatement.setString(3, addictionData.getAddictionLevel().toString());
                    preparedStatement.setInt(4, addictionData.getHeal());
                    preparedStatement.executeUpdate();
                    System.out.println("flushing data");
                } catch (final Exception exception) {
                    NewRoleplayMain.handleError(exception);
                }
            }
        });
    }

    @Override
    public Optional<AddictionData> getDataOfDrug(Player player, Drogen drug) {
        if(!this.addictionLevelMap.containsKey(player.getUniqueId())) {
            return Optional.empty();
        }

        return this.addictionLevelMap.get(player.getUniqueId())
                .stream()
                .filter(data -> data.getDrug() == drug)
                .findFirst();
    }

    @Override
    public boolean isAddictedToAnything(Player player) {
        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("SELECT nrp_id FROM addiction WHERE nrp_id = ? AND addiction_level = 'FULLY_ADDICTED'")) {
            preparedStatement.setInt(1, Script.getNRPID(player));
            try(final ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }
        return false;
    }

    @Override
    public int healPlayer(Player player) {
        final AtomicInteger highestHeal = new AtomicInteger(0);

        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("SELECT heal FROM addiction WHERE nrp_id = ? AND addiction_level = 'FULLY_ADDICTED'")) {
            preparedStatement.setInt(1, Script.getNRPID(player));
            try(final ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    final int heal = resultSet.getInt("heal");
                    if(heal > highestHeal.get()) {
                        highestHeal.set(heal);
                    }
                }
            }
        } catch (final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }

        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("UPDATE addiction SET heal = ? WHERE nrp_id = ? AND addiction_level = 'FULLY_ADDICTED'")) {
            preparedStatement.setInt(1, highestHeal.get());
            preparedStatement.setInt(2, Script.getNRPID(player));
        } catch (final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }
        return highestHeal.get() + 1;
    }

    @Override
    public void clearAddiction(Player player) {
        for (Drogen drug : Drogen.values()) {
            this.setAddictionLevel(player, drug, AddictionLevel.NOT_ADDICTED);
        }
    }
}
