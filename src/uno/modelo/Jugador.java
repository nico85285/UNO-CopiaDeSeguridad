package uno.modelo;

import java.util.ArrayList;
import java.util.List;

public class Jugador {
    private String id;
    private ArrayList<Carta> mano;
    private int puntaje;

    public Jugador(String id){
        this.id = id;
        mano = new ArrayList<>();
        this.puntaje = 0;
    }

    public String getId(){
        return id;
    }

    public void agregarCarta(Carta c){
        mano.add(c);
    }

    public void agregarCartas(Carta[] cartas){   // para robar varias de una
        for(Carta c : cartas){
            mano.add(c);
        }
    }

    public void removerCarta(Carta c){
        mano.remove(c);
    }

    public ArrayList<Carta> getMano(){
        return mano;
    }

    public int getPuntaje() { return puntaje; }

    public void sumarPuntos(int pts) { puntaje += pts; }

    public int getCantidadCartas(){
        return mano.size();
    }
}