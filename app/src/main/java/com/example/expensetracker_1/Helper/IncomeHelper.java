package com.example.expensetracker_1.Helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.expensetracker_1.Domain.Income;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IncomeHelper {

    private static FirebaseAuth mAuth;
    private static FirebaseUser currentUser;
    private static String userId;

    public interface IncomeFetchListener {
        void onFetchSuccess(List<Income> incomes); // Changed to List of Income

        void onFetchFailure(Exception e);
    }

    public static void fetchAllIncomes(IncomeFetchListener listener) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            listener.onFetchFailure(new Exception("User not authenticated."));
            return;
        }

        FirebaseDatabase.getInstance().getReference("income").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Income> incomes = new ArrayList<>(); // Corrected to store Income objects
                        for (DataSnapshot incomeSnapshot : snapshot.getChildren()) {
                            Income income = incomeSnapshot.getValue(Income.class);
                            if (income != null) {
                                income.setId(incomeSnapshot.getKey()); // Set ID from Firebase key
                                incomes.add(income);
                            }
                        }
                        listener.onFetchSuccess(incomes);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFetchFailure(error.toException());
                    }
                });
    }
}
