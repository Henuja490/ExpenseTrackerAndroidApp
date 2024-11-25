package com.example.expensetracker_1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensetracker_1.Adapter.ExpenseAdapter;
import com.example.expensetracker_1.Domain.Expense;
import com.example.expensetracker_1.Helper.ExpenseHelper;
import com.example.expensetracker_1.R;
import com.example.expensetracker_1.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference expensesRef;
    private FirebaseUser currentUser;
    private String userId;

    private List<Expense> expenseList;
    private ExpenseAdapter adapter;

    public double totalExpense = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase Initialization
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            expensesRef = database.getReference("expenses").child(userId);
        }

        // Initialize RecyclerView and Expense List
        expenseList = new ArrayList<>();
        adapter = new ExpenseAdapter(expenseList, expense -> {
            // Open expense detail page for editing
            Intent intent = new Intent(MainActivity.this, expense_detail.class);
            intent.putExtra("expense_id", expense.getId());
            intent.putExtra("expense_title", expense.getTitle());
            intent.putExtra("expense_amount", expense.getAmount());
            startActivityForResult(intent, 1);
        }, this);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        // Fetch existing expenses from Firebase
        fetchExpensesFromFirebase();

        // Set button listeners
        setVariable();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Update expense logic
            String updatedTitle = data.getStringExtra("updated_title");
            double updatedAmount = data.getDoubleExtra("updated_amount", 0.0);
            String expenseKey = data.getStringExtra("expense_key");

            if (expenseKey == null || updatedTitle == null) {
                Toast.makeText(this, "Invalid data received", Toast.LENGTH_SHORT).show();
                return;
            }
            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Month is 0-based, so add 1
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Format as a string
            String dateString = String.format("%04d-%02d-%02d", year, month, day);
            DatabaseReference expenseRef = expensesRef.child(expenseKey);
            Expense updatedExpense = new Expense(expenseKey, updatedTitle, String.valueOf(updatedAmount),dateString);

            expenseRef.setValue(updatedExpense)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "Expense updated successfully");
                        Toast.makeText(MainActivity.this, "Expense updated in database", Toast.LENGTH_SHORT).show();

                        // Update the local list and total
                        for (Expense expense : expenseList) {
                            if (expense.getId().equals(expenseKey)) {
                                totalExpense -= Double.parseDouble(expense.getAmount());
                                expense.setTitle(updatedTitle);
                                expense.setAmount(String.valueOf(updatedAmount));
                                expense.setDate(dateString);
                                totalExpense += updatedAmount;
                                break;
                            }
                        }

                        updateTotalExpenseUI();
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Error updating expense", e);
                        Toast.makeText(MainActivity.this, "Error updating expense", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void setVariable() {
        binding.addbtn.setOnClickListener(view -> {
            // Add new expense
            startActivityForResult(new Intent(MainActivity.this, add_expense.class), 2);
        });

        binding.imageView4.setOnClickListener(view -> {
            // Open user profile
            startActivity(new Intent(MainActivity.this, userprofile.class));
        });
    }

    private void fetchExpensesFromFirebase() {
        ExpenseHelper.fetchAllExpenses(new ExpenseHelper.ExpenseFetchListener() {
            @Override
            public void onFetchSuccess(List<Expense> expenses) {
                expenseList.clear();
                expenseList.addAll(expenses);

                // Calculate total expense
                totalExpense = 0.0;
                for (Expense expense : expenses) {
                    totalExpense += Double.parseDouble(expense.getAmount());
                }

                updateTotalExpenseUI();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFetchFailure(Exception e) {
                Log.e("MainActivity", "Error fetching expenses", e);
            }
        });
    }

    private void updateTotalExpenseUI() {
        // Update the total expense TextView
        binding.textView8.setText("Expense amount: Rs. " + totalExpense);
    }




}
