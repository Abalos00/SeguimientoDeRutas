package com.example.seguimientoderutas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private Button startTrackingButton;
    private Button stopTrackingButton;
    private Button customizeMapButton;
    private Button getLocationButton;

    private List<LatLng> routePoints;
    private DatabaseReference databaseReference;

    private boolean isTracking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtener una instancia de la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Inicializar la FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startTrackingButton = findViewById(R.id.startTrackingButton);
        stopTrackingButton = findViewById(R.id.stopTrackingButton);
        customizeMapButton = findViewById(R.id.customizeMapButton);
        getLocationButton = findViewById(R.id.getLocationButton);

        startTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTracking) {
                    startLocationUpdates();
                    showToast("Tracking started");
                } else {
                    stopLocationUpdates();
                    showToast("Tracking stopped");
                }
                isTracking = !isTracking;
                updateButtonState();
            }
        });

        stopTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationUpdates();
                showToast("Tracking stopped");
                isTracking = false;
                updateButtonState();
            }
        });

        customizeMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapTypesDialog();
            }
        });

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastKnownLocation();
            }
        });

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        routePoints = new ArrayList<>();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    updateMap(location);
                    // Guardar la ubicación en Firebase
                    saveLocationToFirebase(location);
                }
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
            getLastKnownLocation();
        }
    }

    private void updateMap(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        routePoints.add(latLng);
        updateRoute();
    }

    private void startLocationUpdates() {
        if (checkLocationPermission()) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            updateButtonState();
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        updateButtonState();
    }

    private void updateButtonState() {
        startTrackingButton.setEnabled(!isTracking);
        stopTrackingButton.setEnabled(isTracking);
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return false;
        } else {
            return true;
        }
    }

    private void getLastKnownLocation() {
        if (checkLocationPermission()) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            updateMap(location);
                        }
                    });
        }
    }

    private void updateRoute() {
        // Actualizar la ruta en el mapa si es necesario
        showToast("Route updated");
    }

    private void saveLocationToFirebase(Location location) {
        // Guardar la ubicación en Firebase bajo el "userId" y "routeId" actual
        String userId = "userId1"; // Reemplaza con tu lógica para obtener el userId actual
        String routeId = "routeId1"; // Reemplaza con tu lógica para obtener el routeId actual

        // Crear una referencia a la ubicación en Firebase
        DatabaseReference locationReference = databaseReference.child("users").child(userId).child("routes").child(routeId).child("locations").push();

        // Guardar los detalles de la ubicación en Firebase
        locationReference.child("latitude").setValue(location.getLatitude());
        locationReference.child("longitude").setValue(location.getLongitude());
        locationReference.child("timestamp").setValue(System.currentTimeMillis());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Función para cambiar el tipo de mapa
    private void showMapTypesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione el tipo de mapa");

        // Opciones de tipo de mapa
        String[] mapTypes = {"Normal", "Satelital", "Híbrido", "Terreno"};

        builder.setItems(mapTypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle item click and set map type accordingly
                switch (which) {
                    case 0:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case 1:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case 2:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case 3:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                }
            }
        });

        builder.show();
    }
}
