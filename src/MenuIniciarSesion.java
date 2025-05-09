import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;

public class MenuIniciarSesion extends Menu {
    
    private GestorMenus gestorMenus;
    private JFrame frame;
    protected JPanel panel;
    
    protected String nombreMenuPrincipal;
    
    private Simulacion simulacion;
    private Ciudad ciudad;

    public MenuIniciarSesion(Simulacion simulacion, Ciudad ciudad, GestorMenus gestorMenus) {
        this.simulacion = simulacion;
        this.ciudad = ciudad;

        this.gestorMenus = gestorMenus;
        this.frame = gestorMenus.frame;
        
        Menu menu = new Menu();
        menu.nombre = "Iniciar Sesión";
        this.nombreMenuPrincipal = menu.nombre;
        
        frame.setTitle(menu.nombre);  // Establece el título de la ventana
        
        this.panel = menu.crearPanel();
        panel.setBackground(Color.GRAY);

        agregarBotonesMenu(menu.botones);
        
        agregarBotones(menu.botones, panel);
    }
    
    // Se le pasa como parámetro el tipo de usuario que ingresa al sistema, dandole unas opciones u otras
    private void iniciarMenuSistema(TipoUsuario tipoUsuario, Persona personaAccedida) {
        // Se añaden al contenedor de páginas las de cada tipo de usuario
        MenuSistema menuSistema = new MenuSistema(simulacion, ciudad, tipoUsuario, gestorMenus, personaAccedida);
    
        // Al navegar a un nuevo panel, lo agregamos a la pila
        gestorMenus.panelHistory.push(panel);
        gestorMenus.agregarBotonAtras(this, menuSistema.panel);
        gestorMenus.cardsPanel.add(menuSistema.panel, menuSistema.nombreMenuPrincipal);
        gestorMenus.navegarA(this, menuSistema.panel);
    }
    
    private Persona identificarse(Class<?> clasePersona) {
        Menu menu = new Menu();
        menu.nombre = "Identificación de Usuario";
        JDialog dialogo = menu.crearNuevoDialogo();
    
        // Crear el panel para el submenú
        JPanel panel = menu.crearPanel();
    
        // Usar un array para hacer mutable la variable de la ID seleccionada
        final String[] idPersona = {null};
        final Persona[] personaSeleccionada = {null};  // Variable para almacenar la entidad seleccionada
    
        // Crear el campo de texto para ingresar la ID
        JTextField idField = new JTextField();
        idField.setPreferredSize(new Dimension(200, 30));
    
        // Crear y añadir un botón para confirmar la ID
        menu.botones.put("Confirmar ID", new Boton("Confirmar ID", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                idPersona[0] = idField.getText();
                if (idPersona[0] != null && !idPersona[0].isEmpty()) {
                    // Intentar convertir el String a int
                    int idIngresada = -1;
                    try {
                        idIngresada = Integer.parseInt(idPersona[0]);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialogo, "La ID debe ser un número válido.", "Error de Identificación", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
    
                    boolean personaValida = false;
                    // Iterar sobre las entidades para verificar si la ID existe y corresponde a la clase correcta
                    for (Entidad entidad : ciudad.getEntidades()) {
                        if (entidad instanceof Persona && entidad.getId() == idIngresada && clasePersona.isInstance(entidad)) {
                            // Si la ID coincide y es del tipo correcto
                            personaValida = true;
                            personaSeleccionada[0] = (Persona) entidad; // Guardar la entidad seleccionada
                            dialogo.dispose(); // Cerrar la ventana modal
                            break;
                        }
                    }
    
                    if (!personaValida) {
                        JOptionPane.showMessageDialog(dialogo, "ID no válida o no corresponde a un " + (clasePersona == null ? "Administrador" : clasePersona.getSimpleName()), "Error de Identificación", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialogo, "Por favor, ingrese una ID válida.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }));
    
        // Añadir el campo de texto al panel
        panel.add(new JLabel("Ingrese su ID de usuario:"));
        panel.add(idField);
    
        // Añadir el botón al panel
        agregarBotones(menu.botones, panel);
    
        // Añadir el panel al JDialog
        dialogo.add(agregarScroll(panel));
    
        // Mostrar el JDialog como ventana modal
        dialogo.setVisible(true);
    
        // Retorna la entidad seleccionada después de que el diálogo se cierre
        return personaSeleccionada[0];
    }

    private void agregarBotonesMenu(LinkedHashMap<String, Boton> botones) {
        // Definir roles directamente en el HashMap
        botones.put("Administrador", new Boton("Administrador", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Impresora.printVerde("\nInicio de sesión en el sistema como ADMINISTRADOR");
                iniciarMenuSistema(TipoUsuario.ADMINISTRADOR, null);
            }
        }));

        botones.put("Usuario", new Boton("Usuario", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Persona personaIdentificada = identificarse(Usuario.class);
                
                if (personaIdentificada != null) {
                    Impresora.printVerde("\nInicio de sesión de [" + personaIdentificada.toString() + "] en el sistema");
                    
                    if (((Usuario)personaIdentificada).isPremium) {
                        iniciarMenuSistema(TipoUsuario.USUARIO_PREMIUM, personaIdentificada);
                    } else {
                        iniciarMenuSistema(TipoUsuario.USUARIO_NORMAL, personaIdentificada);
                    }
                    
                }
            }
        }));
        botones.put("Técnico de Mantenimiento", new Boton("Técnico de Mantenimiento", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Persona personaIdentificada = identificarse(TecnicoMantenimiento.class);
                
                if (personaIdentificada != null) {
                    Impresora.printVerde("\nInicio de sesión de [" + personaIdentificada.toString() + "] en el sistema");
                    iniciarMenuSistema(TipoUsuario.TECNICO_MANTENIMIENTO, personaIdentificada);
                }
            }
        }));
        botones.put("Mecánico", new Boton("Mecánico", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               Persona personaIdentificada = identificarse(Mecanico.class);
                
                if (personaIdentificada != null) {
                    Impresora.printVerde("\nInicio de sesión de [" + personaIdentificada.toString() + "] en el sistema");
                    iniciarMenuSistema(TipoUsuario.MECANICO, personaIdentificada);
                }
            }
        }));
    }
}
