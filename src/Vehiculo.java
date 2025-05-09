import java.util.ArrayList;
import java.awt.Color;

/**
 * Write a description of class Vehiculo here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
abstract public class Vehiculo extends EntidadMovil {
    // Número de casillas que pueden recorrerse con la batería al tope. Se usa junto al porcentaje para calcular la distancia máxima que puede recorrer un vehículo.
    // Las subclases pueden modificar este valor para ajustar la capacidad de sus baterías.
    protected final int DISTANCIA_MAX_BATERIA;
    
    // Contiene el valor de distancia que puede recorrer el vehículo (indicador de la batería)
    private int autonomiaBateria;
    
    private int recargasBateria;
    
    public static final int DURACION_MAXIMA_RESERVA = 3; // en horas 
    public Tiempo tiempoInicioReserva;
    public boolean isReservado;
    public Usuario usuarioReserva;
    
    private int vecesAlquilado;
    private int vecesArrastrado;
    
    public static enum TipoVehiculo {
        MOTO, BICI, PATINETE;
    }

    /**
     * Constructor for objects of class Vehiculo
     */
    public Vehiculo(int posX, int posY, int distanciaMaxBateria) {
        // initialise instance variables
        super(posX, posY);
        
        isReservado = false;
        usuarioReserva = null;
        
        vecesAlquilado = 0;
        vecesArrastrado = 0;
        recargasBateria = 0;
        
        this.DISTANCIA_MAX_BATERIA = distanciaMaxBateria;
        autonomiaBateria = distanciaMaxBateria;
    }
    
    public int getVecesAlquilado() {
        return vecesAlquilado;
    }
    
    public int getVecesArrastrado() {
        return vecesArrastrado;
    }
    
    public int getTotalFallosMecanicos() {
        return totalFallosMecanicos;
    }
    
    public int getRecargasBateria() {
        return recargasBateria;
    }
    
    public void incrementarRecargasBateria() {
        recargasBateria++;
    }
    
    public void incrementarVecesArrastrado() {
        vecesArrastrado++;
    }
    
    public void incrementarVecesAlquilado() {
        vecesAlquilado++;
    }
    
    private void cancelarReserva() {
        Impresora.printColorClase(this.getClass(), "\n" + this.toSimpleString() + " ha cancelado su reserva realizada por " + usuarioReserva.toSimpleString());
        isReservado = false;
        usuarioReserva = null;
    }
    
    public void actuar(Ciudad ciudad) {
        super.actuar(ciudad);
        
        // En caso de que esté reservado, se comprueba si la reserva ha alcanzado el tiempo máximo
        if (isReservado) {
            
            // se verifica que no haya transcurrido ningún día ni mes ni año
            if (ciudad.tiempo.dia != tiempoInicioReserva.dia ||
                ciudad.tiempo.mes != tiempoInicioReserva.mes ||
                ciudad.tiempo.año != tiempoInicioReserva.año) 
            {  
                Impresora.printColorClase(this.getClass(), "\n" + this.toSimpleString() + " ha sobrepasado el tiempo máximo de reserva");
                cancelarReserva();
                return;
            }
            
            if (ciudad.tiempo.hora - tiempoInicioReserva.hora > DURACION_MAXIMA_RESERVA) {
                Impresora.printColorClase(this.getClass(), "\n" + this.toSimpleString() + " ha sobrepasado el tiempo máximo de reserva");
                cancelarReserva();
                return;
            }
        }
    }
    
    public static int obtenerTotalTiposVehiculo() {
        return TipoVehiculo.values().length;
    }
       
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public int getPorcentajeBateria() {
        // Calcular el porcentaje de batería restante
        return (int) Math.round((double) autonomiaBateria / DISTANCIA_MAX_BATERIA * 100);
    }
    
    public int getAutonomiaBateria() {
        return autonomiaBateria;
    }

    public void setAutonomiaBateria(int autonomiaBateria) {
        this.autonomiaBateria = autonomiaBateria;
    }
    
    public void restarBateria() {
        if (autonomiaBateria > 0) {
            autonomiaBateria--;
        } else {
            Impresora.printNaranja("La batería del vehículo " + toSimpleString() + " se ha agotado!");  
        }
    }
    
    public void sumarBateria() {
        if (autonomiaBateria < DISTANCIA_MAX_BATERIA) {
            autonomiaBateria++;
        } else {
            Impresora.printVerde("La batería del vehículo " + toSimpleString() + " está llena!");  
        }
    }

    @Override
    public String toString() {
        return super.toString() + "  |  Porcentaje batería: " + getPorcentajeBateria() + "%";
    }
}
