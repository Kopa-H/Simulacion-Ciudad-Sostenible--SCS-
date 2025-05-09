/**
 * Write a description of class Persona here.
 *
 * @author Kopa
 * @version (a version number or a date)
 */
public class Persona extends EntidadMovil
{
    // instance variables - replace the example below with your own
    private String nombre;
    private RandomGenerator randomGenerator = new RandomGenerator();
    private static int contadorInstancias = 0;

    /**
     * Constructor for objects of class Persona
     */
    public Persona(int posX, int posY)
    {
        // Se inicializa el nombre de la Persona como " "
        super(posX, posY);
        nombre = randomGenerator.getNombreRandom();
    }
    
    @Override
    public void actuar(Ciudad ciudad) {
        super.actuar(ciudad);
 
        if (!enTrayecto && !isSiguiendoEntidad()) {
            boolean hasToMoveRandom = true;
            
            if (this instanceof Trabajador) {
                // Si el trabajador NO está trabajando
                if (((Trabajador) this).isTrabajando()) {
                    hasToMoveRandom = false;
                }
            }
            // Si no se cumplen las probabilidades, mover de manera aleatoria
            
            if (hasToMoveRandom) {
                moverRandom(ciudad);
            }
        }
    }
    
    /**
     * Sirve para otorgarle un nombre a una Persona
     *
     * @param  nombre es el nombre que se le asignará a la Persona
     * @return   the result produced by sampleMethod
     */
    void setNombre(String nombre) {
        this.nombre = nombre;   
    }
    
    /**
     * Sirve para obtener el nombre de una persona
     *
     * @return   the result produced
     */
    public String getNombre() {
        return nombre;
    }
    
    @Override
    public String toString() {
        return super.toString() + "  |  Nombre: " + nombre;
    }
    
    @Override
    public String toSimpleString() {
        return "[" + getClass().getSimpleName() + "  |  Id: " + this.getId() + "  |  Nombre: " + nombre + "]";
    }
}
