import java.awt.Color;

/**
 * Write a description of class Bicicleta here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Bicicleta extends Vehiculo
{
    private static int contadorInstancias = 0;
    private static final int MAX_DISTANCIA_BATERIA = 300;
    public static Color colorClase = Color.PINK;
    
    /**
     * Constructor for objects of class Bicicleta
     */
    public Bicicleta(int posX, int posY)
    {
        // initialise instance variables
        super(posX, posY, MAX_DISTANCIA_BATERIA);
        setColor(colorClase);
        
        setId(contadorInstancias);
        contadorInstancias++;
    }
}
