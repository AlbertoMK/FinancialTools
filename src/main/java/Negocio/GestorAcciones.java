package Negocio;

import Modelo.AccionETF;
import Modelo.Activo;
import Modelo.CompraVentaAccionETF;
import Otros.Persistencia;
import Otros.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    /**
     *
     * @return HashMap con los sectores y el peso que tiene cada uno en la cartera
     * @implNote Al llamar al método importeActual() para conocer los pesos de los activos en la cartera, los fondos que han sido vendidos
     * también se están contabilizando en el cálculo de los pesos de los sectores.
     */
    public HashMap<String, Double> getPorcentajeSectores() {
        double valorCartera = getValorCartera();
        HashMap<String, Double> resultado = new HashMap<>();
        for(Activo activo : listaActivos) {
            double valorActivo = activo.getImporteActual();
            AccionETF accion = (AccionETF) activo;
            HashMap<String, Double> sectores = accion.getPorcentajeSectores();
            for (String sector : sectores.keySet()) {
                if(resultado.containsKey(sector)){
                    resultado.put(sector, resultado.get(sector) + sectores.get(sector) * (valorActivo / valorCartera));
                }
                else {
                    resultado.put(sector, sectores.get(sector) * (valorActivo / valorCartera));
                }
            }
        }
        return resultado;
    }

    /**
     * Calcula concurrentemente el valor de todas las acciones.
     * @return Diccionario donde la clave es el id de la acción y el valor, su importe actual
     */
    public HashMap<Integer, Double> getImportesActuales() {
        HashMap<AccionETF, CompletableFuture<Double>> valorAcciones = new HashMap<>();
        for (HashMap<String, String> activo : GestorAcciones.getInstance().getActivos()) {
            AccionETF accion = (AccionETF) GestorAcciones.getInstance().getActivoById(Integer.parseInt(activo.get("id")));
            valorAcciones.put(accion, CompletableFuture.supplyAsync(() -> {
                return accion.getImporteActual();
            }));
        }
        CompletableFuture<Void> allOf = CompletableFuture.allOf(valorAcciones.values().toArray(new CompletableFuture[0]));
        allOf.join();

        HashMap<Integer, Double> resultado = new HashMap<>();
        for (AccionETF accion : valorAcciones.keySet()) {
            try {
                resultado.put(accion.getId(), valorAcciones.get(accion).get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Error de concurrencia al obtener el valor de las acciones.");
            }
        }
        return resultado;
    }

    public double getParticipacionesFecha(int idAccion, Calendar fecha) {
        AccionETF accion = (AccionETF) getActivoById(idAccion);
        return accion.getParticipacionesFecha(fecha);
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
