package com.example.medicinereminder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class NearbyMedicalActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_medical);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (checkLocationPermission()) {
            enableUserLocation();
        }
    }

    // ✅ Check permission properly
    private boolean checkLocationPermission() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);

            return false;
        }
    }

    // ✅ Enable location safely
    private void enableUserLocation() {
        try {
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // ✅ Get current location
    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {

                        LatLng userLocation = new LatLng(
                                location.getLatitude(),
                                location.getLongitude());

                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(userLocation, 14));

                        searchNearbyPlaces(userLocation, "pharmacy");
                        searchNearbyPlaces(userLocation, "hospital");
                    }
                });
    }

    private void searchNearbyPlaces(LatLng location, String placeType) {

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                + "location=" + location.latitude + "," + location.longitude
                + "&radius=3000"
                + "&type=" + placeType
                + "&key=YOUR_API_KEY_HERE";

        new Thread(() -> {
            try {
                java.net.URL urlObj = new java.net.URL(url);
                java.io.InputStream is = urlObj.openStream();
                java.util.Scanner scanner =
                        new java.util.Scanner(is).useDelimiter("\\A");

                String result = scanner.hasNext() ? scanner.next() : "";

                org.json.JSONObject jsonObject =
                        new org.json.JSONObject(result);

                org.json.JSONArray results =
                        jsonObject.getJSONArray("results");

                runOnUiThread(() -> {
                    try {
                        for (int i = 0; i < results.length(); i++) {

                            org.json.JSONObject place =
                                    results.getJSONObject(i);

                            org.json.JSONObject geometry =
                                    place.getJSONObject("geometry");

                            org.json.JSONObject loc =
                                    geometry.getJSONObject("location");

                            double lat = loc.getDouble("lat");
                            double lng = loc.getDouble("lng");

                            String name = place.getString("name");

                            LatLng placeLatLng =
                                    new LatLng(lat, lng);

                            mMap.addMarker(new MarkerOptions()
                                    .position(placeLatLng)
                                    .title(name)
                                    .icon(BitmapDescriptorFactory
                                            .defaultMarker(
                                                    placeType.equals("hospital")
                                                            ? BitmapDescriptorFactory.HUE_RED
                                                            : BitmapDescriptorFactory.HUE_GREEN)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ✅ Proper permission result handling
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                enableUserLocation();

            } else {
                Toast.makeText(this,
                        "Location permission is required to show nearby hospitals",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}