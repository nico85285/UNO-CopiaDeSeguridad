package uno.App;

import uno.consola.JuegoConsola;
import uno.vista.MenuPrincipal;   // Ajustá este import según tu paquete real

import javax.swing.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("===== UNO =====");
        System.out.println("1 - Jugar con Interfaz Gráfica");
        System.out.println("2 - Jugar por Consola");
        System.out.print("Elegí una opción: ");

        int opcion = -1;
        while (opcion != 1 && opcion != 2) {
            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception ignored) {}
            if (opcion != 1 && opcion != 2) {
                System.out.print("Opción inválida. Elegí 1 o 2: ");
            }
        }


        //  1) INTERFAZ GRÁFICA

        if (opcion == 1) {
            System.out.println("Iniciando interfaz gráfica...");

            SwingUtilities.invokeLater(() -> {
                new MenuPrincipal().setVisible(true);
            });
            return;
        }


        //  2)CONSOLA


        System.out.print("¿Cuántos jugadores? (2 a 10): ");

        int n = 0;
        while (n < 2 || n > 10) {
            try {
                n = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception ignored) {}
            if (n < 2 || n > 10) {
                System.out.print("Número inválido. Elegí entre 2 y 10: ");
            }
        }

        String[] jugadores = new String[n];

        for (int i = 0; i < n; i++) {
            System.out.printf("Nombre del jugador %d: ", i + 1);
            String nombre = sc.nextLine().trim();
            if (nombre.isEmpty()) nombre = "Jugador" + (i + 1);
            jugadores[i] = nombre;
        }

        JuegoConsola consola = new JuegoConsola(jugadores);
        consola.iniciar();
    }
}
