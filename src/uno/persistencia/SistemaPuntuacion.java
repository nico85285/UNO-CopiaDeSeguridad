package uno.persistencia;

import uno.modelo.Jugador;

import java.io.*;
import java.util.*;

public class SistemaPuntuacion {

    private static final String ARCHIVO = "puntuacionUNO.txt";

    // ============================
    // CARGAR RANKING
    // ============================
    public static List<RegistroPuntaje> cargarPuntos() {
        List<RegistroPuntaje> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    String nombre = partes[0].trim();
                    int pts = Integer.parseInt(partes[1].trim());
                    lista.add(new RegistroPuntaje(nombre, pts));
                }
            }

        } catch (Exception e) {
            // si no existe el archivo, no hago nada
        }

        return lista;
    }


    // ============================
    // GUARDAR RANKING
    // ============================
    public static void guardarPuntos(List<RegistroPuntaje> puntos) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (RegistroPuntaje r : puntos) {
                pw.println(r.nombre + "," + r.puntos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ============================
    // ACTUALIZAR PUNTAJE ACUMULADO
    // ============================
    public static void agregarScore(Jugador ganador) {

        List<RegistroPuntaje> lista = cargarPuntos();

        boolean encontrado = false;

        // Si el jugador ya existe en el ranking, sumar sus puntos
        for (RegistroPuntaje r : lista) {
            if (r.nombre.equals(ganador.getId())) {
                r.puntos += ganador.getPuntaje();  // ← SUMA ACUMULADA
                encontrado = true;
                break;
            }
        }

        // Si no existe, agregarlo nuevo
        if (!encontrado) {
            lista.add(new RegistroPuntaje(ganador.getId(), ganador.getPuntaje()));
        }

        // Ordenar de mayor a menor
        lista.sort((a, b) -> Integer.compare(b.puntos, a.puntos));

        // Mantener solo TOP 5
        if (lista.size() > 5) {
            lista = lista.subList(0, 5);
        }

        guardarPuntos(lista);
    }


    // ============================
    // CLASE INTERNA
    // ============================
    public static class RegistroPuntaje {
        public String nombre;
        public int puntos;

        public RegistroPuntaje(String n, int p) {
            nombre = n;
            puntos = p;
        }
    }
}
