package app.persistence;

import app.model.Color;
import app.utils.DBErrorException;
import app.utils.DataErrorException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ColorRepository {
    public static Color getColor(String hexColor) throws SQLException, DBErrorException, DataErrorException {
        ResultSet rs = Database.query("SELECT * FROM Color WHERE hex = ?", new Object[]{hexColor});
        if (rs == null) throw new DBErrorException();
        if (!rs.next()) throw new DataErrorException(404, "Color not found");

        return new Color(rs.getInt("id"), rs.getString("hex"));
    }

    public static Color addColor(String hexColor) throws SQLException, DataErrorException, DBErrorException {
        Database.update("INSERT INTO Color (hex) VALUES (?)", new Object[]{hexColor});

        try {
            return getColor(hexColor);
        } catch (DataErrorException e) {
            e.printStackTrace();
            throw new DataErrorException(500, "Error adding the color");
        }
    }
}
