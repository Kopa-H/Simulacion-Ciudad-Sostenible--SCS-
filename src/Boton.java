import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;

public class Boton {
    private JButton boton;
    private String nombre;
    private ActionListener accion;

    public Boton(String nombreBoton, ActionListener accion) {
        this.nombre = nombreBoton;
        this.accion = accion;
        
        // Crear el botón con el nombre
        this.boton = new JButton(nombreBoton);
        this.boton.addActionListener(accion);
        
        // Aplicar la estética al botón
        aplicarEstetica();
    }

    public JButton getBoton() {
        return boton;
    }
    
    public String getNombreBoton() {
        return nombre;
    }

    public ActionListener getAccion() {
        return accion;
    }
    
    private void aplicarEstetica() {
        // Estética profesional para los botones
        Font font = new Font("Arial", Font.PLAIN, 16);
        boton.setFont(font);
        boton.setBackground(new Color(0, 123, 255)); // Azul para los botones
        boton.setForeground(Color.WHITE); // Color de texto blanco
        
        // Obtener el tamaño del texto y añadir un margen extra (por ejemplo, 10 píxeles en ambos ejes)
        int width = boton.getPreferredSize().width + 20; // Añadir espacio extra (10 píxeles en ambos lados)
        int height = boton.getPreferredSize().height + 10; // Añadir espacio extra arriba y abajo
        
        // Establecer el tamaño preferido con el margen extra
        boton.setPreferredSize(new Dimension(width, height));
    }

}
