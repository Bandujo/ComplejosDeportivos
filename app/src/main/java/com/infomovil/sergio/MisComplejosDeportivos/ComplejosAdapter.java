package com.infomovil.sergio.MisComplejosDeportivos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bandujo on 28/03/2017.
 */

//Esta clase es la encargada de incluir los centros a la lista con el formato deseado
public class ComplejosAdapter extends BaseAdapter implements Filterable {

    private final List<Centro> centrosDeportivos;
    public LayoutInflater mInflater;
    private FriendFilter friendFilter;
    private List<Centro> centrosFiltrados;


    //Para el formato necesitado su nombre, su tipo y un icono asociado
    static class ViewHolder {
        public TextView nombre;
        public TextView tipo;
        public ImageView icono;
    }

    //Constructor pasando los centros a incluir
    public ComplejosAdapter(Context context, List<Centro> centros) {

        if (context == null || centros == null ) {
            throw new IllegalArgumentException();
        }

        this.centrosDeportivos = centros;
        this.mInflater = LayoutInflater.from(context);
        this.centrosFiltrados = centros;

        getFilter();
    }

    @Override
    public int getCount() {
        return centrosFiltrados.size();
    }

    @Override
    public Object getItem(int position) {
        return centrosFiltrados.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    //Con este metodo se incluyen uno a uno los centros especificando nombre, tipo e icono
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        ViewHolder viewHolder;
        if (rowView == null) {
            rowView = mInflater.inflate(R.layout.formatolista, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.nombre = (TextView) rowView.findViewById(R.id.nombre);
            viewHolder.tipo = (TextView) rowView.findViewById(R.id.tipo);
            viewHolder.icono = (ImageView) rowView.findViewById(R.id.imageView);
            rowView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        //Damos nombre, tipo y miramos que icono corresponde a cada uno
        Centro centro = (Centro) getItem(position);
        viewHolder.nombre.setText(centro.getNombre());
        viewHolder.tipo.setText(centro.getTipo());
        viewHolder.icono.setImageResource(R.drawable.olimpiadas);
        if(centro.getTipo().toLowerCase().contains("rugby") || centro.getTipo().toLowerCase().contains("americano")) viewHolder.icono.setImageResource(R.drawable.rugby);
        if(centro.getTipo().toLowerCase().contains("béisbol")|| centro.getTipo().toLowerCase().contains("beisbol"))viewHolder.icono.setImageResource(R.drawable.beisbol);
        if(centro.getTipo().toLowerCase().contains("padel"))viewHolder.icono.setImageResource(R.drawable.padel);
        if(centro.getTipo().toLowerCase().contains("patinódromo")|| centro.getTipo().toLowerCase().contains("patinodromo"))viewHolder.icono.setImageResource(R.drawable.patines);
        if(centro.getTipo().toLowerCase().contains("atletismo"))viewHolder.icono.setImageResource(R.drawable.corredor);
        if(centro.getTipo().toLowerCase().contains("squash"))viewHolder.icono.setImageResource(R.drawable.squash);
        if(centro.getTipo().toLowerCase().contains("tenis"))viewHolder.icono.setImageResource(R.drawable.tenis);
        if(centro.getTipo().toLowerCase().contains("musculación")|| centro.getTipo().toLowerCase().contains("musculación"))viewHolder.icono.setImageResource(R.drawable.musculacion);
        if(centro.getTipo().toLowerCase().contains("sauna"))viewHolder.icono.setImageResource(R.drawable.sauna);
        return rowView;
    }

    public void addCentro(Centro centro) {

        if (centro == null) {
            throw new IllegalArgumentException();
        }
        centrosDeportivos.add(centro);
        // Importante: notificar que ha cambiado el dataset
        notifyDataSetChanged();
    }

    //Devuelve el filtro
    @Override
    public Filter getFilter() {
        if (friendFilter == null) {
            friendFilter = new FriendFilter();
        }
        return friendFilter;
    }

    //Devuelve una lista con los centros filtrados
    public List<Centro> Filtrados(){

        return centrosFiltrados;
    }
    private class FriendFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<Centro> tempList = new ArrayList<Centro>();

                // Comprueba cada elemento para ver si se adapta a los criterios de la busqueda
                for (Centro centro : centrosDeportivos) {
                    if (centro.getNombre().toLowerCase().contains(constraint.toString().toLowerCase())
                            ||centro.getTipo().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(centro);
                    }
                }
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = centrosDeportivos.size();
                filterResults.values = centrosDeportivos;
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        //Actualiza la lista con los elementos que se adaptan a la búsqueda del filtro
        protected void publishResults(CharSequence constraint, FilterResults results) {
            centrosFiltrados = (ArrayList<Centro>) results.values;
            notifyDataSetChanged();
        }
    }
}
