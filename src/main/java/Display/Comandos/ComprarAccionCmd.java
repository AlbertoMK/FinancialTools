package Display.Comandos;

import Display.Display;
import Negocio.GestorAcciones;
import Otros.Utils;
import org.apache.commons.cli.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ComprarAccionCmd extends Comando {

    Options opciones;
    boolean help = false;
    private int idAccion;
    private String[] argumentos;

    public ComprarAccionCmd() {
        referencia = "comprarAccion";
        opciones = new Options();
        opciones.addOption(new Option("h", "help", false, "Imprime la ayuda del comando"));
    }

    @Override
    public String ejecutar(String[] args) {
        String resultado = "";

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(opciones, args);
            if (cmd.hasOption("h")) {
                help = true;
            } else {
                help = false;
            }
            argumentos = cmd.getArgs();
        } catch (ParseException e) {
            throw new RuntimeException("Error en el parseo de los argumentos");
        }

        if (!help && argumentos.length != 4) {
            throw new RuntimeException("Número de parámetros incorrecto");
        }
        if(!help) {
            try {
                Double.parseDouble(argumentos[0]);
                Double.parseDouble(argumentos[1]);
                Utils.deserializarFecha(argumentos[2]);
                Double.parseDouble(argumentos[3]);
            } catch (RuntimeException ex) {
                throw new RuntimeException("Formato incorrecto de los argumentos.");
            }

            try {
                List<HashMap<String, String>> activos = GestorAcciones.getInstance().getActivos();
                int opcion = Display.selector(activos);
                idAccion = Integer.parseInt(activos.get(opcion).get("id"));

            } catch (NumberFormatException ex) {
                throw new RuntimeException("Error en la opción escogida");
            }
        }

        if (help) {
            HelpFormatter formatter = new HelpFormatter() {
                @Override
                public void printHelp(String cmdLineSyntax, Options options) {
                    String header = "Opciones disponibles:";
                    String footer = "\nEjemplos de uso:\n"
                              + "comprarAccion participaciones precio fecha comision";
                    super.printHelp(cmdLineSyntax, header, options, footer, true);
                }
            };
            formatter.printHelp("comprarAccion", opciones);
        }
        else {
            double participaciones = Double.parseDouble(argumentos[0]);
            double precio = Double.parseDouble(argumentos[1]);
            Calendar fecha = Utils.deserializarFecha(argumentos[2]);
            double comision = Double.parseDouble(argumentos[3]);
            GestorAcciones.getInstance().comprarAccion(idAccion, participaciones, precio, fecha, comision);
            resultado = "Compra realizada con éxito.";
        }
        return resultado;
    }
}
