package com.ecommerce.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.ecommerce.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY_MILLIS = 2000;
    private Handler handler;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isOpenHome()) {
                openHome();
            } else {
                openLogin();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference reference1 = database.getReference("categories");
//        DatabaseReference reference2 = database.getReference("products");
//
//        for (int i = 1; i <= 10; i++) {
//            reference1.child(i + "").setValue(new Category(i + "", "Category " + i));
//            for (int j = 1; j <= 10; j++) {
//                reference2.child(i + "/" + i + "-" + j).setValue(new Product(i + "-" + j, "", (long) (i * j), "Product " + j, (long) j));
//            }
//        }

        handler = new Handler();
        openApp();
    }

    private boolean isOpenHome() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        return user != null;
    }

    protected void openHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void openApp() {
        handler.postDelayed(runnable, SPLASH_DELAY_MILLIS);
    }

    private void open() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}