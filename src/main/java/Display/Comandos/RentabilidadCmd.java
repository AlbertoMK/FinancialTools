package Display.Comandos;

import Modelo.AccionETF;
import Modelo.Deposito;
import Negocio.GestorAcciones;
import Negocio.GestorActivos;
import Negocio.GestorDepoitos;
import Otros.SistemaStocks;
import Otros.Utils;
import org.apache.commons.cli.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RentabilidadCmd extends Comando {

    Options opciones;
    boolean accion, deposito, help;
    Calendar periodo1, periodo2;
    boolean anualizada, eliminarRegalos;

    public RentabilidadCmd() {
        referencia = "rentabilidad";
        opciones = new Options();
        Option o1 = new Option("h", "help", false, "Imprime la ayuda del comando");
        opciones.addOption(o1);
        OptionGroup tipoActivo = new OptionGroup();
        Option o2 = new Option("d", "depositos", false, "Muestra rentabilidad de los depósitos");
        Option o3 = new Option("a", "acciones", false, "Muestra rentabilidad de las acciones");
        Option o4 = new Option("t", "total", false, "Muestra rentabilidad de todos los activos");
        tipoActivo.addOption(o2);
        tipoActivo.addOption(o3);
        tipoActivo.addOption(o4);
        opciones.addOptionGroup(tipoActivo);

        OptionGroup temporalidad = new OptionGroup();
        temporalidad.addOption(new Option("ytd", "año", false, "Calcula la rentabilidad desde el inicio del año actual"));
        temporalidad.addOption(new Option("m", "mes", false, "Calcula la rentabilidad de los últimos 30 días"));
        temporalidad.addOption(new Option("w", "semana", false, "Calcula la rentabilidad de los últimos 7 días"));
        temporalidad.addOption(new Option("day", false, "Calcula la rentabilidad obtenida hoy"));
        opciones.addOptionGroup(temporalidad);

        opciones.addOption(new Option("r", "eliminar regalos", false, "No tiene en cuenta los regalos en las acciones y fondos indexados para el cálculo"));
        opciones.addOption(new Option("y", "anualizada", false, "Calcula la rentabilidad anualizada del periodo indicado"));
    }

    @Override
    public String ejecutar(String[] args) {
        String resultado = "";
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(opciones, args);
            if (cmd.hasOption("h"))
                help = true;
            else if (cmd.hasOption("d")) {
                deposito = true;
                accion = false;
                help = false;
            } else if (cmd.hasOption("a")) {
                accion = true;
                deposito = false;
                help = false;
            } else if (cmd.hasOption("t")) {
                accion = deposito = true;
                help = false;
            } else
                throw new RuntimeException("La primera opción es obligatoria");

            anualizada = cmd.hasOption("y");
            eliminarRegalos = cmd.hasOption("r");
            establecerPeriodos(cmd);
        } catch (ParseException ex) {
            throw new RuntimeException("Error en el parseo de los argumentos");
        }

        if (help) {
            HelpFormatter formatter = new HelpFormatter() {
                @Override
                public void printHelp(String cmdLineSyntax, Options options) {
                    String header = "Opciones disponibles:";
                    String footer = "\nEjemplos de uso:\n"
                              + "  rentabilidad -a [-y] [fechaInicial fechaFinal | -day | -w | -m | -ytd]\n"
                              + "  rentabilidad -d [-y] [fechaInicial fechaFinal | -day | -w | -m | -ytd]\n"
                              + "  rentabilidad -t [-y] [fechaInicial fechaFinal | -day | -w | -m | -ytd]";
                    super.printHelp(cmdLineSyntax, header, options, footer, true);
                }
            };
            formatter.printHelp("rentabilidad", opciones);
        }

        else {
            List<HashMap<String, String>> flujos = new ArrayList<>();
            if (deposito)
                flujos.addAll(GestorDepoitos.getInstance().getFlujosCaja(eliminarRegalos));
            if (accion)
                flujos.addAll(GestorAcciones.getInstance().getFlujosCaja(eliminarRegalos));

            double importeInicial;
            double beneficio;
            double diasTranscurridos;
            if (periodo1 == null) {
                if (accion) {
                    HashMap<Integer, Double> valorAcciones = GestorAcciones.getInstance().getImportesActuales();
                    for (Integer id : valorAcciones.keySet()) {
                        HashMap<String, String> flujo = new HashMap<>();
                        flujo.put("Fecha", Utils.serializarFechaEuropea(Calendar.getInstance()));
                        flujo.put("Flujo", String.valueOf(valorAcciones.get(id)));
                        flujos.add(flujo);
                    }
                }
                if (deposito) {
                    for (HashMap<String, String> activo : GestorDepoitos.getInstance().getActivos()) {
                        Deposito deposito = (Deposito) GestorDepoitos.getInstance().getActivoById(Integer.parseInt(activo.get("id")));
                        if (!deposito.estaVendido()) {
                            HashMap<String, String> flujo = new HashMap<>();
                            flujo.put("Fecha", Utils.serializarFechaEuropea(Calendar.getInstance()));
                            flujo.put("Flujo", String.valueOf(deposito.getImporteActual()));
                            flujos.add(flujo);
                        }
                    }
                }
                HashMap<String, Double> rentabilidad = Utils.calcularRentabilidad(flujos);
                importeInicial = rentabilidad.get("importeInicial");
                beneficio = rentabilidad.get("beneficio");
                diasTranscurridos = rentabilidad.get("duracion");
            } else {
                List<HashMap<String, String>> flujosAnterioresPeriodo1 = flujos.stream()
                          .filter(n -> Utils.deserializarFecha(n.get("Fecha")).before(periodo1) || Utils.deserializarFecha(n.get("Fecha")).equals(periodo1))
                          .collect(Collectors.toList());
                List<HashMap<String, String>> flujosAnterioresPeriodo2 = flujos.stream()
                          .filter(n -> Utils.deserializarFecha(n.get("Fecha")).before(periodo2) || Utils.deserializarFecha(n.get("Fecha")).equals(periodo2))
                          .collect(Collectors.toList());

                if (accion) {
                    flujosAnterioresPeriodo1.addAll(getFlujosAccionesFecha(periodo1));
                    flujosAnterioresPeriodo2.addAll(getFlujosAccionesFecha(periodo2));
                }
                if (deposito) {
                    flujosAnterioresPeriodo1.addAll(getFlujosDepositosFecha(periodo1));
                    flujosAnterioresPeriodo2.addAll(getFlujosDepositosFecha(periodo2));
                }

                HashMap<String, Double> rentabilidadAnterior = Utils.calcularRentabilidad(flujosAnterioresPeriodo1);
                double beneficio1 = rentabilidadAnterior.get("beneficio");
                HashMap<String, Double> rentabilidadPosterior = Utils.calcularRentabilidad(flujosAnterioresPeriodo2);
                double beneficio2 = rentabilidadPosterior.get("beneficio");
                importeInicial = rentabilidadPosterior.get("importeInicial");
                beneficio = beneficio2 - beneficio1;
                diasTranscurridos = (periodo2.getTimeInMillis() - periodo1.getTimeInMillis()) / (24 * 60 * 60 * 1000);
            }

            DecimalFormat df = new DecimalFormat("#.00");
            resultado = "Has obtenido " + df.format(beneficio) + "€ sobre los " + df.format(importeInicial) + "€ invertidos (";
            if (anualizada)
                resultado = resultado + df.format(beneficio / importeInicial * 100 * 365 / diasTranscurridos) + "% anualizado)";
            else resultado = resultado + df.format(beneficio / importeInicial * 100) + "%)";
        }
        return resultado;
    }

    private void establecerPeriodos(CommandLine cmd) {
        String[] argumentos = cmd.getArgs();
        if (cmd.hasOption("-day")) {
            periodo1 = Calendar.getInstance();
            periodo1.add(Calendar.DAY_OF_MONTH, -1);
            periodo2 = Calendar.getInstance();
        } else if (cmd.hasOption("-w")) {
            periodo1 = Calendar.getInstance();
            periodo1.add(Calendar.DAY_OF_MONTH, -7);
            periodo2 = Calendar.getInstance();
        } else if (cmd.hasOption("-m")) {
            periodo1 = Calendar.getInstance();
            periodo1.add(Calendar.MONTH, -1);
            periodo2 = Calendar.getInstance();
        } else if (cmd.hasOption("-ytd")) {
            periodo1 = Calendar.getInstance();
            periodo1.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.JANUARY, 1);
            periodo2 = Calendar.getInstance();
        } else if (argumentos.length == 0) {
            periodo1 = periodo2 = null;
        } else if (argumentos.length == 2) {
            periodo1 = Utils.deserializarFecha(argumentos[0]);
            periodo2 = Utils.deserializarFecha(argumentos[1]);
        } else throw new RuntimeException("Cantidad de argumentos inválida");
    }

    private List<HashMap<String, String>> getFlujosDepositosFecha(Calendar fecha) {
        List<HashMap<String, String>> resultado = new ArrayList<>();
        for (HashMap<String, String> activo : GestorDepoitos.getInstance().getActivos()) {
            Deposito deposito = (Deposito) GestorDepoitos.getInstance().getActivoById(Integer.parseInt(activo.get("id")));
            double valorAcumulado = deposito.getImporteAcumulado(fecha);
            double valorNominalFecha;
            if (deposito.getFechaContratacion().after(fecha) || (deposito.estaVendido() && deposito.getVenta().getFecha().before(fecha)))
                valorNominalFecha = 0;
            else
                valorNominalFecha = deposito.getDesembolso();
            HashMap<String, String> flujo = new HashMap<>();
            flujo.put("Fecha", Utils.serializarFechaEuropea(fecha));
            flujo.put("Flujo", String.valueOf(valorAcumulado + valorNominalFecha));
            resultado.add(flujo);
        }
        return resultado;
    }

    // Devuelve los flujos con los valores de las acciones en la fecha indicada
    // Accede a los precios de manera concurrente para dividir entre 4 el tiempo de ejecución
    private static List<HashMap<String, String>> getFlujosAccionesFecha(Calendar fecha) {
        List<HashMap<String, String>> resultado = new ArrayList<>();
        HashMap<Integer, Double> valorAcciones = GestorAcciones.getInstance().getImportesFecha(fecha);
        for (int id : valorAcciones.keySet()) {
            double participaciones = GestorAcciones.getInstance().getParticipacionesFecha(id, fecha);
            HashMap<String, String> flujo = new HashMap<>();
            flujo.put("Fecha", Utils.serializarFechaEuropea(fecha));
            flujo.put("Flujo", String.valueOf(participaciones * valorAcciones.get(id)));
            resultado.add(flujo);
        }
        return resultado;
    }
}

