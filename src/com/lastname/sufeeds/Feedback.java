package com.lastname.sufeeds;

public class Feedback {
    private int id;
    private int studentId;
    private int topicId;
    private String feedback;

    public Feedback(int id, int studentId, int topicId, String feedback) {
        this.id = id;
        this.studentId = studentId;
        this.topicId = topicId;
        this.feedback = feedback;
    }

    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getTopicId() {
        return topicId;
    }

    public String getFeedback() {
        return feedback;
    }
}
