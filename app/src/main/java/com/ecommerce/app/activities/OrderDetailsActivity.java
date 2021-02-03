package com.ecommerce.app.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.app.R;
import com.ecommerce.app.adapters.OrderDetailsAdapter;
import com.ecommerce.app.models.Order;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OrderDetailsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView no, date, address, total;
    private OrderDetailsAdapter adapter;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oder_details);

        order = getIntent().getParcelableExtra("order");

        toolbar = findViewById(R.id.toolbar);
        setupSupportedActionBar(toolbar);
        setActionBarTitle(order.getId());

        no = findViewById(R.id.no);
        date = findViewById(R.id.date);
        address = findViewById(R.id.address);
        total = findViewById(R.id.total);
        recyclerView = findViewById(R.id.recyclerView);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(order.getDate());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        no.setText(order.getId());
        address.setText(order.getAddress());
        total.setText(order.getTotal() + " L.E");
        date.setText(format.format(calendar.getTime()));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderDetailsAdapter(order.getProducts());
        recyclerView.setAdapter(adapter);
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
}