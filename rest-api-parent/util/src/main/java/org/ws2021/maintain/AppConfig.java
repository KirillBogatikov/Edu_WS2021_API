package org.ws2021.maintain;

public class AppConfig {
    private String databaseHost;
    private String databasePort;
    private String databaseName;
    private String user;
    private String password;
    private String httpPort;
    private boolean updateDatabase;
    
    public String getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(String httpPort) {
        this.httpPort = httpPort;
    }

    public String getDatabaseHost() {
        return databaseHost;
    }

    public void setDatabaseHost(String host) {
        this.databaseHost = host;
    }

    public String getDatabasePort() {
        return databasePort;
    }

    public void setDatabasePort(String port) {
        this.databasePort = port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String database) {
        this.databaseName = database;
    }

    public boolean isUpdateDatabase() {
        return updateDatabase;
    }

    public void setUpdateDatabase(boolean create) {
        this.updateDatabase = create;
    }

    public String getUser() {
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public String toJDBC() {
        return String.format("jdbc:postgresql://%s:%s/%s", databaseHost, databasePort, databaseName);
    }
}
