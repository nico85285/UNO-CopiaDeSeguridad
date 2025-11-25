package uno.vista;
import javax.swing.*;
import java.awt.*;

public class FondoPanel extends JPanel {




    private Image imagen;

    public FondoPanel(String ruta) {
        java.net.URL url = getClass().getResource(ruta);

        if (url != null) {
            imagen = new ImageIcon(url).getImage();

        } else {
            System.out.println("No se encontró la imagen: " + ruta);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (imagen != null) {
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }
}