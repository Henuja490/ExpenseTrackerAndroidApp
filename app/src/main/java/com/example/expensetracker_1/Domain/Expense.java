package com.example.expensetracker_1.Domain;

public class Expense {
    private String id;
    private String title;
    private String amount;
    private String Date;
    // No-argument constructor (required by Firebase)


    public Expense() {
    }

    public Expense(String id, String title, String amount, String date) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        Date = date;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
