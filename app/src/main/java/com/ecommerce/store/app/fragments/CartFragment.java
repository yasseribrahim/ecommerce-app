package com.ecommerce.store.app.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.store.app.R;
import com.ecommerce.store.app.adapters.CartAdapter;
import com.ecommerce.store.app.models.Constants;
import com.ecommerce.store.app.models.Order;
import com.ecommerce.store.app.models.Product;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.EditCartClickListener, PermissionListener {
    private List<Product> carts;
    private DatabaseReference nodeCart, nodeOrder;

    RecyclerView recyclerView;
    ImageView btnRefresh;
    TextView number, total, btnCheckout, address;
    CartAdapter adapter;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location location;
    private final int REQUEST_CHECK_SETTINGS = 43;

    public static CartFragment newInstance() {

        Bundle args = new Bundle();

        CartFragment fragment = new CartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        FirebaseDatabase dp = FirebaseDatabase.getInstance();
        nodeOrder = dp.getReference(Constants.TAG_ORDERS + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        nodeCart = dp.getReference(Constants.TAG_CARTS + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        carts = new ArrayList<>();
        btnCheckout = view.findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (carts.isEmpty()) {
                    Toast.makeText(getContext(), "Please Select at least on product", Toast.LENGTH_SHORT).show();
                    return;
                }

                completeCheckout();
            }
        });
        number = view.findViewById(R.id.number);
        total = view.findViewById(R.id.total);
        address = view.findViewById(R.id.address);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(carts);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        nodeCart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                carts.clear();
                Product product;
                int sum = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    product = child.getValue(Product.class);
                    carts.add(product);

                    sum += product.getPrice() * product.getQuantity();
                }

                number.setText(carts.size() + "");
                total.setText(sum + " L.E");

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnRefresh = view.findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAddress();
            }
        });

        fusedLocationProviderClient = new FusedLocationProviderClient(getContext());
        getAddress();

        return view;
    }

    private void getAddress() {
        if (isPermissionGiven()) {
            getCurrentLocation();
        } else {
            givePermission();
        }
    }

    private boolean isPermissionGiven() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void givePermission() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(this)
                .check();
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse response) {
        Toast.makeText(getContext(), "Permission required for showing location", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {
        getCurrentLocation();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
        if (token != null) {
            token.continuePermissionRequest();
        }
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getContext()).checkLocationSettings(locationSettingsRequest);
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    if (response != null && response.getLocationSettingsStates().isLocationPresent()) {
                        getLastLocation();
                    }
                } catch (ApiException exception) {
                    if (exception.getStatusCode() != Status.RESULT_SUCCESS.getStatusCode()) {
                        if (exception.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            try {
                                resolvable.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    this.getCurrentLocation();
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    location = task.getResult();

                    String address = "No known address";

                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    List<Address> addresses;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (!addresses.isEmpty()) {
                            address = addresses.get(0).getAddressLine(0);

                            CartFragment.this.address.setText(address);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void completeCheckout() {
        if (address.getText().toString().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Please Type Your Address");
            View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.layout_input, (ViewGroup) getView(), false);
            final EditText input = viewInflated.findViewById(R.id.input);
            builder.setView(viewInflated);

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    String address = input.getText().toString();
                    if (address.isEmpty()) {
                        Toast.makeText(getContext(), "InValid Address", Toast.LENGTH_LONG).show();
                        return;
                    }
                    CartFragment.this.address.setText(address);
                    save();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        } else {
            save();
        }
    }

    private void save() {
        Order order = new Order();
        order.setDate(Calendar.getInstance().getTimeInMillis());
        order.setAddress(address.getText().toString());
        order.setProducts(carts);

        nodeOrder.push().setValue(order, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Toast.makeText(getContext(), "Checkout Success", Toast.LENGTH_LONG).show();
                    nodeCart.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Error in checkout: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onRemoveCartClick(int position) {
        Product product = adapter.getItem(position);
        nodeCart.child(product.getId()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

            }
        });
    }

    @Override
    public void onEditCartClick(int position, boolean isAdd) {
        Product product = adapter.getItem(position);
        boolean isChanged = false;
        if (!isAdd && product.getQuantity() > 0) {
            product.setQuantity(product.getQuantity() - 1);
            isChanged = true;
        } else if (isAdd) {
            product.setQuantity(product.getQuantity() + 1);
            isChanged = true;
        }

        if (isChanged) {
            nodeCart.child(product.getId()).setValue(product);
        }
    }
}
