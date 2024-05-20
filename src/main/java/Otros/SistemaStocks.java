package Otros;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class SistemaStocks {

    public static double getPrecio(String ticker) {
        try {
            // Ejecutar el script de Python
            Process proceso = Runtime.getRuntime().exec("python Precio.py getPrecio "+ticker);

            // Leer la salida del script de Python
            BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            String linea;
            linea = reader.readLine();
            int exitCode = proceso.waitFor();
            return Double.parseDouble(linea);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException("Fallo con el sistema de stocks");
        }
    }

    public static HashMap<String, Double> getPrecios(List<String> tickers){
        HashMap<String, Double> resultado = new HashMap<>();
        StringBuffer  sb= new StringBuffer();
        for(String ticker: tickers) {
            sb.append(ticker + ",");
        }
        String parametro = sb.toString().substring(0,sb.length()-1);

        try {
            // Ejecutar el script de Python
            Process proceso = Runtime.getRuntime().exec("python Precio.py getPrecios "+parametro);

            // Leer la salida del script de Python
            BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            String linea;
            int i = 0;
            while ((linea = reader.readLine()) != null) {
                System.out.println(linea);
                resultado.put(tickers.get(i), Double.parseDouble(linea));
                i++;
            }
            int exitCode = proceso.waitFor();
            return resultado;
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException("Fallo con el sistema de stocks");
        }
    }
}
