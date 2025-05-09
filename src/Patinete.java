import java.awt.Color;

/**
 * Write a description of class Patinete here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Patinete extends Vehiculo {
    private static int contadorInstancias = 0;
    private static final int MAX_DISTANCIA_BATERIA = 300;
    public static Color colorClase = new Color(0, 0, 139);
    
    /**
     * Constructor for objects of class Patinete
     */
    public Patinete(int posX, int posY) {
        // initialise instance variables
        super(posX, posY, MAX_DISTANCIA_BATERIA);
        setColor(colorClase);
        
        setId(contadorInstancias);
        contadorInstancias++;
    }
}
