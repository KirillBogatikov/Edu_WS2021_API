package org.ws2021.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public interface StatementOperator<T, S extends Statement> {
    public T operate(Connection connection, S statement) throws SQLException;
}
