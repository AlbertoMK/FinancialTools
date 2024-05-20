package Negocio;

import Modelo.Deposito;

import java.util.Calendar;

public class GestorDepoitos extends GestorActivos {

    public GestorDepoitos() {
        super();
    }

    public void crearDeposito(String nombre, double desembolso, double TAE, Calendar fechaContratacion, double comisionCompra){
        Deposito deposito = new Deposito(siguienteId, nombre, desembolso, TAE, fechaContratacion, comisionCompra);
        siguienteId++;
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
}
