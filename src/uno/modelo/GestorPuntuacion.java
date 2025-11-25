package uno.modelo;

import java.util.List;

public class GestorPuntuacion {

    public int valorCarta(Carta c) {
        if (c == null) return 0;
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
            case RobarDos:
            case Skip:
            case Reversa:
                return 20;
            case Wild:
            case WildCuatro:
                return 50;
            default:
                return 0;
        }
    }

    public int calcularPuntosRonda(List<Jugador> jugadores, Jugador ganador) {
        int total = 0;
        for (Jugador j : jugadores) {
            if (j == ganador) continue;
            for (Carta c : j.getMano()) total += valorCarta(c);
        }
        return total;
    }
}
