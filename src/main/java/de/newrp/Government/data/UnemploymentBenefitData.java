package de.newrp.Government.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
public class UnemploymentBenefitData {

    private final int id;
    private final UUID uuid;
    private final String userName;
    private final int nrpId;

}
