package com.infomovil.sergio.MisComplejosDeportivos;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.infomovil.sergio.MisComplejosDeportivos.R.id.nav_view;

//Clase principal del programa, desde aquí se controla el menú, el acceso al resto de actividades y el fragmento que maneja la lista
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        CentrosListFragment.Callbacks, SearchView.OnQueryTextListener{

    SearchView searchView;
    Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout drawer;
    ComplejosAdapter adapter;
    Boolean fich;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Se crea un fichero para almacenar preferencias si no ha sido creado ya
        File pref = new File(getFilesDir(), "Preferencias");
        try {
            if (!pref.exists()) {
                pref.createNewFile();
                InicializaFichero();
            }
        }
        catch(Exception e){
        }
        fich = FicheroVacio();
        setContentView(R.layout.centros_list_single_pane);
        toolbar = (Toolbar) findViewById(R.id.tbMain);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        drawer = (DrawerLayout)findViewById(R.id.drawer_layout_aux);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name)
        {
            public void onDrawerClosed(View view)
            {
                supportInvalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView)
            {
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportActionBar().setHomeButtonEnabled(true);
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

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //Comprueba si han cambiado las preferencias del usuario
    @Override
    protected void onResume() {
        super.onResume();
        if (searchView != null) {
            if (searchView.getQuery().length() <= 0) {
                searchView.setQuery("", false);
                if(fich!=FicheroVacio()) {
                    ArrayList<Centro> listaDeCentros = SingleCentros.getInstance().getCentros();
                    ListView lv = (ListView) findViewById(R.id.arrival_list_view);
                    if (FicheroVacio()) {
                        adapter = new ComplejosAdapter(this, listaDeCentros);
                        lv.setAdapter(adapter);
                    } else {
                        ArrayList<Centro> aux = new ArrayList<>();
                        for (Centro c : listaDeCentros) {
                            if (!c.getTipo().equals("No hay datos"))
                                aux.add(c);
                        }
                        adapter = new ComplejosAdapter(this, aux);
                        lv.setAdapter(adapter);
                    }
                    fich = FicheroVacio();
                }
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout_aux);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else
                super.onBackPressed();
    }

    //Aquí se configura el searchview (el filtro de centros) en la toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        //searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                ListView lv = (ListView) findViewById(R.id.arrival_list_view);
                ComplejosAdapter ad = new ComplejosAdapter(MainActivity.this, SingleCentros.getInstance().getCentros());
                lv.setAdapter(ad);
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchView.setIconified(true);
                }
            }
        });
        return true;
    }

    @Override
    //Maneja el los clicks del menú
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //Si se pulsa el principal
        if (id == R.id.prin) {
        }

        //Si se pulsa en el mapa
        else if (id == R.id.map) {
            // Handle the camera action
            Intent launchNewIntent = new Intent(MainActivity.this,GeneralMap.class);
            startActivityForResult(launchNewIntent, 0);
        }

        //Si se pulsa el opciones
        else if (id == R.id.opciones) {
            Intent launchNewIntent = new Intent(MainActivity.this,Preferencias_Activity.class);
            launchNewIntent.putExtra("Intento", getIntent());
            startActivityForResult(launchNewIntent, 0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_aux);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        drawer.openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCentroSelected(Centro centro) {
    }

    @Override
    //Cuando estamos buscando y pulsamos ENTER indicamos si no hay coincidencias con la busqueda
    public boolean onQueryTextSubmit(String query) {
        ArrayList<Centro> aux = (ArrayList<Centro>) adapter.Filtrados();
        if(aux.size()==0) {
            Toast toast = Toast.makeText(this, "No hay datos", Toast.LENGTH_LONG);
            toast.show();
        }
        return false;
    }

    @Override
    //Busca dinámicamente según el contenido del searchView
    public boolean onQueryTextChange(String newText) {
        if(!newText.equals("")) {
            ArrayList<Centro> listaDeCentros = SingleCentros.getInstance().getCentros();
            ListView lv = (ListView) findViewById(R.id.arrival_list_view);
            if(FicheroVacio()) {
                adapter = new ComplejosAdapter(this,listaDeCentros);
                lv.setAdapter(adapter);
            }
            else{
                ArrayList<Centro> aux = new ArrayList<>();
                for(Centro c :listaDeCentros){
                    if(!c.getTipo().equals("No hay datos"))
                        aux.add(c);
                }
                adapter = new ComplejosAdapter(this, aux);
                lv.setAdapter(adapter);
            }
            adapter.getFilter().filter(newText);
        }
        return false;
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
}

