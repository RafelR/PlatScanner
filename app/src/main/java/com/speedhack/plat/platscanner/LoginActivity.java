package com.speedhack.plat.platscanner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btLogin;
    TextView toRegister;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
        initViews();

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter your email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter your password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                btLogin.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "Email/password invalid", Toast.LENGTH_SHORT).show();
                                }else{
                                    startActivity(new Intent(LoginActivity.this, UserActivity.class));
                                    finish();
                                }
                                btLogin.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });

        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null){
            startActivity(new Intent(LoginActivity.this, UserActivity.class));
            finish();
        }
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btLogin = findViewById(R.id.bt_login);
        toRegister = findViewById(R.id.to_register);
        progressBar = findViewById(R.id.progress);
    }
}
