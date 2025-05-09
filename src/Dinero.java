import java.util.List;

/**
 * Write a description of class Dinero here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Dinero {
    // Esto quizás debería moverse a otra clase más general
    public double precioTasasEnEuros = 85.57;    
    
    public double totalFacturadoTrabajadores = 0;
    public double totalPagadoUsuarios = 0;
    public double balanceSistema; // totalPagadoUsuarios - totalFacturadoTrabajadores 

    /**
     * Constructor for objects of class Dinero
     */
    public Dinero() {
    }
    
    public double getTotalFacturadoTrabajadores() {
        return totalFacturadoTrabajadores;
    }
    
    public double getTotalPagadoUsuarios() {
        return totalPagadoUsuarios;
    }
    
    public double getBalanceSistema() {
        // Calculamos el balance del sistema
        this.calcularBalanceSistema();
        return balanceSistema;
    }
    
    public void calcularBalanceSistema() {
        balanceSistema = totalPagadoUsuarios - totalFacturadoTrabajadores;
    }
    
    public void setPrecioTasas(double precio) {
        precioTasasEnEuros = precio;
        Impresora.printGris("\nEl precio de las tasas de los usuarios se ha actualizado a " + precioTasasEnEuros + "€");
    }
    
    public void verificarCobroDeTasas(Ciudad ciudad, Tiempo tiempo) {
        List<Entidad> entidades = ciudad.obtenerEntidadesPorClase(Usuario.class);
        
        // Iteramos sobre las entidades y las casteamos a Usuario
        for (Entidad entidad : entidades) {
            
            if (entidad instanceof Usuario) {  // Verificar si la entidad es de tipo Usuario
                Usuario usuario = (Usuario) entidad;  // Hacer el casting explícito
                
                int edadUsuarioEnDias = tiempo.pasarCiclosToDias(usuario.edad);
                
                if (edadUsuarioEnDias % tiempo.diasEntrePagos == 0) {
                    Impresora.printVerde("\nHa llegado el día de cobro para " + usuario.toSimpleString() + ". Procediendo a realizar el cobro...");
                    cobrarTasasUsuarios(usuario);
                }
            }
        }
    }
    
    // El metodo mediante el cual los usuarios pagan sus tasas
    private void cobrarTasasUsuarios(Usuario usuario) {
        if (usuario.getSaldo() - precioTasasEnEuros < 0) {
            Impresora.printRojo("\nEl usuario " + usuario.toSimpleString() + " no ha podido pagar el importe de tasas periódico");
    
            // Calcular la cantidad necesaria para recargar el saldo
            double cantidadNecesaria = precioTasasEnEuros - usuario.getSaldo();
            
            // Generar una cantidad aleatoria entre [cantidadNecesaria, 2 * cantidadNecesaria]
            double cantidadRecargar = cantidadNecesaria + (Math.random() * cantidadNecesaria); // Se añade algo más para la próxima con algo de aleatoriedads
            
            // Recargar el saldo con la cantidad generada
            usuario.recargarSaldo(cantidadRecargar);
            
            Impresora.printVerde("\n" + usuario.toSimpleString() + " ha recargado " + String.format("%.2f", cantidadRecargar) + " \u20AC");
            
            // Se llama a la propia función para proceder con el pago de las tasas
            cobrarTasasUsuarios(usuario);
        
        } else {
            // Si tiene suficiente saldo, paga las tasas
            usuario.setSaldo(usuario.getSaldo() - precioTasasEnEuros);
            usuario.totalPagadoTasas += precioTasasEnEuros;
            Impresora.printVerde("\nEl usuario " + usuario.toSimpleString() + " ha pagado las tasas de " + precioTasasEnEuros + "€.");
        }
    }
}
