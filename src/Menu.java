import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JScrollPane;
import java.util.LinkedHashMap;

// Clase abstracta para manejar los menús
public class Menu {
    protected LinkedHashMap<String, Boton> botones;   
    
    protected String nombre;
    
    // Tamaño ventanas por defecto (cada submenú puede alterarlo)
    protected int WINDOW_WIDTH = 500;
    protected int WINDOW_HEIGHT = 500;
        
    protected JPanel panel;
    protected JFrame frame;
    
    public enum TipoUsuario {
        USUARIO_NORMAL,
        USUARIO_PREMIUM,
        TECNICO_MANTENIMIENTO,
        MECANICO,
        ADMINISTRADOR
    }

    // Constructor
    public Menu() {
        botones = new LinkedHashMap<>();
    }

    // Método abstracto que las subclases implementarán para crear el menú específico
    public void agregarBotones(LinkedHashMap<String, Boton> botones, JPanel panel) {
        // Añadir los botones al menú con sus funcionalidades en el orden de inserción del LinkedHashMap
        for (String nombreBoton : botones.keySet()) {
            Boton boton = botones.get(nombreBoton);
            agregarBotonPosicionado(panel, boton.getBoton(), "middle");
        }       
    }
    
    // Método para crear la ventana
    protected JFrame crearNuevaVentana() {
        // Crear el JFrame para la ventana principal
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        frame.setTitle(nombre);
        
        // Centrar la ventana en la pantalla
        frame.setLocationRelativeTo(null);
        
        Impresora.printNaranja("\nSe ha abierto una nueva ventana " + nombre);
            
        return frame;
    }
    
    // Método para crear un JDialog
    protected JDialog crearNuevoDialogo() {
        JDialog dialog = new JDialog(); // Sin especificar el Frame, se crea independiente
        dialog.setModal(true); // Hace que sea modal
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        dialog.setTitle(nombre);
        
        // Centrar el dialogo en la pantalla
        dialog.setLocationRelativeTo(null);
        
        Impresora.printNaranja("\nSe ha abierto un nuevo diálogo " + nombre);
        
        return dialog;
    }
    
    // Método para crear un panel con desplazamiento
    public JPanel crearPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setName(nombre);  // Asignamos un nombre único al panel
        
        return panel;  // Retornar el panel envuelto en el scroll
    }
    
    public JScrollPane agregarScroll(JPanel panel) {
        // Envolver el panel en un JScrollPane
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        return scrollPane;
    }
    
    // Método para agregar botones con posición controlada
    protected void agregarBotonPosicionado(JPanel panel, JButton boton, String posicion) {
        FlowLayout layout;
        switch (posicion.toLowerCase()) {
            case "left": layout = new FlowLayout(FlowLayout.LEFT); break;
            case "right": layout = new FlowLayout(FlowLayout.RIGHT); break;
            case "middle": default: layout = new FlowLayout(FlowLayout.CENTER); break;
        }

        JPanel buttonPanel = new JPanel(layout);
        buttonPanel.add(boton);
        panel.add(buttonPanel);
        panel.add(Box.createVerticalStrut(10));  
    }
}