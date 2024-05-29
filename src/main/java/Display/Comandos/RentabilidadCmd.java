package Display.Comandos;

import Negocio.GestorAcciones;
import Negocio.GestorActivos;
import Negocio.GestorDepoitos;
import Otros.Utils;
import org.apache.commons.cli.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RentabilidadCmd extends Comando{

    Options opciones;
    String tipo;

    public RentabilidadCmd() {
        referencia = "rentabilidad";
        opciones = new Options();
        OptionGroup primeraOpcion = new OptionGroup();
        Option o1 = new Option("h", "help", false, "Imprime la ayuda del comando");
        Option o2 = new Option("d", "depositos", false, "Muestra rentabilidad de los depósitos");
        Option o3 = new Option("a", "acciones", false, "Muestra rentabilidad de las acciones");
        Option o4 = new Option("t", "total", false, "Muestra rentabilidad de todos los activos");
        primeraOpcion.addOption(o1);
        primeraOpcion.addOption(o2);
        primeraOpcion.addOption(o3);
        primeraOpcion.addOption(o4);
        opciones.addOptionGroup(primeraOpcion);
    }

    @Override
    public String ejecutar(String[] args) {
        String resultado = "";
        CommandLineParser parser = new DefaultParser();
        try{
            CommandLine cmd = parser.parse(opciones, args);
            if(cmd.hasOption("h"))
                tipo = "help";
            else if(cmd.hasOption("d"))
                tipo = "deposito";
            else if(cmd.hasOption("a"))
                tipo = "accion";
            else if(cmd.hasOption("t"))
                tipo = "total";
            else
                throw new RuntimeException("La primera opción es obligatoria");
        } catch (ParseException ex) {
            throw new RuntimeException("Error en el parseo de los argumentos");
        }

        if(tipo.equals("help")){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("rentabilidad", opciones);
        }
        else {
            List<HashMap<String, String>> flujos = null;
            if(tipo.equals("deposito"))
                flujos = GestorDepoitos.getInstance().getFlujosCaja();
            if(tipo.equals("accion"))
                flujos = GestorAcciones.getInstance().getFlujosCaja();
            if(tipo.equals("total")){
                flujos = GestorDepoitos.getInstance().getFlujosCaja();
                flujos.addAll(GestorAcciones.getInstance().getFlujosCaja());
            }
            HashMap<String, Double> rentabilidad = Utils.calcularRentabilidad(flujos);
            DecimalFormat df = new DecimalFormat("#.00");
            double importeInicial = rentabilidad.get("importeInicial");
            double beneficio = rentabilidad.get("beneficio");
            resultado = "Has obtenido "+ df.format(beneficio) + "€ sobre los " + df.format(importeInicial) + "€ invertidos (" + df.format(beneficio/importeInicial*100) + "%)";
        }
        return resultado;
    }
}
