package Display.Comandos;

public abstract class Comando {

    protected static String referencia;

    public Comando(){}

    // Ciertos comandos necesitas imprimir algo por pantalla después de haberse ejecutado. Pueden devolverlo y será procesado desde donde fueron llamados.
    public abstract String ejecutar();

    public static boolean itsMe(String referencia) {
        return referencia.equals(referencia);
    }
}
