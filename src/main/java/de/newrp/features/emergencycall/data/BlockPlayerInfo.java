package de.newrp.features.emergencycall.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@AllArgsConstructor
@Accessors(fluent = true)
public class BlockPlayerInfo {

    private final String userName;
    private final UUID uuid;
    private final int nrpId;

}
