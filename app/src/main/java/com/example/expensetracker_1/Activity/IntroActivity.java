package com.example.expensetracker_1.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.expensetracker_1.databinding.ActivityIntroBinding;

import retrofit2.Call;
import retrofit2.Response;

public class IntroActivity extends BaseActivity {
ActivityIntroBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();
        getWindow().setStatusBarColor(Color.parseColor("#FFE4B5"));
    }



    private void setVariable() {
        binding.signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser() != null){
                    startActivity(new Intent(IntroActivity.this, MainActivity.class));
                }else{
                    startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                }
            }
        });
        binding.sigininbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser() != null){
                    startActivity(new Intent(IntroActivity.this, MainActivity.class));
                }else{
                    startActivity(new Intent(IntroActivity.this, SignUpActivity.class));
                }
            }
        });
    }
}