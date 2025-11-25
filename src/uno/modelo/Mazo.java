package uno.modelo;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class Mazo {
    private int cartasEnElMazo;
    private Carta[] cartas;

    public Mazo(){
        cartas = new Carta[108];             // Segun google el mazo de uno tiene 108 cartas.

    }

    public void armarMazo () {
        Carta.Color[] colores = Carta.Color.values();
        cartasEnElMazo = 0;

        for (int i = 0; i < colores.length-1; i++) {
            Carta.Color color = colores[i];

            cartas[cartasEnElMazo++] = new Carta(color, Carta.Valor.getValor(0));  //Agrego solo un 0 por color

            for (int j = 1; j < 10; j++){
                cartas[cartasEnElMazo++] = new Carta(color, Carta.Valor.getValor(j));   // 2 cartas por color por valor
                cartas[cartasEnElMazo++] = new Carta(color, Carta.Valor.getValor(j));
            }

            Carta.Valor[] valores = new Carta.Valor[]{
                    Carta.Valor.RobarDos, Carta.Valor.Skip, Carta.Valor.Reversa};


            for(Carta.Valor valor : valores){
                cartas[cartasEnElMazo++] = new Carta(color,valor);
                cartas[cartasEnElMazo++] = new Carta(color,valor);
            }
        }

        Carta.Valor[] valores = new Carta.Valor[]{Carta.Valor.Wild, Carta.Valor.WildCuatro};
        for (Carta.Valor valor : valores){
            for (int i = 0; i<4; i++){
                cartas[cartasEnElMazo++] = new Carta(Carta.Color.Wild, valor);
            }
        }
    }

    public void rearmarDesdePila(ArrayList<Carta> pila) {
        // la última carta de la pila es el tope de descarte y NO se usa
        Carta tope = pila.remove(pila.size() - 1);

        // agregar todo el resto al mazo
        for (Carta c : pila) {
            agregarCarta(c);
        }

        // limpiar pila y volver a dejar solo el tope
        pila.clear();
        pila.add(tope);
    }

    public void remplazarMazo (ArrayList<Carta> cartas){
        this.cartas = cartas.toArray(new Carta[cartas.size()]);
        this.cartasEnElMazo = this.cartas.length;
    }

    public boolean isEmpty() {
        return cartasEnElMazo == 0;
    }

    public void mezclar () {
        int n = cartas.length;
        Random random = new Random();

        for (int i = 0; i < cartas.length; i++){

            int valorRandom = i + random.nextInt(n - i);
            Carta cartaRandom = cartas[valorRandom];
            cartas[valorRandom] = cartas[i];
            cartas[i] = cartaRandom;
        }
    }

    public Carta robarCarta () throws IllegalArgumentException {
        if (isEmpty()) {
            throw new IllegalArgumentException("El mazo esta vacio!");
        }

        return cartas[--cartasEnElMazo];
    }

    public ImageIcon robarCartaImagen () throws IllegalArgumentException{
        if (isEmpty()){
            throw new IllegalArgumentException("El mazo esta vacio!");
        }

        return new ImageIcon(cartas[--cartasEnElMazo].toString() + ".png");
    }

    public void agregarCarta(Carta c) {
        if (c == null) return;
        // si no cabe, agrando el array (doblo tamaño)
        if (cartasEnElMazo >= cartas.length) {
            int nuevoTam = cartas.length * 2;
            Carta[] nuevo = new Carta[nuevoTam];
            System.arraycopy(cartas, 0, nuevo, 0, cartasEnElMazo);
            cartas = nuevo;
        }
        cartas[cartasEnElMazo++] = c;
    }

    /** Agregar varias cartas desde una lista (útil al rearmar la pila) */
    public void agregarCartas(java.util.Collection<Carta> coleccion) {
        if (coleccion == null || coleccion.isEmpty()) return;
        for (Carta c : coleccion) {
            agregarCarta(c);
        }
    }

    /** Agregar varias cartas desde un arreglo */
    public void agregarCartas(Carta[] arr) {
        if (arr == null || arr.length == 0) return;
        for (Carta c : arr) {
            agregarCarta(c);
        }
    }

    public Carta[] robarCarta(int n){
        if (n < 0){
            throw new IllegalArgumentException(" asdasdasd");                              // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }

        if (n > cartasEnElMazo){
            throw new IllegalArgumentException("No se pueden robar mas cartas que las que estan en el mazo!. quedan " + cartasEnElMazo+ " cartas!");
        }

        Carta[] carta = new Carta[n];

        for (int i = 0; i < n; i++){
            carta[i] = cartas[--cartasEnElMazo];
        }

        return carta;
    }
}
