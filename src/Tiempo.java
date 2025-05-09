import java.awt.Color;
import java.io.Serializable;

/**
 * Write a description of class TIempo here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Tiempo implements Serializable {
    // Conversión de unidades de tiempo a segundos
    private int segundosEnMinuto = 60;
    private int segundosEnHora = 60 * segundosEnMinuto;
    private int segundosEnDia = 24 * segundosEnHora;
    private int segundosEnMes = 30 * segundosEnDia; // Suponiendo meses de 30 días
    private int segundosEnAño = 12 * segundosEnMes; // Suponiendo años de 12 meses de 30 días cada uno
    
    private int segundosInternosPorCiclo = 5 * 60; // Se mide en segundosInternos / ciclo. Indica el número de segundos que transcurren en cada ciclo del bucle
    private int velocidad = 0; // La velocidad se mide en ciclos / segundoReal
    public static final int MAX_VELOCIDAD = 50; // La máxima velocidad es que se ejecuten 50 ciclos del bucle por cada segundo real.
    // 50 ciclos por segundo indican que la velocidad máxima son 60*50 segundos internos / segundo real
    
    public int segundo = 0;
    public int minuto = 0;
    public int hora = 0;
    public int dia = 0;
    public int mes = 0;
    public int año = 0;
    
    // instance variables - replace the example below with your own
    public int diasEntrePagos = 7;
    
    // Constructor vacío
    public Tiempo() {
        
   }
   
    public void setDiasEntrePagos(int dias) {
        diasEntrePagos = dias;
        Impresora.printGris("\nEl número de días entre pagos de las tasas de los usuarios se ha actualizado a " + diasEntrePagos + " días");
    }
   
    // Constructor
    public Tiempo(int hora, int minuto, int segundo, int dia, int mes, int año) {
        this.hora = hora;
        this.minuto = minuto;
        this.segundo = segundo;
        this.dia = dia;
        this.mes = mes;
        this.año = año;
    }
    
    // Constructor de copia
    public Tiempo(Tiempo otro) {
        this.hora = otro.hora;
        this.minuto = otro.minuto;
        this.segundo = otro.segundo;
        this.dia = otro.dia;
        this.mes = otro.mes;
        this.año = otro.año;
    }
    
    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }
    
    // La velocidad del tiempo se mide en cuántos segundos dura un ciclo
    public int getVelocidad() {
        return velocidad;
    }
    
    /// Método para calcular la diferencia entre dos tiempos y devolver un nuevo objeto Tiempo
    public static Tiempo calcularTiempoEntreTiempos(Tiempo inicio, Tiempo fin) {
        // Variables de diferencia
        int dias = fin.dia - inicio.dia;
        int horas = fin.hora - inicio.hora;
        int minutos = fin.minuto - inicio.minuto;
        int segundos = fin.segundo - inicio.segundo;

        // Ajustar los segundos
        if (segundos < 0) {
            segundos += 60;
            minutos--;
        }

        // Ajustar los minutos
        if (minutos < 0) {
            minutos += 60;
            horas--;
        }

        // Ajustar las horas
        if (horas < 0) {
            horas += 24;
            dias--;
        }

        // Ajustar los días si han cambiado de mes
        if (dias < 0) {
            dias += diasDelMes(inicio.mes, inicio.año);
            fin.mes--;
            if (fin.mes < 1) {
                fin.mes = 12;
                fin.año--;
            }
        }

        // Ajustar los meses si han cambiado de año
        int meses = fin.mes - inicio.mes;
        if (meses < 0) {
            meses += 12;
            fin.año--;
        }

        // Calcular la diferencia en años
        int años = fin.año - inicio.año;

        // Devolver un nuevo objeto Tiempo con la diferencia
        return new Tiempo(horas, minutos, segundos, dias, meses, años);
    }

    // Método para obtener los días en un mes considerando años bisiestos
    private static int diasDelMes(int mes, int año) {
        switch (mes) {
            case 1: // Enero
            case 3: // Marzo
            case 5: // Mayo
            case 7: // Julio
            case 8: // Agosto
            case 10: // Octubre
            case 12: // Diciembre
                return 31;
            case 4: // Abril
            case 6: // Junio
            case 9: // Septiembre
            case 11: // Noviembre
                return 30;
            case 2: // Febrero
                return esBisiesto(año) ? 29 : 28;
            default:
                return 30; // Valor por defecto (seguridad)
        }
    }

    // Método para comprobar si un año es bisiesto
    private static boolean esBisiesto(int año) {
        return (año % 4 == 0 && año % 100 != 0) || (año % 400 == 0);
    }

    public int pasarCiclosToDias(int ciclos) {
        return (ciclos / segundosInternosPorCiclo) / (segundosEnDia);
    }
    
    // Verifica si el alquiler fue en el último mes
    public boolean esUltimoMes(int mesAlquiler) {
        return mesAlquiler == mes;
    }
    
    // Verifica si el alquiler fue en los últimos 3 meses consecutivos
    public boolean esMesReciente(int mesAlquiler, int mesesRecientes) {
        return mesAlquiler >= (mes - mesesRecientes + 1);
    }
    
    // Verifica si el alquiler fue en los últimos 6 meses
    public boolean esUltimosSeisMeses(int mesAlquiler) {
        return mesAlquiler >= (mes - 6);
    }
    
    public Color getColorHora() {
        int horaActual = hora;
        int minutoActual = minuto; // Asumimos que también tienes el minuto para mayor suavidad
    
        // Convertir la hora y minutos actuales a una fracción de 24 horas
        float factorHora = (horaActual + minutoActual / 60f) / 24f;
    
        // Colores base para interpolar (puedes ajustarlos según prefieras)
        Color colorInicio = new Color(0, 0, 128);  // Color para medianoche (azul oscuro)
        Color colorMedio = new Color(255, 255, 200);  // Color para mediodía (amarillo claro)
        Color colorFinal = new Color(0, 0, 128);  // Color para medianoche (cierre del ciclo)
    
        // Dividimos el día en dos transiciones: noche -> día -> noche
        if (factorHora < 0.5f) {
            // De medianoche a mediodía (interpolar entre colorInicio y colorMedio)
            return interpolarColores(colorInicio, colorMedio, factorHora * 2);
        } else {
            // De mediodía a medianoche (interpolar entre colorMedio y colorFinal)
            return interpolarColores(colorMedio, colorFinal, (factorHora - 0.5f) * 2);
        }
    }
    
    // Método para interpolar entre dos colores
    private Color interpolarColores(Color c1, Color c2, float factor) {
        int r = (int) ((c2.getRed() - c1.getRed()) * factor + c1.getRed());
        int g = (int) ((c2.getGreen() - c1.getGreen()) * factor + c1.getGreen());
        int b = (int) ((c2.getBlue() - c1.getBlue()) * factor + c1.getBlue());
    
        return new Color(r, g, b);
    }

    public int getSegundosTotales() {
        // Calcular el total de segundos acumulados
        return segundo +
          (minuto * segundosEnMinuto) +
          (hora * segundosEnHora) +
          (dia * segundosEnDia) +
          (mes * segundosEnMes) +
          (año * segundosEnAño);
    }
    
    public int getCiclosTotales() {
        return getSegundosTotales() / segundosInternosPorCiclo;
    }
    
    public void transcurrirCiclo(Ciudad ciudad, Dinero dinero) {
        for (int i=0; i < segundosInternosPorCiclo; i++) {
            transcurrirSegundo(ciudad, dinero);
        }
    }
    
    public void revertirCiclo() {
        for (int i=0; i < segundosInternosPorCiclo; i++) {
            revertirSegundo();
        }
    }
    
    // Método para formatear el tiempo y la fecha como cadena
    public String formatearTiempo() {
        return String.format(
            "Hora (%02dh:%02dm:%02ds) - Fecha (%02d/%02d/%04d)",
            this.hora, this.minuto, this.segundo, this.dia, this.mes, this.año
        );
    }
    
    public String formatearHora() {
        return String.format(
            "%02dh:%02dm:%02ds",
            this.hora, this.minuto, this.segundo
        );
    }
    
    public void transcurrirSegundo(Ciudad ciudad, Dinero dinero) {
        segundo++;
        
        if (segundo == 60) {  // Verificar si hemos llegado a 60 segundos
            segundo = 0;  // Reiniciar los segundos a cero
            minuto++;
            
            if (minuto == 60) {  // Verificar si hemos llegado a 60 minutos
                minuto = 0;  // Reiniciar los minutos a cero
                hora++;
                
                // Si son las ocho de la mañana
                if (hora == 8) {
                    Impresora.printGris("\nEL DÍA AMANECE SOLEADO [" + formatearTiempo() + "]");
                } else if (hora == 21) {
                    Impresora.printGris("\nLA NOCHE ESTÁ TRANQUILA");
                }
                
                if (hora == 24) {  // Verificar si hemos llegado a 24 horas
                    hora = 0;  // Reiniciar las horas a cero
                    dia++;
                    
                    // Verificamos cobros de tasas con el cambio de día. 
                    dinero.verificarCobroDeTasas(ciudad, this);
                    
                    if (dia == 30) {  // Verificar si hemos llegado a 30 días
                        dia = 0;  // Reiniciar los días a cero
                        mes++;
                    
                        if (mes == 12) {  // Verificar si hemos llegado a 12 meses
                            mes = 0;  // Reiniciar los meses a cero
                            año++;
                        }
                    }
                }
            }
        }
    }
    
    public void revertirSegundo() {
        segundo--;  // Restamos un segundo
        
        if (segundo < 0) {
            segundo = 59;  // Si el segundo es negativo, retrocedemos a 59
            minuto--;  // Restamos un minuto
    
            if (minuto < 0) {
                minuto = 59;  // Si el minuto es negativo, retrocedemos a 59
                hora--;  // Restamos una hora
    
                if (hora < 0) {
                    hora = 23;  // Si la hora es negativa, retrocedemos a 23
                    dia--;  // Restamos un día
    
                    if (dia < 1) {
                        dia = 30;  // Si el día es menor a 1, retrocedemos a 30 (asumiendo que todos los meses tienen 30 días en este ejemplo)
                        mes--;  // Restamos un mes
    
                        if (mes < 1) {
                            mes = 12;  // Si el mes es menor a 1, retrocedemos a diciembre
                            año--;  // Restamos un año
                        }
                    }
                }
            }
        }
    }
    
    public void gestionarTranscursoTiempo(long duracionBucle) {        
        try {
            // Calcular los milisegundos de un ciclo en función de la velocidad.
            // Si la velocidad es 0, el bucle no avanza.
            if (velocidad > 0) {
                // Tiempo objetivo en milisegundos: 1000 ms (1 segundo) dividido por la velocidad (ciclos/segundo real)
                long tiempoObjetivo = 1000L / velocidad;
    
                // Calcular el tiempo restante para alcanzar el tiempo objetivo después de la duración del bucle
                long tiempoRestante = tiempoObjetivo - duracionBucle;
    
                // Si el tiempo restante es positivo, aplicar el delay para alcanzar el objetivo.
                // Si es negativo, significa que el bucle ya ha tardado más de lo deseado, y no se debe aplicar un delay.
                if (tiempoRestante > 0) {
                    Thread.sleep(tiempoRestante);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
