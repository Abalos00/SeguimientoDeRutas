package com.example.seguimientoderutas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private TextView locationTextView;
    private DatabaseReference databaseReference;
    private List<LocationData> routeLocations;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Inicializar vistas y Firebase
        locationTextView = findViewById(R.id.locationTextView);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // Inicializar la lista de ubicaciones
        routeLocations = new ArrayList<>();

        // Recuperar datos de la ruta desde Firebase
        retrieveRouteLocationsFromFirebase();

        // Botón para cargar más ubicaciones
        Button loadMoreButton = findViewById(R.id.loadMoreButton);
        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreLocations();
            }
        });

        // Botón para reiniciar la visualización
        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = 0;
                displayLocationData();
            }
        });
    }

    private void retrieveRouteLocationsFromFirebase() {
        String userId = "userId1";
        String routeId = "routeId1";

        DatabaseReference userReference = databaseReference.child("users").child(userId).child("routes").child(routeId).child("locations");

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    double latitude = locationSnapshot.child("latitude").getValue(Double.class);
                    double longitude = locationSnapshot.child("longitude").getValue(Double.class);
                    long timestamp = locationSnapshot.child("timestamp").getValue(Long.class);

                    LocationData location = new LocationData(latitude, longitude, timestamp);
                    routeLocations.add(location);
                }

                // Ordenar las ubicaciones por timestamp (del más antiguo al más nuevo)
                Collections.sort(routeLocations, (location1, location2) ->
                        Long.compare(location1.getTimestamp(), location2.getTimestamp()));

                // Mostrar los datos en el TextView
                displayLocationData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si es necesario
            }
        });
    }

    private void displayLocationData() {
        StringBuilder locationText = new StringBuilder();

        // Mostrar las ubicaciones en el TextView
        for (int i = currentIndex; i < routeLocations.size(); i++) {
            LocationData locationData = routeLocations.get(i);
            locationText.append("Latitude: ").append(locationData.getLatitude()).append("\n");
            locationText.append("Longitude: ").append(locationData.getLongitude()).append("\n");
            locationText.append("Timestamp: ").append(locationData.getTimestamp()).append("\n\n");
        }

        // Mostrar los datos en el TextView en el hilo principal
        runOnUiThread(() -> locationTextView.setText(locationText.toString()));
    }

    private void loadMoreLocations() {
        // Incrementar el índice para cargar más ubicaciones
        currentIndex += 10;  // Ajusta según la cantidad que desees cargar
        displayLocationData();
    }
}
