package Negocio;

import Modelo.Activo;
import Modelo.Deposito;
import Modelo.VentaDeposito;
import Otros.Persistencia;
import Otros.Utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class GestorDepoitos extends GestorActivos {

    public GestorDepoitos() {
        super();
    }

    public void crearDeposito(String nombre, double desembolso, double TAE, Calendar fechaContratacion, double comisionCompra){
        Deposito deposito = new Deposito(getSiguienteId(), nombre, desembolso, TAE, fechaContratacion, comisionCompra);
        incrementarId();
        listaActivos.add(deposito);
    }

    public void venderDeposito(int id, Calendar fecha, double importeVenta, double comision) {
        Deposito deposito = (Deposito)getActivoById(id);
        deposito.vender(fecha, importeVenta, comision);
    }

    public void añadirRetribucion(int id, Calendar fecha, double importe) {
        Deposito deposito = (Deposito)getActivoById(id);
        deposito.añadirRetribucion(fecha, importe);
    }

    public void load() {
        List<HashMap<String, Object>> representacionesDepositos =  Persistencia.getDepositos();
        for (HashMap<String, Object> representacionDeposito : representacionesDepositos) {
            // atributos básicos
            int id = (int) representacionDeposito.get("id");
            String nombre = (String) representacionDeposito.get("nombre");
            double desembolso = (double) representacionDeposito.get("desembolso");
            Calendar fechaContratacion = (Calendar) representacionDeposito.get("fechaContratacion");
            double comisionCompra = (double) representacionDeposito.get("comisionCompra");
            double tae = (double) representacionDeposito.get("tae");

            //venta
            VentaDeposito venta;
            HashMap<String, String> representacionVenta = (HashMap<String, String>) representacionDeposito.get("venta");
            if(representacionVenta == null)
                venta = null;
            else {
                Calendar fechaVenta = Utils.deserializarFecha(representacionVenta.get("fecha"));
                double comisionVenta = Double.parseDouble(representacionVenta.get("comision"));
                double importeVenta = Double.parseDouble(representacionVenta.get("importeVenta"));
                venta = new VentaDeposito(fechaVenta, comisionVenta, importeVenta);
            }

            // retribuciones
            HashMap<Calendar, Double> retribuciones = new HashMap<>();
            List<HashMap<String, String>> representacionRetribuciones = (List<HashMap<String, String>>) representacionDeposito.get("retribuciones");
            for(HashMap<String, String> representacionRetribucion : representacionRetribuciones) {
                retribuciones.put(Utils.deserializarFecha(representacionRetribucion.get("fecha")),
                        Double.parseDouble(representacionRetribucion.get("importe")));
            }

            this.listaActivos.add(new Deposito(id, nombre, desembolso, tae, fechaContratacion, comisionCompra, retribuciones, venta));
        }
    }
}
