package database;

import model.Investment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBOperations {

    public void addInvestment(Investment inv) throws SQLException {
        String sql = "INSERT INTO investments (symbol, type, quantity, buy_price) VALUES (?,?,?,?)";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inv.getSymbol());
            stmt.setString(2, inv.getType());
            stmt.setDouble(3, inv.getQuantity());
            stmt.setDouble(4, inv.getBuyPrice());
            stmt.executeUpdate();
        }
    }

    public List<Investment> getAllInvestments() throws SQLException {
        List<Investment> list = new ArrayList<>();
        String sql = "SELECT * FROM investments";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Investment(
                        rs.getString("symbol"),
                        rs.getString("type"),
                        rs.getDouble("quantity"),
                        rs.getDouble("buy_price")
                ));
            }
        }
        return list;
    }
}
