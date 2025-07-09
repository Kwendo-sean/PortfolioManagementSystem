package database;

import java.sql.*;

public class DBConnection {
    Connection con;

    public DBConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver loaded successfully");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Driver loading failed: " + cnfe.getMessage());
        }

        String url = "jdbc:postgresql://localhost:5433/portfolio_db";
        String user = "postgres";
        String password = "seanchan";

        try {
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connection established successfully");
        } catch (SQLException sqle) {
            System.out.println("Connection Failed: " + sqle.getMessage());
        }
    }

    public Connection getConnection() {
        return con;
    }
}
