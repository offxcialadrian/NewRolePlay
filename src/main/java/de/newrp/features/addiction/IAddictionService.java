package de.newrp.features.addiction;

import de.newrp.Organisationen.Drogen;
import de.newrp.features.addiction.data.AddictionData;
import de.newrp.features.addiction.data.AddictionLevel;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface IAddictionService {

    AddictionData getAddictionLevel(final Player player, final Drogen drug);

    void setAddictionLevel(final Player player, final Drogen drug, final AddictionLevel addictionLevel);

    int getDrugUsageInTheLastDay(final Player player, final Drogen drug);

    boolean evaluteDrugUse(final Player player, final Drogen drug);

    void flushData(final Player player);

    Optional<AddictionData> getDataOfDrug(final Player player, final Drogen drug);

}
