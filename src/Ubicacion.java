import java.util.Objects;
import java.io.Serializable;

/**
 * Write a description of class Ubicacion here.
 *
 * @author Kopa
 * @version (a version number or a date)
 */
public class Ubicacion implements Serializable {
    // instance variables - replace the example below with your own
    private Integer posX = null;
    private Integer posY = null;

    /**
     * Constructor para crear una ubicación con coordenadas dadas.
     * @param x La coordenada X de la ubicación.
     * @param y La coordenada Y de la ubicación.
     */
    public Ubicacion(int x, int y) {
        this.posX = x;
        this.posY = y;
    }
    
    public Ubicacion() {
        this.posX = 0;
        this.posY = 0;
    }
    
    public void setUbicacion(Ubicacion ubi) {
        posX = ubi.getPosX();
        posY = ubi.getPosY();
    }
    
    public void setUbicacion(int x, int y) {
        this.posX = x;
        this.posY = y;
    }
    
    public void setPosX(int x) {
        this.posX = x;
    }
    
    public void setPosY(int y) {
        this.posY = y;
    }

    /**
     * An example of a method - replace this comment with your own
     *
     * @return    devuelve la ubicación en un array fijo de dos posiciones
     */
    public int[] getUbicacion() {
        return new int[] { posX, posY };
    }
    
    public int getPosX() {
        return posX;
    }
    
    public int getPosY() {
        return posY;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        Ubicacion that = (Ubicacion) o;
        return posX == that.posX && posY == that.posY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(posX, posY);
    }
 
    @Override
    public String toString() {
        String x = (posX != null) ? posX.toString() : "undefined";
        String y = (posY != null) ? posY.toString() : "undefined";
        
        return "[" + x + " | " + y + "]";
    }
}
