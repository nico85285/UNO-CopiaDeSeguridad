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

            // Si hay ganador de la partida, mostrar y salir.
            if (juego.getGanadorPartida() != null) {
                Jugador ganador = juego.getGanadorPartida();
                System.out.println("\n***** ¡GANADOR DE LA PARTIDA! *****");
                System.out.printf("%s ganó la partida con %d puntos.%n",
                        ganador.getId(), ganador.getPuntaje());
                System.out.println("Se actualizó el ranking permanentemente.");
                // Juego ya llama a SistemaPuntuacion.agregarScore() cuando se define ganadorPartida.
                return;
            }

            Jugador actual = juego.getJugadorActual();
            String idActual = actual.getId();

            System.out.println("\n----------------------------------------");
            System.out.println("Turno de: " + idActual);
            Carta tope = juego.getTope();
            System.out.println("Carta en mesa (tope): " + (tope != null ? tope.toString() : " - "));
            System.out.println("Color actual: " + juego.getColorActual() + " | Valor actual: " + juego.getValorActual());
            System.out.println("Tus puntos: " + actual.getPuntaje());
            System.out.println();

            // Mostrar mano
            List<Carta> mano = juego.getManoJugador(idActual);
            System.out.println("Tu mano:");
            for (int i = 0; i < mano.size(); i++) {
                System.out.printf("%d) %s%n", i + 1, mano.get(i).toString());
            }
            System.out.println();

            // Opciones
            System.out.println("Opciones:");
            System.out.println("1 - Jugar carta (ingresá el número de la carta)");
            System.out.println("2 - Robar carta del mazo");
            System.out.println("3 - Mostrar puntuaciones actuales");
            System.out.println("4 - Salir partida (abortar)");
            System.out.print("Elegí opción: ");

            int opcion = leerEnteroSeguro(1, 4);

            switch (opcion) {
                case 1 -> {
                    if (mano.isEmpty()) {
                        System.out.println("No tenés cartas para jugar. Debes robar.");
                        break;
                    }
                    System.out.print("Ingresá el número de la carta a jugar: ");
                    int idx = leerEnteroSeguro(1, mano.size()) - 1;
                    Carta cartaElegida = mano.get(idx);

                    Carta.Color colorDeclarado = null;
                    if (cartaElegida.getColor() == Carta.Color.Wild) {
                        colorDeclarado = elegirColorParaWild();
                        if (colorDeclarado == null) {
                            System.out.println("Cancelado. No se jugó la carta.");
                            break;
                        }
                    }

                    try {
                        juego.jugarCarta(idActual, cartaElegida, colorDeclarado);
                        System.out.println("Carta jugada: " + cartaElegida);
                    } catch (Exception ex) {
                        System.out.println("Movimiento inválido: " + ex.getMessage());
                    }

                    // tras jugar puede haberse terminado la ronda -> mostrar info abajo
                    mostrarGanadorRondaSiCorresponde();
                }
                case 2 -> {
                    try {
                        boolean puedeJugar = juego.robarCarta(idActual);
                        System.out.println("Robaste una carta.");
                        if (puedeJugar) {
                            System.out.println("La carta robada se puede jugar inmediatamente.");
                        } else {
                            System.out.println("No podés jugar la carta robada, el turno pasó.");
                        }
                        mostrarGanadorRondaSiCorresponde();
                    } catch (Exception ex) {
                        System.out.println("No se pudo robar: " + ex.getMessage());
                    }
                }
                case 3 -> {
                    mostrarPuntuacionesTemporales();
                }
                case 4 -> {
                    System.out.println("Abortando la partida en consola. Bye.");
                    return;
                }
            }
        }
    }

    private void mostrarGanadorRondaSiCorresponde() {
        Jugador g = juego.getGanadorRonda();
        if (g != null) {
            System.out.println("\n*** Fin de ronda! ***");
            System.out.printf("Ganador de la ronda: %s (+%d puntos)%n", g.getId(), g.getPuntaje());
            System.out.println("Puntuaciones actuales:");
            for (Jugador j : juego.getListaJugadores()) {
                System.out.printf("- %s: %d puntos%n", j.getId(), j.getPuntaje());
            }
            // limpiar la marca de ganadorRonda para que no vuelva a mostrarse
            juego.clearGanadorRonda();
            System.out.println("*** Nueva ronda iniciada automáticamente ***");
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
                String line = sc.nextLine().trim();
                int v = Integer.parseInt(line);
                if (v < min || v > max) {
                    System.out.print("Valor inválido. Ingresá un número entre " + min + " y " + max + ": ");
                    continue;
                }
                return v;
            } catch (Exception e) {
                System.out.print("Entrada inválida. Ingresá un número: ");
            }
        }
    }

    private Carta.Color elegirColorParaWild() {
        System.out.println("Elegí un color para el Wild:");
        Carta.Color[] opciones = Carta.Color.values();
        // construir lista excluyendo Wild
        List<Carta.Color> lista = new ArrayList<>();
        for (Carta.Color c : opciones) {
            if (c != Carta.Color.Wild) lista.add(c);
        }
        for (int i = 0; i < lista.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, lista.get(i).name());
        }
        System.out.print("Elegí opción (o 0 para cancelar): ");
        int sel = leerEnteroSeguro(0, lista.size());
        if (sel == 0) return null;
        return lista.get(sel - 1);
    }

    // método para ejecutar la consola directamente
    public static void main(String[] args) {
        Scanner scMain = new Scanner(System.in);
        System.out.println("=== UNO - Modo consola ===");
        System.out.print("¿Cuántos jugadores? (2 a 10): ");
        int n;
        while (true) {
            try {
                n = Integer.parseInt(scMain.nextLine().trim());
                if (n >= 2 && n <= 10) break;
                System.out.print("Ingresá un número entre 2 y 10: ");
            } catch (Exception e) {
                System.out.print("Entrada inválida. Ingresá un número: ");
            }
        }

        String[] nombres = new String[n];
        for (int i = 0; i < n; i++) {
            System.out.printf("Nombre del jugador %d: ", i + 1);
            String nombre = scMain.nextLine().trim();
            if (nombre.isEmpty()) nombre = "Jugador" + (i + 1);
            nombres[i] = nombre;
        }

        JuegoConsola consola = new JuegoConsola(nombres);
        consola.iniciar();
    }
}
