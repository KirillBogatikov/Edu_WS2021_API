package org.ws2021.repository;

import static org.ws2021.util.Extractor.readTextSilent;
import static org.ws2021.util.SqlResult.readBytes;
import static org.ws2021.util.SqlResult.readUUID;

import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.ws2021.data.models.User;
import org.ws2021.sql.Database;

public class UserRepository {
    private static final String SELECT = readTextSilent("account/select.sql");
    private static final String AUTH = readTextSilent("account/auth.sql");
    private static final String ID_BY_PHONE = readTextSilent("account/id_by_phone.sql");
    private static final String INSERT = readTextSilent("account/insert.sql");

    private Database database;

    public UserRepository(Database database) {
        this.database = database;
    }

    public User byId(UUID id) throws SQLException {
        return database.prepareStatement(SELECT, (c, s) -> {
            s.setObject(1, id);

            ResultSet r = s.executeQuery();
            if (r.next()) {
                return new User(readUUID(r, "id"), readBytes(r, "hash"), r.getString("firstName"), r.getString("lastName"),
                        r.getString("phone"), r.getString("documentNumber"));
            }
            return null;
        });
    }
    
    public UUID idByPhone(String phone) throws SQLException {
        return database.prepareStatement(ID_BY_PHONE, (c, s) -> {
            s.setString(1, phone);
            
            ResultSet r = s.executeQuery();
            if (r.next()) {
                return readUUID(r, "id");
            }
            return null;
        });
    }

    public byte[] hashByPhone(String phone) throws SQLException {
        return database.prepareStatement(AUTH, (c, s) -> {
            s.setString(1, phone);
            ResultSet r = s.executeQuery();
            
            if (r.next()) {
                return readBytes(r, "hash");
            }
            return null;
        });
    }

    public void add(User user) throws SQLException {
        database.prepareStatement(INSERT, (c, s) -> {
            s.setObject(1, user.getId());
            s.setBinaryStream(2, new ByteArrayInputStream(user.getHash()));
            s.setString(3, user.getFirstName());
            s.setString(4, user.getLastName());
            s.setString(5, user.getPhone());
            s.setString(6, user.getDocumentNumber());

            s.execute();
            return null;
        });
    }
}
