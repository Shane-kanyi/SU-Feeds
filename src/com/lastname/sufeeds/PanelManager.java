package com.lastname.sufeeds;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public class PanelManager extends JFrame {
    private DatabaseManager dbManager;
    private Student student;

    private JTextField classIdField, classNameField, semesterField, topicNameField, topicWeekField, feedbackTextField;
    private JTable classTable, topicTable, feedbackTable;
    private JScrollPane classScrollPane, topicScrollPane, feedbackScrollPane;
    private JComboBox<String> classDropdown, topicDropdown; // Dropdown for Class Names and Topics

    public PanelManager(DatabaseManager dbManager, Student student) {
        this.dbManager = dbManager;
        this.student = student;

        setTitle("SuFeeds - Panel Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Show student info (ID, Name)
        JPanel studentInfoPanel = new JPanel(new FlowLayout());
        studentInfoPanel.add(new JLabel("Student ID: " + student.getId()));
        studentInfoPanel.add(new JLabel("Student Name: " + student.getName()));
        add(studentInfoPanel, BorderLayout.NORTH);

        // Tabbed pane for Classes, Topics, and Feedback
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Classes", initClassPanel());
        tabbedPane.addTab("Topics", initTopicPanel());
        tabbedPane.addTab("Feedback", initFeedbackPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // Initialize Classes Panel
    private JPanel initClassPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new FlowLayout());

        classIdField = new JTextField(10);
        classNameField = new JTextField(10);
        semesterField = new JTextField(5);
        JButton addClassButton = new JButton("Add Class");
        addClassButton.addActionListener(e -> addClass());

        formPanel.add(new JLabel("Class ID:"));
        formPanel.add(classIdField);
        formPanel.add(new JLabel("Class Name:"));
        formPanel.add(classNameField);
        formPanel.add(new JLabel("Semester:"));
        formPanel.add(semesterField);
        formPanel.add(addClassButton);

        panel.add(formPanel, BorderLayout.NORTH);

        // Display existing classes
        classTable = new JTable();
        classScrollPane = new JScrollPane(classTable);
        panel.add(classScrollPane, BorderLayout.CENTER);
        refreshClassesTable();

        return panel;
    }

    // Add Class to the database
    private void addClass() {
        String classId = classIdField.getText();
        String className = classNameField.getText();
        String semester = semesterField.getText();

        if (classId.isEmpty() || className.isEmpty() || semester.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Class ID, Name, and Semester cannot be empty!");
            return;
        }

        try {
            dbManager.addClassForStudent(student.getId(), classId, className, semester);
            JOptionPane.showMessageDialog(this, "Class added successfully!");
            clearClassFields();
            refreshClassesTable();

            // Update class dropdowns in Topics and Feedback panels
            populateClassDropdown();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Clear Class fields after adding
    private void clearClassFields() {
        classIdField.setText("");
        classNameField.setText("");
        semesterField.setText("");
    }

    // Refresh Classes Table after adding a class
    private void refreshClassesTable() {
        try {
            DefaultTableModel model = dbManager.getClassesTable(student.getId());
            classTable.setModel(model);
            classScrollPane.revalidate();
            classScrollPane.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Initialize Topics Panel
    private JPanel initTopicPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new FlowLayout());

        classDropdown = new JComboBox<>();
        populateClassDropdown(); // Populate dropdown with class names

        topicNameField = new JTextField(10);
        topicWeekField = new JTextField(3);
        JButton addTopicButton = new JButton("Add Topic");
        addTopicButton.addActionListener(e -> addTopic());

        formPanel.add(new JLabel("Class Name:"));
        formPanel.add(classDropdown);
        formPanel.add(new JLabel("Topic Name:"));
        formPanel.add(topicNameField);
        formPanel.add(new JLabel("Week:"));
        formPanel.add(topicWeekField);
        formPanel.add(addTopicButton);

        panel.add(formPanel, BorderLayout.NORTH);

        // Display existing topics
        topicTable = new JTable();
        topicScrollPane = new JScrollPane(topicTable);
        panel.add(topicScrollPane, BorderLayout.CENTER);
        refreshTopicsTable();

        return panel;
    }

    // Populate Class dropdown with class names
    private void populateClassDropdown() {
        try {
            Map<String, String> classes = dbManager.getClassIdsAndNames(student.getId());
            classDropdown.removeAllItems();
            classes.forEach((classId, className) -> classDropdown.addItem(className));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add Topic to the database
    private void addTopic() {
        String className = (String) classDropdown.getSelectedItem();
        String topicName = topicNameField.getText();
        String weekStr = topicWeekField.getText();
        if (topicName.isEmpty() || weekStr.isEmpty() || className == null) {
            JOptionPane.showMessageDialog(this, "Class Name, Topic Name, and Week cannot be empty!");
            return;
        }
        try {
            int week = Integer.parseInt(weekStr);

            // Retrieve classId from the class name
            Map<String, String> classIdsAndNames = dbManager.getClassIdsAndNames(student.getId());
            String classId = null;
            for (Map.Entry<String, String> entry : classIdsAndNames.entrySet()) {
                if (entry.getValue().equals(className)) {
                    classId = entry.getKey();
                    break;
                }
            }

            if (classId == null) {
                JOptionPane.showMessageDialog(this, "Class not found!");
                return;
            }

            dbManager.addTopicForStudent(student.getId(), classId, topicName, week);
            JOptionPane.showMessageDialog(this, "Topic added successfully!");
            clearTopicFields();
            refreshTopicsTable();

            // Update topic dropdown in Feedback panel
            populateTopicDropdown();
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Invalid week number.");
            e.printStackTrace();
        }
    }

    // Clear Topic fields after adding
    private void clearTopicFields() {
        topicNameField.setText("");
        topicWeekField.setText("");
    }

    // Refresh Topics Table after adding a topic
    private void refreshTopicsTable() {
        try {
            DefaultTableModel model = dbManager.getTopicsTable(student.getId());
            topicTable.setModel(model);
            topicScrollPane.revalidate();
            topicScrollPane.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Initialize Feedback Panel
    private JPanel initFeedbackPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new FlowLayout());

        // Topic dropdown for selecting the topic
        topicDropdown = new JComboBox<>();
        populateTopicDropdown(); // Populate dropdown with topics

        feedbackTextField = new JTextField(20);
        JButton submitFeedbackButton = new JButton("Submit Feedback");
        submitFeedbackButton.addActionListener(e -> submitFeedback());

        formPanel.add(new JLabel("Select Topic:"));
        formPanel.add(topicDropdown);
        formPanel.add(new JLabel("Feedback:"));
        formPanel.add(feedbackTextField);
        formPanel.add(submitFeedbackButton);

        panel.add(formPanel, BorderLayout.NORTH);

        // Display feedback table
        feedbackTable = new JTable();
        feedbackScrollPane = new JScrollPane(feedbackTable);
        panel.add(feedbackScrollPane, BorderLayout.CENTER);
        refreshFeedbackTable();

        return panel;
    }

    // Populate Topic dropdown with topics for the selected class
    private void populateTopicDropdown() {
        try {
            Map<Integer, String> topics = dbManager.getTopicsForStudent(student.getId());
            topicDropdown.removeAllItems();
            topics.forEach((topicId, topicName) -> topicDropdown.addItem(topicName));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Submit Feedback to the database
    private void submitFeedback() {
        String feedback = feedbackTextField.getText();
        if (feedback.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Feedback cannot be empty!");
            return;
        }

        try {
            // Get selected topic from the dropdown
            String selectedTopic = (String) topicDropdown.getSelectedItem();

            // Fetch the topic ID based on the selected topic name
            int topicId = dbManager.getTopicIdByName(student.getId(), selectedTopic);
            if (topicId == -1) {
                JOptionPane.showMessageDialog(this, "Topic not found!");
                return;
            }

            dbManager.submitFeedbackForStudent(student.getId(), topicId, feedback);
            JOptionPane.showMessageDialog(this, "Feedback submitted successfully!");
            clearFeedbackField();
            refreshFeedbackTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Clear Feedback field after submitting
    private void clearFeedbackField() {
        feedbackTextField.setText("");
    }

    // Refresh Feedback Table to show all feedbacks from all students
    private void refreshFeedbackTable() {
        try {
            DefaultTableModel model = dbManager.getFeedbackTable(); // Fetch all feedbacks
            feedbackTable.setModel(model);
            feedbackScrollPane.revalidate();
            feedbackScrollPane.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
