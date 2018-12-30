package com.speedhack.plat.platscanner;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.speedhack.plat.platscanner.manager.FirebaseManager;

public class RegisterActivity extends AppCompatActivity {

    EditText etFullname, etEmail, etBirth, etPhone, etBrand, etPlat, etPassword, etRePassword;
    Spinner spGender;
    TextView toLogin;
    Button btRegister;
    ProgressBar progressBar;

    FirebaseAuth auth;
    DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        initViews();

        clickbutton();
    }

    private void clickbutton() {
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter your email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter your password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etRePassword.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Enter your confirmation password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etFullname.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Enter your fullname!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etBirth.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Enter your date of birth!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(spGender.getSelectedItem().toString())){
                    Toast.makeText(getApplicationContext(), "Enter your gender!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(etPhone.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Enter your phone number!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(etBrand.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Enter your car's brand!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(etPlat.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Enter your car's plat number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() <6){
                    Toast.makeText(getApplicationContext(), "Password too short!", Toast.LENGTH_SHORT).show();
                    return;
                }

                btRegister.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Email invalid", Toast.LENGTH_SHORT).show();
                                } else {
                                    String id = FirebaseAuth.getInstance().getUid();
                                    FirebaseManager manager = new FirebaseManager(id);
                                    manager.onCreateUser(etFullname.getText().toString(),
                                            etEmail.getText().toString(),
                                            etBirth.getText().toString(),
                                            spGender.getSelectedItem().toString(),
                                            etPhone.getText().toString(),
                                            etBrand.getText().toString(),
                                            etPlat.getText().toString());
                                    finish();
                                }
                                btRegister.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {
        etFullname = findViewById(R.id.et_fullname);
        etEmail = findViewById(R.id.et_email);
        etBirth = findViewById(R.id.et_birth);
        spGender = findViewById(R.id.sp_gender);
        etPhone = findViewById(R.id.et_phone);
        etBrand = findViewById(R.id.et_brand);
        etPlat = findViewById(R.id.et_plat);
        etPassword = findViewById(R.id.et_password);
        etRePassword = findViewById(R.id.et_repassword);
        btRegister = findViewById(R.id.bt_register);
        toLogin = findViewById(R.id.to_login);
        progressBar = findViewById(R.id.progress);
    }
}
