package com.ecommerce.store.app.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.ecommerce.store.app.R;
import com.ecommerce.store.app.models.Constants;
import com.ecommerce.store.app.models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersHistoryActivity extends AppCompatActivity {
    private List<Order> orders;
    private AnyChartView anyChartView;
    private Pie pie;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_history);

        toolbar = findViewById(R.id.toolbar);
        setupSupportedActionBar(toolbar);
        setActionBarTitle("Orders History");

        anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        orders = new ArrayList<>();
        FirebaseDatabase dp = FirebaseDatabase.getInstance();
        DatabaseReference nodeOrder = dp.getReference(Constants.TAG_ORDERS + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        nodeOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                orders.clear();
                Order order;
                for (DataSnapshot child : snapshot.getChildren()) {
                    order = child.getValue(Order.class);
                    order.setId(child.getKey());
                    orders.add(order);
                }

                prepare();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        pie = AnyChart.pie();

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(OrdersHistoryActivity.this, event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prepare() {
        List<DataEntry> data = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            map.put(i + "", 0);
        }
        for (Order order : orders) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(order.getDate());

            int key = calendar.get(Calendar.MONTH) + 1;
            Integer value = map.get(key + "");

            map.put(key + "", value + 1);
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>();
        list.addAll(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> stringIntegerEntry, Map.Entry<String, Integer> t1) {
                Integer key1, key2;
                key1 = Integer.parseInt(stringIntegerEntry.getKey());
                key2 = Integer.parseInt(t1.getKey());
                return key1.compareTo(key2);
            }
        });

        for (Map.Entry<String, Integer> entry : list) {
            data.add(new ValueDataEntry(getMonthForInt(Integer.parseInt(entry.getKey()) - 1), entry.getValue()));
        }

        pie.data(data);

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Months")
                .padding(0d, 0d, 10d, 0d);

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);
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

    private String getMonthForInt(int number) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (number >= 0 && number <= 11 ) {
            month = months[number];
        }
        return month;
    }
}