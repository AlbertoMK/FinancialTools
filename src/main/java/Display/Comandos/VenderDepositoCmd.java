package Display.Comandos;

import Display.Display;
import Negocio.GestorDepoitos;
import Otros.Utils;
import org.apache.commons.cli.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class VenderDepositoCmd extends Comando{

    private Options opciones;
    private boolean help;
    private int idDeposito;
    private String[] argumentos;

    public VenderDepositoCmd() {
        referencia = "venderDeposito";
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

        if(!help && argumentos.length != 3) {
            throw new RuntimeException("Número de parámetros incorrecto");
        }
        if(!help) {
            try {
                Utils.deserializarFecha(argumentos[0]);
                Double.parseDouble(argumentos[1]);
                Double.parseDouble(argumentos[2]);
            } catch (RuntimeException ex) {
                throw new RuntimeException("Formato incorrecto de los argumentos.");
            }

            try{
                List<HashMap<String, String>> activos = GestorDepoitos.getInstance().getActivos();
                // filtra de la lista aquellos depósitos que no estén vendidos
                activos = activos.stream()
                          .filter(n -> n.get("vendido").equals("false"))
                          .collect(Collectors.toList());
                int opcion = Display.selector(activos);
                idDeposito = Integer.parseInt(activos.get(opcion).get("id"));
            } catch (NumberFormatException ex) {
                throw new RuntimeException("Error en la opción escogida");
            }
        }

        if(help) {
            HelpFormatter formatter = new HelpFormatter() {
                @Override
                public void printHelp(String cmdLineSyntax, Options options) {
                    String header = "Opciones disponibles:";
                    String footer = "\nEjemplos de uso:\n"
                              + "venderDeposito fecha importeVenta comision";
                    super.printHelp(cmdLineSyntax, header, options, footer, true);
                }
            };
            formatter.printHelp("venderDeposito", opciones);
        }
        else {
            Calendar fecha = Utils.deserializarFecha(argumentos[0]);
            double importeVenta = Double.parseDouble(argumentos[1]);
            double comision = Double.parseDouble(argumentos[2]);
            GestorDepoitos.getInstance().venderDeposito(idDeposito, fecha, importeVenta, comision);
            resultado = "Venta realizada con éxito";
        }
        return resultado;
    }
}
