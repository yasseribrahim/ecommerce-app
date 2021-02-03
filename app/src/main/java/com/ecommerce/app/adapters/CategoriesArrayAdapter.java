package com.ecommerce.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ecommerce.app.R;
import com.ecommerce.app.models.Category;

import java.util.List;

public class CategoriesArrayAdapter extends ArrayAdapter<Category> {
    private final Context context;
    private final List<Category> categories;
    private final OnItemClickListener listener;

    public CategoriesArrayAdapter(Context context, List<Category> categories, OnItemClickListener listener) {
        super(context, -1, categories);
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_category, parent, false);
        TextView name = rowView.findViewById(R.id.name);
        name.setText(categories.get(position).getName());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClickListener(position);
                }
            }
        });

        return rowView;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position);
    }
}