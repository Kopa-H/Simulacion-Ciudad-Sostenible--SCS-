import java.util.Random;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Clase para generar valores aleatorios, como direcciones y nombres.
 */
public class RandomGenerator implements Serializable {
    // Instancia de Random para generar números aleatorios
    private Random random;

    // Array de nombres aleatorios en español
    private static final String[] nombres = {
        "Juan", "María", "José", "Ana", "Carlos", "Laura", "Miguel", "Pedro", 
        "Lucía", "Luis", "Isabel", "David", "Carmen", "José Luis", "Patricia", 
        "Francisco", "Raquel", "Sergio", "Elena", "Antonio", "Beatriz", "Aina", 
        "Pau"
    };

    /**
     * Constructor para objetos de la clase RandomGenerator
     */
    public RandomGenerator() {
        random = new Random();
    }

    /**
     * Genera una dirección aleatoria.
     *
     * @return Dirección aleatoria (UP, DOWN, LEFT, RIGHT)
     */
    public EntidadMovil.Direcciones getDireccionRandom() {    
        // Generar un número aleatorio entre 0 y 3 usando el método nextInt
        int x = random.nextInt(4); // Esto devuelve 0, 1, 2 o 3
        
        // Realizar el movimiento según el número aleatorio
        switch (x) {
            case 0:
                return EntidadMovil.Direcciones.UP;
            case 1:
                return EntidadMovil.Direcciones.DOWN;
            case 2:
                return EntidadMovil.Direcciones.RIGHT;
            case 3:
                return EntidadMovil.Direcciones.LEFT;
            default:
                throw new IllegalStateException("Dirección aleatoria no válida: " + x);
        }
    }

    /**
     * Genera un nombre aleatorio de un listado de nombres en español.
     *
     * @return Nombre aleatorio en español
     */
    public String getNombreRandom() {
        // Generar un número aleatorio entre 0 y el tamaño del array de nombres
        int index = random.nextInt(nombres.length);
        
        // Devolver el nombre aleatorio
        return nombres[index];
    }
    
    public Ubicacion getUbicacionLibreRandom(Ciudad ciudad) {
        Ubicacion ubi = new Ubicacion();
        int posX, posY;
        
        // Generar posiciones aleatorias hasta que encontremos una libre
        do {
            posX = random.nextInt(Ciudad.ROWS);
            posY = random.nextInt(Ciudad.COLUMNS);
            
            ubi.setUbicacion(posX, posY);
        } while (ciudad.posicionOcupada(ubi));  // Comprobar si la ubicación está ocupada
        
        return ubi;
    }
    
    public Ubicacion getUbicacionLibreAlejadaRandom(Ciudad ciudad, Ubicacion ubicacionReferencia, int distanciaMinima) {
        Ubicacion ubi = new Ubicacion();
        int posX, posY;
        int distanciaTotal;
    
        // Obtener las coordenadas de la ubicación de referencia
        int referenciaX = ubicacionReferencia.getPosX();
        int referenciaY = ubicacionReferencia.getPosY();
    
        // Generar posiciones aleatorias hasta que encontremos una libre y suficientemente alejada
        do {
            posX = random.nextInt(Ciudad.ROWS);
            posY = random.nextInt(Ciudad.COLUMNS);
    
            ubi.setUbicacion(posX, posY);
    
            // Calcular la distancia total (distancia de Manhattan)
            distanciaTotal = Math.abs(referenciaX - posX) + Math.abs(referenciaY - posY);
    
            // Continuar si la ubicación está ocupada o si no cumple la distancia mínima
        } while (ciudad.posicionOcupada(ubi) || distanciaTotal < distanciaMinima);  // Comprobar si la ubicación está ocupada o muy cerca
    
        return ubi;
    }
    
    public Vehiculo getVehiculoDisponibleRandom(Base base) {
        // Verificar si la lista no está vacía
        if (base.vehiculosDisponibles.isEmpty()) {
            return null;
        }
    
        // Devolver un vehículo aleatorio usando la instancia de Random
        return base.vehiculosDisponibles.get(random.nextInt(base.vehiculosDisponibles.size()));
    }
    
    public Entidad getEntidadRandom(Ciudad ciudad, Entidad entidadExcluida, Class<?> clase) {
        // Lista para almacenar las entidades que cumplen con los criterios
        ArrayList<Entidad> entidadesCumplidas = new ArrayList<>();
        
        // Recorremos todas las entidades de la ciudad
        for (Entidad entidad : ciudad.getEntidades()) {
            // Comprobamos si la entidad es de la clase indicada y no es la excluida
            if (clase.isInstance(entidad) && entidad != entidadExcluida) {
                entidadesCumplidas.add(entidad);
            }
        }
        
        // Si no se encontraron entidades que cumplieran el criterio, retornamos null
        if (entidadesCumplidas.isEmpty()) {
            return null;
        }
        
        // Generamos un número aleatorio dentro del rango de la lista de entidades
        int indexRandom = random.nextInt(entidadesCumplidas.size());
        
        // Devolvemos la entidad aleatoria
        return entidadesCumplidas.get(indexRandom);
    }
}
