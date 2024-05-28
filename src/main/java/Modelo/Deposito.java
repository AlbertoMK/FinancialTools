package Modelo;

import Otros.Persistencia;
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
        Persistencia.guardarDeposito(id, desembolso, TAE, fechaContratacion, comisionCompra, nombre);
    }

    // usado para load
    public Deposito(int id, String nombre, double desembolso, double TAE, Calendar fechaContratacion,
                    double comisionCompra, HashMap<Calendar, Double> retribuciones, VentaDeposito venta) {
        super(nombre, id);
        this.desembolso = desembolso;
        this.TAE = TAE;
        this.fechaContratacion = fechaContratacion;
        this.comisionCompra = comisionCompra;
        this.retribuciones = retribuciones;
        this.venta = venta;
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
            // calcular la cantidad acumulada. Para ello calculamos el tiempo desde la última retribución y sobre la TAE hacemos la estimacion
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
        Persistencia.venderDeposito(this);
    }

    public void añadirRetribucion(Calendar fecha, double importe) {
        retribuciones.put(fecha, importe);
        Persistencia.añadirRetribucionDeposito(getId(), fecha, importe);
    }

    public boolean estaVendido() {
        return venta != null;
    }

    @Override
    public HashMap<String, String> getJSON() {
        HashMap<String, String> resultado = new HashMap<>();
        resultado.put("id", String.valueOf(getId()));
        resultado.put("nombre", getNombre());
        resultado.put("desembolso", String.valueOf(desembolso));
        resultado.put("tae", String.valueOf(TAE));
        resultado.put("comisionCompra", String.valueOf(comisionCompra));
        resultado.put("fechaContratacion", Utils.serializarFechaEuropea(fechaContratacion));
        resultado.put("número retribuciones", String.valueOf(retribuciones.keySet().size()));
        resultado.put("vendido", String.valueOf(estaVendido()));
        return resultado;
    }

    @Override
    public List getFlujosCaja() {
        List<HashMap> resultado = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        map.put("Fecha", Utils.serializarFechaEuropea(fechaContratacion));
        map.put("Flujo", String.valueOf(-(desembolso + comisionCompra)));
        resultado.add(map);
        for (Calendar fechaRetribucion : retribuciones.keySet()) {
            map = new HashMap<>();
            map.put("Fecha", Utils.serializarFechaEuropea(fechaRetribucion));
            map.put("Flujo", String.valueOf(retribuciones.get(fechaRetribucion)));
            resultado.add(map);
        }
        Calendar ultimaFecha = null;
        if (estaVendido())
            ultimaFecha = venta.getFecha();
        else
            ultimaFecha = Calendar.getInstance();
        map = new HashMap<>();
        map.put("Fecha", Utils.serializarFechaEuropea(ultimaFecha));
        map.put("Flujo", String.valueOf(getImporteActual()));
        resultado.add(map);
        return resultado;
    }

    public double getDesembolso() {
        return desembolso;
    }

    public double getTAE() {
        return TAE;
    }

    public double getComisionCompra() {
        return comisionCompra;
    }

    public Calendar getFechaContratacion() {
        return fechaContratacion;
    }

    public VentaDeposito getVenta() {
        return venta;
    }
}
