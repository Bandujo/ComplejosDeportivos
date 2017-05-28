package com.infomovil.sergio.MisComplejosDeportivos;

import java.util.ArrayList;

/**
 * Created by Bandujo on 04/04/2017.
 */

//Esta clase es un Singleton donde se almacenan todos los centros descargados de la web
public class SingleCentros {

    private static SingleCentros instance = null;
    private ArrayList<Centro> ListaCentros= new ArrayList<>();

    protected SingleCentros(){}

    public static SingleCentros getInstance(){
        if(instance == null) instance = new SingleCentros();
        return instance;
    }

    public void addCentro(Centro c){
        ListaCentros.add(c);
    }

    public ArrayList<Centro> getCentros(){
        return ListaCentros;
    }


}
