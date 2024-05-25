package Display.Comandos;

import Modelo.Deposito;
import Negocio.GestorAcciones;
import Negocio.GestorDepoitos;
import Otros.Utils;
import org.apache.commons.cli.*;

import java.util.Calendar;

public class CrearCmd extends Comando {

    private String modelo;
    private String[] argumentos;
    Options opciones;

    public CrearCmd() {
        referencia = "crear";
        opciones = new Options();

        OptionGroup opcionModelo = new OptionGroup();
        opcionModelo.addOption(new Option("a", "accion", false, "Crear una accion"));
        opcionModelo.addOption(new Option("d", "deposito", false, "Crear un depósito"));
        opcionModelo.addOption(new Option("h", "help", false, "Imprime la ayuda del comando"));
        opciones.addOptionGroup(opcionModelo);
    }

    @Override
    public String ejecutar(String[] args) {

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(opciones, args);

            if (cmd.hasOption("a")) {
                modelo = "accion";
            } else if (cmd.hasOption("d")) {
                modelo = "deposito";
            } else if (cmd.hasOption("h")) {
                modelo = "help";
            } else {
                throw new ParseException("Debe especificar el tipo: -a para acciones o -d para depósitos.");
            }

            // Comprobación de los argumentos
            argumentos = cmd.getArgs();
            try {
                switch (modelo) {
                    case "accion":
                        if (argumentos.length != 2)
                            throw new RuntimeException("Número de parámetros incorrecto.");
                        break;
                    case "deposito":
                        if (argumentos.length != 5)
                            throw new RuntimeException("Número de parámetros incorrecto.");
                        Double.parseDouble(argumentos[1]);
                        Double.parseDouble(argumentos[2]);
                        Double.parseDouble(argumentos[4]);
                        Utils.deserializarFecha(argumentos[3]);
                        break;
                }
            } catch (RuntimeException ex) {
                throw new RuntimeException("Formato incorrecto de los argumentos.");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }

        String resultado = "";
        String nombre;
        switch(modelo) {
            case "help":
                HelpFormatter formatter = new HelpFormatter() {
                    @Override
                    public void printHelp(String cmdLineSyntax, Options options) {
                        String header = "Opciones disponibles:";
                        String footer = "\nEjemplos de uso:\n"
                                  + "  CrearCmd -a nombre ticker\n"
                                  + "  CrearCmd -d nombre desembolso tae fechaContratacion comisionCompra";
                        super.printHelp(cmdLineSyntax, header, options, footer, true);
                    }
                };
                formatter.printHelp("CrearCmd", opciones);
                break;
            case "deposito":
                nombre = argumentos[0];
                double desembolso = Double.parseDouble(argumentos[1]);
                double tae = Double.parseDouble(argumentos[2]);
                Calendar fechaContratacion = Utils.deserializarFecha(argumentos[3]);
                double comisionCompra = Double.parseDouble(argumentos[4]);
                GestorDepoitos.getInstance().crearDeposito(nombre, desembolso, tae, fechaContratacion, comisionCompra);
                resultado =  "Depósito creado con éxito.";
                break;
            case "accion":
                nombre = argumentos[0];
                String ticker = argumentos[1];
                GestorAcciones.getInstance().crearAccion(nombre, ticker);
                resultado = "Acción creada con éxito";
                break;
        }
        return resultado;
    }
}
