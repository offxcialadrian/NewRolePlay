package de.newrp.config;

import de.newrp.config.data.JDBCConfig;

public class MainConfig {

    private JDBCConfig mainConnection = new JDBCConfig();
    private JDBCConfig forumConnection = new JDBCConfig();

    public MainConfig(JDBCConfig mainConnection, JDBCConfig forumConnection) {
        this.mainConnection = mainConnection;
        this.forumConnection = forumConnection;
    }

    public MainConfig() {

    }

    public JDBCConfig getMainConnection() {
        return mainConnection;
    }

    public JDBCConfig getForumConnection() {
        return forumConnection;
    }
}
