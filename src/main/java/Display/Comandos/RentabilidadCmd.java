package Display.Comandos;

import Modelo.AccionETF;
import Modelo.Deposito;
import Negocio.GestorAcciones;
import Negocio.GestorDepoitos;
import Otros.SistemaStocks;
import Otros.Utils;
import org.apache.commons.cli.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class RentabilidadCmd extends Comando {

    Options opciones;
    boolean accion, deposito, help;
    Calendar periodo1, periodo2;
    boolean anualizada;

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
            }
            else if (cmd.hasOption("a")) {
                accion = true;
                deposito = false;
                help = false;
            }
            else if (cmd.hasOption("t")) {
                accion = deposito = true;
                help = false;
            }
            else
                throw new RuntimeException("La primera opción es obligatoria");

            anualizada = cmd.hasOption("y");

            String[] argumentos = cmd.getArgs();
            if (argumentos.length == 0) {
                periodo1 = periodo2 = null;
            } else if (argumentos.length == 2) {
                periodo1 = Utils.deserializarFecha(argumentos[0]);
                periodo2 = Utils.deserializarFecha(argumentos[1]);
            } else throw new RuntimeException("Cantidad de argumentos inválida");
        } catch (ParseException ex) {
            throw new RuntimeException("Error en el parseo de los argumentos");
        }

        if (help) {
            HelpFormatter formatter = new HelpFormatter() {
                @Override
                public void printHelp(String cmdLineSyntax, Options options) {
                    String header = "Opciones disponibles:";
                    String footer = "\nEjemplos de uso:\n"
                              + "  rentabilidad -a [-y] [fechaInicial fechaFinal]\n"
                              + "  rentabilidad -d [-y] [fechaInicial fechaFinal]\n"
                              + "  rentabilidad -t [-y] [fechaInicial fechaFinal]";
                    super.printHelp(cmdLineSyntax, header, options, footer, true);
                }
            };
            formatter.printHelp("rentabilidad", opciones);
        } else {
            List<HashMap<String, String>> flujos = new ArrayList<>();
            if (deposito)
                flujos.addAll(GestorDepoitos.getInstance().getFlujosCaja());
            if (accion)
                flujos.addAll(GestorAcciones.getInstance().getFlujosCaja());
            double importeInicial;
            double beneficio;
            double diasTranscurridos;
            if (periodo1 == null) {
                if(accion) {
                    for (HashMap<String, String> activo : GestorAcciones.getInstance().getActivos()) {
                        AccionETF accion = (AccionETF) GestorAcciones.getInstance().getActivoById(Integer.parseInt(activo.get("id")));
                        HashMap<String, String> flujo = new HashMap<>();
                        flujo.put("Fecha", Utils.serializarFechaEuropea(Calendar.getInstance()));
                        flujo.put("Flujo", String.valueOf(SistemaStocks.getPrecio(accion.getTicker()) * accion.getParticipacionesFecha(Calendar.getInstance())));
                        flujos.add(flujo);
                    }
                }
                if(deposito) {
                    for (HashMap<String, String> activo : GestorDepoitos.getInstance().getActivos()) {
                        Deposito deposito = (Deposito) GestorDepoitos.getInstance().getActivoById(Integer.parseInt(activo.get("id")));
                        if(!deposito.estaVendido()) {
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

    // Devuelve los flujos con los valores de las acciones en la fecha indicada
    private List<HashMap<String, String>> getFlujosAccionesFecha(Calendar fecha) {
        List<HashMap<String, String>> resultado = new ArrayList<>();
        for (HashMap<String, String> activo : GestorAcciones.getInstance().getActivos()) {
            AccionETF accion = (AccionETF) GestorAcciones.getInstance().getActivoById(Integer.parseInt(activo.get("id")));
            double valorAccion = SistemaStocks.getPrecioFecha(accion.getTicker(), fecha);
            HashMap<String, String> flujo = new HashMap<>();
            flujo.put("Fecha", Utils.serializarFechaEuropea(fecha));
            flujo.put("Flujo", String.valueOf(accion.getParticipacionesFecha(fecha) * valorAccion));
            resultado.add(flujo);
        }
        return resultado;
    }

    private List<HashMap<String, String>> getFlujosDepositosFecha(Calendar fecha) {
        List<HashMap<String, String>> resultado = new ArrayList<>();
        for (HashMap<String, String> activo : GestorDepoitos.getInstance().getActivos()) {
            Deposito deposito = (Deposito) GestorDepoitos.getInstance().getActivoById(Integer.parseInt(activo.get("id")));
            double valorAcumulado = deposito.getImporteAcumulado(fecha);
            double valorNominalFecha;
            if(deposito.getFechaContratacion().after(fecha) || (deposito.estaVendido() && deposito.getVenta().getFecha().before(fecha)))
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
}
