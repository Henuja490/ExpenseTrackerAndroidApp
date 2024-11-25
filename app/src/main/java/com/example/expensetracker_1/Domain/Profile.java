package com.example.expensetracker_1.Domain;

public class Profile {
    private String currency;
    private String id;
    private String phoneno;


    public Profile(String id, String username, String currency) {
        this.id = id;
        this.phoneno = username;
        this.currency = currency;
    }

    public Profile() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
