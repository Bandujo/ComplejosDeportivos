package com.infomovil.sergio.MisComplejosDeportivos;

/**
 * Created by Bandujo on 28/03/2017.
 */

//Tipo de dato a manejar por la aplicación
public class Centro {

    //Atributos
    private String nombre;
    private String tipo;
    private String telefono;
    private String direccion;
    private String descripcion;
    private double X;
    private double Y;

    //Constructor por defecto
    public Centro(){}

    //Constructor con parametros
    public Centro(String nom, String tipo, String tel, String dir, String des, Double X, Double Y) {
        nombre = nom;
        this.tipo = tipo;
        telefono = tel;
        direccion = dir;
        descripcion = des;
        this.X = X;
        this.Y = Y;
    }


    //Getters y Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setX(double x) {
        X = x;
    }

    public void setY(double y) {
        Y = y;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }
}
