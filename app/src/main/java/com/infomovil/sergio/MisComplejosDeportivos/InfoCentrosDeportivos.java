package com.infomovil.sergio.MisComplejosDeportivos;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

//Esta clase nos da información mas detallada del centro deportivo seleccionado
public class InfoCentrosDeportivos extends AppCompatActivity {

    public Double X;
    public Double Y;
    public String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.info_centros_single);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        String direccion = intent.getStringExtra("direccion");
        String telefono = intent.getStringExtra("telefono");
        String descripcion = intent.getStringExtra("descripcion");
        String tipo1 = intent.getStringExtra("tipo");
        X = intent.getDoubleExtra("X", 0);
        Y = intent.getDoubleExtra("Y", 0);

        TextView nom = (TextView) findViewById(R.id.name);
        nom.setText(nombre);

        TextView tipo = (TextView) findViewById(R.id.tipo);
        tipo.setText("Tipo de centro: " + tipo1);

        TextView dir = (TextView) findViewById(R.id.dir);
        dir.setText(direccion);

        TextView tel = (TextView) findViewById(R.id.tel);
        tel.setText(telefono);

        TextView des = (TextView) findViewById(R.id.des);
        des.setText(descripcion);

        //Botón flotante que nos permitirá ver el centro en el mapa
        FloatingActionButton MapaCentroFlotante = (FloatingActionButton) findViewById(R.id.BtnGmapsCentro);
        MapaCentroFlotante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GMapsActivity = new Intent(view.getContext(), FragmentMaps.class);
                GMapsActivity.putExtra("X", X);
                GMapsActivity.putExtra("Y", Y);
                GMapsActivity.putExtra("nombre", nombre);
                startActivity(GMapsActivity);
            }
        });
    }

    //Controlar el click de la Back Arrow
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

}
