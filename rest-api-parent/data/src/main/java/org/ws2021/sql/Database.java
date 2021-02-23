package org.ws2021.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import org.ws2021.maintain.HealthMonitor;
import org.ws2021.util.Extractor;
import org.ws2021.util.Mapping;

public class Database {
    private String url, user, password;

    public Database(HealthMonitor health, String url, String user, String password, boolean update) throws SQLException {
        try {
            this.url = url;
            this.user = user;
            this.password = password;
        } catch(Throwable t) {
            health.panic("Database", t);
        }
        
        if (update) {
            UUID id = health.taskStart("Database", "create");
            statement((c, s) -> s.execute(Extractor.readTextSilent("create.sql")));
            statement((c, s) -> s.execute(Extractor.readTextSilent("backup/airport.sql")));
            statement((c, s) -> s.execute(Extractor.readTextSilent("backup/place.sql")));
            statement((c, s) -> s.execute(Extractor.readTextSilent("backup/flight.sql")));
            health.taskEnd(id);
        }
    }

    public <T> T query(String sql, ModelMapper<T> mapper) throws SQLException {
        return statement((c, s) -> mapper.map(s.executeQuery(sql)));
    }

    public <T> List<T> queryList(String sql, ModelMapper<T> mapper) throws SQLException {
        return statement((c, s) -> {
            ModelMapper<List<T>> listMapper = Mapping.listOf(mapper);
            return listMapper.map(s.executeQuery(sql));
        });
    }

    public <T> T statement(StatementOperator<T, Statement> operator) throws SQLException {
        try (Connection connection = connect(null);
             Statement statement = connection.createStatement()) {
            return operator.operate(connection, statement);
        }
    }

    public <T> T prepareStatement(String sql, StatementOperator<T, PreparedStatement> operator) throws SQLException {
        try (Connection connection = connect(null);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            return operator.operate(connection, statement);
        }
    }

    public Connection connect(Boolean autoCommit) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        if (autoCommit != null) {
            connection.setAutoCommit(autoCommit);
        }
        
        return connection;
    }
}
