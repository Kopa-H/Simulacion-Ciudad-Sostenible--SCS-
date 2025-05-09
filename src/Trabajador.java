import java.util.ArrayList;

/**
 * Write a description of class Trabajador here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
abstract public class Trabajador extends Persona
{
    // Almacena el vehículo que el trabajador tiene asignado
    Entidad entidadAsignada = null;
    
    private boolean isModoTraslado = false;
    private Ubicacion ubicacionTraslado;
    
    public ArrayList<InfoFactura> registroInfoFacturas;
    
    // Variables que registran el tiempo dedicado a cada entidad
    public Tiempo tiempoInicioTrabajo;
    public Tiempo tiempoFinalTrabajo;
    
    public double precioPorHora;
    public double precioBase;
    
    private int trabajosCompletados;
    private double totalFacturado;
    
    /**
     * Constructor for objects of class Trabajador
     */
    public Trabajador(int posX, int posY)
    {
        super(posX, posY);
    
        trabajosCompletados = 0;
        totalFacturado = 0;
    
        registroInfoFacturas = new ArrayList<>();
    }
    
    public int getTrabajosCompletados() {
        return trabajosCompletados;
    }
    
    public double getTotalFacturado() {
        return totalFacturado;
    }
    
    public void sumarTotalFacturado(double x) {
        totalFacturado += x;
    }
    
    public void setUbicacionTraslado(Ubicacion ubi) {
        ubicacionTraslado = ubi;
    }
    
    public Ubicacion getUbicacionTraslado() {
        return ubicacionTraslado;
    }
    
    public void activarModoTraslado() {
        isModoTraslado = true;
    }
    
    public void desactivarModoTraslado() {
        isModoTraslado = false;
    }

    public boolean isModoTraslado() {
        return isModoTraslado;
    }
    
    public Entidad getEntidadAsignada() {
        return entidadAsignada;
    }
    
    public void setEntidadAsignada(Ciudad ciudad, Entidad entidadAsignada) {
        this.entidadAsignada = entidadAsignada;
        
        if (ciudad != null) {
            // Crear una copia usando el constructor de copia
            tiempoInicioTrabajo = new Tiempo(ciudad.tiempo);
        }
        
        if (isModoTraslado) {
            Impresora.printColorClase(this.getClass(), "\n" + toSimpleString() + " se ha asignado " + entidadAsignada.toSimpleString() + " para trasladar");
        } else {
            Impresora.printColorClase(this.getClass(), "\n" + toSimpleString() + " se ha asignado " + entidadAsignada.toSimpleString() + " para trabajar");
        }
    }
    
    public boolean isTrabajando() {
        if (entidadAsignada != null) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public void actuar(Ciudad ciudad) {
        super.actuar(ciudad);
        
        // Si no tiene ninguna entidad asignada, busca uno que necesite atención
        if (entidadAsignada == null) {
            
            // Si las entidades NO son autónomas, se evite que busque asignarse trabajo
            if (!ciudad.isEntidadesAutonomas()) {
                return;
            }
            
            // Se itera sobre todos las entidades y se intenta asignar a su trabajador correspondiente
            for (Entidad entidad : ciudad.getEntidades()) {
                if (intentarAsignarEntidad(ciudad, entidad)) {
                    setEntidadAsignada(ciudad, entidad);
                }                   
            }
        } else {
            
            // Si el trabajador no está en trayecto
            if (!enTrayecto) {
            
                // Si el trabajador se encuentra en la localización de traslado lo ha finalizado
                if (this.isModoTraslado() && ubicacionTraslado.equals(this.getUbicacion())) {
                    Impresora.printColorClase(this.getClass(), "\n" + this.toSimpleString() +  " ha finalizado su traslado de " + entidadAsignada.toSimpleString() + " hacia " + ubicacionTraslado);
                    this.generarFactura(ciudad);
                    this.terminarTrabajo(true);
                    return;
                }
                
                // Si el trabajador aún no se encuentra donde la entidad asignada, planea un trayecto hacia ella
                if (!(entidadAsignada.getUbicacion().equals(this.getUbicacion()))) {
                    planearTrayecto(ciudad, entidadAsignada.getUbicacion(), entidadAsignada);
                    return;
                }
                
                // Si el tanto el trabajador como la entidad están en una base, y están en la misma ubicación, A TRABAJAR!
                if (ciudad.posicionOcupadaPor(this.getUbicacion(), Base.class)) {
                    this.trabajar(ciudad);
                    return;
                }
                
                // Si se ha llegado al vehículo, pero NO está en una base, se lleva a la base más cercana
                // o se lleva a la posición escogida por el usuario desde el menú si el trabajador está en modo traslado
                if (ciudad.posicionOcupadaPor(entidadAsignada.getUbicacion(), Vehiculo.class)) {                    
                    Vehiculo vehiculoAsignado = (Vehiculo) entidadAsignada;
                    
                    if (this.isModoTraslado()) {
                        arrastrarVehiculo(ciudad, this, vehiculoAsignado, this.getUbicacionTraslado(), null);
                    } else {
                        trasladarVehiculoToBase(ciudad, vehiculoAsignado);
                    }
                    
                    return;
                }
            }
        }
    }
    
    public void trasladarVehiculoToBase(Ciudad ciudad, Vehiculo vehiculoAsignado) {
        // La persona planea un trayecto hacia la base más cercana
        Base baseCercana = (Base) ciudad.encontrarEntidadUsableMasCercana(this, Base.class);
        
        // Si no hay ningun base disponible, modifica su trabajo para reparar bases
        if (baseCercana == null) {
            terminarTrabajo(false);
            Impresora.printColorClase(this.getClass(), "\n" + toSimpleString() + " ha abandonado su trabajo con la intención de reparar bases con urgencia");
            
            Base basePorReparar = (Base) ciudad.encontrarEntidadConFalloMecanico(Base.class);
            if (intentarAsignarEntidad(ciudad, basePorReparar)) {
                setEntidadAsignada(ciudad, basePorReparar);  
            }
            
        } else {
            // Si existen bases disponibles a las que llevar el vehículo
            arrastrarVehiculo(ciudad, this, vehiculoAsignado, baseCercana.getUbicacion(), baseCercana);
        }
    }
    
    public void arrastrarVehiculo(Ciudad ciudad, Persona personaQueArrastra, Vehiculo vehiculoArrastrado, Ubicacion ubicacionDestino, Entidad entidadDestino) {
        
        if (entidadDestino != null) {
            planearTrayecto(ciudad, entidadDestino.getUbicacion(), entidadDestino);
            Impresora.printColorClase(this.getClass(), "\n" + toSimpleString() + " se lleva arrastrando a " + vehiculoArrastrado.toSimpleString() + " hacia " + entidadDestino.toSimpleString());
        } else {
            planearTrayecto(ciudad, ubicacionDestino, null);
            Impresora.printColorClase(this.getClass(), "\n" + toSimpleString() + " se lleva arrastrando a " + vehiculoArrastrado.toSimpleString() + " hacia " + ubicacionDestino.toString());
        }
        
        // El vehículo sigue a la persona
        vehiculoArrastrado.empezarSeguimiento(ciudad, this);
        vehiculoArrastrado.incrementarVecesArrastrado();
    }
    
    public void generarFactura(Ciudad ciudad) {
        // Se genera la factura del trabajo realizado
        tiempoFinalTrabajo = new Tiempo(ciudad.tiempo);
        
        InfoFactura factura = new InfoFactura(this, entidadAsignada); 
        registroInfoFacturas.add(factura);
    }
    
    abstract public void trabajar(Ciudad ciudad);
    
    // El parámetro indica si se está llamando a esta función una vez el trabajo ha sido debidamente completado, o se termina por otras razones
    public void terminarTrabajo(boolean trabajoCompletado) {      
        if (trabajoCompletado) {
            Impresora.printColorClase(this.getClass(), "\n" + "El trabajador " + this.toSimpleString() + " ha terminado su trabajo con " + entidadAsignada.toSimpleString());
            trabajosCompletados++;
        } else {
            Impresora.printColorClase(this.getClass(), "\n" + "El trabajador " + this.toSimpleString() + " ha abandonado su trabajo con " + entidadAsignada.toSimpleString());
        }
        
        entidadAsignada = null;
        
        tiempoInicioTrabajo = null;
        tiempoFinalTrabajo = null;
        
        if (isModoTraslado()) {
            desactivarModoTraslado();
            setUbicacionTraslado(null);
            Impresora.printColorClase(this.getClass(), "\n" + "El trabajador " + this.toSimpleString() + " desactiva su modo traslado");
        }
    }
    
    abstract public boolean intentarAsignarEntidad(Ciudad ciudad, Entidad entidad);
    
    @Override
    public String toString() {
        String str = super.toString();  // Llamamos al toString de Trabajador
        if (entidadAsignada != null) {
            str += "  |  Entidad asignada: [" + entidadAsignada.toSimpleString() + " ]";  // Añadimos el vehículo si está asignado
        }
        return str;
    }
}
