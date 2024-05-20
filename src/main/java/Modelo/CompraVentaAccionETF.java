package Modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;

@Entity
@Table(name = "Compraventa")
public class CompraVentaAccionETF {
    @Column(name = "participaciones")
    private double participaciones;
    @Column(name = "precio")
    private double precio;
    @Column(name = "comision")
    private double comision;
    @Column(name = "fecha")
    private Calendar fecha;
    @Column(name = "esCompra")
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
