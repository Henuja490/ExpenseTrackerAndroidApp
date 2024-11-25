package com.example.expensetracker_1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker_1.R;
import com.example.expensetracker_1.databinding.ActivityExpenseDetailBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class expense_detail extends BaseActivity {
    ActivityExpenseDetailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityExpenseDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent intent = getIntent();
        String expenseTitle = intent.getStringExtra("expense_title");
        String expenseAmount = intent.getStringExtra("expense_amount");

        TextView titleTextView = findViewById(R.id.amountEditText3);
        EditText amountEditText = findViewById(R.id.amountEditText2);

        titleTextView.setText(expenseTitle);
        amountEditText.setText(expenseAmount);

        binding.addIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedTitle = titleTextView.getText().toString();  // Ensure this is a string
                double updatedAmount = Double.parseDouble(amountEditText.getText().toString());


                Intent resultIntent = new Intent();
                resultIntent.putExtra("updated_title", updatedTitle);
                resultIntent.putExtra("updated_amount", updatedAmount);
                resultIntent.putExtra("expense_key", intent.getStringExtra("expense_id"));
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        binding.addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteExpense(intent.getStringExtra("expense_id"));
                finish();

            }
        });

    }


    public void deleteExpense(String expenseKey) {
        database = FirebaseDatabase.getInstance();
        expensesRef= database.getReference("expenses").child(userId);
        if (expenseKey == null || expenseKey.isEmpty()) {
            Toast.makeText(this, "Invalid expense key", Toast.LENGTH_SHORT).show();
            return;
        }


        DatabaseReference expenseRef = expensesRef.child(expenseKey);

        expenseRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Expense deleted successfully");
                    Toast.makeText(expense_detail.this, "Expense deleted from database", Toast.LENGTH_SHORT).show();

                    // Safely iterate and remove the expense from the local list

                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error deleting expense", e);
                    Toast.makeText(expense_detail.this, "Error deleting expense", Toast.LENGTH_SHORT).show();
                });
    }




}