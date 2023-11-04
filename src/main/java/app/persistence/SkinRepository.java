package app.persistence;

import app.model.Skin;
import app.model.Type;
import app.utils.DBErrorException;
import app.utils.DataErrorException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SkinRepository {
    public static Collection<Skin> getSkins() throws SQLException, DBErrorException, DataErrorException {
        ResultSet rs = Database.query("SELECT s.*, c.hex FROM Skin AS s INNER JOIN Color AS c ON s.default_color = c.id", null);
        if (rs == null) throw new DBErrorException();
        if (!rs.next()) throw new DataErrorException(404);
        rs.previous();

        ArrayList<Skin> skins = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");

            skins.add(new Skin(id, rs.getString("name"), rs.getFloat("price"), rs.getString("hex")));
        }

        HashMap<Integer, ArrayList<Type>> skinTypes = getAllSkinTypes();
        for (Skin i: skins) {
            i.setTypes(skinTypes.get(i.getId()));
        }

        return skins;

    }

    public static Skin getSkinByID(int id) throws SQLException, DBErrorException, DataErrorException {
        ResultSet rs = Database.query("SELECT * FROM Skin WHERE id = ?", new Object[]{id});
        if (rs == null) throw new DBErrorException();
        if (!rs.next()) throw new DataErrorException(404);

        Skin skin = new Skin(rs.getInt("id"), rs.getString("name"), rs.getFloat("price"), rs.getString("default_color"));

        rs = Database.query("SELECT t.* FROM SkinType AS st INNER JOIN Type AS t ON st.type = t.id WHERE st.skin = ?", new Object[]{id});
        if (rs == null) throw new DBErrorException();
        if (!rs.next()) throw new DataErrorException(404);
        rs.previous();

        ArrayList<Type> types = new ArrayList<>();
        while (rs.next()) {
            types.add(new Type(rs.getInt("id"), rs.getString("name")));
        }

        skin.setTypes(types);

        return skin;
    }

    public static ArrayList<Type> getTypesOfSkin(int id) throws SQLException, DBErrorException, DataErrorException {
        ResultSet typeRs = Database.query("SELECT t.* FROM SkinType AS st INNER JOIN Type AS t ON st.type = t.id WHERE st.skin = ?", new Object[]{id});
        if (typeRs == null) throw new DBErrorException();
        if (!typeRs.next()) throw new DataErrorException(404);
        typeRs.previous();

        ArrayList<Type> types = new ArrayList<>();
        while (typeRs.next()) {
            types.add(new Type(typeRs.getInt("id"), typeRs.getString("name")));
        }

        return types;
    }

    public static HashMap<Integer, ArrayList<Type>> getAllSkinTypes() throws SQLException, DBErrorException, DataErrorException {
        ResultSet rs = Database.query("SELECT st.skin, t.* FROM SkinType AS st INNER JOIN Type AS t ON st.type = t.id", null);
        if (rs == null) throw new DBErrorException();
        if (!rs.next()) throw new DataErrorException(404);
        rs.previous();

        HashMap<Integer, ArrayList<Type>> skinTypes = new HashMap<>();
        while (rs.next()) {
            int skinID = rs.getInt("skin");

            if (!skinTypes.containsKey(skinID)) skinTypes.put(skinID, new ArrayList<>());

            skinTypes.get(skinID).add(new Type(rs.getInt("id"), rs.getString("name")));
        }

        return skinTypes;
    }
}
