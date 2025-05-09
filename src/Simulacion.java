import java.util.ArrayList;
import javax.swing.*;
import java.io.IOException;
import java.awt.Color;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Write a description of class Simulacion here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Simulacion {
        
    public JButton[][] gridButtons; // Matriz para almacenar los botones de la cuadrícula
    
    public static final int ROWS = Ciudad.ROWS;
    public static final int COLUMNS = Ciudad.COLUMNS;
    
    public static final int MAX_ESTADOS_GUARDADOS = 1000000;
    public static final int ESTADOS_ELIMINADOS_SOBRECARGA = 100;
    
    public boolean simulationRunning = true;
    public boolean runningForward = false;
    public boolean runningBackward = false;
    
    public Ciudad ciudad;
    
    public Tiempo tiempo;
    public Dinero dinero;
    
    private ArrayList<Estado> historialEstados;
    
    InterfazSimulacion interfaz;
    
    public Simulacion(Ciudad ciudad) {
        this.ciudad = ciudad;
        tiempo = new Tiempo();
        dinero = new Dinero();
        
        ciudad.tiempo = tiempo;
        ciudad.dinero = dinero;
        
        historialEstados = new ArrayList<>();
        gridButtons = new JButton[ROWS][COLUMNS];
        
        // Se crea la interfaz
        interfaz = new InterfazSimulacion(this, ciudad, gridButtons, tiempo);
    }
    
    public JButton[][] getGridButtons() {
        return gridButtons;
    }
    
    public void setGridButtons(JButton[][] newGridButtons) {
        gridButtons = newGridButtons;
    }
    
    /**
     * Captura el estado actual de la simulación y lo guarda en el historial de estados.
     */
    public void guardarEstado() {
        // Crear una copia profunda del estado actual del grid de botones (interfaz)
        JButton[][] copiaCuadricula = new JButton[Ciudad.ROWS][Ciudad.COLUMNS];
        for (int i = 0; i < Ciudad.ROWS; i++) {
            for (int j = 0; j < Ciudad.COLUMNS; j++) {
                JButton button = getGridButtons()[i][j];
                copiaCuadricula[i][j] = button;  // Copiar cada valor de la cuadrícula
            }
        }
    
        // Crear una copia profunda de las entidades usando el método deepCopy de Entidad
        CopyOnWriteArrayList<Entidad> copiaEntidades = new CopyOnWriteArrayList<>();
        for (Entidad entidad : ciudad.getEntidades()) {
            try {
                // Usamos deepCopy para obtener una copia profunda de la entidad
                Entidad entidadCopia = (Entidad) Entidad.deepCopy(entidad);
                copiaEntidades.add(entidadCopia);  // Agregar la entidad copiada a la lista
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    
        // Crear un nuevo estado con las copias
        Estado estadoActual = new Estado(copiaCuadricula, copiaEntidades);
        
        if (historialEstados.size() > MAX_ESTADOS_GUARDADOS) {
            // Eliminar los primeros 100 estados
            for (int i = 0; i < ESTADOS_ELIMINADOS_SOBRECARGA; i++) {
                historialEstados.remove(0);
            }
        }
        
        // Guardar el estado actual en la lista de historial de estados
        historialEstados.add(estadoActual);
    }
    
    // Método para retroceder al estado anterior
    private void retrocederEstado() {
        if (historialEstados.size() != tiempo.getCiclosTotales()) {
            throw new IllegalStateException("El sistema de retroceso de estados se ha desincronizado!");
        }
        
        if (!historialEstados.isEmpty()) {
            Estado estadoAnterior = historialEstados.remove(historialEstados.size() - 1); // Recupera el último estado
            
            setGridButtons(estadoAnterior.obtenerEstadoCuadricula()); // Restaura la cuadrícula
            
            ciudad.setEntidades(estadoAnterior.obtenerEstadoEntidades()); // Restaura las entidades
            
            // Reconectas o restauras la entidad seguida de cada entidad móvil
            ciudad.reconectarRelacionesEntidades();
        }
    }
    
    public void runSimulacion() {      
        long inicioBucle, duracionBucle;
        interfaz.actualizarEstadoVisual(this, ciudad, tiempo);
        
        while (simulationRunning) {
            
            // Si la velocidad es 0, detener temporalmente la simulación
            if (tiempo.getVelocidad() == 0) {
                try {
                    // Pausa breve para evitar sobrecargar la CPU
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue; // Saltar a la siguiente iteración
            }
            
            inicioBucle = System.currentTimeMillis();
            
            if (runningForward) {
        
                // Se hace que todas las entidades actúen según su estado
                for (Entidad entidad : ciudad.getEntidades()) {
                    entidad.actuar(ciudad);
                }
                
                // Se almacena el estado actual de la simulación para poder retroceder
                guardarEstado();
                
                // Incrementar el contador de pasos
                tiempo.transcurrirCiclo(ciudad, dinero);

            } else if (runningBackward && tiempo.getCiclosTotales() > 0) {
                
                // Se tira para atrás
                retrocederEstado();
                
                tiempo.revertirCiclo();
            }
            
            interfaz.actualizarEstadoVisual(this, ciudad, tiempo);
            
            duracionBucle = System.currentTimeMillis() - inicioBucle;
            tiempo.gestionarTranscursoTiempo(duracionBucle);
        }
    }
    
    // Método que actualiza visualmente la posición de una entidad en la cuadrícula
    public void mostrarEntidad(Ubicacion ubi, Color color) {
        gridButtons[ubi.getPosX()][ubi.getPosY()].setBackground(color); // Actualiza la celda correspondiente
    }

    public void agregarUsuarioNormal() {
        ciudad.agregarEntidad(this, 1, Usuario.class);
    }
    
    public void agregarUsuarioPremium() {
        Usuario usuario = ciudad.agregarEntidad(this, 1, Usuario.class);
        usuario.promocionarUsuario();
    }
    
    public void agregarTecnicoMantenimiento() {
        ciudad.agregarEntidad(this, 1, TecnicoMantenimiento.class);
    }
    
    public void agregarMecanico() {
        ciudad.agregarEntidad(this, 1, Mecanico.class);
    }
    
    public void agregarMoto() {
        ciudad.agregarEntidad(this, 1, Moto.class);
    }
    
    public void agregarGrupoEntidades() {
        // Añadimos n bases con vehículos
        ciudad.agregarBase(this, 1, 1);
        ciudad.agregarBase(this, 1, 1);
        
        // Añadimos n usuarios
        ciudad.agregarEntidad(this, 2, Usuario.class);
        
        // Añadimos n trabajadores de mantenimiento
        ciudad.agregarEntidad(this, 1, TecnicoMantenimiento.class);
        
        // Añadimos n trabajadores de mecánica
        ciudad.agregarEntidad(this, 1, Mecanico.class);
        
        // Añadimos n motos en ubicaciones aleatorias
        ciudad.agregarEntidad(this, 2, Moto.class);
    }
}
