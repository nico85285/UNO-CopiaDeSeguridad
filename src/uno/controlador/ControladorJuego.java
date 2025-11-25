package uno.controlador;
import uno.modelo.Carta;
import uno.modelo.*;
import uno.observer.*;

public class ControladorJuego {
    private Juego modelo;

    public ControladorJuego(Juego modelo) {
        this.modelo = modelo;
    }

    public void jugarCarta(Carta carta, Carta.Color colorElegido) throws Exception {
        modelo.jugarCarta(modelo.getJugadorActual().getId(), carta, colorElegido);
    }

    public void robarCarta() {
        modelo.robarCarta(modelo.getJugadorActual().getId());
    }

    public void pasarTurno() {
        modelo.pasarTurno(modelo.getJugadorActual().getId());
    }

    public Juego getModelo(){ return modelo; }
}