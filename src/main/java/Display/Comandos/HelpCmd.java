package Display.Comandos;

import Display.Display;

import java.util.List;

public class HelpCmd extends Comando{

    public HelpCmd(){
        referencia = "help";
    }

    // no requiere de parámetros
    @Override
    public String ejecutar(String[] args) {
        StringBuilder sb = new StringBuilder();
        List<Comando> comandos = Display.getInstance().getComandos();
        for (int i = 0; i < comandos.size(); i++) {
            sb.append(comandos.get(i).getReferencia());
            if(i < comandos.size() - 1)
                sb.append("\n");
        }
        return sb.toString();
    }
}
