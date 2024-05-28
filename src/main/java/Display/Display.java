package Display;

import Display.Comandos.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Display {

    private static Display instance = null;
    private static Scanner scanner = new Scanner(System.in);
    List<Comando> comandos;

    private Display() {
        comandos = new ArrayList<>();
        comandos.add(new CrearCmd());
        comandos.add(new ComprarAccionCmd());
        comandos.add(new HelpCmd());
        comandos.add(new VenderDepositoCmd());
        comandos.add(new RentabilidadCmd());
    }

    public static Display getInstance() {
        if(instance == null)
            instance = new Display();
        return instance;
    }

    public void run() {
        boolean comandoEncontrado;
        int indexComandos;
        while(true) {
            String linea = readLine();
            String referencia = linea.split(" ")[0];
            List<String> argumentos = getArgumentos(linea.substring(linea.indexOf(" ") + 1));
            String[] argumentosArray = new String[argumentos.size()];
            argumentosArray = argumentos.toArray(argumentosArray);
            comandoEncontrado = false;
            indexComandos = 0;
            while(!comandoEncontrado && indexComandos < comandos.size()) {
                if(comandos.get(indexComandos).itsMe(referencia))
                    comandoEncontrado = true;
                else
                    indexComandos++;
            }
            if(comandoEncontrado){
                try {
                    String resutlado = comandos.get(indexComandos).ejecutar(argumentosArray);
                    if(resutlado.length() > 0)
                        System.out.println(resutlado);
                } catch (RuntimeException ex){
                    System.out.println(ex.getMessage());
                    throw ex;
                }
            }
            else {
                System.out.println("Comando no reconocido");
            }
        }
    }

    public List<Comando> getComandos() {
        return comandos;
    }

    /**
     * Esta función separa por los espacios el comando recibido dando lugar a una lista de String. Mantiene unido como un mismo argumento  aquello que
     * esté entre comillas
     * @return lista de argumentos
     */
    private List<String> getArgumentos(String argumentos) {
        List<String> resultado = new ArrayList<>();
        boolean inQuote = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < argumentos.length(); i++) {
            if(argumentos.charAt(i) == '"'){
                inQuote = !inQuote;
            }
            else if(argumentos.charAt(i) == ' ' && !inQuote){
                resultado.add(sb.toString());
                sb = new StringBuilder();
            }
            else {
                sb.append(argumentos.charAt(i));
            }
        }
        if(sb.length() != 0)
            resultado.add(sb.toString());
        return resultado;
    }

    public static int selector(List<?> list) throws NumberFormatException {
        System.out.println("Escoge una opción");
        int contador = 1;
        for(Object o : list) {
            System.out.println(contador++ +". " + o.toString());
        }
        int opcion = Integer.parseInt(readLine());
        return opcion - 1;
    }

    public static String  readLine() {
        System.out.print(">>");
        return scanner.nextLine();
    }
}
