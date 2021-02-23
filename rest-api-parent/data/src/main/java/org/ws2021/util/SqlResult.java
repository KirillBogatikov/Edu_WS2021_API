package org.ws2021.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SqlResult {
    public static UUID readUUID(ResultSet result, String columnName) throws SQLException {
        return result.getObject(columnName, UUID.class);
    }

    public static byte[] readBytes(ResultSet result, String columnName) throws SQLException {
        try(InputStream input = result.getBinaryStream(columnName)) {
            
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int count;
            
            while((count = input.read(buffer)) > 0) {
                bytes.write(buffer, 0, count);
            }
            
            return bytes.toByteArray();
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }
}
