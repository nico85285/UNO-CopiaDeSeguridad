package uno.modelo;

import java.util.ArrayList;
import java.util.List;

public class ManejadorPila {
    private final ArrayList<Carta> pila = new ArrayList<>();

    public void agregarCarta(Carta c) {
        if (c != null) pila.add(c);
    }

    public Carta getTope() {
        return pila.isEmpty() ? null : pila.get(pila.size() - 1);
    }

    public ArrayList<Carta> getPila() {
        return pila;
    }

    public void limpiar() {
        pila.clear();
    }

    public Carta rearmarEnMazo(Mazo mazo) {
        if (pila.isEmpty()) return null;
        Carta tope = pila.remove(pila.size() - 1);
        for (Carta c : pila) {
            mazo.agregarCarta(c);
        }
        pila.clear();
        pila.add(tope);
        mazo.mezclar();
        return tope;
    }

    public Carta colocarInicialValida(Mazo mazo) {
        Carta primera = mazo.robarCarta();
        while (primera.getValor() == Carta.Valor.Wild ||
                primera.getValor() == Carta.Valor.WildCuatro ||
                primera.getValor() == Carta.Valor.Skip ||
                primera.getValor() == Carta.Valor.Reversa ||
                primera.getValor() == Carta.Valor.RobarDos) {
            mazo.agregarCarta(primera);
            mazo.mezclar();
            primera = mazo.robarCarta();
        }
        pila.add(primera);
        return primera;
    }
}
