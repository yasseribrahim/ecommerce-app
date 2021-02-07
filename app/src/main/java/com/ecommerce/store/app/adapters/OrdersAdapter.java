package com.ecommerce.store.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.store.app.R;
import com.ecommerce.store.app.models.Order;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    private List<Order> orders;
    private OnItemClickListener listener;

    // data is passed into the constructor
    public OrdersAdapter(List<Order> orders, OnItemClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order = orders.get(position);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(order.getDate());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        holder.no.setText(order.getId());
        holder.address.setText(order.getAddress());
        holder.total.setText(order.getTotal() + " L.E");
        holder.number.setText(order.getProducts().size() + "");
        holder.date.setText(format.format(calendar.getTime()));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return orders.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView no;
        TextView address;
        TextView total;
        TextView number;
        TextView date;

        ViewHolder(View view) {
            super(view);
            no = view.findViewById(R.id.no);
            address = view.findViewById(R.id.address);
            total = view.findViewById(R.id.total);
            number = view.findViewById(R.id.number);
            date = view.findViewById(R.id.date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onItemClickListener(getAdapterPosition());
                }
            });
        }
    }

    // convenience method for getting data at click position
    public Order getItem(int id) {
        return orders.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position);
    }
}