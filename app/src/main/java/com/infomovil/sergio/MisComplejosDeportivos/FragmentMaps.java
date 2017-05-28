package com.infomovil.sergio.MisComplejosDeportivos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.infomovil.sergio.MisComplejosDeportivos.R.id.map;

/**
 * Created by Bandujo on 05/04/2017.
 */

//Muestra el centro deportivo indicado en el mapa si puede y sino lo indica
    public class FragmentMaps extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    Double X;
    Double Y;
    String nombre;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        X = intent.getDoubleExtra("X", 0);
        Y = intent.getDoubleExtra("Y", 0);
        nombre= intent.getStringExtra("nombre");
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        this.onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
       finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    //Si tiene datos de la localización muestra el centro en el mapa, si no lo indica
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if(X!= 0 && Y!= 0) {
            LatLng lon = new LatLng(X, Y);
            mMap.addMarker(new MarkerOptions()
                    .position(lon)
                    .title(nombre));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lon, 14f));
        }
        else{
            Toast toast = Toast.makeText(this, "No se tienen datos de la localización de este centro", Toast.LENGTH_LONG);
            toast.show();
        }
    }


}
