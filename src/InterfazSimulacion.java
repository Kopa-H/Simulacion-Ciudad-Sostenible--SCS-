import javax.swing.*;
import javax.swing.event.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.text.*;
import java.awt.Dimension;
import java.util.ArrayList;

public class InterfazSimulacion extends JFrame {   
    private static final Color DESCRIPTION_PANE_COLOR = Color.CYAN;
    
    private JPanel gridPanel;
    private JPanel southPanel;
    private JTextPane panelTextoInfo;
    private JPanel tiempoPanel;
    private JPanel checkBoxPanel;
    private JCheckBox checkBoxAutonomia;
    
    private JLabel tiempoLabel;

    JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, -Tiempo.MAX_VELOCIDAD, Tiempo.MAX_VELOCIDAD, 0);
    JLabel sliderLabel = new JLabel("Ajustar velocidad: " + 0 + " ciclos/segundo", JLabel.CENTER);
    JPanel sliderPanel = new JPanel();

    private boolean isLocked = false;

    private static final int HORIZONTAL_WINDOW_SIZE = 1500;
    private static final int VERTICAL_WINDOW_SIZE = 1000;
    
    private int rowSelected = 0;
    private int colSelected = 0;

    public InterfazSimulacion(Simulacion simulacion, Ciudad ciudad, JButton[][] gridButtons, Tiempo tiempo) {
        setTitle("Simulación de Ciudad");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gridPanel = new JPanel(new GridLayout(Simulacion.ROWS, Simulacion.COLUMNS));
        
        // Se crea la ventana independiente para el estatus panel
        Menu menu = new Menu();
        menu.nombre = "Información de Ubicación Seleccionada";
        JFrame frame = menu.crearNuevaVentana();
        frame.setSize(600, 400);
        frame.setAlwaysOnTop(true);
        frame.setLocation(0, 0);
        panelTextoInfo = new JTextPane();  // Cambiado a JTextPane

        for (int i = 0; i < Ciudad.ROWS; i++) {
            for (int j = 0; j < Ciudad.COLUMNS; j++) {
                JButton button = new JButton();
                button.setBackground(Color.WHITE);
                gridButtons[i][j] = button;

                final int row = i;
                final int col = j;
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (!isLocked) {
                            rowSelected = row;
                            colSelected = col;
                            actualizarEstadoInfoPanel(ciudad, row, col);
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (!isLocked) {
                            panelTextoInfo.revalidate();
                            panelTextoInfo.repaint();
                        }
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        isLocked = !isLocked;
                        
                        if (!isLocked) {
                            panelTextoInfo.setText("");
                        }
                        
                        panelTextoInfo.repaint();
                    }
                });

                gridPanel.add(button);
            }
        }

        agregarEsteticaPanelInfo();
        // Añadir el panel al JFrame
        frame.add(panelTextoInfo);
        frame.setVisible(true);
        
        add(gridPanel, BorderLayout.CENTER);
        
        // Crear el JLabel y establecerle el nombre
        tiempoLabel = new JLabel();
        // Ahora añadimos el panel de tiempo al panel exterior
        tiempoPanel = new JPanel();
        tiempoPanel.add(tiempoLabel);  // Añadir el panel con el label al panel exterior

        // Determinar el espaciado de las marcas principales dinámicamente, ajustándolo al rango
        int majorSpacing = tiempo.MAX_VELOCIDAD / 5;  // Dividir el máximo en 5 partes para las marcas principales
        
        // Determinar el espaciado de las marcas menores como una fracción de las principales
        int minorSpacing = majorSpacing / 5;  // Por ejemplo, dividir el espaciado mayor en 5 para las marcas menores
        
        // Configurar el espaciado en el slider
        speedSlider.setMajorTickSpacing(majorSpacing);
        speedSlider.setMinorTickSpacing(minorSpacing);
        
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                actualizarSliderSpeedSimulation(simulacion, tiempo);
            }
        });

        sliderPanel.setLayout(new BorderLayout());
        sliderPanel.add(sliderLabel, BorderLayout.NORTH);
        sliderPanel.add(speedSlider, BorderLayout.CENTER);

        southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(tiempoPanel);
        southPanel.add(Box.createVerticalStrut(10));
        southPanel.add(sliderPanel);
        southPanel.add(Box.createVerticalStrut(10));

        add(southPanel, BorderLayout.SOUTH);

        // Añadir el checkbox
        checkBoxAutonomia = new JCheckBox("Activar Autonomía");
        checkBoxAutonomia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBoxAutonomia.isSelected()) {
                    ciudad.activarAutonomiaEntidades();
                } else {
                    ciudad.desactivarAutonomiaEntidades();
                }
            }
        });

        checkBoxPanel = new JPanel();
        checkBoxPanel.add(checkBoxAutonomia);
        
        // Posicionar el checkbox en la parte derecha inferior
        southPanel.add(checkBoxPanel);
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    public void agregarEsteticaPanelInfo() {
         // Configuración de JTextPane
        panelTextoInfo.setEditable(false);
        panelTextoInfo.setBackground(DESCRIPTION_PANE_COLOR);  // Color de fondo cian
        StyledDocument doc = panelTextoInfo.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        panelTextoInfo.setPreferredSize(new java.awt.Dimension(200, 100));  // Tamaño fijo para el JTextPane
        panelTextoInfo.setSize(new java.awt.Dimension(200, 100));  // Fijar el tamaño
        panelTextoInfo.setMinimumSize(new java.awt.Dimension(200, 100));  // Establecer el tamaño mínimo
        panelTextoInfo.setMaximumSize(new java.awt.Dimension(200, 100));  // Establecer el tamaño máximo
        panelTextoInfo.setCaretColor(DESCRIPTION_PANE_COLOR); 
        
        UtilidadesMenu.aplicarFuenteTexto(panelTextoInfo);
    }
    
    public void actualizarEstadoVisual(Simulacion simulacion, Ciudad ciudad, Tiempo tiempo) {       
        // Limpiar la cuadrícula visual (poner todos los botones su color según la hora)
        for (int i = 0; i < ciudad.ROWS; i++) {
            for (int j = 0; j < ciudad.COLUMNS; j++) {
                simulacion.gridButtons[i][j].setBackground(Color.WHITE);
            }
        }
        
        // Mover todas las personas en la ciudad y actualizar su posición
        Ubicacion ubi = new Ubicacion();
        Color color;
        int x, y;
        boolean entidadVisible;
        for (Entidad entidad : ciudad.getEntidades()) {
            x = entidad.getUbicacion().getPosX();
            y = entidad.getUbicacion().getPosY();
            
            // Si su posición coincide con una base, entonces no se muestra
            if (!(entidad instanceof Base) && ciudad.posicionOcupadaPor(entidad.getUbicacion(), Base.class)) {
                continue;
            }
        
            ubi.setUbicacion(x, y);
            color = entidad.getColor();
        
            simulacion.mostrarEntidad(ubi, color); // Actualiza la posición en la interfaz gráfica
        }
        
        actualizarAspectoDiaNoche(tiempo);
        
        actualizarTiempoLabel(tiempo);
        
        // Sirve para que se actualice la información mostrada de la casilla señalada de la interfaz
        actualizarInfoCasillaSeleccionada(ciudad);
    }
    
    private void actualizarAspectoDiaNoche(Tiempo tiempo) {
        Color colorDia = tiempo.getColorHora();
        this.getContentPane().setBackground(colorDia);
        gridPanel.setBackground(colorDia);
        sliderPanel.setBackground(colorDia);
        speedSlider.setBackground(colorDia);
        southPanel.setBackground(colorDia);
        checkBoxPanel.setBackground(colorDia);
        checkBoxAutonomia.setBackground(colorDia);
        tiempoPanel.setBackground(colorDia);
        
        // Determinar el color de texto adecuado para contraste
        Color colorTexto = obtenerColorTextoContraste(colorDia);
        
        // Aplicar color de texto a los labels
        sliderLabel.setForeground(colorTexto);
        tiempoLabel.setForeground(colorTexto);
        checkBoxAutonomia.setForeground(colorTexto);
    }
    
    // Método para determinar el color de texto adecuado con contraste asegurado
    private Color obtenerColorTextoContraste(Color fondo) {
        // Calcula el brillo del color de fondo utilizando la fórmula para la luminancia
        double luminancia = (0.2126 * fondo.getRed() + 0.7152 * fondo.getGreen() + 0.0722 * fondo.getBlue()) / 255;
        
        // Si la luminancia es mayor que 0.5 (fondo claro), usar color negro; si no, usar blanco
        if (luminancia > 0.5) {
            return Color.BLACK;  // Fondo claro, texto oscuro
        } else {
            return Color.WHITE;  // Fondo oscuro, texto claro
        }
    }
    
    public void actualizarEstadoInfoPanel(Ciudad ciudad, int row, int col) {       
        panelTextoInfo.setText("");
        
        // Separar las entidades de tipo Base de las demás
        ArrayList<Entidad> entidadesBase = new ArrayList<>();
        ArrayList<Entidad> entidadesPersona = new ArrayList<>();
        ArrayList<Entidad> otrasEntidades = new ArrayList<>();
        
        for (Entidad entidad : ciudad.getEntidades()) {
            if (entidad instanceof Base) {
                entidadesBase.add(entidad);  // Añadir las entidades Base a una lista separada
            } else if (entidad instanceof Persona) {
                entidadesPersona.add(entidad);
            } else {
                otrasEntidades.add(entidad);  // Añadir el resto a otra lista
            }
        }
    
        // Mostrar las entidades Base primero
        for (Entidad entidad : entidadesBase) {
            if (entidad.getUbicacion().getPosX() == row && entidad.getUbicacion().getPosY() == col) {
                panelTextoInfo.setText(panelTextoInfo.getText() + entidad.toString() + "\n\n");
            }
        }
        
        for (Entidad entidad : entidadesPersona) {
            if (entidad.getUbicacion().getPosX() == row && entidad.getUbicacion().getPosY() == col) {
                panelTextoInfo.setText(panelTextoInfo.getText() + entidad.toString() + "\n\n");
            }
        }
    
        // Luego mostrar las otras entidades
        for (Entidad entidad : otrasEntidades) {
            if (entidad.getUbicacion().getPosX() == row && entidad.getUbicacion().getPosY() == col) {
                panelTextoInfo.setText(panelTextoInfo.getText() + entidad.toString() + "\n\n");
            }
        }
        
        // Asegúrate de mover el caret al inicio para que se vea el contenido superior
        panelTextoInfo.setCaretPosition(0); // Mover el caret al principio
    
        if (panelTextoInfo.getText().length() > 0) {
            panelTextoInfo.revalidate();
            panelTextoInfo.repaint();
        } else {
            panelTextoInfo.setText("Vacío en (" + row + "," + col + ")");
        }
    }

    public void actualizarSliderSpeedSimulation(Simulacion simulacion, Tiempo tiempo) {
        int sliderValue = speedSlider.getValue();
        int deadZone = 0;

        if (Math.abs(sliderValue) <= deadZone) {
            tiempo.setVelocidad(0);
            simulacion.runningForward = false;
            simulacion.runningBackward = false;
            speedSlider.setValue(0);
        } else if (sliderValue > deadZone) {
            tiempo.setVelocidad(sliderValue);
            simulacion.runningForward = true;
            simulacion.runningBackward = false;
        } else if (sliderValue < -deadZone) {
            tiempo.setVelocidad(-sliderValue);
            simulacion.runningForward = false;
            simulacion.runningBackward = true;
        }

        int velocidadActual = tiempo.getVelocidad();
        String signo = "";
        
        if (simulacion.runningForward && velocidadActual != 0) {
            signo = " +";
        } else if (!simulacion.runningForward && velocidadActual != 0) {
            signo = " -";
        }
        
        sliderLabel.setText("Velocidad: " + signo + Math.abs(velocidadActual) + " ciclos/segundo");
    }

    public void actualizarTiempoLabel(Tiempo tiempo) {
        // Formatear el tiempo en una cadena
        String tiempoFormateado = tiempo.formatearTiempo();
        // Actualizar el texto del label con la cadena formateada
        tiempoLabel.setText("[ " + tiempoFormateado + " ]");
    }
    
    public void actualizarInfoCasillaSeleccionada(Ciudad ciudad) {
        actualizarEstadoInfoPanel(ciudad, rowSelected, colSelected);
    }
}
