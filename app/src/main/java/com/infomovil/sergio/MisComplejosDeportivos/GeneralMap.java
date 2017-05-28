package com.infomovil.sergio.MisComplejosDeportivos;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import static com.infomovil.sergio.MisComplejosDeportivos.R.id.map;

/**
 * Created by Bandujo on 09/04/2017.
 */
//Muestra todos los centros deportivos en el mapa
public class GeneralMap extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    GoogleMap mMap;
    SingleCentros centros = SingleCentros.getInstance();
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    LatLng loc;
    ArrayList<Centro> centros1 = centros.getCentros();
    Marker[] chinchetas = new Marker[centros1.size()];


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        googleApiClient= new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

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
    public void onStart(){

        googleApiClient.connect();
        super.onStart();
    }
    @Override
    public void onStop(){
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    //Pinta todos los centros deportivos en el mapa indicando el nombre y, si se conoce, el tipo
    public void onMapReady(GoogleMap map) {
        mMap = map;
        LatLng lon;
        String tipo;

        for (int i = 0; i<centros1.size(); i++) {
            lon = new LatLng(centros1.get(i).getX(), centros1.get(i).getY());
            if(!centros1.get(i).getTipo().equals("No hay datos"))
                tipo=", " +centros1.get(i).getTipo();
            else{
                tipo="";}

            chinchetas[i]=mMap.addMarker(new MarkerOptions()
                    .position(lon)
                    .title(centros1.get(i).getNombre() + tipo));

        }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.5293101, -5.6573233), 12f));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    //Los siguientes métodos se encargan de pedir permisos de ubicación al usuario si no los ha concedido ya
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,getApplicationContext(),this)) {
            fetchLocationData();
        }
        else {
            requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getApplicationContext(), this);
        }
    }

    public void requestPermission(String strPermission,int perCode,Context _c,Activity _a){
        Toast.makeText(getApplicationContext(),"Es necesario poder acceder a tu localización para el normal uso de la aplicación",Toast.LENGTH_LONG).show();
        ActivityCompat.requestPermissions(_a,new String[]{strPermission},perCode);
    }

    public static boolean checkPermission(String strPermission, Context _c, Activity _a){
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocationData();
                } else {
                    Toast.makeText(getApplicationContext(),"Permiso denegado, algunas funciones de la aplicación no estarán disponibles",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    private void fetchLocationData()
    {
        //Aquí se accede cuando los permisos ya han sido concedidos
        try{
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            loc = new LatLng(0, 0);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
            }
            else {
                //Coge la ubicación del usuario y la añade al mapa en azul, además busca el centro deportivo más cercano y cambia el color
                //de su chincheta a amarillo
                loc = new LatLng(location.getLatitude(),location.getLongitude());
                double menor = 999999;
                double metros;
                int marker = 0;
                LatLng lon;
                for (int i = 0; i<centros1.size(); i++) {
                    lon = new LatLng(centros1.get(i).getX(), centros1.get(i).getY());
                    metros = SphericalUtil.computeDistanceBetween(lon, loc);
                    if(metros < menor) {
                        menor = metros;
                        marker = i;
                    }
                }
                chinchetas[marker].setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                mMap.addMarker(new MarkerOptions().position(loc).title("Aquí estás")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f));
            }
        }
        catch (Exception e) {}
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}
