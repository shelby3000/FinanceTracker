package com.finance;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinanceManager {
    private static final String URL = "jdbc:mysql://localhost:3306/finance_tracker";
    private static final String USER = "finance_user"; // or "root"
    private static final String PASSWORD = "password123"; // your password
   // Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
    private Connection connect() {
        //Connection conn = null;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }

    public void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "amount DOUBLE," +
                "category VARCHAR(50)," +
                "date VARCHAR(10)," +
                "description VARCHAR(255))";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("DB init failed: " + e.getMessage());
        }
    }

    public void addTransaction(double amount, String category, String date, String description) {
        String sql = "INSERT INTO transactions(amount, category, date, description) VALUES(?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative");
            if (category.isEmpty()) throw new IllegalArgumentException("Category cannot be empty");
            pstmt.setDouble(1, amount);
            pstmt.setString(2, category);
            pstmt.setString(3, date);
            pstmt.setString(4, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Add failed: " + e.getMessage());
        }
    }

    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getString("date"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Fetch failed: " + e.getMessage());
        }
        return transactions;
    }

    public double getBalance() {
        double balance = 0;
        for (Transaction t : getTransactions()) {
            if (t.getCategory().equalsIgnoreCase("income")) {
                balance += t.getAmount();
            } else {
                balance -= t.getAmount();
            }
        }
        return balance;
    }

    public Map<String, Double> getCategorySummary() {
        Map<String, Double> summary = new HashMap<>();
        String sql = "SELECT category, SUM(amount) as total FROM transactions GROUP BY category";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                summary.put(rs.getString("category"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            System.out.println("Summary failed: " + e.getMessage());
        }
        return summary;
    }

    public void exportSummary() {
        try (java.io.FileWriter writer = new java.io.FileWriter("summary.txt")) {
            writer.write("Balance: $" + getBalance() + "\n");
            for (Map.Entry<String, Double> entry : getCategorySummary().entrySet()) {
                writer.write(entry.getKey() + ": $" + entry.getValue() + "\n");
            }
        } catch (java.io.IOException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }
}