package Display.Comandos;

import Negocio.GestorAcciones;
import org.apache.commons.cli.*;

import java.text.DecimalFormat;
import java.util.*;

public class SectoresCmd extends Comando{

    private boolean help;
    Options opciones;

    public SectoresCmd(){
        referencia = "sectores";
        opciones = new Options();
        // TODO Se podría añadir una opción más para incluir depósitos en el cálculo y así saber cuanto pesa la renta fija de la cartera
        opciones.addOption(new Option("h", "help", false, "Muestra el peso en cada sector que tiene tu cartera."));
    }

    @Override
    public String ejecutar(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(opciones, args);
            if(cmd.hasOption("h"))
                help = true;
            else help = false;

            if(help) {
                HelpFormatter formatter = new HelpFormatter() {
                    @Override
                    public void printHelp(String cmdLineSyntax, Options options) {
                        String header = "Opciones disponibles:";
                        String footer = "\nEjemplos de uso:\n"
                                  + "  sectores";
                        super.printHelp(cmdLineSyntax, header, options, footer, true);
                    }
                };
                formatter.printHelp("sectores", opciones);
            }

            else {
                HashMap<String, Double> pesos = GestorAcciones.getInstance().getPorcentajeSectores();
                //ordenar el hashmap por peso
                List<Map.Entry<String, Double>> entryList = new ArrayList<>(pesos.entrySet());

                // Ordenar la lista de Map.Entry según los valores (en orden ascendente)
                Collections.sort(entryList, new Comparator<Map.Entry<String, Double>>() {
                    @Override
                    public int compare(Map.Entry<String, Double> entry1, Map.Entry<String, Double> entry2) {
                        // Comparar los valores de las entradas
                        return Double.compare(entry2.getValue(), entry1.getValue());
                    }
                });

                DecimalFormat df = new DecimalFormat("#.##");
                for (Map.Entry<String, Double> entry : entryList) {
                    System.out.println(entry.getKey() + "\t" + df.format(entry.getValue())+"%");
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return "";
    }
}
