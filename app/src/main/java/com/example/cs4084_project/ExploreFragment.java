package com.example.cs4084_project;

import static com.google.android.libraries.mapsplatform.transportation.consumer.model.Location.*;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.mapsplatform.transportation.consumer.model.Location;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import im.delight.android.location.SimpleLocation;

public class ExploreFragment extends Fragment implements OnMapReadyCallback {

    FirebaseAuth auth;
    FirebaseUser user;
    GoogleMap myMap;
    View mapView;
    View rootView;

    private FusedLocationProviderClient fusedLocationClient;

    public ExploreFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        this.rootView = rootView;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        if (user == null) {
            Intent intent = new Intent(requireActivity().getApplicationContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        }

        SupportMapFragment mapView = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        mapView.getMapAsync((OnMapReadyCallback) this);
        return rootView;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        SimpleLocation location = new SimpleLocation(this.getContext());
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this.getContext());
        }

        LatLng pos = new LatLng(70,70);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        LatLng sydney = new LatLng(-34, 151);
        LatLng TamWorth = new LatLng(-31.083332, 150.916672);
        LatLng NewCastle = new LatLng(-32.916668, 151.750000);
        LatLng Brisbane = new LatLng(-27.470125, 153.021072);

        ArrayList<Object> locationArrayList = new ArrayList<>();

        locationArrayList.add(sydney);
        locationArrayList.add(TamWorth);
        locationArrayList.add(NewCastle);
        locationArrayList.add(Brisbane);

        for (int i = 0; i < locationArrayList.size(); i++) {

            // below line is use to add marker to each location of our array list.
            googleMap.addMarker(new MarkerOptions().position((LatLng) locationArrayList.get(i)).title("Marker"));

            // below line is use to zoom our camera on map.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));

            // below line is use to move our camera to the specific location.
            googleMap.moveCamera(CameraUpdateFactory.newLatLng((LatLng) locationArrayList.get(i)));
        }
    }

}