package com.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Arrays;

public class SQLTableEditor extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private Connection conn;
    private JPanel buttonPanel;
    private final String[] tableNames = {"customer", "flight", "hotel", "booking"}; // add your actual table names here

    public SQLTableEditor() {
        setTitle("Travel App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 500);
        setLayout(new BorderLayout());

        // Button panel for switching tables
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (String tableName : tableNames) {
            JButton btn = new JButton(tableName);
            btn.addActionListener(e -> loadTable(tableName));
            buttonPanel.add(btn);
        }
        add(buttonPanel, BorderLayout.NORTH);

        // Table display
        model = new DefaultTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Update button
        JButton updateBtn = new JButton("Update DB");
        updateBtn.addActionListener(e -> updateDatabase());
        controlPanel.add(updateBtn);

        //Add Button
        JButton addBtn = new JButton("Add Row");
        addBtn.addActionListener(e -> insertNewRow());
        controlPanel.add(addBtn);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteSelectedRow());
        controlPanel.add(deleteBtn);

        add(controlPanel, BorderLayout.SOUTH);


        connectAndLoadInitialTable();
    }

    private void connectAndLoadInitialTable() {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_planner", "admin", "mytravels");
            loadTable(tableNames[0]); // load first table initially
        } catch (SQLException e) {
            showError("Connection failed: " + e.getMessage());
        }
    }

    private void loadTable(String tableName) {
        model.setRowCount(0);
        model.setColumnCount(0);

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData meta = rs.getMetaData();

            // Add columns
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                model.addColumn(meta.getColumnName(i));
            }

            // Add rows
            while (rs.next()) {
                Object[] row = new Object[meta.getColumnCount()];
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            table.setModel(model);
        } catch (SQLException e) {
            showError("Failed to load table '" + tableName + "': " + e.getMessage());
        }
    }

    private void updateDatabase() {
        int colCount = model.getColumnCount();
        if (colCount < 1) return;

        try {
            String tableName = getSelectedTableName();
            String idColumn = model.getColumnName(0); // Assumes first column is ID

            for (int i = 0; i < model.getRowCount(); i++) {
                StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
                for (int j = 1; j < colCount; j++) {
                    sql.append(model.getColumnName(j)).append("=?, ");
                }
                sql.setLength(sql.length() - 2); // Remove trailing comma
                sql.append(" WHERE ").append(idColumn).append("=?");

                PreparedStatement stmt = conn.prepareStatement(sql.toString());
                for (int j = 1; j < colCount; j++) {
                    stmt.setObject(j, model.getValueAt(i, j));
                }
                stmt.setObject(colCount, model.getValueAt(i, 0)); // where id=...
                stmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Database updated.");
        } catch (SQLException e) {
            showError("Update failed: " + e.getMessage());
        }
    }

    private String getSelectedTableName() {
        for (Component c : buttonPanel.getComponents()) {
            if (c instanceof JButton btn && btn.hasFocus()) {
                return btn.getText();
            }
        }
        return tableNames[0]; // fallback
    }

    private void deleteSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No row selected to delete.");
            return;
        }

        try {
            String tableName = getSelectedTableName();
            String idColumn = model.getColumnName(0);
            Object idValue = model.getValueAt(selectedRow, 0);

            if (idValue == null) {
                model.removeRow(selectedRow);
                return;
            }

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM " + tableName + " WHERE " + idColumn + "=?");
            stmt.setObject(1, idValue);
            stmt.executeUpdate();

            model.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Row deleted.");
        } catch (SQLException e) {
            showError("Delete failed: " + e.getMessage());
        }
    }

    private void insertNewRow() {

    }
    
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SQLTableEditor().setVisible(true));
    }
}
