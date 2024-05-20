package Otros;

import Modelo.Deposito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        Calendar fecha = Calendar.getInstance();
        fecha.set(2024,3, 23);
        Deposito deposito = new Deposito(1, "Deposito prueba", 2000, 3.1, fecha, 1);
        Persistencia.guardarDeposito(deposito);
    }
}
