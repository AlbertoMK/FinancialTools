package Otros;

import Negocio.GestorAcciones;
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
import java.util.TimeZone;

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

    /**
     * Este método sigue los siguientes pasos:
     * 1. Si la fecha es de fin de semana, calcula la fecha del viernes anterior ya que el mercado está cerrado en fin de semana y no hay datos.
     * 2. Formatea la fecha a zona horaria UTC y en formato segundos para cumplir con el formato de Yahoo Finance
     * 3. Calcula la fecha del día siguiente para dar el intervalo a la web
     * 4. Calcula las fechas en segundos para cumplir con el formato de Yahoo Finance
     * 5. Accede a la url, busca el dato que interesa y lo devuelve.
     * @param ticker Ticker del activo del cual obtener el precio
     * @param fecha Fecha para la cual se quiere calcular el precio
     * @return Precio del activo indicado en la fecha indicada
     */
    public static double getPrecioFecha(String ticker, Calendar fecha) {
        Calendar hoy = Calendar.getInstance();
        if(fecha.get(Calendar.DAY_OF_MONTH) == hoy.get(Calendar.DAY_OF_MONTH) && fecha.get(Calendar.MONTH) == hoy.get(Calendar.MONTH) && fecha.get(Calendar.YEAR) == hoy.get(Calendar.YEAR))
            return getPrecio(ticker);
        long sec1, sec2;
        if (Persistencia.getTipo(GestorAcciones.getInstance().getAccionByTicker(ticker).getId()).equals("CRYPTOCURRENCY")){
            Calendar fechaUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            fechaUTC.set(fecha.get(Calendar.YEAR), fecha.get(Calendar.MONTH), fecha.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            sec1 = sec2 = fechaUTC.getTimeInMillis()/1000;
        } else {
            int diaSemana = fecha.get(Calendar.DAY_OF_WEEK);
            if(diaSemana == Calendar.SATURDAY)
                fecha.add(Calendar.DAY_OF_MONTH, -1);
            else if (diaSemana == Calendar.SUNDAY)
                fecha.add(Calendar.DAY_OF_MONTH, -2);
            Calendar fechaUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            fechaUTC.set(fecha.get(Calendar.YEAR), fecha.get(Calendar.MONTH), fecha.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            Calendar diaSiguiente = (Calendar) fechaUTC.clone();
            diaSiguiente.add(Calendar.DAY_OF_MONTH, 1);
            sec1 = fechaUTC.getTimeInMillis()/1000;
            sec2 = diaSiguiente.getTimeInMillis()/1000;
        }
        try {
            boolean registroEncontrado = false;
            double precio = 0;
            while(!registroEncontrado){
                String url = String.format("https://es.finance.yahoo.com/quote/%s/history/?period1=%d&period2=%d&interval=1d&frequency=1d", ticker, sec1, sec2);
                Document doc = Jsoup.connect(url).get();
                Element tabla = doc.select("table[data-test=historical-prices]").first();
                String precioS = tabla.select("tbody > tr > td:nth-of-type(5) > span").text().replace(".","").replace(",",".");
                if(precioS.isEmpty()){
                    sec2 = sec1;
                    sec1 -= (24*60*60);
                }
                else {
                    precio = Double.parseDouble(precioS);
                    registroEncontrado = true;
                }
            }
            String divisa = getDivisa(ticker);
            if(divisa.equals("EUR"))
                return precio;
            else if(divisa.equals("USD"))
                return dolarAEuro(precio);
            else throw new RuntimeException("Divisa no reconocida");
        } catch (IOException e) {
            throw new RuntimeException("Error con el sistema de stocks.");
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
