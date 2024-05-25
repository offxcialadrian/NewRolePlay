package de.newrp.features.bizwar.config;

import de.newrp.config.data.LocationConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BizWarShopConfig {

    private int shopId;
    private LocationConfig attackerSpawn;
    private LocationConfig defenderSpawn;
    private int profitPerHour;

}
