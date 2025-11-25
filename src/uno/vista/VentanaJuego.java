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

    // Componentes UI
    private JLabel lblJugadorActual;
    private JLabel lblPuntos;
    private JLabel lblDireccion;
    private JLabel lblTope;
    private JButton btnRobar;
    private JButton btnPasar;
    private JPanel panelMano; // botones de cartas
    private JLabel lblConteoJugadores;

    public VentanaJuego(Juego modelo, ControladorJuego controlador) {
        super("UNO - Juego");
        this.modelo = modelo;
        this.controlador = controlador;

        // Fondo correcto, con ruta correcta
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

        // --- Panel superior: información del juego ---

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

        // --- Panel central: pila + mazo ---
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        panelCentro.setOpaque(false);

        // Pila (tope)
        JPanel panelPila = new JPanel(new BorderLayout());
        panelPila.setOpaque(false);
        panelPila.add(lblTope, BorderLayout.CENTER);

        // Mazo -> botón para robar
        btnRobar = crearBotonMazo(); // <-- usa tu botón con imagen
        JPanel panelMazo = new JPanel(new FlowLayout());
        panelMazo.setOpaque(false);
        panelMazo.setBorder(BorderFactory.createEmptyBorder(80, 0, 0, 50));
        panelMazo.add(btnRobar);

        panelCentro.add(panelPila, BorderLayout.CENTER);
        panelCentro.add(panelMazo, BorderLayout.EAST);

        add(panelCentro, BorderLayout.CENTER);

        // --- Panel inferior: mano del jugador y acciones ---
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

    // ----------------- Handlers -----------------

    private void onRobar() {
        try {
            boolean puedeJugar = controlador.robarCarta();
            // el modelo notificará y la vista se actualizará
            if (puedeJugar) {
                JOptionPane.showMessageDialog(this,
                        "Robaste una carta que puedes jugar. Puedes jugarla ahora.",
                        "Robar carta", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            // controlador.robarCarta no lanza checked en tu versión, pero por las dudas:
            JOptionPane.showMessageDialog(this, "Error al robar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onPasar() {
        // Pasar simplemente avanza el turno en el modelo.
        // No exponemos un método 'pasar' en controlador -> lo simulamos robando sin robar:
        // Llamamos directamente al modelo para avanzarTurno? mejor usar robarCarta con control?
        // En tu modelo hay avanzarTurno() privado; para pasar usamos jugarCarta nula no establecida.
        // Simpliquemos: llamamos al modelo para avanzar turno vía un pequeño truco:
        try {
            // Para respetar MVC usamos controlador.robarCarta() pero no es ideal.
            // Mejor: avanzamos el turno jugando una carta inválida no es correcto.
            // Así que usaremos reflection-like approach avoided: en vez de eso,
            // pedimos al usuario confirmar y llamamos a robarCarta y luego descartar la carta.
            // Para simplicidad semántica realizamos oficialmente un avance de turno
            // mediante jugarCarta de una carta inexistente está mal; por tanto vamos a:
            // -> llamar al modelo para robar una carta y luego pasar el turno si no puede jugar.
            boolean puedeJugar = controlador.robarCarta();
            if (!puedeJugar) {
                // si no puede jugar la carta robada, ya se avanzó el turno en el modelo
                // (esa es la semántica que definiste)
            } else {
                // si puede jugarla, mostramos y dejamos que el usuario la juegue o la descarte
                JOptionPane.showMessageDialog(this, "Robaste una carta que puedes jugar.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al pasar: " + ex.getMessage());
        }
    }

    // ----------------- Vista <-> Modelo -----------------


    //Crea el botón visual para representar una carta en la mano.

    private JButton crearBotonCarta(Carta carta) {
        String iconPath = carta.toString() + ".png"; // ejemplo: Rojo_Uno.png
        JButton btn = new JButton();

        // Intentar cargar icono si existe en recursos del proyecto
        try {
            java.net.URL resource = getClass().getResource("/Resources/" + iconPath);
            if (resource != null) {
                ImageIcon ico = new ImageIcon(resource);
                // escalamos icon un poco si es muy grande
                Image img = ico.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setText("");
            } else {
                // fallback a texto
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
        // Si no es el turno del usuario actual, avisar
        String jugadorIdActual = modelo.getJugadorActual().getId();
        // suponemos que esta ventana representa al jugador cuyo turno sea local; si es multijugador local,
        // esto está bien; si hay jugadores remotos, habría que controlar identidad.
        // Llamamos al controlador para jugar la carta (pasa el color si es Wild)
        Carta.Color colorElegido = null;
        if (carta.getColor() == Carta.Color.Wild) {
            colorElegido = pedirColorWild();
            if (colorElegido == null) return; // canceló selección
        }

        try {
            controlador.jugarCarta(carta, colorElegido);
            // la actualización de UI vendrá por el modelo.notificar() que llama actualizar()
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo jugar la carta: " + ex.getMessage(),
                    "Movimiento inválido", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Carta.Color pedirColorWild() {
        Carta.Color[] opciones = Carta.Color.values();
        // construir un array de strings con colores, excluyendo Wild como opción selectable
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

        // mapear index a enum (teniendo en cuenta que filramos Wild)
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


    // ----------------- Observer -----------------

    @Override
    public void actualizar() {
        SwingUtilities.invokeLater(this::refreshUI);
    }

    private void refreshUI() {

        //CARTEL DE PUNTUACION AL TERMINAR UNA RONDA
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

            // limpiar el ganador de la ronda para no repetir cartel
            modelo.clearGanadorRonda();
        }

        // 🔥 1) Verificar si hay ganador antes de refrescar nada
        if (modelo.getGanadorPartida() != null) {
            Jugador ganador = modelo.getGanadorPartida();

            JOptionPane.showMessageDialog(this,
                    "¡Ganó la partida " + ganador.getId() +
                            " con " + ganador.getPuntaje() + " puntos!",
                    "FIN DEL JUEGO", JOptionPane.INFORMATION_MESSAGE);

            System.exit(0);
            return;
        }

        // 🔥 2) Todo lo demás sigue igual...
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

        // Tope
        Carta tope = modelo.getTope();
        if (tope != null) {
            lblTope.setIcon(cargarIconoCarta(tope));
            lblTope.setText("");
        } else {
            lblTope.setIcon(null);
            lblTope.setText(" - ");
        }

        // Mano del jugador
        panelMano.removeAll();
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