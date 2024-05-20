package Negocio;

import Modelo.AccionETF;

import java.util.Calendar;

public class GestorAcciones extends GestorActivos {

    public void crearAccion(String nombre, String ticker) {
        AccionETF accion = new AccionETF(getSiguienteId(), nombre, ticker);
        incrementarId();
        listaActivos.add(accion);
    }

    public void comprarAccion(int id, double participaciones, double precio, Calendar fecha, double comision) {
        AccionETF accion = (AccionETF)getActivoById(id);
        accion.comprar(participaciones, precio, fecha, comision);
    }

    public void venderAccion(int id, double participaciones, double precio, Calendar fecha, double comision) {
        AccionETF accion = (AccionETF)getActivoById(id);
        accion.vender(participaciones, precio, fecha, comision);
    }
}
