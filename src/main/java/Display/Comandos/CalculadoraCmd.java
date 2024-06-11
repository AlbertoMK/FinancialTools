package Display.Comandos;

import Display.Ventanas.VentanaEmergente;
import org.apache.commons.cli.*;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;

import javax.swing.*;
import java.awt.*;


public class CalculadoraCmd extends Comando {

    private Options opciones;
    private String tipo;
    private boolean grafico;

    public CalculadoraCmd() {
        referencia = "calculadora";
        opciones = new Options();
        OptionGroup grupo1 = new OptionGroup();
        grupo1.addOption(new Option("h", "help", false, "Imprime la ayuda del comando."));
        grupo1.addOption(new Option("ic", false, "Realiza cálculos de interés compuesto"));
        opciones.addOptionGroup(grupo1);
        opciones.addOption(new Option("g", "grafico", false, "Muestra un gráfico para facilitar la comprensión del resultado."));
    }

//    public static void main(String[] args) {
//        XYChart chart = new XYChartBuilder().width(800).height(600).title("Ejemplo de XChart").xAxisTitle("X").yAxisTitle("Y").build();
//        chart.addSeries("Seno", xData, yData);
//        SwingUtilities.invokeLater(() -> {
//            VentanaEmergente v = new VentanaEmergente(new XChartPanel<>(chart));
//        });
//    }

    @Override
    public String ejecutar(String[] args) {
        String resultado = "";
        String[] argumentos;

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(opciones, args);
            argumentos = cmd.getArgs();
            if (cmd.hasOption("h"))
                tipo = "help";
            else if (cmd.hasOption("ic")) {
                tipo = "interes compuesto";
                if (argumentos.length != 4)
                    throw new RuntimeException("Número de parámetros incorrecto.");
            } else
                throw new RuntimeException("Opción no reconocida.");
            grafico = cmd.hasOption("g");
        } catch (ParseException ex) {
            throw new RuntimeException("Error en el parseo de los argumentos.");
        }

        if (tipo.equals("help")) {
            HelpFormatter formatter = new HelpFormatter() {
                @Override
                public void printHelp(String cmdLineSyntax, Options options) {
                    String header = "Opciones disponibles:";
                    String footer = "\nEjemplos de uso:\n"
                              + "  calculadora -ic numPeriodos interés/periodo capitalInicial aportacion/periodo\n" +
                              "  calculadora -ic -g numPeriodos interés/periodo capitalInicial aportacion/periodo";
                    super.printHelp(cmdLineSyntax, header, options, footer, true);
                }
            };
            formatter.printHelp("calculadora", opciones);
        } else if (tipo.equals("interes compuesto")) {
            int numPeriodos;
            double interes, capitalInicial, aportacion;
            try {
                numPeriodos = Integer.parseInt(argumentos[0]);
                interes = Double.parseDouble(argumentos[1]) / 100;
                capitalInicial = Double.parseDouble(argumentos[2]);
                aportacion = Double.parseDouble(argumentos[3]);
            } catch (NumberFormatException ex) {
                throw new RuntimeException("Formato incorrecto de los argumentos.");
            }
            double valorPropio = capitalInicial + aportacion * numPeriodos;
            double valorFinal = capitalInicial * Math.pow(1 + interes, numPeriodos) + aportacion * (Math.pow(1 + interes, numPeriodos+1) - 1) / interes - aportacion;
            double interesGenerado = valorFinal - valorPropio;
            resultado = String.format("Al final de los %d periodos tu patrimonio tendrá un valor de %.2f€, de los cuales %.2f€ son aportados por ti y %.2f€ son de intereses.", numPeriodos, valorFinal, valorPropio, interesGenerado);

            if (grafico) {
                CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title("Evolución del interés compuesto").xAxisTitle("Periodo").yAxisTitle("Patrimonio").build();
                chart.getStyler().setOverlapped(true);
                chart.getStyler().setSeriesColors(new Color[]{new Color(0, 107, 18), new Color(52, 136, 255)});
                int[] xData = new int[numPeriodos];
                int[] yDataFinal = new int[numPeriodos];
                int[] yDataPropio = new int[numPeriodos];
                for (int i = 0; i < numPeriodos; i++) {
                    xData[i] = i + 1;
                    yDataFinal[i] = (int) (capitalInicial * Math.pow(1 + interes, i + 1) + aportacion * (Math.pow(1 + interes, i + 2) - 1) / interes - aportacion);
                    yDataPropio[i] = (int) (capitalInicial + aportacion * (i + 1));
                }
                chart.addSeries("Intereses", xData, yDataFinal);
                chart.addSeries("Aportaciones", xData, yDataPropio);
                SwingUtilities.invokeLater(() -> {
                    VentanaEmergente v = new VentanaEmergente(new XChartPanel<>(chart));
                });
            }
        }
        return resultado;
    }
}
