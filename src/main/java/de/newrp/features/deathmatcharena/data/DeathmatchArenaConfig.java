package de.newrp.features.deathmatcharena.data;

import de.newrp.config.data.LocationConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(fluent = true)
public class DeathmatchArenaConfig {

    private List<String> activatedWeapons = new ArrayList<String>() {{
        add("Peacekeeper");
        add("Striker");
        add("Guardian");
        add("Ivory");
    }};

    private Map<String, List<LocationConfig>> arenas = new HashMap<String, List<LocationConfig>>() {{
        put("default", new ArrayList<LocationConfig>() {{
            add(new LocationConfig(0, 0, 0, 0, 0));
        }});
    }};

    private int drugAmount = 10;

    private LocationConfig enterLocation = new LocationConfig(0, 0, 0, 0, 0);

}
