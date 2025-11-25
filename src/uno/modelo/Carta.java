package uno.modelo;

public class Carta {

    public enum Color {
        Rojo, Azul, Verde, Amarillo, Wild;

        private static final Color[] colores = Color.values();

        public static Color getColor(int i){
            return Color.colores[i];
        }
    }

    public enum Valor {
        Cero, Uno, Dos, Tres, Cuatro, Cinco, Seis, Siete, Ocho, Nueve, RobarDos, Skip, Reversa, Wild, WildCuatro;

        private static final Valor[] valores = Valor.values();

        public static Valor getValor(int i){
            return Valor.valores[i];
        }
   }

    private final Color color;
    private final Valor valor;

    public Carta (final Color color, final Valor valor){
        this.color = color;
        this.valor = valor;
    }

    public Color getColor() {
        return this.color;
    }

    public Valor getValor() {
        return this.valor;
    }

    public String toString(){
        return color + "_" + valor;
    }
}
