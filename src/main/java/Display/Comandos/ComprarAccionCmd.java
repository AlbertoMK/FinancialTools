package Display.Comandos;

import Display.Display;
import Negocio.GestorAcciones;
import Otros.Utils;

import java.util.HashMap;
import java.util.List;

public class ComprarAccionCmd extends Comando{

    private int idAccion;
    private String[] argumentos;

    public ComprarAccionCmd(String[] args) {
        argumentos = args;
        if(args.length != 4){
            throw new RuntimeException("Número de parámetros incorrecto");
        }
        else{
            try {
                Double.parseDouble(args[0]);
                Double.parseDouble(args[1]);
                Utils.deserializarFecha(args[2]);
                Double.parseDouble(args[3]);
            } catch (RuntimeException ex) {
                throw new RuntimeException("Formato incorrecto de los argumentos.");
            }
            List<HashMap<String, String>> activos =  GestorAcciones.getInstance().getActivos();
            int opcion = Display.selector(activos);
            idAccion = Integer.parseInt(activos.get(opcion).get("id"));
        }
    }
    @Override
    public String ejecutar() {

        GestorAcciones.getInstance().comprarAccion();
    }
}
