import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Write a description of class Entidad here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class Entidad implements Serializable
{
    public Ubicacion ubicacion;
    private Color color; // color mostrado en la visualización
    
    private static final int ESTADO_MECANICO_PERFECTO = 750;
    private static final int ESTADO_MECANICO_SUMADO_POR_REPARACION = 5;
    
    // Variable de instancia para almacenar el ID único de cada usuario
    private int id;
    
    // Variable que va aumentando la probabilidad de que los usuarios reporten fallos mecanicos cuanto más baja está
    private int estadoMecanico = ESTADO_MECANICO_PERFECTO; 
    
    private int contadorComprobacionFallo = 0; // Contador de llamadas a la función
    private final int limiteComprobacionFallo = 250; // Número de llamadas antes de hacer la comprobación

    protected int edad; // El número de steps que ha vivido la entidads
    
    // Variable registra si la entidad sufre un daño mecánico. En ese caso, el próximo usuario en interactuar con ella lo notificará y el mecánico lo arreglará.
    private boolean tieneFalloMecanico = false;
    private boolean tieneAlertaFalloMecanico = false;
    
    public int totalFallosMecanicos = 0;
    
    /**
     * Constructor que permite inicializar la EntidadMovil en una ubicación específica.
     * 
     * @param x Coordenada X inicial
     * @param y Coordenada Y inicial
     */
    public Entidad(int x, int y) {
        ubicacion = new Ubicacion(x, y); // Ubicación inicial en las coordenadas (x, y)
    }
    
    /**
     * Método para obtener la ubicación actual de la entidad.
     * 
     * @return La ubicación actual (coordenadas X e Y).
     */
    public Ubicacion getUbicacion() {
        return ubicacion;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public Color getColor() {
        return color;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public double getPorcentajeEstadoMecanico() {
        return ((double) estadoMecanico / ESTADO_MECANICO_PERFECTO) * 100;
    }
    
    public void activarAlertaFalloMecanico() {
        tieneAlertaFalloMecanico = true;
        Impresora.printNaranja("\nSe ha ACTIVADO una alerta de fallo mecánico sobre " + this.toSimpleString());
    }
    
    public void desactivarAlertaFalloMecanico() {
        tieneAlertaFalloMecanico = false;
        Impresora.printVerde("\nSe ha DESACTIVADO una alerta de fallo mecánico sobre " + this.toSimpleString());
    }
    
    public boolean tieneAlertaFalloMecanico() {
        return tieneAlertaFalloMecanico;
    }
    
    public boolean tieneFalloMecanico() {
        return tieneFalloMecanico;
    }
   
    // La edad se almacena en ciclos vividos (para más info revisar la clase Tiempo)
    public void sumarEdad() {
        edad++;
    }
    
    public double getProbabilidadFalloMecanico() {
        // Regla de tres inversa: si estadoMecanico es 0, fallo 100%; si es estadoMaximo, fallo 0%.
        return 100 - getPorcentajeEstadoMecanico();
    }
    
    public void deteriorarEstadoMecanico() {
        if (estadoMecanico > 0) {
            // Hacer que la disminución de estadoMecanico sea aleatoria
            if (new Random().nextDouble() < 0.5) { // Probabilidad de restar
                estadoMecanico--; // Disminuye el estado mecánico en 1, aunque la entidad ya tenga un fallo mecáncico (se deteriora por envejecimiento)
            }
            
            // Si la entidad ya tiene un fallo mecánico, no hace falta seguir
            if (tieneFalloMecanico) {
                return;
            }
            
            contadorComprobacionFallo++; // Incrementa el contador en cada llamada
    
            // Solo comprueba la probabilidad de fallo cuando el contador llega al límite
            if (contadorComprobacionFallo >= limiteComprobacionFallo) {
                // Genera un número aleatorio y verifica si ocurre un fallo mecánico
                if (new Random().nextDouble() * 100 < getProbabilidadFalloMecanico()) {
                    activarFalloMecanico();
                    
                    // Solo las bases alertan del fallo, los otros vehículos requieren de que un usuario los vaya a utilizar, lo vea y lo notifique
                    if (this instanceof Base) {
                        activarAlertaFalloMecanico();
                    }
                }
                contadorComprobacionFallo = 0; // Reinicia el contador después de la comprobación
            }
        } else {
            
            // Si la entidad ya tiene un fallo mecánico, no hace falta seguir
            if (tieneFalloMecanico) {
                return;
            }
            
            activarFalloMecanico(); // Si el estado mecánico llega a 0, forzar el fallo
            
            // Solo las bases alertan del fallo, los otros vehículos requieren de que un usuario los vaya a utilizar, lo vea y lo notifique
            if (this instanceof Base) {
                activarAlertaFalloMecanico();
            }
        }
    }
    
    public void restaurarEstadoMecanico() {
        estadoMecanico += ESTADO_MECANICO_SUMADO_POR_REPARACION;
        
        // Se tiene que cargar al tope para repararse
        if (getPorcentajeEstadoMecanico() >= 100) {
            desactivarFalloMecanico();
            desactivarAlertaFalloMecanico();
        }
    }
    
    // Función que se llama si ocurre un fallo mecánico
    public void activarFalloMecanico() {
        tieneFalloMecanico = true;
        totalFallosMecanicos++;
        Impresora.printNaranja("\nSe ha ACTIVADO un fallo mecánico en " + this.toSimpleString());
    }
    
    public void desactivarFalloMecanico() {
        tieneFalloMecanico = false;
        Impresora.printVerde("\nSe ha DESACTIVADO un fallo mecánico en " + this.toSimpleString());
    }
    
    // Acción que ejecuta el deterioro y posibilidad de fallo
    public void actuar(Ciudad ciudad) {      
        if (this instanceof Vehiculo || this instanceof Base) {
            this.deteriorarEstadoMecanico();
        }
        
        this.sumarEdad();
    }
    
    // Método para hacer una copia profunda de una entidad
    public static Object deepCopy(Object object) throws IOException, ClassNotFoundException {
        // Serializa el objeto a un flujo de bytes
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);

        // Deserializa el objeto para obtener una copia profunda
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return objectInputStream.readObject();
    }

    @Override
    public String toString() {
        String str = "Ubi: " + ubicacion.toString() + "  |  Id: " + id + "  |  Tipo: " + getClass().getSimpleName();
        
        // Solo añadir estado mecánico y fallo mecánico si es una instancia de Vehiculo o Base
        if (this instanceof Vehiculo || this instanceof Base) {
            str += "  |  Estado Mecánico: " + (int) getPorcentajeEstadoMecanico() + "%" + "  |  Alerta Fallo Mecánico: " + tieneAlertaFalloMecanico() + "  |  Fallo Mecánico: " + tieneFalloMecanico();
        }
        
        return str;
    }
    
    public String toSimpleString() {
        return "[" + getClass().getSimpleName() + "  |  Id: " + this.getId() + "]";
    }
}
