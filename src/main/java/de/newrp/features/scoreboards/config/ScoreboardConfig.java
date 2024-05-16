package de.newrp.features.scoreboards.config;

import de.newrp.features.scoreboards.data.ScoreboardTeamData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
public class ScoreboardConfig {

    private Set<ScoreboardTeamData> teams = new HashSet<ScoreboardTeamData>() {{
        add(new ScoreboardTeamData("0001nrp", "§c§l", "", "", false, false));
    }};

}
