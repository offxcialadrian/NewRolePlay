package de.newrp.config;

import de.newrp.config.data.JDBCConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MainConfig {

    private JDBCConfig mainConnection = new JDBCConfig();
    private JDBCConfig forumConnection = new JDBCConfig();
    private String jdaBotToken = "";
    private int maxRoadBlockAmount = 50;
    private List<String> recommendationItems = new ArrayList<String>() {{
        add("UnicaCity Discord");
        add("UnicaCity Website");
        add("Empfehlung durch jemand anderen");
        add("Voteseite (minecraft-server.eu)");
        add("Voteseite (minecraft-serverlist.net)");
        add("Social Media (bspw. TikTok)");
        add("LabyMod Partners (Serverliste/Website)");
        add("Sonstiges");
        add("Keine Angabe");
    }};
    private String teamspeakQueryUser = "";
    private String teamspeakQueryPassword = "";
    private int groupPrice = 10000;
}
