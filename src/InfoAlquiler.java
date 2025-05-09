import java.io.Serializable;

public class InfoAlquiler implements Serializable {
    private Usuario usuario;
    
    private Vehiculo vehiculoAlquilado;
    
    private int mesAlquiler;
    
    private Tiempo tiempoInicioAlquiler, tiempoFinalAlquiler, tiempoAlquiler;
    
    InfoAlquiler(Usuario usuario, Vehiculo vehiculoAlquilado) {
        this.usuario = usuario;
        
        this.tiempoInicioAlquiler = usuario.tiempoInicioAlquiler;
        this.tiempoFinalAlquiler = usuario.tiempoFinalAlquiler;
        this.mesAlquiler = this.tiempoFinalAlquiler.mes;
        
        this.tiempoAlquiler = Tiempo.calcularTiempoEntreTiempos(tiempoInicioAlquiler, tiempoFinalAlquiler);
        this.vehiculoAlquilado = vehiculoAlquilado;
    }
    
    public int getMesAlquiler() {
        return mesAlquiler;
    }
    
    public Class<? extends Vehiculo> getClaseVehiculoAlquilado() {
        return vehiculoAlquilado.getClass();
    }

    @Override
    public String toString() {
        return "===== ALQUILER =====\n" +
               "Usuario: " + usuario.getNombre() + "\n" +
               "Tiempo inicio: " + tiempoInicioAlquiler.formatearTiempo() + "\n" +
               "Tiempo final: " + tiempoFinalAlquiler.formatearTiempo() + "\n" +
               "Duración: " + tiempoAlquiler.formatearHora() + "\n" +
               "Vehículo Alquilado: " + vehiculoAlquilado.toSimpleString() + "\n" +
               "=====================";
    }
}
