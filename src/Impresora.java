import java.awt.Color;

public final class Impresora {
    // Definir códigos de color
    public static final String RESET = "\u001B[0m";
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CIAN = "\u001B[36m";
    public static final String BLANCO = "\u001B[37m";
    public static final String NARANJA = "\u001B[38;5;214m"; // Aproximación con un color cercano al naranja
    public static final String GRIS = "\u001B[38;5;242m";  // Gris
    public static final String CIAN_CLARO = "\u001B[38;5;51m"; // Cian claro
    public static final String MAGENTA_CLARO = "\u001B[38;5;13m"; // Magenta claro
    public static final String BLANCO_BRILLANTE = "\u001B[97m"; // Blanco brillante

    // Constructor privado para evitar instanciación
    private Impresora() {
        // No se instancia
    }

    // Método estático para imprimir en color negro (modo normal)
    public static void print(String mensaje) {
        System.out.println(mensaje);
    }
    
    // Método estático para imprimir en el color de la clase recibida
    public static void printColorClase(Class<?> clase, String mensaje) {
        try {
            // Obtener el campo estático colorClase directamente desde la clase
            Color color = (Color) clase.getField("colorClase").get(null);

            // Convertir el color de java.awt.Color a un código ANSI
            String colorAnsi = convertirColorAAnsi(color);

            // Imprimir el mensaje en el color de la clase
            System.out.println(colorAnsi + mensaje + RESET);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println("No se encontró el campo colorClase o no es accesible.");
        }
    }

    // Método para convertir un color java.awt.Color a un código ANSI
    private static String convertirColorAAnsi(Color color) {
        int rojo = color.getRed();
        int verde = color.getGreen();
        int azul = color.getBlue();

        // Convertir RGB a un color ANSI 256 usando el formato \u001B[38;5;Xm
        int colorAnsi = 16 + (36 * (rojo / 51)) + (6 * (verde / 51)) + (azul / 51);
        return "\u001B[38;5;" + colorAnsi + "m";
    }
        
    
    // Método estático para imprimir en color rojo
    public static void printRojo(String mensaje) {
        System.out.println(ROJO + mensaje + RESET);
    }

    // Método estático para imprimir en color verde
    public static void printVerde(String mensaje) {
        System.out.println(VERDE + mensaje + RESET);
    }

    // Método estático para imprimir en color amarillo
    public static void printAmarillo(String mensaje) {
        System.out.println(AMARILLO + mensaje + RESET);
    }

    // Método estático para imprimir en color azul
    public static void printAzul(String mensaje) {
        System.out.println(AZUL + mensaje + RESET);
    }

    // Método estático para imprimir en color magenta
    public static void printMagenta(String mensaje) {
        System.out.println(MAGENTA + mensaje + RESET);
    }

    // Método estático para imprimir en color cian
    public static void printCian(String mensaje) {
        System.out.println(CIAN + mensaje + RESET);
    }

    // Método estático para imprimir en color blanco
    public static void printBlanco(String mensaje) {
        System.out.println(BLANCO + mensaje + RESET);
    }

    // Método estático para imprimir en color naranja (aproximado)
    public static void printNaranja(String mensaje) {
        System.out.println(NARANJA + mensaje + RESET);
    }

    // Método estático para imprimir en color gris
    public static void printGris(String mensaje) {
        System.out.println(GRIS + mensaje + RESET);
    }

    // Método estático para imprimir en color cian claro
    public static void printCianClaro(String mensaje) {
        System.out.println(CIAN_CLARO + mensaje + RESET);
    }

    // Método estático para imprimir en color magenta claro
    public static void printMagentaClaro(String mensaje) {
        System.out.println(MAGENTA_CLARO + mensaje + RESET);
    }

    // Método estático para imprimir en blanco brillante
    public static void printBlancoBrillante(String mensaje) {
        System.out.println(BLANCO_BRILLANTE + mensaje + RESET);
    }
}
