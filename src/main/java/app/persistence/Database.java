package app.persistence;

import java.sql.*;

public class Database {
    private final String HOST = "jdbc:mysql://localhost:3306/";
    private final String USER = "root";
    private final String PASSWORD = "1234";
    private final String DB = "j2d_videogame";
    private static Connection connection;

    private Database() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(HOST + DB, USER, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() {
        if (connection == null) new Database();
        return connection;
    }

    private static PreparedStatement preparedStatement(String sql, Object[] values) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        if (values == null) return ps;

        int index = 1;
        for (Object i: values) {
            if (i instanceof String) ps.setString(index++, (String) i);
            else if (i instanceof Integer) ps.setInt(index++, (Integer) i);
            else if (i instanceof Float) ps.setFloat(index++, (Float) i);
        }

        return ps;
    }

    public static ResultSet query(String sql, Object[] values) {
        try {
            return preparedStatement(sql, values).executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void update(String sql, Object[] values) {
        try {
            preparedStatement(sql, values).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
