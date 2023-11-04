package app;

import app.model.Skin;
import app.model.Type;
import app.persistence.Database;
import app.utils.JSONReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        JSONObject json = JSONReader.readFromFile("skins.json");

        try {
            loadSkins(json.getJSONArray("skins"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        HashMap<Integer, Skin> skins = new HashMap<>();
        HashMap<Integer, Type> types = new HashMap<>();

        try {
            ResultSet rs = Database.query("SELECT * FROM Skin", null);
            if (rs == null) throw new Exception("Database error");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                float price = rs.getFloat("price");
                String defaultColor = rs.getString("default_color");

                skins.put(id, new Skin(id, name, price, defaultColor));
            }

            rs = Database.query("SELECT * FROM Type", null);
            if (rs == null) throw new Exception("Database error");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");

                types.put(id, new Type(id, name));
            }

            rs = Database.query("SELECT * FROM SkinType", null);
            if (rs == null) throw new Exception("Database error");

            while (rs.next()) {
                int skin = rs.getInt("skin");
                int type = rs.getInt("type");

                skins.get(skin).addType(types.get(type));
            }
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        SpringApplication.run(Main.class, args);
    }

    private static void loadSkins(JSONArray skins) throws SQLException {
        for (int i = 0; i < skins.length(); i++) {
            JSONObject obj = skins.getJSONObject(i);

            String color = obj.getString("color");

            ResultSet rs = Database.query("SELECT * FROM Color WHERE hex = ? LIMIT 1", new Object[]{color});
            if (rs == null) continue;

            if (!rs.next()) {
                Database.update("INSERT INTO Color(hex) VALUES(?)", new Object[]{color});
                System.out.println("Added color '" + color + "' to Database");
            }

            String name = obj.getString("name");
            float price = obj.getFloat("price");

            rs = Database.query("SELECT * FROM Skin WHERE name = ? LIMIT 1", new Object[]{name});
            if (rs == null) continue;

            if (!rs.next()) {
                Database.update("INSERT INTO Skin(name, price, default_color) VALUES(?, ?, (SELECT id FROM Color WHERE hex = ?))", new Object[]{name, price, color});
                System.out.println("Added Skin '" + name + "' to Database");
            }

            JSONArray types = obj.getJSONArray("types");
            for (int j = 0; j < types.length(); j++) {
                String type = types.getString(j);

                rs = Database.query("SELECT * FROM Type WHERE name = ?", new Object[]{type});
                if (rs == null) continue;

                if (!rs.next()) {
                    Database.update("INSERT INTO Type(name) VALUES(?)", new Object[]{type});
                    System.out.println("Added type '" + type + "' to Database");
                }

                rs = Database.query("SELECT * FROM Skin AS s INNER JOIN SkinType AS st ON s.id = st.skin INNER JOIN Type AS t ON t.id = st.type WHERE s.name = ? AND t.name = ? LIMIT 1", new Object[]{name, type});
                if (rs == null) continue;

                if (!rs.next()) {
                    Database.update("INSERT INTO SkinType(skin, type) VALUES((SELECT id FROM Skin WHERE name = ?), (SELECT id FROM Type WHERE name = ?))", new Object[]{name, type});
                }
            }
        }
    }
}