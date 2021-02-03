package com.ecommerce.app.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ecommerce.app.R;
import com.ecommerce.app.activities.BarCodeActivity;
import com.ecommerce.app.models.Product;
import com.ecommerce.app.utils.BarCodeHelper;

import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {
    private List<Product> products;

    // data is passed into the constructor
    public OrderDetailsAdapter(List<Product> products) {
        this.products = products;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_details, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(OrderDetailsAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.name.setText(product.getName());
        holder.price.setText(product.getPrice() + "");
        holder.quantity.setText(product.getQuantity() + "");

        holder.barcode.setImageBitmap(BarCodeHelper.getBitmap(product.getId(), (int) holder.barcode.getResources().getDimension(R.dimen.dimen_300dp), (int) holder.barcode.getResources().getDimension(R.dimen.dimen_40dp)));

        Glide.with(holder.itemView.getContext()).load(product.getImage()).placeholder(R.drawable.ic_default_product).into(holder.image);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return products.size();
    }

    public Product getItem(int i) {
        return products.get(i);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView price;
        TextView quantity;
        ImageView barcode;

        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            price = view.findViewById(R.id.price);
            quantity = view.findViewById(R.id.quantity);
            barcode = view.findViewById(R.id.barcode);
            barcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), BarCodeActivity.class);
                    intent.putExtra("code", getItem(getLayoutPosition()).getId());
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}