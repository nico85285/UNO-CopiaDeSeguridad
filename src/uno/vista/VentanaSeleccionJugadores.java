package uno.vista;

import uno.modelo.Juego;
import uno.controlador.ControladorJuego;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class VentanaSeleccionJugadores extends JFrame {

    private JComboBox<Integer> selectorCantidad;
    private JPanel panelNombres;
    private java.util.List<JTextField> camposNombres = new ArrayList<>();

    public VentanaSeleccionJugadores() {
        setTitle("Seleccionar jugadores");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));


        JPanel panelTop = new JPanel();
        panelTop.add(new JLabel("Cantidad de jugadores (2 a 10):"));

        selectorCantidad = new JComboBox<>();
        for (int i = 2; i <= 10; i++) selectorCantidad.addItem(i);

        panelTop.add(selectorCantidad);
        add(panelTop, BorderLayout.NORTH);

        //TEXTO
        panelNombres = new JPanel();
        panelNombres.setLayout(new GridLayout(10, 1, 5, 5));
        add(panelNombres, BorderLayout.CENTER);


        JButton btnGenerar = new JButton("Generar campos");
        btnGenerar.addActionListener(e -> generarCampos());
        panelTop.add(btnGenerar);


        JButton btnJugar = new JButton("Comenzar partida");
        btnJugar.addActionListener(e -> iniciarJuego());
        add(btnJugar, BorderLayout.SOUTH);
    }

    private void generarCampos() {
        panelNombres.removeAll();
        camposNombres.clear();

        int cant = (int) selectorCantidad.getSelectedItem();

        for (int i = 0; i < cant; i++) {
            JTextField campo = new JTextField();
            campo.setBorder(BorderFactory.createTitledBorder("Jugador " + (i + 1)));
            camposNombres.add(campo);
            panelNombres.add(campo);
        }

        panelNombres.revalidate();
        panelNombres.repaint();
    }

    private void iniciarJuego() {
        int cant = camposNombres.size();

        if (cant < 2) {
            JOptionPane.showMessageDialog(this, "Debes generar al menos 2 jugadores.");
            return;
        }

        String[] nombres = new String[cant];

        for (int i = 0; i < cant; i++) {
            String nombre = camposNombres.get(i).getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los jugadores deben tener nombre.");
                return;
            }
            nombres[i] = nombre;
        }


        Juego modelo = new Juego(nombres);
        ControladorJuego controlador = new ControladorJuego(modelo);


        VentanaJuego ventanaJuego = new VentanaJuego(modelo, controlador);
        ventanaJuego.setVisible(true);

        this.dispose(); // PARA CERRAR ESTA VENTANA
    }
}