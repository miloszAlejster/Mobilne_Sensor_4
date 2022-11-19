package com.example.sensorapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.BinderThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LocationActivity extends AppCompatActivity {
    private Button getLocationButton;
    private Button getAddressButton;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    private Location lastLocation;
    private TextView locationTextView;
    private TextView addressTextView;
    private FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        // Texts
        locationTextView = findViewById(R.id.text_view_location);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        addressTextView = findViewById(R.id.text_view_address);
        // Buttons
        getLocationButton = findViewById(R.id.loc_button);
        getLocationButton.setOnClickListener(v -> getLocation());
        getAddressButton = findViewById(R.id.button_address);
        getAddressButton.setOnClickListener(v -> executeGeocoding());

    }

    private void getLocation(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},
            REQUEST_LOCATION_PERMISSION);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(
                    location -> {
                        if(location != null){
                            lastLocation = location;
                            locationTextView.setText(
                                    getString(R.string.location_text,
                                            location.getLatitude(),
                                            location.getLongitude(),
                                            location.getTime())
                            );
                        } else {
                            locationTextView.setText("no location :}");
                        }
                    }
            );
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this, "denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private String locationGeocoding(Context context, Location location){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        String resultMessage = "";
        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );
        } catch(IOException ioException) {
            resultMessage = "not available";
            Log.d(TAG, resultMessage, ioException);
        }
        if(addresses == null || addresses.isEmpty()){
            if(resultMessage.isEmpty()){
                resultMessage = "not available";
                Log.d(TAG, resultMessage);
            }
        } else {
            Address address = addresses.get(0);
            List<String> addressParts = new ArrayList<>();
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++){
                addressParts.add(address.getAddressLine(i));
            }
            resultMessage = TextUtils.join("\n", addressParts);
        }
        return resultMessage;
    }
    private void executeGeocoding(){
        if(lastLocation != null){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> returnedAddress = executor.submit(
                    () -> locationGeocoding(getLocationButton.getContext(), lastLocation)
            );
            try {
                String result = returnedAddress.get();
                addressTextView.setText(getString(R.string.address_text,
                        result, System.currentTimeMillis()));
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }
    }
}