package Negocio;

import Modelo.AccionETF;
import Modelo.CompraVentaAccionETF;
import Otros.Persistencia;
import Otros.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class GestorAcciones extends GestorActivos {

    private static GestorAcciones instance = null;

    private GestorAcciones(){}

    public static GestorAcciones getInstance() {
        if (instance == null)
            instance = new GestorAcciones();
        return instance;
    }

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

    @Override
    protected void load() {
        List<HashMap<String, Object>> representaciones = Persistencia.getAcciones();
        for (HashMap<String, Object> representacion : representaciones) {
            int idAccion = (int) representacion.get("id");
            String nombre = (String) representacion.get("nombre");
            String ticker = (String) representacion.get("ticker");
            List<HashMap<String, String>> representacionesCompraVentas = (List<HashMap<String, String>>) representacion.get("compraventas");
            List<CompraVentaAccionETF> compraventas = new ArrayList<>();
            for (HashMap<String, String> representacionCompraVentas : representacionesCompraVentas) {
                double participaciones = Double.parseDouble(representacionCompraVentas.get("participaciones"));
                double precio = Double.parseDouble(representacionCompraVentas.get("precio"));
                Calendar fecha = Utils.deserializarFecha(representacionCompraVentas.get("fecha"));
                double comision = Double.parseDouble(representacionCompraVentas.get("comision"));
                boolean esCompra = Boolean.parseBoolean(representacionCompraVentas.get("esCompra"));
                CompraVentaAccionETF compraventa = new CompraVentaAccionETF(participaciones, precio, comision, fecha, esCompra);
                compraventas.add(compraventa);
            }
            this.listaActivos.add(new AccionETF(idAccion, nombre, ticker, compraventas));
        }
    }
}
