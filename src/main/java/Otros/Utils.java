package Otros;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Utils {

    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public static Calendar deserializarFecha(String time) {
        Date date = null;
        try {
            date = sdf.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String serializarFecha(Calendar calendar){
        return sdf.format(calendar.getTime());
    }

    /**
     *
     * @param flujosCaja Cada elemento de la lista representa un flujo de caja. Cada uno de estos se representan con la fecha e importe del flujo.
     *
     * @return Devuelve un diccionario con las siguientes claves: importeInicial, beneficio
     */
    public static HashMap<String, Double> calcularRentabilidad(List<HashMap<String, String>> flujosCaja) {
        double capitalInicial = 0;
        double remanente = 0;
        double beneficio = 0;
        flujosCaja.sort((s1, s2) -> Utils.deserializarFecha(s1.get("Fecha")).compareTo(Utils.deserializarFecha(s2.get("Fecha"))));
        for(HashMap<String, String> flujo : flujosCaja) {
            beneficio += Double.parseDouble(flujo.get("Flujo"));
            if (Double.parseDouble(flujo.get("Flujo")) > 0)
                remanente += Double.parseDouble(flujo.get("Flujo"));
            else {
                if (Math.abs(Double.parseDouble(flujo.get("Flujo"))) > remanente) {
                    capitalInicial += Math.abs(Double.parseDouble(flujo.get("Flujo"))) - remanente;
                    remanente = 0;
                }
                else
                    remanente -= Math.abs(Double.parseDouble(flujo.get("Flujo")));
            }
        }
        HashMap<String, Double> resultado = new HashMap<>();
        resultado.put("importeInicial", capitalInicial);
        resultado.put("beneficio", beneficio);
        return resultado;
    }
}
