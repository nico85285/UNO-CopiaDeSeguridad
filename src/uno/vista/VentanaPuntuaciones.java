package uno.vista;

import uno.persistencia.SistemaPuntuacion;
import uno.persistencia.SistemaPuntuacion.RegistroPuntaje;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaPuntuaciones extends JFrame {

    public VentanaPuntuaciones() {
        setTitle("Ranking TOP 5");
        setSize(300, 300);
        setLocationRelativeTo(null);

        List<RegistroPuntaje> lista = SistemaPuntuacion.cargarPuntos();

        StringBuilder texto = new StringBuilder("TOP 5 Mejores Puntajes:\n\n");

        int pos = 1;
        for (RegistroPuntaje r : lista) {
            texto.append(pos++)
                    .append(". ")
                    .append(r.nombre)
                    .append(" - ")
                    .append(r.puntos)
                    .append(" puntos\n");
        }

        JTextArea area = new JTextArea(texto.toString());
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 16));

        add(new JScrollPane(area));
    }
}