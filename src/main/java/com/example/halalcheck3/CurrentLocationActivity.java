package com.example.halalcheck3;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class CurrentLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private boolean locationPermissionGranted;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlacesClient placesClient;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
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
                getDeviceLocation();
            } catch (SecurityException e) {
                e.printStackTrace();
                Log.e("LocationAPI", "SecurityException: " + e.getMessage());
            }
        }
    }

    private void getDeviceLocation() {
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                LatLng currentLatLng = new LatLng(latitude, longitude);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                                getNearbyHalalRestaurants(location);
                            } else {
                                Toast.makeText(CurrentLocationActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("LocationAPI", "Error getting device location", e);
                            Toast.makeText(CurrentLocationActivity.this, "Error getting device location", Toast.LENGTH_SHORT).show();
                        }
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
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject place = results.getJSONObject(i);
                                JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
                                double lat = location.getDouble("lat");
                                double lng = location.getDouble("lng");
                                String name = place.getString("name");
                                LatLng latLng = new LatLng(lat, lng);
                                mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }
}
