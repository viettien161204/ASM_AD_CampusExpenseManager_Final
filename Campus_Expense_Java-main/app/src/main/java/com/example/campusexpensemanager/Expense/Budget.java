package com.example.campusexpensemanager.Expense;

public class Budget {
    private int id;
    private float amount;
    private String email;
    private String studentID;

    public Budget(int id, float amount, String email, String studentID) {
        this.id = id;
        this.amount = amount;
        this.email = email;
        this.studentID = studentID;
    }

    public int getId() {
        return id;
    }

    public float getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }

    public String getStudentID() {
        return studentID;
    }
}