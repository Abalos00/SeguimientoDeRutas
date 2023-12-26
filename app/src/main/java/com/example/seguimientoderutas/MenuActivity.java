package com.example.seguimientoderutas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private Button mapButton, historyButton, detailsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Inicializar vistas
        mapButton = findViewById(R.id.mapButton);
        historyButton = findViewById(R.id.historyButton);
        detailsButton = findViewById(R.id.detailsButton); // Agrega el botón para ver detalles

        // Configurar listener para el botón de ver mapa
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToMapActivity();
            }
        });

        // Configurar listener para el botón de ver historial
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToHistoryActivity();
            }
        });

        // Configurar listener para el botón de ver detalles
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToDetailsActivity();
            }
        });
    }

    private void redirectToMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    private void redirectToHistoryActivity() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    private void redirectToDetailsActivity() {
        // Agrega la lógica para abrir la actividad de detalles aquí
        Intent intent = new Intent(this, DetailsActivity.class);
        startActivity(intent);
    }
}
