package com.example.seguimientoderutas;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

// Update the import statement for View
import android.view.View;

public class RouteDetailsAdapter extends RecyclerView.Adapter<RouteDetailsAdapter.ViewHolder> {

    private List<LocationData> routeList;

    public RouteDetailsAdapter(List<LocationData> routeList) {
        this.routeList = routeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_route_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationData locationData = routeList.get(position);
        holder.locationTextView.setText("Ubicación: " + locationData.getLatitude() + ", " + locationData.getLongitude());
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView locationTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
        }
    }
}
