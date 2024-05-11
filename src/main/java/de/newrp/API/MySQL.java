package de.newrp.API;

import de.newrp.NewRoleplayMain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends Database {
    private final String user;
    private final String database;
    private final String password;
    private final int port;
    private final String hostname;

    public MySQL(String hostname, int port, String database,
                 String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
    }

    @Override
    public Connection openConnection() {
        if (checkConnection()) {
            return connection;
        }
        String connectionURL = "jdbc:mysql://"
                + this.hostname + ":" + this.port;
        if (database != null) {
            connectionURL = connectionURL + "/" + this.database;
        }
        try {
            connection = DriverManager.getConnection(connectionURL,
                    this.user, this.password);
        } catch (SQLException e) {
            NewRoleplayMain.handleError(e);
        }
        return connection;
    }
}
