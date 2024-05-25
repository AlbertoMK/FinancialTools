package Negocio;

import Modelo.Activo;
import Otros.Persistencia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class GestorActivos {

    protected List<Activo> listaActivos;

    public GestorActivos(){
        listaActivos = new ArrayList<>();
    }

    public abstract void load();

    public static void incrementarId() {
        Persistencia.incrementarId();
    }

    protected int getSiguienteId() {
        return Persistencia.getSiguienteId();
    }

    public List<HashMap<String, String>> getFlujosCaja() {
        ArrayList<HashMap<String, String>> resultado = new ArrayList<>();
        for(Activo activo : listaActivos) {
            resultado.addAll(activo.getFlujosCaja());
        }
        return resultado;
    }

    public List<HashMap<String, String>> getActivos() {
        ArrayList<HashMap<String, String>> resultado = new ArrayList();
        for(Activo activo : listaActivos) {
            resultado.add(activo.getJSON());
        }
        return resultado;
    }

    public Activo getActivoById(int id) {
        Activo resultado = null;
        int i = 0;
        while(i < listaActivos.size() && resultado == null){
            if(listaActivos.get(i).getId() == id)
                resultado = listaActivos.get(i);
            i++;
        }
        return resultado;
    }
}
