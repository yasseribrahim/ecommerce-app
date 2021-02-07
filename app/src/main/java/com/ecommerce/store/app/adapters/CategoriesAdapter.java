package com.ecommerce.store.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ecommerce.store.app.R;
import com.ecommerce.store.app.models.Category;

import java.util.List;
import java.util.Map;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private List<Category> categories;
    private Map<String, Integer> sizes;
    private OnItemClickListener listener;

    // data is passed into the constructor
    public CategoriesAdapter(List<Category> categories, Map<String, Integer> sizes, OnItemClickListener listener) {
        this.categories = categories;
        this.sizes = sizes;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoriesAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(CategoriesAdapter.ViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.name.setText(category.getName());
        holder.products.setText("Products: " + getCategorySize(category.getId()));
        Glide.with(holder.itemView.getContext()).load(category.getImage()).placeholder(R.drawable.ic_default_product).into(holder.image);
    }

    private int getCategorySize(String id) {
        try {
            Integer size = sizes.get(id);
            return size != null ? size : 0;
        } catch (Exception ex) {
        }
        return 0;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return categories.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView products;

        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            products = view.findViewById(R.id.products);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onItemClickListener(getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position);
    }
}