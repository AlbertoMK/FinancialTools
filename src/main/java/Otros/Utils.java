package Otros;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {

    private final static SimpleDateFormat sdfEuropea = new SimpleDateFormat("dd/MM/yyyy"); //MM en mayúsculas para que no lo confunda con minutos
    private final static SimpleDateFormat sdfAmericana = new SimpleDateFormat("yyyy-MM-dd");

    public static Calendar deserializarFecha(String time) {
        Date date = null;
        try {
            date = sdfEuropea.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String serializarFechaEuropea(Calendar calendar) {
        return sdfEuropea.format(calendar.getTime());
    }

    private static String serializarFechaAmericana(Calendar calendar) {
        String resultado = sdfAmericana.format(calendar.getTime());
        return resultado;
    }

    public static java.sql.Date CalendarToSQLDate(Calendar calendar){
        return java.sql.Date.valueOf(serializarFechaAmericana(calendar));
    }

    public static Calendar SQLDateToCalendar(java.sql.Date date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        return c;
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
        Calendar primeraFecha = Utils.deserializarFecha(flujosCaja.get(0).get("Fecha"));
        Calendar ultimaFecha = Utils.deserializarFecha(flujosCaja.get(flujosCaja.size()-1).get("Fecha"));
        double diasTranscurridos = (ultimaFecha.getTimeInMillis() - primeraFecha.getTimeInMillis())  / (24 * 60 * 60 * 1000);
        HashMap<String, Double> resultado = new HashMap<>();
        resultado.put("importeInicial", capitalInicial);
        resultado.put("beneficio", beneficio);
        resultado.put("duracion", diasTranscurridos); // tiempo transcurrido desde el primero flujo hasta el último en días
        return resultado;
    }
}
