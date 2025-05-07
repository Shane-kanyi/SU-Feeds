package com.lastname.sufeeds;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class SuFeedsGUI extends JFrame {
    private DatabaseManager dbManager;
    private JTextField studentNameField, studentEmailField;
    private JTextField studentIdField, studentPasswordField;

    public SuFeedsGUI(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        setTitle("SuFeeds - Feedback System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Registration Panel
        JPanel registrationPanel = new JPanel(new BorderLayout());
        registrationPanel.add(initRegistrationPanel(), BorderLayout.CENTER);
        tabbedPane.add("Register", registrationPanel);

        // Login Panel
        JPanel loginPanel = new JPanel(new BorderLayout());
        loginPanel.add(initLoginPanel(), BorderLayout.CENTER);
        tabbedPane.add("Login", loginPanel);

        add(tabbedPane);
    }

    // Registration Panel
    private JPanel initRegistrationPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        studentIdField = new JTextField(10);
        studentNameField = new JTextField(10);
        studentPasswordField = new JTextField(10);
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> registerStudent());

        panel.add(new JLabel("Student ID:"));
        panel.add(studentIdField);
        panel.add(new JLabel("Name:"));
        panel.add(studentNameField);
        panel.add(new JLabel("Password:"));
        panel.add(studentPasswordField);
        panel.add(registerButton);

        return panel;
    }

    // Login Panel
    private JPanel initLoginPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JTextField loginStudentIdField = new JTextField(10);
        JTextField loginPasswordField = new JTextField(10);
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> loginStudent(loginStudentIdField.getText(), loginPasswordField.getText()));

        panel.add(new JLabel("Student ID:"));
        panel.add(loginStudentIdField);
        panel.add(new JLabel("Password:"));
        panel.add(loginPasswordField);
        panel.add(loginButton);

        return panel;
    }

    // Register Student
    private void registerStudent() {
        try {
            int studentId = Integer.parseInt(studentIdField.getText()); // Number format exception if input is not an integer
            String name = studentNameField.getText();
            String password = studentPasswordField.getText();

            if (dbManager.checkIfStudentExists(studentId)) {
                JOptionPane.showMessageDialog(this, "Student ID already exists! Please try a different ID.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Student student = new Student(studentId, name, password);
                dbManager.createStudent(student); // This can throw SQLException
                JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                switchToLoginPanel(); // Switch to the Login panel after successful registration
            }

            // Clear the fields after the operation
            studentIdField.setText("");
            studentNameField.setText("");
            studentPasswordField.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student ID must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Optional: Print the stack trace for debugging
        }
    }

    // Switch to Login Panel
    private void switchToLoginPanel() {
        JTabbedPane tabbedPane = (JTabbedPane) getContentPane().getComponent(0);
        tabbedPane.setSelectedIndex(1); // Switch to the Login tab
    }

    // Login Student
    private void loginStudent(String studentIdText, String password) {
        try {
            int studentId = Integer.parseInt(studentIdText); // Number format exception if input is not an integer
            Student student = dbManager.login(studentId, password);

            if (student != null) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                openMainApplicationPanel(student); // Open the main application after successful login
            } else {
                JOptionPane.showMessageDialog(this, "Invalid student ID or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student ID must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Login failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Optional: Print the stack trace for debugging
        }
    }

    // Open Main Application Panel (Student, Class, Topic, Feedback)
    private void openMainApplicationPanel(Student student) {
        PanelManager panelManager = new PanelManager(dbManager, student); // Updated to pass dbManager and student
        panelManager.setVisible(true); // Open the main application window
        dispose(); // Close the current frame
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseManager dbManager = new DatabaseManager();
            SuFeedsGUI gui = new SuFeedsGUI(dbManager);
            gui.setVisible(true);
        });
    }
}
