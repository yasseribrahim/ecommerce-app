package com.ecommerce.app.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.app.R;
import com.ecommerce.app.adapters.ProductsAdapter;
import com.ecommerce.app.models.Constants;
import com.ecommerce.app.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements ProductsAdapter.AddToCartClickListener, PermissionListener {
    private DatabaseReference nodeCart;
    private AutoCompleteTextView textsearch;
    private TextView message;
    private ImageButton search_voice, btnBarcode;
    int voicecode = 1;

    RecyclerView recyclerView;
    ProductsAdapter adapter;
    private List<String> cartsIds;
    private List<Product> products, searchedProducts;

    public static SearchFragment newInstance() {

        Bundle args = new Bundle();

        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        message = rootView.findViewById(R.id.message);
        search_voice = rootView.findViewById(R.id.voice);
        textsearch = rootView.findViewById(R.id.textsearch);
        btnBarcode = rootView.findViewById(R.id.btn_barcode);

        textsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(textsearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cartsIds = new ArrayList<>();
        products = new ArrayList<>();
        searchedProducts = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductsAdapter(searchedProducts, cartsIds, this);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase dp = FirebaseDatabase.getInstance();
        DatabaseReference node = dp.getReference(Constants.TAG_PRODUCTS);
        ProgressDialogFragment.show(getChildFragmentManager());
        node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                products.clear();
                Product product;
                for (DataSnapshot child : snapshot.getChildren()) {
                    for (DataSnapshot subChild : child.getChildren()) {
                        product = subChild.getValue(Product.class);
                        products.add(product);
                    }
                }

                ProgressDialogFragment.hide(getChildFragmentManager());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        nodeCart = dp.getReference(Constants.TAG_CARTS + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
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

        btnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPermissionGiven()) {
                    startScan();
                } else {
                    givePermission();
                }
            }
        });

        search_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                startActivityForResult(intent, voicecode);
            }
        });

        return rootView;
    }

    private boolean isPermissionGiven() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void givePermission() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(this)
                .check();
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse response) {
        Toast.makeText(getContext(), "Permission required for use Camera", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {
        startScan();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
        if (token != null) {
            token.continuePermissionRequest();
        }
    }

    private void startScan() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt(getString(R.string.str_scan));
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == voicecode && resultCode == getActivity().RESULT_OK) {
            ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            textsearch.setText(text.get(0));
            textsearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = textsearch.getText().toString();
                    search(name);
                }
            });
            return;
        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result.getContents() == null) {
            Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
        } else {
            searchById(result.getContents());
        }
    }

    private void searchById(String id) {
        searchedProducts.clear();
        if (id != null && !id.isEmpty()) {
            for (Product product : products) {
                if (product.getId().toLowerCase().contains(id.toLowerCase())) {
                    searchedProducts.add(product);
                }
            }
        }

        refresh();
    }

    private void search(String name) {
        searchedProducts.clear();
        if (!name.isEmpty()) {
            for (Product product : products) {
                if (product.getName().toLowerCase().contains(name.toLowerCase())) {
                    searchedProducts.add(product);
                }
            }
        }

        refresh();
    }

    private void refresh() {
        message.setVisibility(View.GONE);
        if (searchedProducts.isEmpty()) {
            message.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAddToCartClick(int position) {
        Product product = adapter.getItem(position);

        Product cartProduct = new Product(product.getId(), product.getImage(), product.getPrice(), product.getName(), 1L);
        nodeCart.child(product.getId()).setValue(cartProduct);
    }
}