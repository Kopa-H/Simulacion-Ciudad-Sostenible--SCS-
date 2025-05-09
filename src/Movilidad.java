import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

/**
 * Se trata de la clase principal del programa, encargada de poner a funcionar el sistema.
 *
 * @author KOPA
 * @version (a version number or a date)
 */
public class Movilidad {

    private static Ciudad ciudad;
    private static Simulacion simulacion;
    private static GestorMenus gestorMenus;
    
    private static void mostrarMensajeBienvenida() {
        Impresora.print("\n\n\n");
        Impresora.print("------------------------------------------------------------------------------------------------");
        Impresora.printAzul("Sea bienvenido al programa SGMS.");
        Impresora.printAzul("Siéntase libre de experimentar como desee con la simulación.");
        Impresora.print("------------------------------------------------------------------------------------------------");
    }
    
    /**
     * Función principal que ejecuta el sistema.
     * 
     * @param args  Los argumentos de la línea de comandos proporcionados al ejecutar el programa.
     */
    public static void main(String[] args) {
        
        mostrarMensajeBienvenida();
        
        ciudad = new Ciudad();
        
        simulacion = new Simulacion(ciudad);
        
        gestorMenus = new GestorMenus(simulacion, ciudad);

        simulacion.runSimulacion();
    }
}
