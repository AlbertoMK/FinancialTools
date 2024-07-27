package Modelo;

import java.util.HashMap;
import java.util.List;

public abstract class Activo {

    private int id;
    private String nombre;

    public Activo(String nombre, int id) {
        this.nombre = nombre;
        this.id = id;
    }

    public abstract double getImporteInicial();

    public abstract double getImporteActual();

    public abstract HashMap<String, String> getJSON();

    public abstract List getFlujosCaja(boolean eliminarRegalos);

    public String getNombre() {
        return nombre;
    }

    public int getId() {
        return id;
    }
}