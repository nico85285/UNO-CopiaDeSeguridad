package uno.modelo;

import java.util.ArrayList;


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

    public void removerCarta(Carta c){
        mano.remove(c);
    }

    public ArrayList<Carta> getMano(){
        return mano;
    }

    public int getPuntaje() { return puntaje; }

    public void sumarPuntos(int pts) { puntaje += pts; }

}