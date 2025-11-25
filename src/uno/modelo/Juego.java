package uno.modelo;
import uno.persistencia.SistemaPuntuacion;
import uno.observer.Sujeto;
import javax.swing.*;
import java.util.List;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Juego extends Sujeto {

    private Mazo mazo;
    private ArrayList<Jugador> jugadores;
    private ArrayList<Carta> pila;


    private Jugador ganadorRonda = null;

    private Carta.Color colorActual;
    private Carta.Valor valorActual;
    private boolean direccion; // false = horario, true = antihorario
    private int jugadorActual;

    // Puntuaciones
    private Jugador ganadorPartida = null;
    private static final int PUNTOS_META = 100;                   // PUNTOS PARA GANAR LA PARTIDA !!!!!!!!!!!!!!!!

    // ==============================
    // CONSTRUCTOR
    // ==============================

    public Juego(String[] idsJugadores) {
        mazo = new Mazo();
        mazo.armarMazo();
        mazo.mezclar();

        pila = new ArrayList<>();
        jugadores = new ArrayList<>();

        // Crear jugadores y repartir 7
        for (String id : idsJugadores) {
            Jugador j = new Jugador(id);
            j.agregarCartas(mazo.robarCarta(7));
            jugadores.add(j);
        }

        jugadorActual = 0;
        direccion = false;

        // Primera carta en la mesa
        Carta primera = mazo.robarCarta();
        while (primera.getValor() == Carta.Valor.Wild ||
                primera.getValor() == Carta.Valor.WildCuatro ||
                primera.getValor() == Carta.Valor.Skip ||
                primera.getValor() == Carta.Valor.Reversa ||
                primera.getValor() == Carta.Valor.RobarDos) {

            mazo.agregarCarta(primera);
            mazo.mezclar();
            primera = mazo.robarCarta();
        }

        pila.add(primera);
        colorActual = primera.getColor();
        valorActual = primera.getValor();
    }

    // ==============================
    // GETTERS
    // ==============================

    public Jugador getJugadorActual() { return jugadores.get(jugadorActual); }
    public Carta getTope() { return pila.isEmpty() ? null : pila.get(pila.size() - 1); }
    public Carta.Color getColorActual() { return colorActual; }
    public Carta.Valor getValorActual() { return valorActual; }
    public Jugador getGanadorPartida() { return ganadorPartida; }

    public Jugador getGanadorRonda() {
        return ganadorRonda;
    }

    public List<Jugador> getListaJugadores() {
        return jugadores;
    }

    public void clearGanadorRonda() {
        ganadorRonda = null;
    }

    public int getCantidadJugadores() { return jugadores.size(); }

    public ArrayList<Carta> getManoJugador(String id) {
        for (Jugador j : jugadores)
            if (j.getId().equals(id)) return j.getMano();
        return null;
    }

    // ==============================
    // MECÁNICA BÁSICA
    // ==============================

    public boolean puedeJugar(Carta carta) {
        return carta.getColor() == colorActual ||
                carta.getValor() == valorActual ||
                carta.getColor() == Carta.Color.Wild;
    }

    public void jugarCarta(String idJugador, Carta carta, Carta.Color colorElegido)
            throws InvalidPlayException {

        Jugador jugador = getJugadorPorId(idJugador);

        if (jugador != getJugadorActual())
            throw new InvalidPlayException("No es tu turno.");

        if (!puedeJugar(carta))
            throw new InvalidPlayException("La carta no coincide con el color o valor actual.");

        // Colocar carta
        jugador.removerCarta(carta);
        pila.add(carta);

        colorActual = carta.getColor();
        valorActual = carta.getValor();

        aplicarEfectoCarta(carta, colorElegido);

        // =============================
        // ¿GANÓ LA RONDA?
        // =============================
        if (jugador.getMano().isEmpty()) {

            int puntosRonda = calcularPuntosRonda(jugador);
            jugador.sumarPuntos(puntosRonda);

            ganadorRonda = jugador;   // 🔥 IMPORTANTE

            // Notificar ANTES
            notificar();

            // Si ganó la partida completa
            if (jugador.getPuntaje() >= PUNTOS_META) {
                ganadorPartida = jugador;

                // 🔥 Guardar score permanente
                SistemaPuntuacion.agregarScore(jugador);

                notificar();                                 //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                return;
            }

            // Si NO ganó la partida -> arrancar nueva ronda
            reiniciarRonda();
            return;
        }

        avanzarTurno();
        notificar();
    }

    // ==============================
    // EFECTO DE CARTAS ESPECIALES
    // ==============================

    private void aplicarEfectoCarta(Carta carta, Carta.Color colorElegido) {

        switch (carta.getValor()) {

            case RobarDos -> {
                avanzarTurno();
                Jugador j = getJugadorActual();
                j.agregarCarta(mazo.robarCarta());
                j.agregarCarta(mazo.robarCarta());
            }

            case Skip -> {
                avanzarTurno();
            }

            case Reversa -> {
                direccion = !direccion;
            }

            case Wild -> {
                colorActual = colorElegido;
            }

            case WildCuatro -> {
                colorActual = colorElegido;
                avanzarTurno();
                Jugador j = getJugadorActual();
                for (int i = 0; i < 4; i++) j.agregarCarta(mazo.robarCarta());
            }
        }
    }

    // ==============================
    // PUNTUACIÓN DE LA RONDA
    // ==============================

    private int valorCarta(Carta c) {
        switch (c.getValor()) {
            case Cero: return 0;
            case Uno: return 1;
            case Dos: return 2;
            case Tres: return 3;
            case Cuatro: return 4;
            case Cinco: return 5;
            case Seis: return 6;
            case Siete: return 7;
            case Ocho: return 8;
            case Nueve: return 9;
            case RobarDos, Skip, Reversa: return 20;
            case Wild, WildCuatro: return 50;
            default: return 0;
        }
    }

    private int calcularPuntosRonda(Jugador ganador) {
        int total = 0;

        for (Jugador j : jugadores) {
            if (j == ganador) continue;

            for (Carta c : j.getMano()) {
                total += valorCarta(c);
            }
        }

        return total;
    }

    // ==============================
    // REINICIAR RONDA
    // ==============================

    private void reiniciarRonda() {
        prepararNuevaRonda();
        notificar();
    }

    private void prepararNuevaRonda() {

        pila.clear();

        // Nuevo mazo
        mazo = new Mazo();
        mazo.armarMazo();
        mazo.mezclar();

        // Vaciar manos
        for (Jugador j : jugadores)
            j.getMano().clear();

        // Repartir 7
        for (int i = 0; i < 7; i++)
            for (Jugador j : jugadores)
                j.agregarCarta(mazo.robarCarta());

        // Carta inicial válida
        Carta inicial;
        do {
            inicial = mazo.robarCarta();
        } while (inicial.getValor() == Carta.Valor.Wild ||
                inicial.getValor() == Carta.Valor.WildCuatro);

        pila.add(inicial);

        colorActual = inicial.getColor();
        valorActual = inicial.getValor();
        jugadorActual = 0;
        direccion = false;
    }

    // ==============================
    // OTROS
    // ==============================

    private void avanzarTurno() {
        jugadorActual = direccion
                ? (jugadorActual - 1 + jugadores.size()) % jugadores.size()
                : (jugadorActual + 1) % jugadores.size();
    }

    private Jugador getJugadorPorId(String id) {
        for (Jugador j : jugadores)
            if (j.getId().equals(id)) return j;
        throw new IllegalArgumentException("Jugador no encontrado: " + id);
    }

    private void rearmarDesdePila() {
        Carta tope = pila.remove(pila.size() - 1);
        for (Carta c : pila) mazo.agregarCarta(c);
        pila.clear();
        pila.add(tope);
        mazo.mezclar();
    }

    public boolean robarCarta(String idJugador) {
        Jugador j = getJugadorPorId(idJugador);

        if (j != getJugadorActual())
            throw new IllegalStateException("No es tu turno.");

        if (mazo.isEmpty())
            rearmarDesdePila();

        Carta robada = mazo.robarCarta();
        j.agregarCarta(robada);

        boolean puede = puedeJugar(robada);

        if (!puede)
            avanzarTurno();

        notificar();
        return puede;
    }
}


class InvalidPlayException extends Exception {
    public InvalidPlayException(String message) {
        super(message);
    }
}