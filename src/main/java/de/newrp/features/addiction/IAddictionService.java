package de.newrp.features.addiction;

import de.newrp.Organisationen.Drogen;
import de.newrp.features.addiction.data.AddictionLevel;
import org.bukkit.entity.Player;

public interface IAddictionService {

    AddictionLevel getAddictionLevel(final Player player, final Drogen drug);

    void setAddictionLevel(final Player player, final Drogen drug, final AddictionLevel addictionLevel);

    int getDrugUsageInTheLastDay(final Player player, final Drogen drug);

    void evaluteDrugUse(final Player player, final Drogen drug);

    void flushData(final Player player, final Drogen drug);

}
