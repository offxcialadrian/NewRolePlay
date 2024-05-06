package de.newrp.config;

import de.newrp.config.data.JDBCConfig;

public class MainConfig {

    private JDBCConfig mainConnection = new JDBCConfig();
    private JDBCConfig forumConnection = new JDBCConfig();
    private String jdaBotToken = "";

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
}
