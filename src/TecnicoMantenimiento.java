import java.awt.Color;

/**
 * Write a description of class TrabajadorMantenimiento here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class TecnicoMantenimiento extends Trabajador {
    
    private static int contadorInstancias = 0;
    public static Color colorClase = Color.MAGENTA;
    
    /**
     * Constructor for objects of class TrabajadorMantenimiento
     */
    public TecnicoMantenimiento(int posX, int posY)
    {
        super(posX, posY);
        setColor(colorClase);
        
        precioPorHora = 7.5;
        precioBase = 15;
        
        setId(contadorInstancias);
        contadorInstancias++;
    }

    @Override
    public void actuar(Ciudad ciudad) {        
         // Asciende en la jerarquía de clases y hereda las funciones de actuación de EntidadMovil
        super.actuar(ciudad);
    }
    
    public void trabajar(Ciudad ciudad) {

        if (entidadAsignada instanceof Vehiculo vehiculoAsignado) {
            // Recarga la batería del vehículo
            vehiculoAsignado.sumarBateria();
        
            // Si se ha cargado la batería por completo, el trabajador abandona su labor
            if (vehiculoAsignado.getPorcentajeBateria() >= 100) {
                Impresora.printColorClase(this.getClass(), "\n" + this.toSimpleString() + " ha cargado por completo a " + vehiculoAsignado.toSimpleString());
                
                vehiculoAsignado.incrementarRecargasBateria();
                this.generarFactura(ciudad);
                terminarTrabajo(true);
            }
        // Los técnicos de mantenimiento pueden reparar bases, pero NO vehículos
        } else if (entidadAsignada instanceof Base base) {
            base.restaurarEstadoMecanico();
        
            if (!(base.tieneFalloMecanico())) {
                this.generarFactura(ciudad);
                terminarTrabajo(true);
            }
        }
    }
    
    @Override
    public boolean intentarAsignarEntidad(Ciudad ciudad, Entidad entidad) {
        // Si otro trabajdor tiene la entidad asignada
        if (ciudad.existeTrabajadorConEntidadAsignada(entidad)) {
            return false;
        }
        
        if (entidad instanceof Vehiculo vehiculo) {
            if (vehiculo.getPorcentajeBateria() < 20) {
                return true;         
            }
        } else if (entidad instanceof Base base) {
            if (base.tieneAlertaFalloMecanico()) {
                return true;
            }
        }
        
        return false;
    }
}
