package com.example.halalcheck3;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CurrentLocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private boolean locationPermissionGranted;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private RequestQueue requestQueue;
    private CustomInfoWindowAdapter infoWindowAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        requestQueue = Volley.newRequestQueue(this);

        getLocationPermission();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            initializeMap();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                initializeMap();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (locationPermissionGranted) {
            try {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                // Initialize custom info window adapter
                infoWindowAdapter = new CustomInfoWindowAdapter(this);
                mMap.setInfoWindowAdapter(infoWindowAdapter);

                // Set info window click listener
                mMap.setOnInfoWindowClickListener(this);

                // Set marker click listener
                mMap.setOnMarkerClickListener(marker -> {
                    // Fetch place details and update marker snippet with phone number
                    String placeId = (String) marker.getTag();
                    Toast.makeText(getApplicationContext(), "Click on marker" +placeId, Toast.LENGTH_LONG).show();
                    if (placeId != null) {
                        getPlaceDetails(placeId, marker);
                    }
                    return false; // Return false to indicate that we haven't consumed the event
                });

                getDeviceLocation();
            } catch (SecurityException e) {
                e.printStackTrace();
                Log.e("LocationAPI", "SecurityException: " + e.getMessage());
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // Handle info window clicks if needed
    }

    private void getDeviceLocation() {
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            LatLng currentLatLng = new LatLng(latitude, longitude);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                            getNearbyHalalRestaurants(location);
                        } else {
                            Toast.makeText(CurrentLocationActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        Log.e("LocationAPI", "Error getting device location", e);
                        Toast.makeText(CurrentLocationActivity.this, "Error getting device location", Toast.LENGTH_SHORT).show();
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e("LocationAPI", "SecurityException: " + e.getMessage());
        }
    }

    private void getNearbyHalalRestaurants(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + latitude + "," + longitude +
                "&radius=1500" +
                "&types=restaurant" +
                "&keyword=halal" +
                "&key=" + getString(R.string.google_maps_key);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("results")) {
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject place = results.getJSONObject(i);
                                JSONObject geometry = place.getJSONObject("geometry");
                                if (geometry.has("location")) {
                                    JSONObject locationObject = geometry.getJSONObject("location");
                                    double lat = locationObject.getDouble("lat");
                                    double lng = locationObject.getDouble("lng");
                                    String name = place.getString("name");
                                    String placeId = place.getString("place_id");
                                    LatLng latLng = new LatLng(lat, lng);
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title(name));
                                    marker.setTag(placeId);
                                }
                            }
                        } else {
                            Log.e("PlacesAPI", "No results found");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("PlacesAPI", "Error parsing JSON response: " + e.getMessage());
                    }
                },
                error -> {
                    error.printStackTrace();
                    Log.e("PlacesAPI", "Error fetching nearby restaurants: " + error.getMessage());
                });

        requestQueue.add(request);
    }

    private void getPlaceDetails(String placeId, Marker marker) {
        String placeDetailsUrl = "https://maps.googleapis.com/maps/api/place/details/json?" +
                "place_id=" + placeId +
                "&fields=formatted_phone_number" +
                "&key=" + getString(R.string.google_maps_key);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, placeDetailsUrl, null,
                response -> {
                    try {
                        if (response.has("result")) {
                            JSONObject result = response.getJSONObject("result");
                            String phoneNumber = result.optString("formatted_phone_number", "Phone number not available");
                            marker.setSnippet(phoneNumber);
                            marker.showInfoWindow();
                        } else {
                            Log.e("PlacesAPI", "No details found for place ID: " + placeId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("PlacesAPI", "Error parsing JSON response: " + e.getMessage());
                    }

                },
                error -> {
                    error.printStackTrace();
                    Log.e("PlacesAPI", "Error fetching place details: " + error.getMessage());
                });

        requestQueue.add(request);
    }
}
class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter , View.OnClickListener {
//class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;
    private final Context mContext;
    private String mPhoneNumber;

    CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_dialog, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        render(marker);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void render(Marker marker) {

        Toast.makeText(mContext.getApplicationContext(), "Render marker is called", Toast.LENGTH_LONG).show();
        TextView titleTextView = mWindow.findViewById(R.id.title);
        TextView phoneTextView = mWindow.findViewById(R.id.phone);
        Button viewMenuButton = mWindow.findViewById(R.id.view_menu_button);

        titleTextView.setText(marker.getTitle());

        // Get phone number from marker snippet
        mPhoneNumber = marker.getSnippet();
        if (mPhoneNumber != null && !mPhoneNumber.isEmpty()) {
            phoneTextView.setText(mPhoneNumber);

            fetchMenuFromFirebase(mPhoneNumber);
        } else {
            phoneTextView.setText("Phone number not available");
        }


        // Set click listener for view menu button
        viewMenuButton.setOnClickListener(this);
      /*  viewMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext.getApplicationContext(), "Click on View menu button", Toast.LENGTH_LONG).show();
            }
        });*/
    }


    @Override
    public void onClick(View v) {
        Toast.makeText(mContext.getApplicationContext(), "Click on marker", Toast.LENGTH_LONG).show();
        if (v.getId() == R.id.view_menu_button) {
            if (mPhoneNumber != null && !mPhoneNumber.isEmpty()) {
                Toast.makeText(mContext.getApplicationContext(), "Found phone number", Toast.LENGTH_LONG).show();
                fetchMenuFromFirebase(mPhoneNumber);
            } else {
                Toast.makeText(mContext, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchMenuFromFirebase(String phoneNumber) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("businesses");

        databaseReference.orderByChild("phone").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("phone").getValue(String.class).equals(phoneNumber)) {
                            String userId = snapshot.getKey();
                            Intent intent = new Intent(mContext, MenuClass.class);
                            intent.putExtra("UserId", userId);
                            intent.putExtra("phone_number", phoneNumber);
                            mContext.startActivity(intent);
                            return;
                        }
                    }
                    Toast.makeText(mContext, "Menu not found for this restaurant", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Menu not found for this restaurant", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mContext, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
