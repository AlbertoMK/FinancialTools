package Modelo;

import Otros.Persistencia;
import Otros.SistemaStocks;
import Otros.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AccionETF extends Activo{

    private String ticker;
    private List<CompraVentaAccionETF> compraventas;

    public AccionETF(int id, String nombre, String ticker) {
        super(nombre, id);
        this.ticker = ticker;
        compraventas = new ArrayList<>();
        Persistencia.guardarAccion(id, nombre, ticker);
    }

    public AccionETF(int id, String nombre, String ticker, List<CompraVentaAccionETF> compraventas) {
        super(nombre, id);
        this.ticker = ticker;
        this.compraventas = compraventas;
    }

    public void comprar(double participaciones, double precio, Calendar fecha, double comision){
        CompraVentaAccionETF compra = new CompraVentaAccionETF(participaciones, precio, comision, fecha, true);
        compraventas.add(compra);
        Persistencia.comprarVenderAccion(getId(), participaciones, precio, fecha, comision, true);
    }

    public void vender(double participaciones, double precio, Calendar fecha, double comision){
        CompraVentaAccionETF venta = new CompraVentaAccionETF(participaciones, precio, comision, fecha, false);
            compraventas.add(venta);
        Persistencia.comprarVenderAccion(getId(), participaciones, precio, fecha, comision, false);
    }

    @Override
    public double getImporteInicial() {
        double resultado = 0;
        for (CompraVentaAccionETF compraventa : compraventas){
            if (compraventa.esCompra())
                resultado += (compraventa.getParticipaciones() * compraventa.getPrecio());
        }
        return resultado;
    }

    @Override
    public double getImporteActual() {
        double valorParticipacion = SistemaStocks.getPrecio(ticker);
        double comisiones = 0;
        double participacionesTotales = 0;
        double importeVendido = 0;
        for (CompraVentaAccionETF compraVenta : compraventas) {
            comisiones += compraVenta.getComision();
            if (compraVenta.esCompra())
                participacionesTotales += compraVenta.getParticipaciones();
            else
                importeVendido += compraVenta.getParticipaciones() * compraVenta.getPrecio();
        }
        return importeVendido + participacionesTotales * valorParticipacion - comisiones;
    }

    @Override
    public HashMap<String, String> getJSON() {
        HashMap<String, String> resultado = new HashMap<>();
        resultado.put("id", String.valueOf(getId()));
        resultado.put("nombre", String.valueOf(getNombre()));
        resultado.put("ticker", String.valueOf(ticker));
        return resultado;
    }

    @Override
    public List getFlujosCaja() {
        List<HashMap<String, String>> resultado = new ArrayList<>();
        for (CompraVentaAccionETF compraventa : compraventas) {
            HashMap<String, String> flujo = new HashMap<>();
            flujo.put("Fecha", Utils.serializarFechaEuropea(compraventa.getFecha()));
            double cantidad = compraventa.getPrecio() * compraventa.getParticipaciones();
            if (compraventa.esCompra())
                cantidad *= -1;
            flujo.put("Flujo", String.valueOf(cantidad));
            resultado.add(flujo);
        }
        HashMap<String, String> flujo = new HashMap<>();
        flujo.put("Fecha", Utils.serializarFechaEuropea(Calendar.getInstance()));
        flujo.put("Flujo", String.valueOf(getImporteActual()));
        resultado.add(flujo);
        return resultado;
    }

    /**
     *
     * @return HashMap con los sectores como clave y el porcentaje de peso como valor
     */
    public HashMap<String, Double> getPorcentajeSectores() {
        String tipo = SistemaStocks.getTipoAccionETF(ticker);
        HashMap<String, Double> resultado = new HashMap<>();
        switch (tipo) {
            case "ETF":
                String url = "https://finance.yahoo.com/quote/"+ticker+"/holdings";
                try {
                    Document doc = Jsoup.connect(url).get();
                    Element containerSection = doc.select("section[data-testid=etf-sector-weightings-overview]").first();
                    Element container = containerSection.select(".container").first();
                    Elements innerDivs = container.select("div.content");
                    for (Element e : innerDivs) {
                        String sector = e.select("a").first().text();
                        String porcentajeS = e.select("span.data").first().text();
                        double porcentaje = Double.valueOf(porcentajeS.substring(0, porcentajeS.length() - 1));
                        resultado.put(sector, porcentaje);
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "EQUITY":
                resultado.put(SistemaStocks.getSectorAccion(ticker), 100.0);
                break;
            case "CRYPTOCURRENCY":
                resultado.put("Crypto", 100.0);
                break;
            default:
                throw new RuntimeException("Tipo no reconocido para acción " + getNombre() + "y ticker " + ticker);
        }
        return resultado;
    }
}
