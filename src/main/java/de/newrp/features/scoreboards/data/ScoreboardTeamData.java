package de.newrp.features.scoreboards.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.format.NamedTextColor;

@Data
@AllArgsConstructor
@Accessors(fluent = true)
public class ScoreboardTeamData {

    private final String teamName;
    private final String displayPrefix;
    private final String displayColor;
    private final String displaySuffix;
    private final boolean collide;
    private final boolean nameInvisible;

}
