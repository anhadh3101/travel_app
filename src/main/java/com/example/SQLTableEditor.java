package com.example;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SQLTableEditor extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private Connection conn;

    public SQLTableEditor() {
        setTitle("SQL Table Editor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);

        model = new DefaultTableModel();
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton updateButton = new JButton("Update DB");
        add(updateButton, BorderLayout.SOUTH);

        updateButton.addActionListener(e -> updateDatabase());

        connectAndLoadData();
    }

    private void connectAndLoadData() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/travel_planner", "admin", "mytravels");

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery("SELECT * FROM customer");

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            // Add column names
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(meta.getColumnName(i));
            }

            // Add rows
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void updateDatabase() {
        try {
            for (int i = 0; i < model.getRowCount(); i++) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE customer SET name=?, email=? WHERE customer_id=?");
                stmt.setString(1, model.getValueAt(i, 1).toString());
                stmt.setString(2, model.getValueAt(i, 2).toString());
                stmt.setInt(3, Integer.parseInt(model.getValueAt(i, 0).toString()));
                stmt.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Database updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Update failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SQLTableEditor().setVisible(true));
    }
}
