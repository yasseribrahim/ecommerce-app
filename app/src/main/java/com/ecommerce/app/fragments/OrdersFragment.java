package com.ecommerce.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.app.R;
import com.ecommerce.app.activities.OrderDetailsActivity;
import com.ecommerce.app.adapters.OrdersAdapter;
import com.ecommerce.app.models.Constants;
import com.ecommerce.app.models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements OrdersAdapter.OnItemClickListener {
    private List<Order> orders;
    private DatabaseReference nodeOrder;

    RecyclerView recyclerView;
    TextView number;
    OrdersAdapter adapter;

    public static OrdersFragment newInstance() {

        Bundle args = new Bundle();

        OrdersFragment fragment = new OrdersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oders, container, false);

        FirebaseDatabase dp = FirebaseDatabase.getInstance();
        nodeOrder = dp.getReference(Constants.TAG_ORDERS + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        orders = new ArrayList<>();
        number = view.findViewById(R.id.number);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrdersAdapter(orders, this);
        recyclerView.setAdapter(adapter);

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

                number.setText(orders.size() + "");

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    @Override
    public void onItemClickListener(int position) {
        Order order = adapter.getItem(position);

        Intent intent = new Intent(getContext(), OrderDetailsActivity.class);
        intent.putExtra("order", order);
        startActivity(intent);
    }
}
