package de.newrp.features.deathmatcharena.data;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(fluent = true)
public class DeathmatchArenaStats {

    private int kills = 0;
    private int deaths = 0;
    private int shotsFired = 0;
    private int shotsHit = 0;

}
