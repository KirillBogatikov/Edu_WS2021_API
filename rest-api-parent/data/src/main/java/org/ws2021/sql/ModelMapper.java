package org.ws2021.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ModelMapper<T> {
    public T map(ResultSet sql) throws SQLException;
}
