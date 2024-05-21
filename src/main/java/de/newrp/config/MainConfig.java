package de.newrp.config;

import de.newrp.config.data.JDBCConfig;

import java.util.ArrayList;
import java.util.List;

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

    public MainConfig(JDBCConfig mainConnection, JDBCConfig forumConnection, String jdaBotToken) {
        this.mainConnection = mainConnection;
        this.forumConnection = forumConnection;
        this.jdaBotToken = jdaBotToken;
    }

    public MainConfig() {

    }

    public JDBCConfig getMainConnection() {
        return mainConnection;
    }

    public JDBCConfig getForumConnection() {
        return forumConnection;
    }

    public String getJdaBotToken() {
        return jdaBotToken;
    }

    public int getMaxRoadBlockAmount() {
        return maxRoadBlockAmount;
    }

    public List<String> getRecommendationItems() {
        return recommendationItems;
    }
}
