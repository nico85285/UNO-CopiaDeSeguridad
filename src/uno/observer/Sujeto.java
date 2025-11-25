package uno.observer;

import java.util.ArrayList;

public class Sujeto {

    private ArrayList<Observador> observadores = new ArrayList<>();

    public void agregarObservador(Observador o){
        observadores.add(o);
    }

    public void notificar(){

        for(Observador o: observadores){
            o.actualizar();
        }

    }
}
