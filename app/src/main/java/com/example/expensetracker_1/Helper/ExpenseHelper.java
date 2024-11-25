package com.example.expensetracker_1.Helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.expensetracker_1.Activity.BaseActivity;
import com.example.expensetracker_1.Domain.Expense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExpenseHelper  {
    static FirebaseAuth mAuth;
    public FirebaseDatabase database;
    public String TAG;
    public DatabaseReference expensesRef;
    public DatabaseReference incomeRef;

    public static FirebaseUser currentUser;
    public static String userId;
    public interface ExpenseFetchListener {
        void onFetchSuccess(List<Expense> expenses);

        void onFetchFailure(Exception e);
    }


    public static void fetchAllExpenses(ExpenseFetchListener listener) {
        mAuth = FirebaseAuth.getInstance();


        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }
        FirebaseDatabase.getInstance().getReference("expenses").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Expense> expenses = new ArrayList<>();
                        for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                            Expense expense = expenseSnapshot.getValue(Expense.class);
                            if (expense != null) {
                                expense.setId(expenseSnapshot.getKey()); // Set ID from Firebase key
                                expenses.add(expense);
                            }
                        }
                        listener.onFetchSuccess(expenses);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFetchFailure(error.toException());
                    }
                });
    }
}
