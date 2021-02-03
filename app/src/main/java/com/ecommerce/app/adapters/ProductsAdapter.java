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

import java.util.ArrayList;
import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    private List<Product> products;
    private List<String> cartIds;
    private AddToCartClickListener listener;

    // data is passed into the constructor
    public ProductsAdapter(List<Product> products, AddToCartClickListener listener) {
        this(products, new ArrayList<>(), listener);
    }

    public ProductsAdapter(List<Product> products, List<String> cartIds, AddToCartClickListener listener) {
        this.products = products;
        this.cartIds = cartIds;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.name.setText(product.getName());
        holder.price.setText(product.getPrice() + "");
        holder.quantity.setText(product.getQuantity() + "");

        holder.btnAddToCart.setVisibility(View.VISIBLE);
        holder.cartIcon.setVisibility(View.GONE);
        if (cartIds.contains(product.getId())) {
            holder.btnAddToCart.setVisibility(View.GONE);
            holder.cartIcon.setVisibility(View.VISIBLE);
        }

        holder.barcode.setImageBitmap(BarCodeHelper.getBitmap(product.getId(), (int) holder.barcode.getResources().getDimension(R.dimen.dimen_300dp), (int) holder.barcode.getResources().getDimension(R.dimen.dimen_40dp)));

        Glide.with(holder.itemView.getContext()).load(product.getImage()).placeholder(R.drawable.ic_default_product).into(holder.image);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return products.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView name;
        TextView price;
        TextView quantity;
        TextView btnAddToCart;
        ImageView cartIcon;
        ImageView barcode;

        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            barcode = view.findViewById(R.id.barcode);
            cartIcon = view.findViewById(R.id.cart_icon);
            name = view.findViewById(R.id.name);
            price = view.findViewById(R.id.price);
            quantity = view.findViewById(R.id.quantity);
            btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
            btnAddToCart.setOnClickListener(this);
            barcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), BarCodeActivity.class);
                    intent.putExtra("code", getItem(getLayoutPosition()).getId());
                    view.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (listener != null) listener.onAddToCartClick(getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Product getItem(int id) {
        return products.get(id);
    }

    // parent activity will implement this method to respond to click events
    public interface AddToCartClickListener {
        void onAddToCartClick(int position);
    }
}