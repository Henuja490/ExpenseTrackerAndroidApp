package com.example.expensetracker_1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.expensetracker_1.APIServices.CurrencyApiService;
import com.example.expensetracker_1.Domain.CurrencyResponse;
import com.example.expensetracker_1.Domain.Expense;
import com.example.expensetracker_1.Domain.Income;
import com.example.expensetracker_1.R;
import com.example.expensetracker_1.databinding.ActivityAddExpenseBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class add_expense extends BaseActivity {
    private static final String TAG = "add_expense";

    private ActivityAddExpenseBinding binding;
    private EditText titleEditText, amountEditText;
    private String selectedCurrency;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        titleEditText = binding.amountEditText3;
        amountEditText = binding.amountEditText2;

        Spinner currencySpinner = findViewById(R.id.currencySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.currency_array,
                android.R.layout.simple_spinner_item
        );
        binding.currencyConvertButton.setOnClickListener(view -> fetchUserCurrencyAndConvert(userId, selectedCurrency, Double.parseDouble(amountEditText.getText().toString())));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCurrency = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.addIncomeButton.setOnClickListener(view -> handleAddIncome());
        binding.addExpenseButton.setOnClickListener(view -> handleAddExpense());
    }





    private void handleAddExpense() {
        String title = titleEditText.getText().toString();
        String amount = amountEditText.getText().toString();

        if (title.isEmpty() || amount.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is 0-based, so add 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String dateString = String.format("%04d-%02d-%02d", year, month, day);

        DatabaseReference incomeRef = FirebaseDatabase.getInstance()
                .getReference("expenses")
                .child(userId);
        String key = incomeRef.push().getKey();
        if (key != null) {
            Expense expenses = new Expense(key, title, amount,dateString);
            incomeRef.child(key).setValue(expenses)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Expense added successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(add_expense.this, MainActivity.class));
                        resetFieldsAndNavigate();
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding income", e));
        }
    }

    private void handleAddIncome() {
        String title = titleEditText.getText().toString();
        String amount = amountEditText.getText().toString();

        if (title.isEmpty() || amount.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is 0-based, so add 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String dateString = String.format("%04d-%02d-%02d", year, month, day);

        DatabaseReference incomeRef = FirebaseDatabase.getInstance()
                .getReference("income")
                .child(userId);
        String key = incomeRef.push().getKey();
        if (key != null) {
            Income income = new Income(key, title, amount,dateString);
            incomeRef.child(key).setValue(income)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Income added successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(add_expense.this, MainActivity.class));
                        resetFieldsAndNavigate();
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding income", e));
        }
    }


    private void resetFieldsAndNavigate() {
        titleEditText.setText("");
        amountEditText.setText("");
        startActivity(new Intent(add_expense.this, MainActivity.class));
    }
    private void fetchUserCurrencyAndConvert(String userId, String targetCurrency, double amount) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("profile").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String baseCurrency = snapshot.child("currency").getValue(String.class);

                    if (baseCurrency != null) {
                        convertCurrency(targetCurrency, baseCurrency, amount);
                    } else {
                        amountEditText.setText("Error: User currency not found.");
                    }
                } else {
                    amountEditText.setText("Error: User not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                amountEditText.setText("Error: Failed to fetch user data.");
            }
        });
    }


    public void convertCurrency(String baseCurrency, String targetCurrency, double amount) {
        String apiUrl = "https://apilayer.net/api/live?access_key=d826f906089e5d1cb737e658fc5ad88b&currencies="
                + targetCurrency + "&source=" + baseCurrency + "&format=1";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apilayer.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CurrencyApiService apiService = retrofit.create(CurrencyApiService.class);


        apiService.getExchangeRates(apiUrl).enqueue(new Callback<CurrencyResponse>() {
            @Override
            public void onResponse(Call<CurrencyResponse> call, Response<CurrencyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CurrencyResponse currencyResponse = response.body();

                    Log.d("CurrencyConversion", "API Response: " + response.body().toString());

                    String rateKey = baseCurrency + targetCurrency;  // e.g., "USDLKR"
                    Double rate = currencyResponse.getQuotes().get(rateKey);

                    if (rate != null) {
                        double convertedAmount = amount * rate;
                        amountEditText.setText(String.format("%.2f", convertedAmount));
                    } else {
                        amountEditText.setText("Error: Rate not found.");
                    }
                } else {
                    Log.e("CurrencyConversion", "Error: " + response.code());
                    amountEditText.setText("Error: Failed to fetch data.");
                }
            }


            @Override
            public void onFailure(Call<CurrencyResponse> call, Throwable t) {
                Log.e("CurrencyConversion", "Error: " + t.getMessage());
                amountEditText.setText("Error: Failed to fetch data.");
            }
        });
    }

}
