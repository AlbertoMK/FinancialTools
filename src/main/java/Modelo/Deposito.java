package Modelo;

import Otros.Utils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Deposito extends Activo{

    private double desembolso, TAE, comisionCompra;
    private Calendar fechaContratacion;
    private HashMap<Calendar, Double> retribuciones;
    private VentaDeposito venta;

    public Deposito(int id, String nombre, double desembolso, double TAE, Calendar fechaContratacion, double comisionCompra) {
        super(nombre, id);
        this.desembolso = desembolso;
        this.TAE = TAE;
        this.fechaContratacion = fechaContratacion;
        this.comisionCompra = comisionCompra;
        retribuciones = new HashMap<>();
        venta = null;
    }

    @Override
    public double getImporteInicial() {
        return desembolso;
    }

    @Override
    public double getImporteActual() {
        if (estaVendido())
            return venta.getImporteVenta() - comisionCompra - venta.getComision();
        else {
            double totalRetribuciones = 0;
            for (double retribucion : retribuciones.values()) {
                totalRetribuciones += retribucion;
            }
            // calcular la cantidad acumulada. Para ello calculamos el tiempo desde la última retribucióny sobre la TAE hacemos la estimacion
            Calendar ultimaFecha = fechaContratacion;
            for (Calendar fecha : retribuciones.keySet()) {
                if(fecha.after(ultimaFecha)){
                    ultimaFecha = fecha;
                }
            }
            long tiempoTranscurrido = TimeUnit.MILLISECONDS.toDays(Calendar.getInstance().getTimeInMillis() - ultimaFecha.getTimeInMillis());
            double acumulado = TAE / 100 * tiempoTranscurrido / 365 * desembolso;
            return desembolso + totalRetribuciones + acumulado - comisionCompra;
        }
    }

    public void vender(Calendar fecha, double importeVenta, double comision) {
        venta = new VentaDeposito(fecha, comision, importeVenta);
    }

    public void añadirRetribucion(Calendar fecha, double importe) {
        retribuciones.put(fecha, importe);
    }

    public boolean estaVendido() {
        return venta != null;
    }

    @Override
    public HashMap<String, String> getJSON() {
        return null;
    }

    @Override
    public List getFlujosCaja() {
        List<HashMap> resultado = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        map.put("Fecha", Utils.serializarFecha(fechaContratacion));
        map.put("Flujo", String.valueOf(-(desembolso + comisionCompra)));
        resultado.add(map);
        for (Calendar fechaRetribucion : retribuciones.keySet()) {
            map = new HashMap<>();
            map.put("Fecha", Utils.serializarFecha(fechaRetribucion));
            map.put("Flujo", String.valueOf(retribuciones.get(fechaRetribucion)));
            resultado.add(map);
        }
        Calendar ultimaFecha = null;
        if (estaVendido())
            ultimaFecha = venta.getFecha();
        else
            ultimaFecha = Calendar.getInstance();
        map = new HashMap<>();
        map.put("Fecha", Utils.serializarFecha(ultimaFecha));
        map.put("Flujo", String.valueOf(retribuciones.get(getImporteActual())));
        resultado.add(map);
        return resultado;
    }
}
