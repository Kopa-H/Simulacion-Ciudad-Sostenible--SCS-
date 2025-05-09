import java.util.ArrayList;
import java.awt.Color;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class Ciudad {
    // Variables de instancia
    
    private CopyOnWriteArrayList<Entidad> entidades;
    private boolean autonomiaEntidades = false;
    
    private static final int DISTANCIA_MINIMA_ENTRE_BASES = 20;
    
    // Simulación copia su tiempo y dinero en ciudad, y se mantienen conectados
    public Tiempo tiempo;
    public Dinero dinero;
    
    public static final int ROWS = 50;
    public static final int COLUMNS = 50;
    
    public static final int METROS_POR_CUADRANTE = 100;

    /**
     * Constructor para objetos de la clase Ciudad.
     */ 
    public Ciudad() {
        entidades = new CopyOnWriteArrayList<>(); // Inicializamos el lista de personas       
    }
    
    public CopyOnWriteArrayList<Entidad> getEntidades() {
        return entidades;
    }
    
    public void setEntidades(CopyOnWriteArrayList<Entidad> newEntidades) {
        entidades = newEntidades;
    }
    
    public List<Entidad> ordenarEntidadesPorId(List<Entidad> entidades) {
        // Ordenar las entidades por su ID
        entidades.sort((entidad1, entidad2) -> {
            // Asegúrate de que las entidades son del tipo correcto
            if (entidad1 instanceof Usuario && entidad2 instanceof Usuario) {
                Usuario usuario1 = (Usuario) entidad1;
                Usuario usuario2 = (Usuario) entidad2;
                return Integer.compare(usuario1.getId(), usuario2.getId());  // Comparar por ID
            }
            return 0;  // Si no son usuarios, no se ordena (puedes manejar este caso según sea necesario)
        });
        return entidades;
    }
    
    public boolean isEntidadesAutonomas() {
        return autonomiaEntidades;
    }
    
    public void activarAutonomiaEntidades() {
        autonomiaEntidades = true;
        Impresora.printVerde("\nSe ha activado la autonomía de las entidades");
    }
    
    public void desactivarAutonomiaEntidades() {
        autonomiaEntidades = false;
        Impresora.printRojo("\nSe ha desactivado la autonomía de las entidades");
    }
    
    public List<Entidad> obtenerEntidadesPorClase(Class<? extends Entidad> tipoClase) {
        List<Entidad> entidadesFiltradas = new CopyOnWriteArrayList<>();
    
        // Iterar sobre la lista de entidades
        for (Entidad entidad : entidades) {
            // Si la entidad es del tipo especificado, añadirla a la lista filtrada
            if (tipoClase.isInstance(entidad)) {
                entidadesFiltradas.add(entidad);
            }
        }
    
        return entidadesFiltradas;
    }
    
    // Método para mover una entidad al principio de la lista
    public void moverEntidadAlPrincipio(Entidad entidad) {
        if (entidades.contains(entidad)) {
            entidades.remove(entidad);      // Elimina la entidad de su posición actual
            entidades.add(0, entidad);      // Añade la entidad al principio de la lista (índice 0)
        }
    }
    
    // Método para añadir una entidad a la ciudad
    public void addElement(Entidad entidad) {
        entidades.add(entidad); // Añadir al final de la lista
    }
    
        /**
     * Método para agregar n entidades de un tipo específico en ubicaciones aleatorias.
     * 
     * @param cantidad La cantidad de entidades a agregar.
     * @param tipoEntidad El tipo de entidad a agregar (por ejemplo, Usuario, Moto, Base).
     * @param ciudad La ciudad en la que se agregan las entidades.
     * @return La última entidad creada.
     */
    public <T extends Entidad> T agregarEntidad(Simulacion simulacion, int cantidad, Class<T> claseEntidad) {
        T ultimaEntidad = null;  // Variable para almacenar la última entidad creada
        RandomGenerator randomGenerator = new RandomGenerator();
        
        for (int i = 0; i < cantidad; i++) {
            Ubicacion ubi = null;
            boolean espacioEncontrado = false;
    
            // Si la clase es Base, buscar una ubicación alejada de al menos 20 bloques de cualquier otra Base
            if (claseEntidad.equals(Base.class)) {
                for (int intentos = 0; intentos < 100; intentos++) {  // Limitar el número de intentos
                    Ubicacion ubicacionCandidata = randomGenerator.getUbicacionLibreRandom(this);
                    boolean esAlejada = true;
    
                    // Verificar que esté al menos a 20 bloques de cualquier otra Base
                    for (Entidad entidad : obtenerEntidadesPorClase(Base.class)) {
                        int distancia = Math.abs(entidad.getUbicacion().getPosX() - ubicacionCandidata.getPosX()) +
                                        Math.abs(entidad.getUbicacion().getPosY() - ubicacionCandidata.getPosY());
                        
                        if (distancia < DISTANCIA_MINIMA_ENTRE_BASES) {
                            esAlejada = false;
                            break;  // Si hay una base cercana, romper el bucle interno
                        }
                    }
    
                    if (esAlejada) {
                        ubi = ubicacionCandidata;
                        espacioEncontrado = true;
                        break;  // Salir del bucle si se encontró un lugar adecuado
                    }
                }
    
                // Si no se encontró una ubicación válida, mostrar mensaje y continuar
                if (!espacioEncontrado) {
                    Impresora.printRojo("\nNo se ha encontrado espacio disponible para agregar una nueva Base");
                    return null;
                }
            } else {
                // Si no es una Base, se genera una ubicación libre sin restricciones adicionales
                ubi = randomGenerator.getUbicacionLibreRandom(this);
            }
            
            try {
                // Crear la entidad de acuerdo con el tipo y la ubicación
                T entidad = claseEntidad.getConstructor(int.class, int.class).newInstance(ubi.getPosX(), ubi.getPosY());
                ultimaEntidad = entidad;  // Actualizar la última entidad creada
    
                // Añadir la entidad a la ciudad
                addElement((Entidad) entidad);
                Color color = entidad.getColor();
                simulacion.mostrarEntidad(entidad.getUbicacion(), color);
    
                Impresora.printAzul("\nSe ha añadido una entidad " + claseEntidad.getSimpleName() + " en: " + ubi.toString());
    
                // Si no existe ninguna base y se está añadiendo un vehículo, se agregan dos bases
                if (encontrarEntidad(Base.class, 0) == null && !claseEntidad.equals(Base.class)) {
                    agregarBase(simulacion, 1, 1);
                    agregarBase(simulacion, 1, 1);
                    Impresora.printAzul("\nSe han añadido dos bases dado que no existía ninguna");
                }
            
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return ultimaEntidad;  // Retornar la última entidad creada
    }
    
        
    public void agregarBase(Simulacion simulacion, int numBicicletas, int numPatinetes) {
        Base base = agregarEntidad(simulacion, 1, Base.class);
        
        // Si no ha habido espacio suficiente para generar una nueva base
        if (base == null) {
            return;
        }
        
        for (int i = 0; i < numBicicletas; i++) {
            // Añadir bici
            Bicicleta bici = new Bicicleta(base.getUbicacion().getPosX(), base.getUbicacion().getPosY());
            base.agregarVehiculoDisponible(bici);
            addElement(bici); 
            Impresora.printAzul("\nSe ha añadido una bici a la base " + base.toSimpleString());
        }
        
        for (int i = 0; i < numPatinetes; i++) {
            // Añadir patinete
            Patinete patinete = new Patinete(base.getUbicacion().getPosX(), base.getUbicacion().getPosY());
            base.agregarVehiculoDisponible(patinete);
            addElement(patinete); 
            Impresora.printAzul("\nSe ha añadido una bici a la base " + base.toSimpleString());
        }
    }
    
    public Entidad encontrarEntidad(Class<?> claseEntidad, int indiceEntidad) {
        for (Entidad entidad : entidades) {
            if (claseEntidad.isInstance(entidad) && entidad.getId() == indiceEntidad) {
                return entidad;
            }
        }
        
        return null;
    }
    
    public Entidad encontrarEntidadUsableMasCercana(EntidadMovil entidadBuscando, Class<?> claseEntidad) {
        Entidad entidadMasCercana = null;
        int distanciaMinima = Integer.MAX_VALUE; // Inicializamos con el valor más alto posible
    
        // Iteramos sobre las entidades
        for (Entidad entidad : entidades) {
            // Comprobamos si la entidad es una instancia del tipo proporcionado
            if (claseEntidad.isInstance(entidad)) {
                
                // SE REALIZAN COMPROBACIONES DE QUE LA ENTIDAD ES VÁLIDA PARA SER USADA
                
                // Si la entidad tiene activado una alerta de fallo mecánico, se ignora
                if (entidad.tieneAlertaFalloMecanico()) {
                    continue;
                }
                
                // Si la entidad es una instancia de EntidadMovil, verificamos si está en trayecto
                if (entidad instanceof EntidadMovil) {
                    EntidadMovil entidadMovil = (EntidadMovil) entidad;
                    if (entidadMovil.enTrayecto) {
                        continue; // Si está en trayecto, la ignoramos
                    }
                }
                
                // Si la entidad es un Vehículo y quien busca es una Persona
                if (entidad instanceof Vehiculo && entidadBuscando instanceof Usuario) {
                    Vehiculo vehiculo = (Vehiculo) entidad;
                    Usuario usuario = (Usuario) entidadBuscando;
    
                    // Si el vehículo tiene menos del 10% de batería, lo descartamos
                    if (vehiculo.getPorcentajeBateria() < 10) {
                        continue;
                    }
    
                    // Si el vehículo tiene menos del 20% de batería y la persona no es premium, lo descartamos
                    if (vehiculo.getPorcentajeBateria() < 20 && !usuario.isPremium) {
                        continue;
                    }
    
                    // Si el vehículo está reservado y la entidad que busca NO es la que hizo la reserva, se descarta
                    if (vehiculo.isReservado && entidadBuscando != vehiculo.usuarioReserva) {
                        continue;
                    }
                }
                
                // Calculamos la distancia Manhattan entre la ubicación actual y la entidad
                int distancia = Math.abs(entidad.getUbicacion().getPosX() - entidadBuscando.getUbicacion().getPosX()) 
                              + Math.abs(entidad.getUbicacion().getPosY() - entidadBuscando.getUbicacion().getPosY());
    
                // Si la distancia calculada es menor que la distancia mínima, actualizamos la entidad más cercana
                if (distancia < distanciaMinima) {
                    distanciaMinima = distancia;
                    entidadMasCercana = entidad;
                }
            }
        }
    
        // Devolvemos la entidad más cercana, o null si no se encontró ninguna
        return entidadMasCercana;
    }
    
    public Entidad encontrarEntidadConFalloMecanico(Class<?> claseEntidad) {
        Entidad entidadEncontrada = null;
        
        // Iteramos sobre las entidades
        for (Entidad entidad : entidades) {
            // Comprobamos si la entidad es una instancia del tipo proporcionado
            if (claseEntidad.isInstance(entidad) && entidad.tieneFalloMecanico()) {
                entidadEncontrada = entidad;
                break;
            }
        }
        
        return entidadEncontrada;
    }
    
    public boolean existeTrabajadorConEntidadAsignada(Entidad entidadAsignada) {
             // Iteramos sobre las entidades
        for (Entidad entidad : entidades) {
            // Comprobamos si la entidad es una instancia del tipo proporcionado
            if (entidad instanceof Trabajador trabajador && trabajador.getEntidadAsignada() == entidadAsignada) {
                return true;
            }
        }
        
        return false;
    }
    
    // Método que verifica si una posición está ocupada
    public boolean posicionOcupada(Ubicacion ubicacion) {
        // Recorrer las entidades existentes y verificar si alguna está en la misma posición
        for (Entidad entidad : entidades) {
            if (entidad.getUbicacion().getPosX() == ubicacion.getPosX() && entidad.getUbicacion().getPosY() == ubicacion.getPosY()) {
                return true; // La posición está ocupada
            }
        }
        return false; // La posición no está ocupada
    }

    public boolean posicionOcupadaPor(Ubicacion ubicacion, Class<?> claseEntidad) {
        // Iterar sobre las entidades existentes
        for (Entidad entidad : entidades) {
            // Comprobamos si la entidad es una instancia del tipo proporcionado
            if (claseEntidad.isInstance(entidad)) {
                
                if (entidad.getUbicacion().getPosX() == ubicacion.getPosX() && entidad.getUbicacion().getPosY() == ubicacion.getPosY()) {
                    return true; // La posición está ocupada por una entidad del tipo especificado
                }
            }
        }
        return false; // La posición no está ocupada por una entidad del tipo especificado
    }
        
    public void reconectarRelacionesEntidades() {
        for (Entidad entidad : entidades) {
            
            if (entidad instanceof EntidadMovil entidadMovil) {
                
                // Se actualizan las relaciones de destino
                if (entidadMovil.getEntidadDestino() != null) {
                    int idEntidadDestino = entidadMovil.getEntidadDestino().getId();
                    Entidad entidadDestino = encontrarEntidad(entidadMovil.getEntidadDestino().getClass(), idEntidadDestino);
                    
                    if (entidadDestino != null) {
                        entidadMovil.setEntidadDestino(entidadDestino);
                        entidadMovil.setUbicacionDestino(entidadDestino.getUbicacion());
                    }
                }
                
                // Se actualizan las relaciones de seguimiento
                if (entidadMovil.isSiguiendoEntidad() && entidadMovil.getEntidadSeguida() != null) {
                    // Obtener la entidad seguida usando algún identificador único
                    int idEntidadSeguida = entidadMovil.getEntidadSeguida().getId(); // Suponiendo que las entidades tienen un ID
                    Entidad entidadSeguida = encontrarEntidad(entidadMovil.getEntidadSeguida().getClass(), idEntidadSeguida);
                    
                    if (entidadSeguida instanceof EntidadMovil) {
                        entidadMovil.setEntidadSeguida((EntidadMovil) entidadSeguida); // Reconectar la entidad seguida
                    } else {
                        entidadMovil.setEntidadSeguida(null); // Si no se encuentra la entidad, se desconecta
                    }
                }
            }
    
            // Se actualizan las relaciones de asignación de trabajo
            if (entidad instanceof Trabajador trabajador && trabajador.getEntidadAsignada() != null) {
                // Obtener la entidad asignada usando algún identificador único
                int idEntidadAsignada = trabajador.getEntidadAsignada().getId(); // Suponiendo que las entidades tienen un ID
                Entidad entidadAsignada = encontrarEntidad(trabajador.getEntidadAsignada().getClass(), idEntidadAsignada);
                
                if (entidadAsignada != null) {
                    trabajador.setEntidadAsignada(null, entidadAsignada); // Reconectar la entidad asignada
                } else {
                    trabajador.setEntidadAsignada(null, null); // Si no se encuentra la entidad, se desconecta
                }
            }
            
            // Si la entidad es de tipo Base, reconectar los vehículos
            if (entidad instanceof Base base) {
                base.reconectarVehiculosBase(this); // Reconecta los vehículos disponibles e inhabilitados
            }
        }
    }
}