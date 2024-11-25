package com.example.expensetracker_1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.expensetracker_1.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import retrofit2.Call;
import retrofit2.Response;

public class SignUpActivity extends BaseActivity {
    ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();
    }



    private void setVariable() {
        binding.signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.editTextText3.getText().toString();
                String Password = binding.passEdt.getText().toString();
                if(Password.length()<6){
                    Toast.makeText(SignUpActivity.this, "Your password must be 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email,Password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isComplete()){
                            Log.i(TAG,"onComplete: ");
                            startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                        }else{
                            Log.i(TAG,"Failure: "+task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}