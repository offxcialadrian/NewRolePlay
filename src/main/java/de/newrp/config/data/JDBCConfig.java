package de.newrp.config.data;

public class JDBCConfig {

    private String hostname = "hostname";
    private String username = "username";
    private String password = "password";
    private String database = "database";
    private int port = 3306;

    public JDBCConfig(final String hostname, final String username, final String password, final String database, final int port) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.database = database;
        this.port = port;
    }

    public JDBCConfig() {

    }

    public String getHostname() {
        return hostname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public int getPort() {
        return port;
    }
}
