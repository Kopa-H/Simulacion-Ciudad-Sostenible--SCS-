import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import javax.swing.JScrollPane;
import java.util.LinkedHashMap;

public class GestorMenus extends Menu {

    protected CardLayout cardLayout;  
    protected JPanel cardsPanel;  // Panel que usará CardLayout
    protected Stack<JPanel> panelHistory = new Stack<>();
    protected JFrame frame;
    protected JPanel panelGestorMenus;
    
    protected static final int WINDOW_WIDTH = 500;
    protected static final int WINDOW_HEIGHT = 700;
    
    private Simulacion simulacion;
    private Ciudad ciudad;
    
    // Constructor
    public GestorMenus(Simulacion simulacion, Ciudad ciudad) { 
        this.simulacion = simulacion;
        this.ciudad = ciudad;
        
        cardLayout = new CardLayout();
    
        // Se crea un JPanel que usa la organización de CardLayout
        cardsPanel = new JPanel(cardLayout);

        Menu menu = new Menu();
        menu.nombre = "Gestor Menús";

        frame = menu.crearNuevaVentana(); // Se crea una nueva ventana para GestorMenus, por la que se navegará a los distintos submenús
        frame.add(cardsPanel);
        
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        panelGestorMenus = menu.crearPanel();
        panelGestorMenus.setBackground(Color.GRAY);
        
        // Añadir los menús al contenedor CardLayout
        cardsPanel.add(panelGestorMenus, menu.nombre);  // Añadir el panel principal al CardLayout
        
        // Añadimos la ventana principal a la pila
        panelHistory.push(panelGestorMenus);
        
        LinkedHashMap<String, Boton> botones = new LinkedHashMap<>();
        // Crear y añadir el botón de iniciar sesión al HashMap
        botones.put("IniciarSesion", new Boton("Iniciar Sesión", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                iniciarMenuIniciarSesion(frame);
            }
        }));
        
        agregarBotones(botones, panelGestorMenus);
        
        frame.setVisible(true);
    }
    
    private void iniciarMenuIniciarSesion(JFrame frame) {
        MenuIniciarSesion menuIniciarSesion = new MenuIniciarSesion(simulacion, ciudad, this);
    
        // Al navegar a un nuevo panel, lo agregamos a la pila

        agregarBotonAtras(this, menuIniciarSesion.panel);
        cardsPanel.add(menuIniciarSesion.panel, menuIniciarSesion.nombreMenuPrincipal);
        navegarA(this, menuIniciarSesion.panel);
    }
    
    // Método para agregar un botón "Atrás" con funcionalidad
    protected void agregarBotonAtras(Menu menuAnterior, JPanel panel) {
        JButton botonAtras = new JButton("Atrás");
        botonAtras.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!panelHistory.isEmpty()) {
                    // Obtener el último panel de la pila
                    JPanel panelAnterior = panelHistory.pop();
                    
                    // Si la pila se ha vaciado, se añade la inicial que es la de Gestor Menús
                    if (panelHistory.isEmpty()) {
                        panelHistory.push(panelGestorMenus);
                    }
                    
                    // Navegar al panel anterior
                    navegarA(menuAnterior, panelAnterior);
                }
            }
        });
        agregarBotonPosicionado(panel, botonAtras, "left");
    }

    // Método para navegar a un nuevo panel y almacenar el panel actual en la pila
    protected void navegarA(Menu menu, JPanel panel) {
        if (panel == null) {
            Impresora.printRojo("\nError al navegar, panel nulo");
            return;
        }
        
        // Mostrar el panel
        cardLayout.show(cardsPanel, panel.getName());
        
        frame.setTitle(panel.getName());
        
        Impresora.printNaranja("\nNavegando a " + panel.getName());
    }
}