package Otros;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class SistemaStocks {

    // TODO HACER LA CONVERSIÓN SI EL PRECIO ESTÁ EXPRESADO EN UNA DIVISA QUE NO SEAN EUROS

    public static double getPrecio(String ticker) {
        try {
            // Ejecutar el script de Python
            Process proceso = Runtime.getRuntime().exec("python Precio.py getPrecio "+ticker);

            // Leer la salida del script de Python
            BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            String linea = reader.readLine();
            int exitCode = proceso.waitFor();
            String divisa = linea.split(",")[1];
            if(divisa.equals("EUR"))
                return Double.parseDouble(linea.split(",")[0]);
            else if(divisa.equals("USD"))
                return dolarAEuro(Double.parseDouble(linea.split(",")[0]));
            else throw new RuntimeException("Divisa no reconocida");
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
                String divisa = linea.split(",")[1];
                if(divisa.equals("EUR"))
                    resultado.put(tickers.get(i), Double.parseDouble(linea.split(",")[0]));
                else if(divisa.equals("USD"))
                    resultado.put(tickers.get(i), dolarAEuro(Double.parseDouble(linea.split(",")[0])));
                else throw new RuntimeException("Divisa no reconocida");
                i++;
            }
            int exitCode = proceso.waitFor();
            return resultado;
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException("Fallo con el sistema de stocks");
        }
    }

    /**
     * Hace un web scraping a la página de google que contiene el ratio de cambio
     * @param dollars cantidad en dólares que convertir a euros
     * @return euros a los que equivale dollars
     */
    private static double dolarAEuro(double dollars) {
        try {
            // Obtener el HTML de la URL
            String url = "https://www.google.com/search?q=dolar+a+euro";
            Document doc = Jsoup.connect(url).get();

            // Encontrar el elemento <span> deseado
            Elements spans = doc.select("span.DFlfde.SwHCTb");
            Element span = spans.get(0);
            double rate = Double.parseDouble(span.text().replace(',','.'));
            return dollars * rate;
        } catch (IOException e) {
            throw new RuntimeException("Error en la conversión de divisas");
        }
    }
}
