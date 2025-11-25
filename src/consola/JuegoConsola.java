package uno.consola;

import uno.modelo.Carta;
import uno.modelo.Juego;
import uno.modelo.Jugador;
import uno.persistencia.SistemaPuntuacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JuegoConsola {

    private final Juego juego;
    private final Scanner sc = new Scanner(System.in);

    public JuegoConsola(String[] nombres) {
        this.juego = new Juego(nombres);
    }

    public void iniciar() {
        System.out.println("=== Iniciando partida en consola ===");
        while (true) {

            if (juego.getGanadorPartida() != null) {
                Jugador ganador = juego.getGanadorPartida();
                System.out.println("\n***** ¡GANADOR DE LA PARTIDA! *****");
                System.out.printf("%s ganó la partida con %d puntos.%n",
                        ganador.getId(), ganador.getPuntaje());
                return;
            }

            Jugador actual = juego.getJugadorActual();
            String idActual = actual.getId();

            System.out.println("\n----------------------------------------");
            System.out.println("Turno de: " + idActual);

            Carta tope = juego.getTope();
            System.out.println("Carta en mesa: " + (tope != null ? tope.toString() : " - "));
            System.out.println("Color actual: " + juego.getColorActual() +
                    " | Valor actual: " + juego.getValorActual());
            System.out.println("Tus puntos: " + actual.getPuntaje());
            System.out.println();

            List<Carta> mano = juego.getManoJugador(idActual);
            System.out.println("Tu mano:");
            for (int i = 0; i < mano.size(); i++) {
                System.out.printf("%d) %s%n", i + 1, mano.get(i));
            }
            System.out.println();

            System.out.println("Opciones:");
            System.out.println("1 - Jugar carta");
            System.out.println("2 - Robar carta");
            System.out.println("3 - Pasar turno");
            System.out.println("4 - Mostrar puntuaciones");
            System.out.println("5 - Salir");
            System.out.print("Elegí opción: ");

            int opcion = leerEnteroSeguro(1, 5);

            switch (opcion) {

                case 1 -> {
                    if (mano.isEmpty()) {
                        System.out.println("No tenés cartas. Debes robar.");
                        break;
                    }

                    System.out.print("Número de carta a jugar: ");
                    int idx = leerEnteroSeguro(1, mano.size()) - 1;
                    Carta cartaElegida = mano.get(idx);

                    Carta.Color colorDeclarado = null;
                    if (cartaElegida.getColor() == Carta.Color.Wild) {
                        colorDeclarado = elegirColorParaWild();
                        if (colorDeclarado == null) {
                            System.out.println("Cancelado.");
                            break;
                        }
                    }

                    try {
                        juego.jugarCarta(idActual, cartaElegida, colorDeclarado);
                        System.out.println("Carta jugada: " + cartaElegida);
                    } catch (Exception ex) {
                        System.out.println("Movimiento inválido: " + ex.getMessage());
                    }

                    mostrarGanadorRondaSiCorresponde();
                }

                case 2 -> {
                    try {
                        juego.robarCarta(idActual);
                        System.out.println("Robaste una carta.");
                    } catch (Exception ex) {
                        System.out.println("No se pudo robar: " + ex.getMessage());
                    }
                }

                case 3 -> {
                    try {
                        juego.pasarTurno(idActual);
                        System.out.println("Turno pasado.");
                    } catch (Exception ex) {
                        System.out.println("No se pudo pasar turno: " + ex.getMessage());
                    }
                }

                case 4 -> {
                    mostrarPuntuacionesTemporales();
                }

                case 5 -> {
                    System.out.println("Saliendo del juego...");
                    return;
                }
            }
        }
    }


    private void mostrarGanadorRondaSiCorresponde() {
        Jugador g = juego.getGanadorRonda();
        if (g != null) {
            System.out.println("\n*** Fin de ronda! ***");
            System.out.printf("Ganador de la ronda: %s%n", g.getId());
            System.out.println("Puntuaciones actuales:");
            for (Jugador j : juego.getListaJugadores()) {
                System.out.printf("- %s: %d puntos%n", j.getId(), j.getPuntaje());
            }
            juego.clearGanadorRonda();
            System.out.println("*** Nueva ronda iniciada ***");
        }
    }

    private void mostrarPuntuacionesTemporales() {
        System.out.println("\nPUNTAJES ACTUALES:");
        for (Jugador j : juego.getListaJugadores()) {
            System.out.printf("- %s: %d puntos%n", j.getId(), j.getPuntaje());
        }
    }

    private int leerEnteroSeguro(int min, int max) {
        while (true) {
            try {
                int v = Integer.parseInt(sc.nextLine().trim());
                if (v < min || v > max)
                    System.out.print("Ingresá un número entre " + min + " y " + max + ": ");
                else
                    return v;
            } catch (Exception e) {
                System.out.print("Entrada inválida. Ingresá un número: ");
            }
        }
    }

    private Carta.Color elegirColorParaWild() {
        System.out.println("Elegí un color:");
        Carta.Color[] colores = Carta.Color.values();
        List<Carta.Color> lista = new ArrayList<>();

        for (Carta.Color c : colores) {
            if (c != Carta.Color.Wild) lista.add(c);
        }

        for (int i = 0; i < lista.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, lista.get(i));
        }

        System.out.print("Elegí opción (0 para cancelar): ");
        int sel = leerEnteroSeguro(0, lista.size());
        if (sel == 0) return null;
        return lista.get(sel - 1);
    }
}
