package uno.modelo;

public class GestorEfectos {

    public static class EfectoAccion {
        public int cartasARobar = 0;
        public boolean saltar = false;
        public boolean invertir = false;
        public Carta.Color colorDeclarado = null;
    }

    public EfectoAccion evaluar(Carta carta, Carta.Color colorDeclarado) {
        EfectoAccion r = new EfectoAccion();
        if (carta == null) return r;

        switch (carta.getValor()) {
            case RobarDos -> {
                r.cartasARobar = 2;
            }
            case WildCuatro -> {
                r.cartasARobar = 4;
                r.colorDeclarado = colorDeclarado;
            }
            case Wild -> {
                r.colorDeclarado = colorDeclarado;
            }
            case Skip -> {
                r.saltar = true;
            }
            case Reversa -> {
                r.invertir = true;
            }
            default -> { /* no hay efecto */ }
        }

        return r;
    }
}
