package Display;

import Negocio.GestorAcciones;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Display {

    private static Scanner scanner = new Scanner(System.in);

    public static int selector(List<?> list) throws NumberFormatException {
        int contador = 1;
        for(Object o : list) {
            System.out.println(++contador+". " + o.toString());
        }
        int opcion = Integer.parseInt(readLine());
        return opcion - 1;
    }

    public static String  readLine() {
        System.out.print(">>");
        return scanner.nextLine();
    }
}
