package Display.Ventanas;

import javax.swing.*;
import java.awt.*;

public class VentanaEmergente extends JFrame {

    public VentanaEmergente(Component comp) {
        add(comp);
        pack();
        setVisible(true);
    }
}
