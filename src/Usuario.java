import java.awt.Color;
import java.util.ArrayList;

/**
 * Write a description of class Usuario here.
 *
 * @author Kopa
 * @version (a version number or a date)
 */
public class Usuario extends Persona {
    private static int contadorInstancias = 0;
    public static Color colorClase = Color.ORANGE;
    
    public ArrayList<InfoAlquiler> registroInfoAlquileres;

    // instance variables - replace the example below with your own
    private double saldo;
    public double totalPagadoTasas;
    
    public boolean isPremium;
    
    // Variables que registran el tiempo de cada alquiler
    public Tiempo tiempoInicioAlquiler;
    public Tiempo tiempoFinalAlquiler;
    
    /**
     * Constructor for objects of class Usuario
     */
    public Usuario(int posX, int posY) {
        // initialise instance variables
        super(posX, posY);
        setColor(colorClase);
        // Se añade entre 50 y 200 €
        saldo = 50 + Math.random() * (150);
        totalPagadoTasas = 0;
        isPremium = false;
        
        registroInfoAlquileres = new ArrayList<>();
        
        setId(contadorInstancias);  // Asignamos el ID único a esta instancia
        contadorInstancias++;
    }
    
    public double getTotalPagadoTasas() {
        return totalPagadoTasas;
    }
    
    public void generarInfoAlquiler(Ciudad ciudad, Vehiculo vehiculoAlquilado) {
        // Se genera la factura del trabajo realizado
        tiempoFinalAlquiler = new Tiempo(ciudad.tiempo);
        
        InfoAlquiler alquiler = new InfoAlquiler(this, vehiculoAlquilado); 
        registroInfoAlquileres.add(alquiler);
    }
    
    @Override
    public void actuar(Ciudad ciudad) {
        super.actuar(ciudad);
        
        if (ciudad.isEntidadesAutonomas() && !enTrayecto && !isSiguiendoEntidad()) {
            intentarPlanearTrayecto(ciudad, Base.class);
            intentarPlanearTrayecto(ciudad, Moto.class);
        }
    }
    
    public void reservarVehiculo() {
        if (!isPremium) {
            return;
        }
    }

    public double getSaldo() {
        return saldo;
    }
    
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
    
    public void recargarSaldo(double x) {
        if (x > 0) {
            saldo += x;
        }
    }
    
    public void promocionarUsuario() {
        if (!isPremium) {
            isPremium = true;
            Impresora.printVerde("\nEl usuario " + this.toSimpleString() + " ha sido promocionado a Usuario Premium");
        }
    }
    
    /**
     * Informar de un problema con un vehículo o con una base de bicicletas o patinetes
     */
    public void alertarFalloMecanico(Entidad entidad) {
        if (!getEntidadSeguida().tieneAlertaFalloMecanico() && (entidad instanceof Vehiculo || entidad instanceof Base)) {
            Impresora.printColorClase(this.getClass(), "\n" + this.toSimpleString() + " ha activado la alerta de fallo mecánico de " + getEntidadSeguida().toSimpleString());
            entidad.activarAlertaFalloMecanico();
        }
    }
    
    @Override
    public String toString() {
        String str = super.toString();  // Llamamos al toString de Trabajador
        str += "  |  Saldo: [" + String.format("%.2f", saldo) + " \u20AC]";
        str += "  |  Premium: [" + isPremium + "]";
        return str;
    }
}
