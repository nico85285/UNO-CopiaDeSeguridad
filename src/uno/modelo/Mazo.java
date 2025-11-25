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

        if (pila.size() <= 1) return;
        Carta tope = pila.remove(pila.size() - 1);
        cartasEnElMazo = 0;

        for (Carta c : pila) {
            agregarCarta(c);
        }

        pila.clear();
        pila.add(tope);

        mezclar();
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

    public void agregarCarta(Carta c) {
        if (c == null) return;

        if (cartasEnElMazo >= cartas.length) {
            int nuevoTam = cartas.length * 2;
            Carta[] nuevo = new Carta[nuevoTam];
            System.arraycopy(cartas, 0, nuevo, 0, cartasEnElMazo);
            cartas = nuevo;
        }
        cartas[cartasEnElMazo++] = c;
    }

}
