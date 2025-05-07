package com.lastname.sufeeds;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public class DatabaseManager {
    private final String url = "jdbc:postgresql://localhost:5432/db_first_last_studentNumber"; // Update with your database details
    private final String user = "postgres"; // Update with your username
    private final String password = "mylani"; // Update with your password

    public DatabaseManager() {
        try {
            java.lang.Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found.");
            e.printStackTrace();
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void createStudent(Student student) throws SQLException {
        String sql = "INSERT INTO tbl_students(id, name, password) VALUES(?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, student.getId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error during student registration: " + e.getMessage());
            throw e;
        }
    }

    public boolean checkIfStudentExists(int studentId) {
        String sql = "SELECT 1 FROM tbl_students WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Student login(int studentId, String password) throws SQLException {
        String sql = "SELECT id, name, password FROM tbl_students WHERE id = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(rs.getInt("id"), rs.getString("name"), rs.getString("password"));
                }
            }
        }
        return null;
    }

    public void addClassForStudent(int studentId, String classId, String className, String semester) throws SQLException {
        String sql = "INSERT INTO tbl_classes(student_id, class_id, class_name, semester) VALUES(?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setString(2, classId);
            stmt.setString(3, className);
            stmt.setString(4, semester);
            stmt.executeUpdate();
        }
    }

    public DefaultTableModel getClassesTable(int studentId) throws SQLException {
        String sql = "SELECT class_id, class_name, semester FROM tbl_classes WHERE student_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Class ID");
            model.addColumn("Class Name");
            model.addColumn("Semester");

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("class_id"),
                        rs.getString("class_name"),
                        rs.getString("semester")
                });
            }
            return model;
        }
    }

    public Map<String, String> getClassIdsAndNames(int studentId) throws SQLException {
        String sql = "SELECT class_id, class_name FROM tbl_classes WHERE student_id = ?";
        Map<String, String> classes = new HashMap<>();
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    classes.put(rs.getString("class_id"), rs.getString("class_name"));
                }
            }
        }
        return classes;
    }

    public void addTopicForStudent(int studentId, String classId, String topicName, int week) throws SQLException {
        String sql = "INSERT INTO tbl_topics(student_id, class_id, topic_name, week) VALUES(?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setString(2, classId);
            stmt.setString(3, topicName);
            stmt.setInt(4, week);
            stmt.executeUpdate();
        }
    }

    public DefaultTableModel getTopicsTable(int studentId) throws SQLException {
        String sql = "SELECT t.topic_name, t.week, c.class_name, t.id AS topic_id " +
                "FROM tbl_topics t " +
                "JOIN tbl_classes c ON t.class_id = c.class_id " +
                "WHERE t.student_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Topic Name");
            model.addColumn("Week");
            model.addColumn("Class Name");
            model.addColumn("Topic ID");

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("topic_name"),
                        rs.getInt("week"),
                        rs.getString("class_name"),
                        rs.getInt("topic_id")
                });
            }
            return model;
        }
    }

    // This method returns all topics associated with a student
    public Map<Integer, String> getTopicsForStudent(int studentId) throws SQLException {
        String sql = "SELECT id, topic_name FROM tbl_topics WHERE student_id = ?";
        Map<Integer, String> topics = new HashMap<>();
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    topics.put(rs.getInt("id"), rs.getString("topic_name"));
                }
            }
        }
        return topics;
    }

    // This method gets the topic ID by name for a student
    public int getTopicIdByName(int studentId, String topicName) throws SQLException {
        String sql = "SELECT id FROM tbl_topics WHERE student_id = ? AND topic_name = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setString(2, topicName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1; // Return -1 if the topic is not found
    }

    public void submitFeedbackForStudent(int studentId, int topicId, String feedback) throws SQLException {
        String sql = "INSERT INTO tbl_feedback(student_id, topic_id, feedback_content) VALUES(?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, topicId);
            stmt.setString(3, feedback);
            stmt.executeUpdate();
        }
    }

    public DefaultTableModel getFeedbackTable() throws SQLException {
        String sql = "SELECT s.name AS student_name, t.topic_name, f.feedback_content " +
                "FROM tbl_feedback f " +
                "JOIN tbl_students s ON f.student_id = s.id " +
                "JOIN tbl_topics t ON f.topic_id = t.id";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Student Name");
            model.addColumn("Topic Name");
            model.addColumn("Feedback");

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("student_name"),
                        rs.getString("topic_name"),
                        rs.getString("feedback_content")
                });
            }
            return model;
        }
    }
}
