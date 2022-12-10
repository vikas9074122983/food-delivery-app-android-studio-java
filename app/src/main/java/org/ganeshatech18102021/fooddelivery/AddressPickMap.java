package org.ganeshatech18102021.fooddelivery;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.ganeshatech18102021.fooddelivery.databinding.ActivityAddressPickMapBinding;
import org.ganeshatech18102021.fooddelivery.user.home;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddressPickMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityAddressPickMapBinding binding;
    private Button pickbtn;
    private double lat,lon;
    public String pickedAddress;
    private Marker marker;
    private Geocoder geocoder;
    private List<Address> addresses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddressPickMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.pickaddrmap);
        mapFragment.getMapAsync(this);
        pickbtn = findViewById(R.id.pick_addr_btn);
        pickbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                putdata();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng india = new LatLng(20.5937, 78.9629);
        marker = mMap.addMarker(new MarkerOptions().position(india).title("Marker in india"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india,5.0f));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull @NotNull LatLng latLng) {
                lat = latLng.latitude;
                lon = latLng.longitude;
                addresses = new ArrayList<>();
                geocoder = new Geocoder(AddressPickMap.this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null){
                    pickedAddress = addresses.get(0).getLocality();
                }

                if (marker != null) {
                    marker.remove();
                }

                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Marker")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                Log.d("TAG", "onMarkerDragEnd: "+lat+" "+lon+" "+pickedAddress);
            }
        });
    }
    private void putdata() {
        if (pickedAddress != null){
            Intent intent = new Intent(AddressPickMap.this, UserHomepage.class);
            intent.putExtra("address",pickedAddress);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Please select location", Toast.LENGTH_SHORT).show();
        }
    }
}