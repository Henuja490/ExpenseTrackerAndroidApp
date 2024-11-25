package com.example.expensetracker_1.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker_1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import retrofit2.Call;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    public FirebaseDatabase database;
    public String TAG;
    public DatabaseReference expensesRef;
    public DatabaseReference incomeRef;

    public DatabaseReference Profileref;

    public FirebaseUser currentUser;
    public String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();


        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            incomeRef = database.getReference("income").child(userId);
            expensesRef = database.getReference("expenses").child(userId);
            Profileref = database.getReference("profile").child(userId);
        }




        getWindow().setStatusBarColor(getResources().getColor(R.color.white));


    }


}