package com.infomovil.sergio.MisComplejosDeportivos;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Bandujo on 22/05/2017.
 */

//Fragmento de la actividad de información de un centro deportivo concreto
public class InfoCentrosFragment extends Fragment {

    Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        toolbar = (Toolbar) getView().findViewById(R.id.tb);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.info_centros_fragment, container, false);
        return rootView;
    }


}
