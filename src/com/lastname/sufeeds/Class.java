package com.lastname.sufeeds;

public class Class {
    private String classId;  // Change to String for alphanumeric class IDs
    private int studentId;
    private String name;
    private String semester;

    public Class(String classId, int studentId, String name, String semester) {
        this.classId = classId;
        this.studentId = studentId;
        this.name = name;
        this.semester = semester;
    }

    public String getClassId() {
        return classId;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getSemester() {
        return semester;
    }
}

