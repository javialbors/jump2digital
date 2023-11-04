package app.persistence;

import app.model.Skin;
import app.model.Type;
import app.model.User;
import app.utils.DataErrorException;
import app.utils.DBErrorException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class UserRepository {

    public static User getUserByID(int id) throws SQLException, DBErrorException, DataErrorException {
        ResultSet rs = Database.query("SELECT * FROM User WHERE id = ?", new Object[]{id});
        if (rs == null) throw new DBErrorException();
        if (!rs.next()) throw new DataErrorException(404, "User not found");

        return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("api_key"), rs.getFloat("funds"));
    }

    public static User getUser(String username) throws SQLException, DBErrorException, DataErrorException {
        ResultSet rs = Database.query("SELECT * FROM User WHERE username = ?", new Object[]{username});
        if (rs == null) throw new DBErrorException();
        if (!rs.next()) throw new DataErrorException(404, "User not found");

        return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("api_key"), rs.getFloat("funds"));
    }

    public static User addUser(String username, String hashedPassword, String apiKey) throws SQLException, DataErrorException, DBErrorException {
        Database.update("INSERT INTO User(username, password, api_key) VALUES (?, ?, ?)", new Object[]{username, hashedPassword, apiKey});

        try {
            return getUser(username);
        } catch (DataErrorException e) {
            e.printStackTrace();
            throw new DataErrorException(500, "Error creating the user");
        }
    }

    public static User getUserByKey(String apiKey) throws SQLException, DBErrorException, DataErrorException {
        ResultSet rs = Database.query("SELECT id, username, funds FROM User WHERE api_key = ?", new Object[]{apiKey});
        if (rs == null) throw new DBErrorException();
        if (!rs.next()) throw new DataErrorException(404, "Invalid API Key");

        return new User(rs.getInt("id"), rs.getString("username"), rs.getFloat("funds"));
    }

    public static User buySkin(int userID, int skinID, float funds) throws SQLException, DataErrorException, DBErrorException {
        Database.update("INSERT INTO UserSkinColor (user, skin, color) VALUES (?, ?, (SELECT default_color FROM Skin WHERE id = ?))", new Object[]{userID, skinID, skinID});
        Database.update("UPDATE User SET funds = ? WHERE id = ?", new Object[]{funds, userID});

        return getUserByID(userID);
    }

    public static Collection<Skin> getSkinsFromUser(int userID) throws SQLException, DBErrorException, DataErrorException {
        ResultSet rs = Database.query("SELECT s.*, c.hex FROM UserSkinColor AS usc INNER JOIN User AS u ON usc.user = u.id INNER JOIN Skin AS s ON usc.skin = s.id INNER JOIN Color AS c ON usc.color = c.id WHERE u.id = ?", new Object[]{userID});
        if (rs == null) throw new DBErrorException();

        ArrayList<Skin> userSkins = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");

            userSkins.add(new Skin(id, rs.getString("name"), rs.getString("hex")));
        }

        HashMap<Integer, ArrayList<Type>> skinTypes = SkinRepository.getAllSkinTypes();
        for (Skin i: userSkins) {
            i.setTypes(skinTypes.get(i.getId()));
        }

        return userSkins;
    }

    public static Skin getUserSkinByID(int userID, int skinID) throws SQLException, DBErrorException, DataErrorException {
        ResultSet rs = Database.query("SELECT s.*, c.hex FROM Skin AS s INNER JOIN UserSkinColor AS usc ON s.id = usc.skin INNER JOIN User AS u ON usc.user = u.id INNER JOIN Color AS c ON usc.color = c.id WHERE u.id = ? AND s.id = ? LIMIT 1", new Object[]{userID, skinID});
        if (rs == null) throw new DBErrorException();
        if (!rs.next()) throw new DataErrorException(404, "Skin not found");

        Skin skin = new Skin(rs.getInt("id"), rs.getString("name"), rs.getString("hex"));
        skin.setTypes(SkinRepository.getTypesOfSkin(skinID));

        return skin;
    }

    public static Skin updateSkinColor(int userID, int skinID, int colorID) throws SQLException, DataErrorException, DBErrorException {
        Database.update("UPDATE UserSkinColor SET color = ? WHERE user = ? AND skin = ? LIMIT 1", new Object[]{colorID, userID, skinID});

        return getUserSkinByID(userID, skinID);
    }

    public static Collection<Skin> deleteUserSkin(int userID, int skinID) throws SQLException, DataErrorException, DBErrorException {
        Database.update("DELETE FROM UserSkinColor WHERE user = ? AND skin = ?", new Object[]{userID, skinID});

        return getSkinsFromUser(userID);
    }
}
