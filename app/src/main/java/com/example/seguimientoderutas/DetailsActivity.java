package com.example.seguimientoderutas;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private GoogleMap routeMap;
    private TextView distanceTextView;
    private TextView durationTextView;
    private RecyclerView recyclerView;

    private PolylineOptions polylineOptions;
    private LatLng lastLocation;
    private double distanciaTotal;
    private long tiempoTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initialize views
        distanceTextView = findViewById(R.id.routeDistanceTextView);
        durationTextView = findViewById(R.id.routeDurationTextView);
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize route-related variables
        polylineOptions = new PolylineOptions().width(5).color(Color.RED);
        distanciaTotal = 0.0;
        tiempoTotal = 0L;

        // Configure RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the map from the fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.routeMap);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                routeMap = googleMap; // Assign the GoogleMap instance to the class-level variable

                // Get selected route data from Firebase
                getSelectedRouteFromFirebase();
            }
        });
    }

    private void getSelectedRouteFromFirebase() {
        // Retrieve the unique identifier of the selected route
        String routeId = "routeId1"; // You should obtain this value somehow

        // Query Firebase to get route information
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("routes");
        Query query = databaseReference.orderByChild("routeId").equalTo(routeId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LocationData> selectedRoute = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Convert Firebase data to the LocationData class
                    LocationData locationData = snapshot.getValue(LocationData.class);
                    selectedRoute.add(locationData);
                }

                // Display the route on the map and calculate/distance/duration
                displayRoute(selectedRoute);

                // Set up RecyclerView adapter
                RouteDetailsAdapter adapter = new RouteDetailsAdapter(selectedRoute);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if necessary
            }
        });
    }

    private void displayRoute(List<LocationData> routeLocations) {
        // Same code as before for displaying route on the map
        // ...

        // Refresh the map UI
        if (routeMap != null) {
            routeMap.clear();
            routeMap.addPolyline(polylineOptions);
            routeMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 15));
        }

        // Calculate and display distance and duration
        distanceTextView.setText("Distancia: " + distanciaTotal + " metros");
        durationTextView.setText("Duración: " + tiempoTotal + " milisegundos");
    }


    // Función para obtener el tiempo real de una ubicación en milisegundos
    private long obtenerTiempoDeUbicacion(LatLng ubicacion) {
        // Aquí debes implementar la lógica para obtener el tiempo real en milisegundos
        // asociado a la ubicación. Puedes hacer una consulta a Firebase usando la ubicación,
        // o utilizar la lógica que mejor se adapte a tu aplicación.
        // ...

        return System.currentTimeMillis(); // Reemplaza con el tiempo real obtenido de Firebase
    }

    // Función para calcular la distancia haversina entre dos puntos en la superficie de una esfera
    private double haversine(LatLng puntoA, LatLng puntoB) {
        final int RADIO_TIERRA = 6371000; // Radio de la Tierra en metros

        double latitudA = Math.toRadians(puntoA.latitude);
        double latitudB = Math.toRadians(puntoB.latitude);
        double diferenciaLatitud = Math.toRadians(puntoB.latitude - puntoA.latitude);
        double diferenciaLongitud = Math.toRadians(puntoB.longitude - puntoA.longitude);

        double a = Math.sin(diferenciaLatitud / 2) * Math.sin(diferenciaLatitud / 2) +
                Math.cos(latitudA) * Math.cos(latitudB) *
                        Math.sin(diferenciaLongitud / 2) * Math.sin(diferenciaLongitud / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIO_TIERRA * c;
    }
}
