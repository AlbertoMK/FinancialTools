package Modelo;

import java.util.Calendar;

public class VentaDeposito {
    private Calendar fecha;
    private double comision, importeVenta;

    public VentaDeposito(Calendar fecha, double comision, double importeVenta) {
        this.fecha = fecha;
        this.comision = comision;
        this.importeVenta = importeVenta;
    }

    public Calendar getFecha() {
        return fecha;
    }

    public double getComision() {
        return comision;
    }

    public double getImporteVenta() {
        return importeVenta;
    }
}
