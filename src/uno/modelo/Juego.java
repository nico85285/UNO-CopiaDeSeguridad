package uno.modelo;
import uno.persistencia.SistemaPuntuacion;
import uno.observer.Sujeto;
import java.util.List;
import java.util.ArrayList;

public class Juego extends Sujeto {

    private Mazo mazo;
    private ArrayList<Jugador> jugadores;
    private ManejadorPila manejadorPila;
    private ManejadorTurnos manejadorTurnos;
    private GestorEfectos gestorEfectos;
    private GestorPuntuacion gestorPuntuacion;

    private Jugador ganadorRonda = null;
    private Jugador ganadorPartida = null;
    private static final int PUNTOS_META = 100;

    private Carta.Color colorActual;
    private Carta.Valor valorActual;

    private boolean yaRoboEnEsteTurno = false;

    public Juego(String[] idsJugadores) {
        mazo = new Mazo();
        mazo.armarMazo();
        mazo.mezclar();

        jugadores = new ArrayList<>();
        for (String id : idsJugadores) {
            jugadores.add(new Jugador(id));
        }

        manejadorPila = new ManejadorPila();
        manejadorTurnos = new ManejadorTurnos(jugadores.size());
        gestorEfectos = new GestorEfectos();
        gestorPuntuacion = new GestorPuntuacion();

        repartirInicial();

        Carta inicial = manejadorPila.colocarInicialValida(mazo);
        colorActual = inicial.getColor();
        valorActual = inicial.getValor();
    }

    private void avanzarTurno() {
        manejadorTurnos.avanzarTurno();
        yaRoboEnEsteTurno = false;
    }

    public Jugador getJugadorActual() {
        return jugadores.get(manejadorTurnos.getJugadorActualIndex());
    }

    public Carta getTope() { return manejadorPila.getTope(); }
    public Carta.Color getColorActual() { return colorActual; }
    public Carta.Valor getValorActual() { return valorActual; }
    public Jugador getGanadorPartida() { return ganadorPartida; }
    public Jugador getGanadorRonda() { return ganadorRonda; }
    public List<Jugador> getListaJugadores() { return jugadores; }
    public void clearGanadorRonda() { ganadorRonda = null; }
    public int getCantidadJugadores() { return jugadores.size(); }

    public ArrayList<Carta> getManoJugador(String id) {
        for (Jugador j : jugadores) if (j.getId().equals(id)) return j.getMano();
        return null;
    }

    //-----------------------------------------------------------------------------------------------
    // Lógica principal

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
            throw new InvalidPlayException("La carta no coincide.");


        yaRoboEnEsteTurno = false;

        jugador.removerCarta(carta);
        manejadorPila.agregarCarta(carta);

        colorActual = carta.getColor();
        valorActual = carta.getValor();

        GestorEfectos.EfectoAccion accion = gestorEfectos.evaluar(carta, colorElegido);

        if (accion.invertir) manejadorTurnos.invertirDireccion();

        if (accion.cartasARobar > 0) {
            avanzarTurno();
            Jugador objetivo = getJugadorActual();

            for (int i = 0; i < accion.cartasARobar; i++) {
                if (mazo.isEmpty()) {
                    mazo.rearmarDesdePila(manejadorPila.getPila());
                }
                objetivo.agregarCarta(mazo.robarCarta());
            }
        }

        if (accion.saltar) avanzarTurno();

        if (accion.colorDeclarado != null &&
                (carta.getValor() == Carta.Valor.Wild || carta.getValor() == Carta.Valor.WildCuatro)) {
            colorActual = accion.colorDeclarado;
        }

        if (jugador.getMano().isEmpty()) {                                                    // PARA SABER SI GANO LA RONDA
            int puntosRonda = gestorPuntuacion.calcularPuntosRonda(jugadores, jugador);
            jugador.sumarPuntos(puntosRonda);
            ganadorRonda = jugador;
            notificar();

            if (jugador.getPuntaje() >= PUNTOS_META) {
                ganadorPartida = jugador;
                SistemaPuntuacion.agregarScore(jugador);
                notificar();
                return;
            }

            reiniciarRonda();
            return;
        }

        avanzarTurno();
        notificar();
    }


    public void robarCarta(String idJugador) {
        Jugador j = getJugadorPorId(idJugador);

        if (j != getJugadorActual())
            throw new IllegalStateException("No es tu turno.");

        if (yaRoboEnEsteTurno)
            throw new IllegalStateException("Solo podés robar una vez por turno.");

        if (mazo.isEmpty()) {
            mazo.rearmarDesdePila(manejadorPila.getPila());
        }

        Carta robada = mazo.robarCarta();
        j.agregarCarta(robada);

        yaRoboEnEsteTurno = true;

        notificar();
    }

    //-----------------------------------------------------------------------------------------------
    // Reiniciar ronda

    private void repartirInicial() {
        for (int i = 0; i < 7; i++) {                                                                   //7
            for (Jugador j : jugadores) {
                if (mazo.isEmpty()) manejadorPila.rearmarEnMazo(mazo);
                j.agregarCarta(mazo.robarCarta());
            }
        }
    }

    public void pasarTurno(String idJugador) {
        Jugador actual = getJugadorActual();

        if (!actual.getId().equals(idJugador)) {
            throw new IllegalStateException("No es tu turno.");
        }

        if (!yaRoboEnEsteTurno) {
            throw new IllegalStateException("Debés robar una carta antes de pasar el turno.");
        }

        manejadorTurnos.avanzarTurno();
        yaRoboEnEsteTurno = false;

        notificar();
    }

    private void reiniciarRonda() {
        manejadorPila.limpiar();

        mazo = new Mazo();
        mazo.armarMazo();
        mazo.mezclar();

        for (Jugador j : jugadores) j.getMano().clear();

        repartirInicial();

        Carta inicial = manejadorPila.colocarInicialValida(mazo);
        colorActual = inicial.getColor();
        valorActual = inicial.getValor();

        manejadorTurnos.resetear();
        yaRoboEnEsteTurno = false;

        notificar();
    }

    private Jugador getJugadorPorId(String id) {
        for (Jugador j : jugadores) if (j.getId().equals(id)) return j;
        throw new IllegalArgumentException("Jugador no encontrado: " + id);
    }
}


class InvalidPlayException extends Exception {          //FIJARME DE MOVELO
    public InvalidPlayException(String message) {
        super(message);
    }
}