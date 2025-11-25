package uno.persistencia;

import uno.modelo.Jugador;

import java.io.*;
import java.util.*;

public class SistemaPuntuacion {

    private static final String ARCHIVO = "puntuacionUNO.txt";


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
            // SI NO EXISTE ...
        }

        return lista;
    }


    public static void guardarPuntos(List<RegistroPuntaje> puntos) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (RegistroPuntaje r : puntos) {
                pw.println(r.nombre + "," + r.puntos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void agregarScore(Jugador ganador) {          //axtualiza

        List<RegistroPuntaje> lista = cargarPuntos();

        boolean encontrado = false;

        for (RegistroPuntaje r : lista) {   //si ya existe
            if (r.nombre.equals(ganador.getId())) {
                r.puntos += ganador.getPuntaje();
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            lista.add(new RegistroPuntaje(ganador.getId(), ganador.getPuntaje()));
        }

        lista.sort((a, b) -> Integer.compare(b.puntos, a.puntos));

        if (lista.size() > 5) {                                                         // 5
            lista = lista.subList(0, 5);
        }

        guardarPuntos(lista);
    }

    public static class RegistroPuntaje {
        public String nombre;
        public int puntos;

        public RegistroPuntaje(String n, int p) {
            nombre = n;
            puntos = p;
        }
    }
}
