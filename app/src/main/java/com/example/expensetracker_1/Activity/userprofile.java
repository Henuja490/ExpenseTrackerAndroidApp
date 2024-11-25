package com.example.expensetracker_1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensetracker_1.APIServices.InfobipSMS;
import com.example.expensetracker_1.Adapter.IncomeAdapter;
import com.example.expensetracker_1.Domain.Expense;
import com.example.expensetracker_1.Domain.Income;
import com.example.expensetracker_1.Domain.Profile;
import com.example.expensetracker_1.Helper.IncomeHelper;
import com.example.expensetracker_1.databinding.ActivityUserprofileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class userprofile extends BaseActivity {
    private List<Income> IncomeList;
    private IncomeAdapter adapter;
    private List<Expense> expenseList;
    public double totalIncome=0.0;
    public double totalExpense=0.0;

    public Double Balance = 0.0;

    public TextView phoneno;
ActivityUserprofileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityUserprofileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        IncomeList = new ArrayList<>();
        adapter = new IncomeAdapter(IncomeList, expense -> {
            // Open expense detail page for editing
            Intent intent = new Intent(userprofile.this, income_details.class);
            intent.putExtra("expense_id", expense.getId());
            intent.putExtra("expense_title", expense.getTitle());
            intent.putExtra("expense_amount", expense.getAmount());
            startActivityForResult(intent, 1);
        }, this);
        phoneno = binding.amountEditText7;
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView2.setAdapter(adapter);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("profile");
        String userId = currentUser.getUid();

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("phoneno").getValue(String.class);

                    String C = snapshot.child("currency").getValue(String.class);

                    binding.amountEditText7.setText(name);
                    binding.amountEditText.setText(C);
                } else {
                    Log.e("Profile", "User profile not found!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching profile", error.toException());
            }
        });


        fetchIncomeData();
        binding.imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();


                Toast.makeText(userprofile.this, "You have logged out", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(userprofile.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.addIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.amountEditText7.getText().toString();
                String curr = binding.amountEditText.getText().toString();
                updateProfile(name,curr);
            }
        });


    }




    private void updateProfile(String name, String curr) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userExpensesRef   =database.getReference("profile").child(userId);

        userExpensesRef.push();
        Profile expense = new Profile (userId,name, curr);

        userExpensesRef.setValue(expense)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Income added successfully"))
                .addOnFailureListener(e -> Log.e("Firebase", "Error adding expense", e));
        startActivity(new Intent(userprofile.this, userprofile.class));

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
            DatabaseReference expenseRef = incomeRef.child(expenseKey);
            Income updatedExpense = new Income(expenseKey, updatedTitle, String.valueOf(updatedAmount),dateString);

            expenseRef.setValue(updatedExpense)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "Income updated successfully");
                        Toast.makeText(userprofile.this, "Income updated in database", Toast.LENGTH_SHORT).show();

                        // Update the local list and total
                        for (Income expense : IncomeList) {
                            if (expense.getId().equals(expenseKey)) {
                                totalExpense -= Double.parseDouble(expense.getAmount());
                                expense.setTitle(updatedTitle);
                                expense.setAmount(String.valueOf(updatedAmount));
                                expense.setDate(dateString);
                                totalExpense += updatedAmount;
                                break;
                            }
                        }

                        calculateBalance();
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Error updating expense", e);
                        Toast.makeText(userprofile.this, "Error updating expense", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void fetchIncomeData() {
        IncomeHelper.fetchAllIncomes(new IncomeHelper.IncomeFetchListener() {
            @Override
            public void onFetchSuccess(List<Income> incomes) {
                IncomeList.clear();
                IncomeList.addAll(incomes);
                totalIncome = 0.0;
                for (Income expense : IncomeList) {
                    totalIncome += Double.parseDouble(expense.getAmount());
                }

                calculateBalance();
                adapter.notifyDataSetChanged(); // Refresh the adapter
            }

            @Override
            public void onFetchFailure(Exception e) {
                Log.e("MainActivity", "Error fetching incomes", e);
                Toast.makeText(userprofile.this, "Failed to load incomes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBalanceExpenseUI(Double Ba) {
        // Update the total expense TextView

        binding.textView8.setText("Balance amount: Rs. " + Ba);
    }

    public void calculateBalance() {
        DatabaseReference incomeRef = FirebaseDatabase.getInstance().getReference("income").child(userId);
        DatabaseReference expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(userId);

        final double[] totalIncome = {0.0};
        final double[] totalExpenses = {0.0};

        // Fetch Total Income
        incomeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalIncome[0] = 0.0;
                for (DataSnapshot incomeSnapshot : snapshot.getChildren()) {
                    Income income = incomeSnapshot.getValue(Income.class);
                    if (income != null) {
                        totalIncome[0] += Double.parseDouble(income.getAmount());
                    }
                }

                // After fetching income, fetch expenses
                fetchExpenses(totalIncome[0]);
            }

            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching income", error.toException());
            }
        });
    }

    private void fetchExpenses(double incomeTotal) {
        expensesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalExpenses = 0.0;
                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);
                    if (expense != null) {
                        totalExpenses += Double.parseDouble(expense.getAmount());
                    }
                }

                // Calculate Balance
                double balance = incomeTotal - totalExpenses;

                if(balance<=incomeTotal*15/100 && balance>incomeTotal*10/100){
                    InfobipSMS smsSender = new InfobipSMS();
                    String apiKey = "518c192a8fb9c057a07eac016362b4c7-6507e48f-80ad-46b8-b43b-7c3796ca10ac";  // Replace with your API key
                    String senderId = "447860064836";  // Replace with your sender ID
                    String recipientPhone = phoneno.getText().toString();  // Replace with the recipient's phone number
                    String message = "Dear customer,Your expenses has reached to 15% of your income.Please check the app. ";
                    // Example: Sending SMS

                        smsSender.sendSMS(apiKey, senderId, recipientPhone, message);

                } else if (balance<incomeTotal*10/100 && balance>incomeTotal*5/100) {
                    InfobipSMS smsSender = new InfobipSMS();
                    String apiKey = "518c192a8fb9c057a07eac016362b4c7-6507e48f-80ad-46b8-b43b-7c3796ca10ac";  // Replace with your API key
                    String senderId = "447860064836";  // Replace with your sender ID
                    String recipientPhone = phoneno.getText().toString();  // Replace with the recipient's phone number
                    String message = "Dear customer,Your expenses has reached to 10% of your income.Please check the app. ";
                    // Example: Sending SMS
                    smsSender.sendSMS(apiKey, senderId, recipientPhone, message);
                } else if (balance<incomeTotal*5/100) {
                    InfobipSMS smsSender = new InfobipSMS();
                    String apiKey = "518c192a8fb9c057a07eac016362b4c7-6507e48f-80ad-46b8-b43b-7c3796ca10ac";  // Replace with your API key
                    String senderId = "447860064836";  // Replace with your sender ID
                    String recipientPhone = phoneno.getText().toString();  // Replace with the recipient's phone number
                    String message = "Dear customer, Your expenses has reached to 5% of your income.Please check the app. ";
                    // Example: Sending SMS
                    smsSender.sendSMS(apiKey, senderId, recipientPhone, message);
                }

                // Update the UI
                updateBalanceExpenseUI(balance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching expenses", error.toException());
            }
        });
    }



}