package Display.Comandos;

public abstract class Comando {

    protected String referencia;

    // Ciertos comandos necesitas imprimir algo por pantalla después de haberse ejecutado. Pueden devolverlo y será procesado desde donde fueron llamados.
    public abstract String ejecutar(String[] args);

    public boolean itsMe(String referencia) {
        return this.referencia.equals(referencia);
    }

    public String getReferencia(){
        return referencia;
    }
}
