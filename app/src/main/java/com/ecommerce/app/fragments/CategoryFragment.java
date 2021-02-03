package com.ecommerce.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ecommerce.app.R;
import com.ecommerce.app.activities.ProductActivity;
import com.ecommerce.app.adapters.CategoriesArrayAdapter;
import com.ecommerce.app.models.Category;
import com.ecommerce.app.models.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {
    private ListView list;
    CategoriesArrayAdapter adapter;
    ArrayList<Category> categories;

    @Nullable
    private FirebaseAuth mAuth;

    public static CategoryFragment newInstance() {

        Bundle args = new Bundle();

        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        mAuth = FirebaseAuth.getInstance();
        categories = new ArrayList<>();
        list = rootView.findViewById(R.id.list_category);
        adapter = new CategoriesArrayAdapter(getContext(), categories, new CategoriesArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                Category clickedItem = (Category) list.getItemAtPosition(position);
                Intent i = new Intent(getContext(), ProductActivity.class);

                i.putExtra("id", clickedItem.getId());
                i.putExtra("name", clickedItem.getName());
                startActivity(i);
            }
        });

        list.setAdapter(adapter);

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
        //progressBar.setVisibility(View.VISIBLE);
        return rootView;
    }
}
