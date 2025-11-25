package uno.vista;
import uno.modelo.*;
import uno.controlador.ControladorJuego;
import uno.observer.Observador;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaJuego extends JFrame implements Observador {

    private final Juego modelo;
    private final ControladorJuego controlador;

    private JLabel lblJugadorActual;
    private JLabel lblPuntos;
    private JLabel lblDireccion;
    private JLabel lblTope;
    private JButton btnRobar;
    private JButton btnPasar;
    private JPanel panelMano;
    private JLabel lblConteoJugadores;

    public VentanaJuego(Juego modelo, ControladorJuego controlador) {
        super("UNO - Juego");
        this.modelo = modelo;
        this.controlador = controlador;

        FondoPanel fondo = new FondoPanel("/Resources/ImagenFondoVentanaPrincipal.png");
        setContentPane(fondo);
        fondo.setLayout(new BorderLayout(10, 10));

        initUI();

        this.modelo.agregarObservador(this);
        actualizar();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        // PANEL SUPERIOR ! ! ! ! ! ! !

        JPanel panelTop = new JPanel(new GridLayout(2, 2, 10, 10));
        panelTop.setOpaque(false);
        lblJugadorActual = new JLabel("Turno de: -");
        lblJugadorActual.setFont(lblJugadorActual.getFont().deriveFont(18f));
        lblDireccion = new JLabel("Dirección: -");
        lblTope = new JLabel("Tope: -", SwingConstants.CENTER);
        lblTope.setFont(lblTope.getFont().deriveFont(16f));
        lblConteoJugadores = new JLabel("Jugadores: -");

        panelTop.add(lblJugadorActual);
        panelTop.add(lblConteoJugadores);
        panelTop.add(lblDireccion);
        panelTop.add(new JLabel()); // hueco
        add(panelTop, BorderLayout.NORTH);

        lblPuntos = new JLabel("Puntos: -");
        panelTop.add(lblPuntos);

        // PANEL CENTRAL ! ! ! ! ! ! !
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        panelCentro.setOpaque(false);

        // Pila
        JPanel panelPila = new JPanel(new BorderLayout());
        panelPila.setOpaque(false);
        panelPila.add(lblTope, BorderLayout.CENTER);

        // Mazo
        btnRobar = crearBotonMazo(); // <-- usa tu botón con imagen
        JPanel panelMazo = new JPanel(new FlowLayout());
        panelMazo.setOpaque(false);
        panelMazo.setBorder(BorderFactory.createEmptyBorder(80, 0, 0, 50));
        panelMazo.add(btnRobar);

        panelCentro.add(panelPila, BorderLayout.CENTER);
        panelCentro.add(panelMazo, BorderLayout.EAST);

        add(panelCentro, BorderLayout.CENTER);

        // PANEL INFERIOR ! ! ! ! ! ! !
        JPanel panelBottom = new JPanel(new BorderLayout(10, 10));
        panelBottom.setOpaque(false);

        panelMano = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panelMano.setOpaque(false);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelAcciones.setOpaque(false);
        btnPasar = new JButton("Pasar turno");
        btnPasar.addActionListener(e -> onPasar());
        panelAcciones.add(btnPasar);



        JScrollPane scroll = new JScrollPane(panelMano,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        panelBottom.add(scroll, BorderLayout.CENTER);
        panelBottom.add(panelAcciones, BorderLayout.SOUTH);

        add(panelBottom, BorderLayout.SOUTH);
    }





    private void onRobar() {
        try {
            controlador.robarCarta();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al robar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onPasar() {
        try {
            controlador.pasarTurno();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al pasar turno: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //


    private JButton crearBotonCarta(Carta carta) {
        String iconPath = carta.toString() + ".png";
        JButton btn = new JButton();

        try {
            java.net.URL resource = getClass().getResource("/Resources/" + iconPath);
            if (resource != null) {
                ImageIcon ico = new ImageIcon(resource);

                Image img = ico.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH); // ESCALADO
                btn.setIcon(new ImageIcon(img));
                btn.setText("");
            } else {

                btn.setText(carta.toString());
            }

        } catch (Exception ex) {
            btn.setText(carta.toString());
        }

        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);

        btn.addActionListener(e -> onClickCarta(carta));
        return btn;
    }



    private void onClickCarta(Carta carta) {

        String jugadorIdActual = modelo.getJugadorActual().getId();  // PARA CUANDO HAGA EL MULTIJUGADOR

        Carta.Color colorElegido = null;
        if (carta.getColor() == Carta.Color.Wild) {
            colorElegido = pedirColorWild();
            if (colorElegido == null) return; // canceló selección
        }

        try {
            controlador.jugarCarta(carta, colorElegido);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo jugar la carta: " + ex.getMessage(),
                    "Movimiento inválido", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Carta.Color pedirColorWild() {
        Carta.Color[] opciones = Carta.Color.values();

        String[] nombres = java.util.Arrays.stream(opciones)
                .filter(c -> c != Carta.Color.Wild)
                .map(Enum::name)
                .toArray(String[]::new);

        int sel = JOptionPane.showOptionDialog(this,
                "Elige un color:",
                "Color Wild",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                nombres,
                nombres[0]);

        if (sel < 0) return null;

        java.util.List<Carta.Color> lista = new java.util.ArrayList<>();
        for (Carta.Color c : opciones) if (c != Carta.Color.Wild) lista.add(c);

        return lista.get(sel);
    }

    private ImageIcon cargarIconoCarta(Carta c) {
        String ruta = "/Resources/" + c.getColor() + "_" + c.getValor() + ".png";
        java.net.URL url = getClass().getResource(ruta);

        if (url == null) {
            System.out.println("No se encontró la imagen: " + ruta);
            return null;
        }

        return new ImageIcon(url);
    }

    private JButton crearBotonMazo() {
        JButton btn = new JButton();

        try {
            java.net.URL resource = getClass().getResource("/Resources/CartaAtras.png");
            if (resource != null) {
                ImageIcon ico = new ImageIcon(resource);
                Image img = ico.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setText("");
            } else {
                btn.setText("Mazo");
            }
        } catch (Exception e) {
            btn.setText("Mazo");
        }

        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);

        btn.addActionListener(e -> onRobar());
        return btn;
    }


    // OBSERVER

    @Override
    public void actualizar() {
        SwingUtilities.invokeLater(this::refreshUI);
    }

    private void refreshUI() {

        Jugador ganadorRonda = modelo.getGanadorRonda();

        if (modelo.getGanadorRonda() != null) {

            StringBuilder msg = new StringBuilder("¡" + modelo.getGanadorRonda().getId() +
                    " ganó la ronda!\n\nPuntajes actuales:\n");

            for (Jugador j : modelo.getListaJugadores()) {
                msg.append(j.getId())
                        .append(": ")
                        .append(j.getPuntaje())
                        .append(" puntos\n");
            }

            JOptionPane.showMessageDialog(this, msg.toString(), "Fin de la ronda", JOptionPane.INFORMATION_MESSAGE);

            modelo.clearGanadorRonda();
        }


        if (modelo.getGanadorPartida() != null) {                                    // VERIFICA GANADOR O NO
            Jugador ganador = modelo.getGanadorPartida();

            JOptionPane.showMessageDialog(this,
                    "¡Ganó la partida " + ganador.getId() +
                            " con " + ganador.getPuntaje() + " puntos!",
                    "FIN DEL JUEGO", JOptionPane.INFORMATION_MESSAGE);

            System.exit(0);
            return;
        }


        Jugador actual = modelo.getJugadorActual();
        lblPuntos.setText("Puntos actuales: " + actual.getPuntaje());
        lblJugadorActual.setText("Turno: " + actual.getId());
        lblDireccion.setText("Dirección: " +
                (modelo.getValorActual() != null ?
                        (modelo.getValorActual() == Carta.Valor.Reversa ? "Reversa activa" : "Normal")
                        : "Normal")
        );
        lblConteoJugadores.setText(
                "Cartas del actual: " + modelo.getManoJugador(actual.getId()).size() +
                        " | Total jugadores: " + modelo.getCantidadJugadores()
        );


        Carta tope = modelo.getTope();                                          // TOPE
        if (tope != null) {
            lblTope.setIcon(cargarIconoCarta(tope));
            lblTope.setText("");
        } else {
            lblTope.setIcon(null);
            lblTope.setText(" - ");
        }


        panelMano.removeAll();                                                      //MANO
        List<Carta> mano = modelo.getManoJugador(actual.getId());
        if (mano != null) {
            for (Carta c : mano) {
                JButton btn = crearBotonCarta(c);
                panelMano.add(btn);
            }
        }
        panelMano.revalidate();
        panelMano.repaint();
    }
}