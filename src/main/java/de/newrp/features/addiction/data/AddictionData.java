package de.newrp.features.addiction.data;

import de.newrp.Organisationen.Drogen;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddictionData {

    private final int nrpId;
    private final Drogen drug;
    private AddictionLevel addictionLevel;
    private int usage;
    private int heal;

}
