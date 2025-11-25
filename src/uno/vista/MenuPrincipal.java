package uno.vista;

import javax.swing.*;
import java.awt.*;
import uno.modelo.Juego;
import uno.controlador.ControladorJuego;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("UNO - Menú Principal");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1, 10, 10));

        JButton btnJugar = new JButton("Jugar");
        JButton btnPuntuaciones = new JButton("Puntuaciones");

        add(btnJugar);
        add(btnPuntuaciones);

        // Acción del botón Jugar
        btnJugar.addActionListener(e -> {
            VentanaSeleccionJugadores ventana = new VentanaSeleccionJugadores();
            ventana.setVisible(true);
            this.dispose();
        });

        // Acción del botón Puntuaciones
        btnPuntuaciones.addActionListener(e -> {
            VentanaPuntuaciones ventana = new VentanaPuntuaciones();
            ventana.setVisible(true);
        });
    }
}