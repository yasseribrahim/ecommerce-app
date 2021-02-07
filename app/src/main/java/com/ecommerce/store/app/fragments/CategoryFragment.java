package com.ecommerce.store.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.store.app.R;
import com.ecommerce.store.app.activities.ProductActivity;
import com.ecommerce.store.app.adapters.CategoriesAdapter;
import com.ecommerce.store.app.models.Category;
import com.ecommerce.store.app.models.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryFragment extends Fragment {
    RecyclerView recyclerView;
    CategoriesAdapter adapter;
    List<Category> categories;
    Map<String, Integer> sizes;

    public static CategoryFragment newInstance() {

        Bundle args = new Bundle();

        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        categories = new ArrayList<>();
        sizes = new HashMap<>();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new CategoriesAdapter(categories, sizes, new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                Category clickedItem = categories.get(position);
                Intent intent = new Intent(getContext(), ProductActivity.class);

                intent.putExtra("id", clickedItem.getId());
                intent.putExtra("name", clickedItem.getName());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        //FirebaseUser User=mAuth.getCurrentUser();
        FirebaseDatabase dp = FirebaseDatabase.getInstance();
        DatabaseReference node = dp.getReference(Constants.TAG_CATEGORIES);

        ProgressDialogFragment.show(getChildFragmentManager());
        node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                categories.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    categories.add(child.getValue(Category.class));
                }

                adapter.notifyDataSetChanged();

                ProgressDialogFragment.hide(getChildFragmentManager());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference nodeProducts = dp.getReference(Constants.TAG_PRODUCTS);
        nodeProducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                sizes.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    sizes.put(child.getKey(), (int) child.getChildrenCount());
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return rootView;
    }
}
