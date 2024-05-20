package Modelo;
import java.util.Calendar;

public class CompraVentaAccionETF {

    private double participaciones;
    private double precio;
    private double comision;
    private Calendar fecha;
    private boolean esCompra;

    public CompraVentaAccionETF(double participaciones, double precio, double comision, Calendar fecha, boolean esCompra) {
        this.participaciones = participaciones;
        this.precio = precio;
        this.comision = comision;
        this.fecha = fecha;
        this.esCompra = esCompra;
    }

    public double getParticipaciones() {
        return participaciones;
    }

    public double getPrecio() {
        return precio;
    }

    public double getComision() {
        return comision;
    }

    public Calendar getFecha() {
        return fecha;
    }

    public boolean esCompra() {
        return esCompra;
    }
}
