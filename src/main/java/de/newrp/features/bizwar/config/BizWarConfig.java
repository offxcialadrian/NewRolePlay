package de.newrp.features.bizwar.config;

import de.newrp.config.data.LocationConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BizWarConfig {

    private Set<BizWarShopConfig> shopConfigs = new HashSet<>() {{
        add(new BizWarShopConfig(1, new LocationConfig(0, 0, 0, 0, 0), new LocationConfig(0, 0, 0, 0, 0), 15));
    }};

}
