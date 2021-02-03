package com.ecommerce.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ecommerce.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    private TextView signup, forgetPassword;
    private ProgressBar progressBar;
    private EditText email, password;
    private FirebaseAuth auth;
    private TextView login;
    private CheckBox rememberMe;
    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rememberMe = findViewById(R.id.remember_me);
        login = findViewById(R.id.login);
        signup = (TextView) findViewById(R.id.signup);
        forgetPassword = (TextView) findViewById(R.id.forget_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pass);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        rememberMe.setChecked(getRememberMe());
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                setRememberMe(checked);
            }
        });

        users = database.getReference("user");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = LoginActivity.this.email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (!isValidEmailAndPassword(email, pass)) {
                    Toast.makeText(getApplicationContext(), "Invalid Email Or Password", Toast.LENGTH_LONG).show();
                    return;
                }

                showProgressBar();
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid username or password: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                        hideProgressBar();
                    }
                });
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(i);

            }
        });
    }

    private void setRememberMe(boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("remember_me", value);
        editor.apply();
    }

    private boolean getRememberMe() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getBoolean("remember_me", false);
    }

    private boolean isValidEmailAndPassword(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            return false;
        }
        return true;
    }

    private void updateUI(FirebaseUser user) {
    }

    private void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void singin(String email, String pass) {
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(email).exists()) {
                    if (!email.isEmpty()) {
                        UserInfo ue = snapshot.child(email).getValue(UserInfo.class);
                        if (ue.getEmail().equals(email)) {
                            Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(i);
                            finishAffinity();
                        } else
                            Toast.makeText(LoginActivity.this, "not regester", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
