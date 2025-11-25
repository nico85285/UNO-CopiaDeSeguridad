package uno.modelo;

public class ManejadorTurnos {
    private int jugadorActual;
    private boolean direccion;                                                  // false = horario, true = antihorario !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private int cantidadJugadores;

    public ManejadorTurnos(int cantidadJugadores) {
        this.cantidadJugadores = Math.max(1, cantidadJugadores);
        this.jugadorActual = 0;
        this.direccion = false;
    }

    public int getJugadorActualIndex() {
        return jugadorActual;
    }

    public void invertirDireccion() {
        direccion = !direccion;
    }

    public void avanzarTurno() {
        if (cantidadJugadores <= 0) return;
        if (!direccion) {
            jugadorActual = (jugadorActual + 1) % cantidadJugadores;
        } else {
            jugadorActual = (jugadorActual - 1 + cantidadJugadores) % cantidadJugadores;
        }
    }

    public void resetear() {
        jugadorActual = 0;
        direccion = false;
    }
}

