package Otros;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SistemaStocks {

    private static String getDivisa(String ticker) {
        String url = "https://es.finance.yahoo.com/quote/"+ticker;
        try {
            Document doc = Jsoup.connect(url).get();
            String[] arr = doc.select("div#quote-header-info > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(2) > span").first().text().split(" ");
            return arr[arr.length - 1];
        } catch (IOException e) {
            throw new RuntimeException("Fallo con el sistema de stocks");
        }
    }

    public static double getPrecio(String ticker) {
        String url = "https://es.finance.yahoo.com/quote/"+ticker;
        try {
            Document doc = Jsoup.connect(url).get();
            Element container = doc.select("div#quote-header-info").first();
            Element container2 = container.select("fin-streamer[data-field=regularMarketPrice]").first();
            double precio =  Double.parseDouble(container2.text().replace(".","").replace(",","."));
            String divisa = getDivisa(ticker);
            if(divisa.equals("EUR"))
                return precio;
            else if(divisa.equals("USD"))
                return dolarAEuro(precio);
            else throw new RuntimeException("Divisa no reconocida");
        } catch (IOException e) {
            throw new RuntimeException("Fallo con el sistema de stocks");
        }
    }

    // TODO devuelve el precio del activo en la fecha indicada
    public static double getPrecioFecha(String ticker, Calendar fecha) {
        // acceder al activo, tabla.
        // calcular el día al que hay que acceder, si cae en sabado o domingo acceder al dato del viernes anterior
        // especificar como periodo 1 = fecha, periodo 2 = día siguiente
        // acceder al único registro que mostrará la tabla.
        return 0;
    }

    public static String getTipoAccionETF(String ticker) {
        try {
            // Ejecutar el script de Python
            Process proceso = Runtime.getRuntime().exec("python Precio.py getTipo " + ticker);
            // Leer la salida del script de Python
            BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Fallo con el sistema de stocks");
        }
    }

    // Devuelve el sector en el que compite una empresa. No llamar al método con tickers que no sean de empresas.
    public static String getSectorAccion(String ticker) {
        try {
            // Ejecutar el script de Python
            Process proceso = Runtime.getRuntime().exec("python Precio.py getSector " + ticker);
            // Leer la salida del script de Python
            BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            return reader.readLine();
        } catch (IOException e) {
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
