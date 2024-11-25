package com.example.expensetracker_1.Domain;

public class Income {
    private String Id;
    private String title;
    private String amount;
    private String date;


    public Income() {
    }

    public Income(String id, String title, String amount, String date) {
        Id = id;
        this.title = title;
        this.amount = amount;
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return Id;
    }
}
