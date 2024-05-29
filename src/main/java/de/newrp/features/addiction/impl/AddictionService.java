package de.newrp.features.addiction.impl;

import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Drogen;
import de.newrp.features.addiction.IAddictionService;
import de.newrp.features.addiction.data.AddictionData;
import de.newrp.features.addiction.data.AddictionLevel;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddictionService implements IAddictionService {

    private final Map<UUID, AddictionData> addictionLevelMap = new HashMap<>();

    @Override
    public AddictionLevel getAddictionLevel(Player player, Drogen drug) {
        if(addictionLevelMap.containsKey(player.getUniqueId())) {
            return addictionLevelMap.get(player.getUniqueId()).getAddictionLevel();
        }

        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("SELECT * FROM addiction WHERE nrp_id = ? AND drug_id = ?")) {
            preparedStatement.setInt(1, Script.getNRPID(player));
            preparedStatement.setInt(2, drug.getID());
            try(final ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    final AddictionLevel addictionLevel = AddictionLevel.valueOf(resultSet.getString("addiction_level"));
                    addictionLevelMap.put(player.getUniqueId(), new AddictionData(Script.getNRPID(player), drug, addictionLevel));
                    return addictionLevel;
                }
            }
        } catch (final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }
        return null;
    }

    @Override
    public void setAddictionLevel(Player player, Drogen drug, AddictionLevel addictionLevel) {
        this.addictionLevelMap.get(player.getUniqueId()).setAddictionLevel(addictionLevel);
    }

    @Override
    public int getDrugUsageInTheLastDay(Player player, Drogen drug) {
        return 0;
    }

    @Override
    public void evaluteDrugUse(Player player, Drogen drug) {

    }

    @Override
    public void flushData(Player player, Drogen drug) {
        final AddictionData addictionData = this.addictionLevelMap.get(player.getUniqueId());
        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("UPDATE addiction SET addiction_level = ? WHERE nrp_id = ? AND drug_id = ?")) {
            preparedStatement.setString(1, addictionData.getAddictionLevel().toString());
            preparedStatement.setInt(2, addictionData.getNrpId());
            preparedStatement.setInt(3, addictionData.getDrug().getID());
            preparedStatement.executeUpdate();
        } catch (final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }
    }
}
