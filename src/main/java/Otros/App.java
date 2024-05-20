package Otros;

import java.util.ArrayList;
import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        /*
        System.out.println(Otros.SistemaStocks.getPrecio("AAPL"));
        System.out.println(Otros.SistemaStocks.getPrecio("MSFT"));
        System.out.println(Otros.SistemaStocks.getPrecio("SBUX"));
        System.out.println(Otros.SistemaStocks.getPrecio("TSLA"));
        System.out.println(Otros.SistemaStocks.getPrecio("V"));
         */

        ArrayList<String> tickers = new ArrayList<>();
        tickers.add("AAPL");
        tickers.add("MSFT");
        tickers.add("SBUX");
        tickers.add("TSLA");
        tickers.add("V");
        HashMap<String, Double> resultado = SistemaStocks.getPrecios(tickers);
        System.out.println(resultado);
    }
}
