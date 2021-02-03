package com.ecommerce.app.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ecommerce.app.R;
import com.ecommerce.app.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private FirebaseAuth auth;
    private static final String TAG = "Signup";
    private static final int GENDER_MALE = 1;
    private static final int GENDER_FEMALE = 2;

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView birthDate;
    private DatePickerDialog.OnDateSetListener listener;
    private EditText username;
    private EditText mail;
    private EditText password;
    private EditText job;
    private TextView signup;
    private RadioButton male, female;
    private int selectedGender;
    private long maxid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.toolbar);
        setupSupportedActionBar(toolbar);
        setActionBarTitle("New Account");

        auth = FirebaseAuth.getInstance();

        selectedGender = -1;

        birthDate = (TextView) findViewById(R.id.birth_date);
        male = findViewById(R.id.gender_male);
        female = findViewById(R.id.gender_female);
        username = (EditText) findViewById(R.id.username1);
        mail = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pass);
        job = (EditText) findViewById(R.id.job);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        signup = findViewById(R.id.signup);

        male.setOnCheckedChangeListener(this);
        female.setOnCheckedChangeListener(this);

        listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = day + "/" + month + "/" + year;
                birthDate.setText(date);
            }
        };

        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegisterActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        listener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mail.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String username = RegisterActivity.this.username.getText().toString().trim();
                String job = RegisterActivity.this.job.getText().toString().trim();
                String perthdate = birthDate.getText().toString().trim();

                if (username.isEmpty()) {
                    RegisterActivity.this.username.setError("Full name is required");
                    RegisterActivity.this.username.requestFocus();
                    return;
                }
                if (pass.isEmpty()) {
                    password.setError("Password is required");
                    password.requestFocus();
                    return;
                }
                if (job.isEmpty()) {
                    RegisterActivity.this.job.setError("Jop is required");
                    RegisterActivity.this.job.requestFocus();
                    return;
                }
                if (selectedGender < 0) {
                    Toast.makeText(getApplicationContext(), "Gender is required", Toast.LENGTH_LONG).show();
                    return;
                }
                if (perthdate.isEmpty()) {
                    birthDate.setError("Full date is required");
                    birthDate.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    mail.setError("Mail is required");
                    mail.requestFocus();
                    return;
                }
                User user = new User(username, email, perthdate, job, selectedGender + "");

                showProgressBar();
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase dp = FirebaseDatabase.getInstance();
                            String id = task.getResult().getUser().getUid();
                            DatabaseReference node = dp.getReference("customers");
                            node.child(String.valueOf(id)).setValue(user);
                            Toast.makeText(RegisterActivity.this, "SignUp Successful, Please Login Now!", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(i);
                            finishAffinity();
                        } else {
                            Toast.makeText(RegisterActivity.this, "SignUp Fail: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                        hideProgressBar();
                    }
                });
            }
        });
    }

    protected void setupSupportedActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolBarShadowStyle);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    protected void setActionBarTitle(int titleId) {
        getSupportActionBar().setTitle(titleId);
    }

    protected void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        switch (button.getId()) {
            case R.id.gender_male:
                if (checked) {
                    selectedGender = GENDER_MALE;
                }
                break;
            case R.id.gender_female:
                if (checked) {
                    selectedGender = GENDER_FEMALE;
                }
                break;
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}