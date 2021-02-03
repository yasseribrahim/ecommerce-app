package com.ecommerce.app.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.app.R;
import com.ecommerce.app.adapters.ProductsAdapter;
import com.ecommerce.app.fragments.ProgressDialogFragment;
import com.ecommerce.app.models.Constants;
import com.ecommerce.app.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity implements ProductsAdapter.AddToCartClickListener {
    private List<Product> products;
    private List<String> cartsIds;
    private String id, name;
    private DatabaseReference nodeCart;
    private Toolbar toolbar;
    RecyclerView recyclerView;
    TextView categoryName;
    ProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        toolbar = findViewById(R.id.toolbar);
        setupSupportedActionBar(toolbar);
        setActionBarTitle("Categories");

        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");

        categoryName = findViewById(R.id.name);
        recyclerView = findViewById(R.id.recyclerView);

        categoryName.setText(name);

        cartsIds = new ArrayList<>();
        products = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductsAdapter(products, cartsIds, this);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase dp = FirebaseDatabase.getInstance();
        DatabaseReference node = dp.getReference(Constants.TAG_PRODUCTS + "/" + id);
        nodeCart = dp.getReference(Constants.TAG_CARTS + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        ProgressDialogFragment.show(getSupportFragmentManager());
        node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                products.clear();
                Product product;
                for (DataSnapshot child : snapshot.getChildren()) {
                    product = child.getValue(Product.class);
                    products.add(product);
                }

                adapter.notifyDataSetChanged();

                ProgressDialogFragment.hide(getSupportFragmentManager());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        nodeCart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                cartsIds.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    cartsIds.add(child.getKey());
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    @Override
    public void onAddToCartClick(int position) {
        Product product = adapter.getItem(position);

        Product cartProduct = new Product(product.getId(), product.getImage(), product.getPrice(), product.getName(), 1L);
        nodeCart.child(product.getId()).setValue(cartProduct);
    }
}