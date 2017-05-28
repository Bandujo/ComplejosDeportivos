package com.infomovil.sergio.MisComplejosDeportivos;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//Fragmento asociado a MainActivity
public class CentrosListFragment extends Fragment implements AdapterView.OnItemClickListener {

    String url = "http://datos.gijon.es/doc/deporte/otros-espacios-deportivos.json";
    public SingleCentros centros = SingleCentros.getInstance();
    ProgressBar progressBar;
    Toolbar toolbar;
    Toolbar tb;
    ArrayList<Centro> listaDeCentros = new ArrayList<>();
    ComplejosAdapter adapter;


    public CentrosListFragment() {
    }

    public Callbacks mCallback = new Callbacks() {
        @Override
        public void onCentroSelected(Centro centro) {
        }
    };


    public interface Callbacks {
        public void onCentroSelected(Centro centro);
    }


    @Override
    //Llena la lista con los centros deportivos
    public void onActivityCreated(Bundle savedInstanceState) {
        listaDeCentros = centros.getCentros();
        super.onActivityCreated(savedInstanceState);
        tb= (Toolbar) getView().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        progressBar = (ProgressBar) getView().findViewById(R.id.progress_bar);
        if(listaDeCentros.size() == 0) {
            tb.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            DownloadJsonTask descarga = new DownloadJsonTask();
            descarga.execute(url);
        }
        else{
            populateList(listaDeCentros);
        }

        toolbar = (Toolbar) getView().findViewById(R.id.tbMain);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        //Permite acceder a los detalles del centro deportivo seleccionado
        ListView lv = (ListView) getView().findViewById(R.id.arrival_list_view);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Centro centro = (Centro) parent.getItemAtPosition(position);
                mCallback.onCentroSelected(centro);
                Intent newActivity = new Intent(view.getContext(), InfoCentrosDeportivos.class);
                newActivity.putExtra("nombre", centro.getNombre());
                newActivity.putExtra("direccion", centro.getDireccion());
                newActivity.putExtra("tipo", centro.getTipo());
                newActivity.putExtra("telefono", centro.getTelefono());
                newActivity.putExtra("descripcion", centro.getDescripcion());
                newActivity.putExtra("X", centro.getX());
                newActivity.putExtra("Y", centro.getY());
                startActivity(newActivity);
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.centros_list_fragment, container, false);
        return rootView;
    }

    //Comprueba las preferencias del usuario para saber si quiere mostrar todos los centros o solo de los que se conoce el tipo
    protected boolean FicheroVacio(){
        InputStream buffer = null;
        ObjectInput input = null;
        boolean state = true;
        try {
            buffer = new BufferedInputStream(getActivity().openFileInput("Preferencias"));
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallback =(Callbacks) context;
        }
        catch(ClassCastException e){
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}

    //Rellena la lista con los centros, incluira todos o solo los de tipo conocido segun las preferencias del usuario
    public void populateList(ArrayList<Centro> a) {
        if (!(a == null)) {
            ListView lv = (ListView) getView().findViewById(R.id.arrival_list_view);
            if(FicheroVacio()) {
                adapter = new ComplejosAdapter(getActivity(), listaDeCentros);
                lv.setAdapter(adapter);
            }
            else{
                    ArrayList<Centro> aux = new ArrayList<>();
                    for(Centro c :listaDeCentros){
                        if(!c.getTipo().equals("No hay datos"))
                            aux.add(c);
                    }
                adapter = new ComplejosAdapter(getActivity(), aux);
                lv.setAdapter(adapter);
            }
        }
        tb.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

    }

    private InputStream openHttpInputStream(String myUrl)
            throws MalformedURLException, IOException, ProtocolException {
        InputStream is;
        URL url = new URL(myUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        // Aquí se hace realmente la petición
        conn.connect();
        is = conn.getInputStream();
        return is;
    }


    //Esta clase es la que descarga los datos del Json y los mete en el Singleton
    private class DownloadJsonTask extends AsyncTask<String, Void, ArrayList<Centro>> {

        protected ArrayList<Centro> doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0].toString());
            } catch (IOException e) {
                return null;
            } catch (JSONException e) {
                return null;
            }
        }


        private ArrayList<Centro> downloadUrl(String myUrl) throws IOException, JSONException {
            InputStream is = null;

            try {
                is = openHttpInputStream(myUrl);
                String aux = streamToString(is);
                return parseJsonDeportivoFile(aux);
            } finally {
                // Asegurarse de que el InputStream se cierra
                if (is != null) {
                    is.close();
                }

            }
        }

        // Pasa un InputStream a un String
        public String streamToString(InputStream stream) throws IOException,
                UnsupportedEncodingException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = 0;
            do {
                length = stream.read(buffer);
                if (length != -1) {
                    baos.write(buffer, 0, length);
                }
            } while (length != -1);
            return baos.toString("UTF-8");
        }

        @Override
        protected void onPostExecute(ArrayList<Centro> result) {
            populateList(result);

        }

        private static final String DIRECTORIOS_TAG = "directorios";
        private static final String DIRECTORIO_TAG = "directorio";
        private static final String NOMBRE_TAG = "nombre";
        private static final String TELEFONO_TAG = "telefono";
        private static final String DIRECCION_TAG = "direccion";
        private static final String LOCALIZACION_TAG = "localizacion";
        private static final String DESCRIPCION_TAG = "descripcion";


        private ArrayList<Centro> parseJsonDeportivoFile(String jsonDeportivoInformation)
                throws JSONException {
            JSONObject root = new JSONObject(jsonDeportivoInformation);
            JSONObject directorios = root.getJSONObject(DIRECTORIOS_TAG);
            JSONArray directoriosArray = directorios.getJSONArray(DIRECTORIO_TAG);

            //Recorre todos los datos del json proporcionado con los centros deportivos
            for(int i = 0; i<directoriosArray.length(); i++){

                JSONObject unDir = directoriosArray.getJSONObject(i);
                Centro c = new Centro();

                //Obtenemos el nombre y el tipo
                String[] NombreYTipo = unDir.getJSONObject(NOMBRE_TAG).getString("content").split("\\|");
                String nombre;
                String tipo;

                //Todos los centros tienen un nombre por lo que no es necesario controlarlo
                //Como no todos tienen un tipo tenemos una alternativa en la que indicaremos que "No hay datos"
                if (NombreYTipo.length>1) {
                    nombre = unDir.getJSONObject(NOMBRE_TAG).getString("content").split("\\|")[1];
                    tipo = unDir.getJSONObject(NOMBRE_TAG).getString("content").split("\\|")[0];
                }
                else{
                    nombre = unDir.getJSONObject(NOMBRE_TAG).getString("content");
                    tipo = "No hay datos";
                }

                //Si tiene un teléfono lo guardamos, si no indicamos que "No hay datos"
                String telefono;
                try{
                    telefono = unDir.getJSONObject(TELEFONO_TAG).getString("content");
                }

                catch(Exception e) {
                    telefono="No hay datos";
                }

                //Si tiene una descripción la guardamos, si no indicamos que "No hay datos"
                String descripcion;
                try{
                    String des = unDir.getJSONObject(DESCRIPCION_TAG).getString("content");
                    des = des.split("<img")[0];
                    descripcion = Html.fromHtml(des).toString();
                }
                catch(Exception e){
                    descripcion = "No hay datos";
                }

                //Obtenemos la dirección y la formateamos para que sea legible ya que contiene caracteres de stilo HTML
                //Si no hay una dirección asociada indicamos que "No hay datos"
                String dir;
                try {
                    String direccion = unDir.getJSONArray(DIRECCION_TAG).getJSONObject(1).getString("content");
                    Spanned dirr = Html.fromHtml(direccion);
                    dir = dirr.toString().split("\n")[0];
                }
                catch(Exception e){
                    dir = "No hay datos";
                }

                //Algunas direcciones tienen la calle dos veces repetida, una indicada con "C/" y otra con "Calle", por eso lo controlamos
                //Y mostramos solo una en la aplicación
                if(dir.indexOf("C/")>=0 && dir.indexOf("Calle")>=0) {
                    dir = dir.split("Calle")[0];
                    dir = dir.substring(0, dir.length()-2);
                }

                //Guardamos la localización para poder mostrar los centros en un mapa, si no tenemos esos datos guardamos
                //X = 0 e Y = 0 y más adelante lo controlamos para indicarle al usuario que no se puede mostrar
                double X;
                double Y;
                try{
                    String localizacion = unDir.getJSONObject(LOCALIZACION_TAG).getString("content");
                    X = Double.parseDouble(localizacion.split(" ")[0]);
                    Y = Double.parseDouble(localizacion.split(" ")[1]);
                }
                catch(Exception e){
                    X = 0;
                    Y = 0;
                }

                //Con todos los datos obtenidos modificamos el centro deportivo y lo añadimos a la lista de centros
                c.setNombre(nombre);
                c.setTipo(tipo);
                c.setTelefono(telefono);
                c.setDireccion(dir);
                c.setDescripcion(descripcion);
                c.setX(X);
                c.setY(Y);
                centros.addCentro(c);
            }

            //Ordenamos el array de centros por nombre para hacer más sencillo navegar por la lista
            Collections.sort(centros.getCentros(),
                    new Comparator<Centro>() {
                        @Override
                        public int compare(Centro o1, Centro o2) {
                            return o1.getNombre().compareTo(o2.getNombre());
                        }
                    }
            );
            //También se podrían ordenar por tipo, en ese caso utilizaríamos el comparator de abajo
            /*Collections.sort(centros.getCentros(),
                    new Comparator<Centro>() {
                        @Override
                        public int compare(Centro o1, Centro o2) {
                            if(o1.getTipo()=="No hay datos") return 1;
                            if(o2.getTipo()=="No hay datos") return -1;
                            //if(o1.getTipo() == "No hay datos" && o2.getTipo() == "No hay datos") return 0;
                            return o1.getTipo().compareTo(o2.getTipo());
                        }
                    }
            );*/
            return centros.getCentros();
        }
    }
}
