package com.ecommerce.app.activities;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.ecommerce.app.R;
import com.ecommerce.app.utils.BarCodeHelper;

/**
 * A login screen that offers login via email/password.
 */
public class BarCodeActivity extends AppCompatActivity {
    ImageView barcode;
    FrameLayout close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        barcode = findViewById(R.id.barcode);
        close = findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        String code = getIntent().getStringExtra("code");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int margin = getResources().getDimensionPixelSize(R.dimen.dimens_50dp);
        int height = getResources().getDimensionPixelSize(R.dimen.dimens_70dp);
        int width = displayMetrics.widthPixels - (2 * margin);
        int background = ResourcesCompat.getColor(getResources(), R.color.white, null);
        barcode.setImageBitmap(BarCodeHelper.getBitmap(code, width, height));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}