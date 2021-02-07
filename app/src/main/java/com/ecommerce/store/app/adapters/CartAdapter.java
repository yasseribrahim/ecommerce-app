package com.ecommerce.store.app.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ecommerce.store.app.R;
import com.ecommerce.store.app.activities.BarCodeActivity;
import com.ecommerce.store.app.models.Product;
import com.ecommerce.store.app.utils.BarCodeHelper;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<Product> products;
    private EditCartClickListener listener;

    // data is passed into the constructor
    public CartAdapter(List<Product> products) {
        this.products = products;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView price;
        TextView quantity;
        ImageView minus, add, remove;
        ImageView barcode;

        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            barcode = view.findViewById(R.id.barcode);
            name = view.findViewById(R.id.name);
            price = view.findViewById(R.id.price);
            quantity = view.findViewById(R.id.quantity);
            remove = view.findViewById(R.id.remove);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onRemoveCartClick(getAdapterPosition());
                }
            });
            minus = view.findViewById(R.id.minus);
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onEditCartClick(getAdapterPosition(), false);
                }
            });
            add = view.findViewById(R.id.add);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onEditCartClick(getAdapterPosition(), true);
                }
            });
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

    // convenience method for getting data at click position
    public Product getItem(int id) {
        return products.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(EditCartClickListener itemClickListener) {
        this.listener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface EditCartClickListener {
        void onEditCartClick(int position, boolean isAdd);

        void onRemoveCartClick(int position);
    }
}