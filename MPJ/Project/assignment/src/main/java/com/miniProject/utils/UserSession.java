package com.miniProject.utils;

public class UserSession {
    private static int studentId;
    private static int teacherId;
    private static int classId; // New field
    private static String role;

    // Getters and setters
    public static int getStudentId() {
        return studentId;
    }

    public static void setStudentId(int studentId) {
        UserSession.studentId = studentId;
    }

    public static int getTeacherId() {
        return teacherId;
    }

    public static void setTeacherId(int teacherId) {
        UserSession.teacherId = teacherId;
    }

    public static int getClassId() {  // Add this method
        return classId;
    }

    public static void setClassId(int classId) {  // Add this method
        UserSession.classId = classId;
    }

    public static String getRole() {
        return role;
    }

    public static void setRole(String role) {
        UserSession.role = role;
    }

    public static void reset() {
        studentId = 0;
        teacherId = 0;
        classId = 0; // Reset class ID
        role = null;
    }
}
