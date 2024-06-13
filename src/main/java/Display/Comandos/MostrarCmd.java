package Display.Comandos;
import Modelo.AccionETF;
import Modelo.Activo;
import Modelo.Deposito;
import Negocio.GestorAcciones;
import Negocio.GestorDepoitos;
import Otros.Utils;
import de.vandermeer.asciitable.CWC_FixedWidth;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import org.apache.commons.cli.*;
import de.vandermeer.asciitable.AsciiTable;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MostrarCmd extends Comando{

    private boolean help;
    Options opciones;

    public MostrarCmd() {
        referencia = "mostrar";
        opciones = new Options();
        opciones.addOption(new Option("h", "help", false, "Muestra un resumen de todos los activos."));
    }

    @Override
    public String ejecutar(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(opciones, args);
            if (cmd.hasOption("h"))
                help = true;
            else help = false;

            if (help) {
                HelpFormatter formatter = new HelpFormatter() {
                    @Override
                    public void printHelp(String cmdLineSyntax, Options options) {
                        String header = "Opciones disponibles:";
                        String footer = "\nEjemplos de uso:\n"
                                  + "  mostrar";
                        super.printHelp(cmdLineSyntax, header, options, footer, true);
                    }
                };
                formatter.printHelp("mostrar", opciones);
            }

            else {
                AsciiTable at = new AsciiTable();
                at.addRule();
                at.addRow(null, null, null, "Acciones").setTextAlignment(TextAlignment.CENTER);
                at.addRule();
                at.addRow("Nombre", "Ticker", "Participaciones", "Valor");
                at.addRule();

                HashMap<AccionETF, CompletableFuture<Double>> valorAcciones = new HashMap<>();
                for (HashMap<String, String> activo : GestorAcciones.getInstance().getActivos()) {
                    AccionETF accion = (AccionETF) GestorAcciones.getInstance().getActivoById(Integer.parseInt(activo.get("id")));
                    valorAcciones.put(accion, CompletableFuture.supplyAsync(() -> {
                        return accion.getImporteActual();
                    }));
                }
                CompletableFuture<Void> allOf = CompletableFuture.allOf(valorAcciones.values().toArray(new CompletableFuture[0]));
                allOf.join();

                for (AccionETF accion : valorAcciones.keySet()) {
                    try {
                        at.addRow(accion.getNombre(), accion.getTicker(), String.format("%.6f", accion.getParticipacionesFecha(Calendar.getInstance())), String.format("%.3f", valorAcciones.get(accion).get()));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }

                at.addRule();
                at.getRenderer().setCWC(new CWC_FixedWidth().add(20).add(10).add(15).add(15));
                String tableText = at.render();
                System.out.println(tableText);

                AsciiTable at2 = new AsciiTable();
                at2.addRule();
                at2.addRow(null, null, null, null, null, "Depósitos").setTextAlignment(TextAlignment.CENTER); // título
                at2.addRule();
                at2.addRow("Nombre", "Desembolso", "TAE", "Fecha de contratación", "Valor", "Vendido");
                at2.addRule();
                for (HashMap<String, String> activo : GestorDepoitos.getInstance().getActivos()) {
                    Deposito deposito = (Deposito) GestorDepoitos.getInstance().getActivoById(Integer.parseInt(activo.get("id")));
                    at2.addRow(deposito.getNombre(), deposito.getDesembolso(), deposito.getTAE(), Utils.serializarFechaEuropea(deposito.getFechaContratacion()), String.format("%.3f", deposito.getImporteActual()), deposito.estaVendido() ? "Sí" : "No");
                }
                at2.addRule();
                at2.getRenderer().setCWC(new CWC_FixedWidth().add(30).add(15).add(15).add(15).add(15).add(15));
                String tableText2 = at2.render();
                System.out.println(tableText2);
            }
        } catch (ParseException ex) {
            throw new RuntimeException("Error con el parseo de las opciones");
        }
        return "";
    }
}
