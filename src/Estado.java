import java.util.ArrayList;
import javax.swing.JButton;
import java.util.concurrent.CopyOnWriteArrayList;

public class Estado {
    private JButton[][] estadoCuadricula;
    private CopyOnWriteArrayList<Entidad> estadoEntidades;

    public Estado(JButton[][] cuadricula, CopyOnWriteArrayList<Entidad> entidades) {
        this.estadoCuadricula = cuadricula;
        this.estadoEntidades = entidades;
    }

    // Métodos para acceder al estado
    public JButton[][] obtenerEstadoCuadricula() {
        return estadoCuadricula;
    }

    public CopyOnWriteArrayList<Entidad> obtenerEstadoEntidades() {
        return estadoEntidades;
    }
}
