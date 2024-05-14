package de.newrp.features.scoreboards.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(fluent = true)
public class ScoreboardTeamData {

    private final String teamName;
    private final String displayPrefix;
    private final boolean collide;
    private final boolean nameInvisible;

}
