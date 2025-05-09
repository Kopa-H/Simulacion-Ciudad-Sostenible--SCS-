import java.util.ArrayList;
import java.awt.Color;

/**
 * Write a description of class Base here.
 *
 * @author Kopa
 * @version (a version number or a date)
 */
public class Base extends EntidadFija
{
    private static int contadorInstancias = 0;
    public static Color colorClase = Color.RED;

    public ArrayList<Vehiculo> vehiculosDisponibles; // Vehículos almacenados dentro de la base disponibles para ser usados
    
    public ArrayList<Vehiculo> vehiculosInhabilitados; // Aquí están los vehículos sin batería (el técnico debe recargarla) y los averiados

    /**
     * Constructor for objects of class Base
     */
    public Base(int x, int y)
    {
        super(x, y);
        setColor(colorClase);

        vehiculosDisponibles = new ArrayList<>();
        vehiculosInhabilitados = new ArrayList<>();
        
        setId(contadorInstancias);
        contadorInstancias++;
    }

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void agregarVehiculoDisponible(Vehiculo v) {
        vehiculosDisponibles.add(v);
    }
    
    // Sirve para sacar el numero de vehiculos disponibles de un tipo que tiene la base
    private int getNumeroVehiculosDisponibles(Class<?> claseVehiculo) {
        int num = 0;
        
        for (Vehiculo vehiculo : vehiculosDisponibles) {
            // Comparamos el tipo de la clase del vehículo con el tipo proporcionado
            if (vehiculo.getClass().equals(claseVehiculo)) {
                num++;
            }
        }
        
        return num; // Recuerda devolver el resultado
    }
    
    // Sirve para sacar el numero de vehiculos inhabilitados de un tipo que tiene la base
    private int getNumeroVehiculosInhabilitados(Class<?> claseVehiculo) {
        int num = 0;
        
        for (Vehiculo vehiculo : vehiculosInhabilitados) {
            // Comparamos el tipo de la clase del vehículo con el tipo proporcionado
            if (vehiculo.getClass().equals(claseVehiculo)) {
                num++;
            }
        }
        
        return num; // Recuerda devolver el resultado
    }
    
    public void reconectarVehiculosBase(Ciudad ciudad) {
        // Reconectar los vehículos disponibles
        for (int i = 0; i < vehiculosDisponibles.size(); i++) {
            Vehiculo vehiculo = vehiculosDisponibles.get(i);
            
            // Asegurarse de que el vehículo esté correctamente reconectado
            if (vehiculo != null) {
                int idVehiculo = vehiculo.getId(); // Obtener el ID del vehículo
                Vehiculo vehiculoReconectado = (Vehiculo) ciudad.encontrarEntidad(vehiculo.getClass(), idVehiculo);
                
                if (vehiculoReconectado != null) {
                    vehiculosDisponibles.set(i, vehiculoReconectado); // Reemplazar el vehículo reconectado
                } else {
                    vehiculosDisponibles.set(i, null); // Si no se encuentra el vehículo, lo ponemos a null
                }
            }
        }
        
        // Reconectar los vehículos inhabilitados
        for (int i = 0; i < vehiculosInhabilitados.size(); i++) {
            Vehiculo vehiculo = vehiculosInhabilitados.get(i);
            
            // Asegurarse de que el vehículo esté correctamente reconectado
            if (vehiculo != null) {
                int idVehiculo = vehiculo.getId(); // Obtener el ID del vehículo
                Vehiculo vehiculoReconectado = (Vehiculo) ciudad.encontrarEntidad(vehiculo.getClass(), idVehiculo);
                
                if (vehiculoReconectado != null) {
                    vehiculosInhabilitados.set(i, vehiculoReconectado); // Reemplazar el vehículo reconectado
                } else {
                    vehiculosInhabilitados.set(i, null); // Si no se encuentra el vehículo, lo ponemos a null
                }
            }
        }
    }
    
        
    @Override
    public String toString() {
       return String.format("%s  |  vehiculosDisponibles: [motos = %d | bicis = %d | patinetes = %d]  |  vehiculosInhabilitados: [motos = %d | bicis = %d | patinetes = %d]",
            super.toString(), getNumeroVehiculosDisponibles(Moto.class), getNumeroVehiculosDisponibles(Bicicleta.class), getNumeroVehiculosDisponibles(Patinete.class),
            getNumeroVehiculosInhabilitados(Moto.class), getNumeroVehiculosInhabilitados(Bicicleta.class), getNumeroVehiculosInhabilitados(Patinete.class));
    }
}
