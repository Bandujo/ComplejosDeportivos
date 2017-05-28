package com.infomovil.sergio.MisComplejosDeportivos;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

//Esta clase controla las preferencias del usuario respecto a si quiere que se muestren todos los centros en la lista
//o solo aquellos de los que se conoce el tipo
public class Preferencias_Activity extends AppCompatActivity {

    CheckBox cb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferencias_layout);

        cb = (CheckBox)findViewById(R.id.cbPref);

        if(!FicheroVacio())
            cb.setChecked(false);
        else
            cb.setChecked(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_preferencias);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    //Evento que se activa cuando se cambia el checkbox
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if(!checked)
            CambiaFichero();
        else {
            InicializaFichero();
        }
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

    //Comprueba las preferencias del usuario para saber si quiere mostrar todos los centros o solo de los que se conoce el tipo
    protected boolean FicheroVacio(){
        InputStream buffer = null;
        ObjectInput input = null;
        boolean state = true;
        try {
            buffer = new BufferedInputStream(openFileInput("Preferencias"));
            input = new ObjectInputStream(buffer);
            Object aux = input.readObject();
            if(aux.equals("Marcado"))
                state = true;
            else
                state = false;
        }
        catch(Exception ex) {
        }
        finally{
            try{
                input.close();
            }
            catch(Exception ex){

            }
        }
        return state;
    }

    //Deja el fichero con "Marcado" escrito, lo cual será nuestra opción por defecto que indica que mostramos todos los centros
    public void InicializaFichero(){
        FileOutputStream file = null;
        OutputStream buffer = null;
        ObjectOutput output = null;
        try {
            file = openFileOutput("Preferencias", Context.MODE_PRIVATE);
            buffer = new BufferedOutputStream(file);
            output = new ObjectOutputStream(buffer);
            output.writeObject("Marcado");
        }
        catch(Exception ex) {
        }
        finally{
            try{
                output.close();
            }
            catch(Exception ex){
            }
        }
    }

    //Deja el fichero con "Desmarcado" escrito, lo cual será nuestra opción para indicar que solo queremos mostrar centros
    //cuyo tipo es conocido
    protected void CambiaFichero(){
        FileOutputStream file = null;
        OutputStream buffer = null;
        ObjectOutput output = null;
        try {
            file = openFileOutput("Preferencias", Context.MODE_PRIVATE);
            buffer = new BufferedOutputStream(file);
            output = new ObjectOutputStream(buffer);
            output.writeObject("Desmarcado");
        }
        catch(Exception ex) {
        }
        finally{
            try{
                output.close();
            }
            catch(Exception ex){
            }
        }
    }

}
