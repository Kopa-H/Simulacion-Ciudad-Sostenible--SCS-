
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JScrollPane;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.JFrame;

public class MenuSistema extends Menu {

    private UtilidadesMenu utilidadesMenu = new UtilidadesMenu();
    
    private TipoUsuario tipoUsuario;
    private GestorMenus gestorMenus;
    private JFrame frame;
    
    protected String nombreMenuPrincipal;
    
    Simulacion simulacion;
    Ciudad ciudad;
    Persona personaAccedida;

    /**
     * Constructor para la clase MenuSistema
     * @param tipoUsuario Tipo de usuario que se pasará a la interfaz
     */
    public MenuSistema(Simulacion simulacion, Ciudad ciudad, TipoUsuario tipoUsuario, GestorMenus gestorMenus, Persona personaAccedida) {
        this.simulacion = simulacion;
        this.ciudad = ciudad;
        this.personaAccedida = personaAccedida;

        this.tipoUsuario = tipoUsuario;  // Asignamos el tipo de usuario
        this.gestorMenus = gestorMenus;
        this.frame = gestorMenus.frame;

        Menu menu = new Menu();
        menu.nombre = "Menú " + tipoUsuario.name();

        this.nombreMenuPrincipal = menu.nombre; 
        
        frame.setTitle(menu.nombre);
        
        panel = menu.crearPanel();
        panel.setBackground(Color.GREEN);
        
        agregarBotonesMenu();
        
        agregarBotones(botones, panel);
    }
    
    private void agregarBotonesMenu() {
        // Dependiendo del tipo de usuario, los botones serán diferentes
        switch (tipoUsuario) {
            case TipoUsuario.ADMINISTRADOR:
                agregarOpcionesAdministrador();
                break;

            case TipoUsuario.USUARIO_NORMAL:
                agregarOpcionesUsuarioNormal();
                break;

            case TipoUsuario.USUARIO_PREMIUM:
                agregarOpcionesUsuarioNormal();
                String nombreMenu = "Reservar Vehículo";
                botones.put(nombreMenu, new Boton(nombreMenu, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(frame, "Reservar seleccionada");
                    }
                }));
                break;

            case TipoUsuario.TECNICO_MANTENIMIENTO:
            case TipoUsuario.MECANICO:
                agregarOpcionesComunesTrabajadores();
                break;
        }
    }
    
    private void agregarOpcionesAdministrador() {
        String nombreMenu;
        
        // Botones existentes
        nombreMenu = "Gestor de Entidades";
        botones.put(nombreMenu, new Boton(nombreMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                iniciarGestorEntidades();
            }
        }));
    
        // Nuevos botones con sus respectivas acciones
        nombreMenu = "Visualizar Estados Baterías";
        botones.put(nombreMenu, new Boton(nombreMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                utilidadesMenu.mostrarInfo(ciudad, UtilidadesMenu.TipoInfoMostrada.BATERIAS);
            }
        }));
        
        nombreMenu = "Visualizar Estados Mecánicos";
        botones.put(nombreMenu, new Boton(nombreMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                utilidadesMenu.mostrarInfo(ciudad, UtilidadesMenu.TipoInfoMostrada.ESTADO_MECANICO);
            }
        }));
        
        nombreMenu = "Visualizar Estado Bases";
        botones.put(nombreMenu, new Boton(nombreMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                utilidadesMenu.mostrarInfo(ciudad, UtilidadesMenu.TipoInfoMostrada.BASES);
            }
        }));
        
        nombreMenu = "Visualizar Estado Promociones";
        botones.put(nombreMenu, new Boton(nombreMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                visualizarEstadoPromociones();
            }
        }));
        
        nombreMenu = "Modificar Tasas del Servicio";
        botones.put(nombreMenu, new Boton(nombreMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                iniciarMenuTarifas();
            }
        }));
        
        nombreMenu = "Visualizar Estadísticas";
        botones.put(nombreMenu, new Boton(nombreMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                visualizarEstadisticas(ciudad);
            }
        }));
    }
    
    public void visualizarEstadisticas(Ciudad ciudad) {
        Menu menu = new Menu();
        menu.nombre = "Estadísticas de la Simulación";
    
        JFrame frame = menu.crearNuevaVentana();
        frame.setSize(600, 650);
        
        JPanel panel = menu.crearPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // Asegura que los componentes se coloquen en vertical
    
        // Método para actualizar las estadísticas
        Runnable actualizarEstadisticas = () -> {
            panel.removeAll();  // Limpiar el panel antes de volver a agregar las estadísticas
    
            ArrayList<Usuario> usuarios = new ArrayList<>();
            ArrayList<Trabajador> trabajadores = new ArrayList<>();
            ArrayList<Vehiculo> vehiculos = new ArrayList<>();
    
            // Recolectar los datos actualizados de la ciudad
            for (Entidad entidad : ciudad.getEntidades()) {
                if (entidad instanceof Usuario usuario) {
                    usuarios.add(usuario);
                } else if (entidad instanceof Trabajador trabajador) {
                    trabajadores.add(trabajador);
                } else if (entidad instanceof Vehiculo vehiculo) {
                    vehiculos.add(vehiculo);
                }
            }
    
            // Agregar estadísticas actualizadas al panel
            panel.add(generarEstadisticasGenerales(ciudad));
            panel.add(generarEstadisticasUsuarios(ciudad, usuarios));
            panel.add(generarEstadisticasTrabajadores(ciudad, trabajadores));
            panel.add(generarEstadisticasVehiculos(vehiculos));
    
            // Revalidar y repintar el panel para que se muestren los cambios
            panel.revalidate();
            panel.repaint();
        };
    
        // Ejecutar la actualización inicial
        actualizarEstadisticas.run();
    
        // Crear el Timer que actualiza las estadísticas cada 5 segundos (5000 milisegundos)
        Timer timer = new Timer(2000, e -> actualizarEstadisticas.run());
        timer.start();
    
        // Detener el Timer cuando la ventana se cierra
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                timer.stop();
                frame.dispose();
            }
        });
    
        frame.add(agregarScroll(panel));
        frame.setVisible(true);
    }
    
    private JPanel generarEstadisticasGenerales(Ciudad ciudad) {
        // Obtenemos los valores necesarios
        double totalFacturadoTrabajadores = ciudad.dinero.getTotalFacturadoTrabajadores();
        double totalPagadoUsuarios = ciudad.dinero.getTotalPagadoUsuarios();
        double balanceSistema = ciudad.dinero.getBalanceSistema();

        // Creamos el panel para las estadísticas generales
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // Organizar en vertical

        JLabel labelCabecera = new JLabel("Estadísticas Generales");
        UtilidadesMenu.aplicarFuenteCabecera(labelCabecera);
        panel.add(labelCabecera);
        
        // Creamos etiquetas para mostrar las estadísticas generales
        JLabel labelFacturadoTrabajadores = new JLabel("    Total Facturado por Trabajadores: " + String.format("%.2f", totalFacturadoTrabajadores) + " €");
        JLabel labelPagadoUsuarios = new JLabel("    Total Pagado por Usuarios: " + String.format("%.2f", totalPagadoUsuarios) + " €");
        JLabel labelBalanceSistema = new JLabel("    Balance del Sistema: " + String.format("%.2f", balanceSistema) + " €");
       
        UtilidadesMenu.aplicarFuenteTexto(labelFacturadoTrabajadores);
        UtilidadesMenu.aplicarFuenteTexto(labelPagadoUsuarios);
        UtilidadesMenu.aplicarFuenteTexto(labelBalanceSistema);
        
        // Añadimos las etiquetas al panel
        panel.add(labelFacturadoTrabajadores);
        panel.add(labelPagadoUsuarios);
        panel.add(labelBalanceSistema);
        panel.add(Box.createVerticalStrut(10));  // Espaciado de 10 píxeles

        // Devolvemos el panel con las estadísticas generales
        return panel;
    }
    
    private JPanel generarEstadisticasUsuarios(Ciudad ciudad, ArrayList<Usuario> usuarios) {
        // Inicializamos las variables para estadísticas
        int usuariosNormales = 0;
        int usuariosPremium = 0;
        
        double precioTasas = ciudad.dinero.precioTasasEnEuros;
        int diasEntrePagos = ciudad.tiempo.diasEntrePagos;
        double totalPagadoTasas = 0;
    
        // Calculamos las estadísticas a partir de la lista de usuarios
        for (Usuario usuario : usuarios) {
            if (usuario.isPremium) {
                usuariosPremium++;
            } else {
                usuariosNormales++;
            }
            totalPagadoTasas += usuario.getTotalPagadoTasas();
        }
        
        ciudad.dinero.totalPagadoUsuarios = totalPagadoTasas;
    
        // Creamos el panel donde mostraremos las estadísticas
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // Layout para organizar los elementos en vertical
        
        JLabel labelCabecera = new JLabel("Estadísticas Usuarios");
        UtilidadesMenu.aplicarFuenteCabecera(labelCabecera);
        panel.add(labelCabecera);
    
        // Creamos etiquetas para mostrar cada estadística
        JLabel labelUsuariosNormales = new JLabel("    Usuarios Normales: " + usuariosNormales);
        JLabel labelUsuariosPremium = new JLabel("    Usuarios Premium: " + usuariosPremium);
        
        JLabel labelPrecioTasas = new JLabel("    Precio Tasas: " + String.format("%.2f", precioTasas) + " €");
        JLabel labelDiasEntrePagos = new JLabel("    Días entre Pagos de Tasas: " + diasEntrePagos + " días");
        JLabel labelTotalPagadoTasas = new JLabel("    Total Pagado en Tasas: " + String.format("%.2f", totalPagadoTasas) + " €");
        
        UtilidadesMenu.aplicarFuenteTexto(labelUsuariosNormales);
        UtilidadesMenu.aplicarFuenteTexto(labelUsuariosPremium);
        UtilidadesMenu.aplicarFuenteTexto(labelPrecioTasas);
        UtilidadesMenu.aplicarFuenteTexto(labelDiasEntrePagos);
        UtilidadesMenu.aplicarFuenteTexto(labelTotalPagadoTasas);

        panel.add(labelUsuariosNormales);
        panel.add(labelUsuariosPremium);
        panel.add(Box.createVerticalStrut(10));  // Espaciado de 10 píxeles
        panel.add(labelPrecioTasas);
        panel.add(labelDiasEntrePagos);
        panel.add(labelTotalPagadoTasas);
        panel.add(Box.createVerticalStrut(10));  // Espaciado de 10 píxeles
    
        // Devolvemos el panel listo para ser agregado al panel general
        return panel;
    }
    
    private JPanel generarEstadisticasTrabajadores(Ciudad ciudad, ArrayList<Trabajador> trabajadores) {    
        // Inicializamos las variables de estadísticas
        int mecanicos = 0;
        int tecnicosMantenimiento = 0;
        
        int trabajosCompletadosMecanicos = 0;
        int trabajosCompletadosTecnicosMantenimiento = 0;
        
        double totalFacturadoMecanicos = 0;
        double totalFacturadoTecnicosMantenimiento = 0;

        // Calculamos las estadísticas a partir de la lista de trabajadores
        for (Trabajador trabajador : trabajadores) {
            if (trabajador instanceof Mecanico) {
                mecanicos++;
                totalFacturadoMecanicos += trabajador.getTotalFacturado();
                trabajosCompletadosMecanicos += trabajador.getTrabajosCompletados();
            } else if (trabajador instanceof TecnicoMantenimiento) {
                tecnicosMantenimiento++;
                totalFacturadoTecnicosMantenimiento += trabajador.getTotalFacturado();
                trabajosCompletadosTecnicosMantenimiento += trabajador.getTrabajosCompletados();
            }
        }

        // Actualizamos el total facturado a los trabajadores en la ciudad
        ciudad.dinero.totalFacturadoTrabajadores = totalFacturadoMecanicos + totalFacturadoTecnicosMantenimiento;

        // Creamos el panel donde mostraremos las estadísticas de los trabajadores
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // Organizar en vertical
        
        // Añadimos una cabecera
        JLabel labelCabecera = new JLabel("Estadísticas Trabajadores");
        UtilidadesMenu.aplicarFuenteCabecera(labelCabecera);
        panel.add(labelCabecera);

        // Creamos etiquetas para mostrar las estadísticas de trabajadores
        JLabel labelMecanicos = new JLabel("    Mecánicos: " + mecanicos);
        JLabel labelTecnicosMantenimiento = new JLabel("    Técnicos de Mantenimiento: " + tecnicosMantenimiento);
        
        JLabel labelTrabajosCompletadosMecanicos = new JLabel("    Trabajos Completados por Mecánicos: " + trabajosCompletadosMecanicos);
        JLabel labelTrabajosCompletadosTecnicosMantenimiento = new JLabel("    Trabajos Completados por Técnicos Mantenimiento: " + trabajosCompletadosTecnicosMantenimiento);
        
        JLabel labelTotalFacturadoMecanicos = new JLabel("    Total Facturado por Mecánicos: " + String.format("%.2f", totalFacturadoMecanicos) + " €");
        JLabel labelTotalFacturadoTecnicosMantenimiento = new JLabel("    Total Facturado por Técnicos de Mantenimiento: " + String.format("%.2f", totalFacturadoTecnicosMantenimiento) + " €");
        
        UtilidadesMenu.aplicarFuenteTexto(labelMecanicos);
        UtilidadesMenu.aplicarFuenteTexto(labelTecnicosMantenimiento);
        UtilidadesMenu.aplicarFuenteTexto(labelTrabajosCompletadosMecanicos);
        UtilidadesMenu.aplicarFuenteTexto(labelTrabajosCompletadosTecnicosMantenimiento);
        UtilidadesMenu.aplicarFuenteTexto(labelTotalFacturadoMecanicos);
        UtilidadesMenu.aplicarFuenteTexto(labelTotalFacturadoTecnicosMantenimiento);

        // Añadimos las etiquetas al panel
        panel.add(labelMecanicos);
        panel.add(labelTecnicosMantenimiento);
        panel.add(Box.createVerticalStrut(10));  // Espaciado de 10 píxeles
        panel.add(labelTrabajosCompletadosMecanicos);
        panel.add(labelTrabajosCompletadosTecnicosMantenimiento);
        panel.add(Box.createVerticalStrut(10));  // Espaciado de 10 píxeles
        panel.add(labelTotalFacturadoMecanicos);
        panel.add(labelTotalFacturadoTecnicosMantenimiento);
        panel.add(Box.createVerticalStrut(10));  // Espaciado de 10 píxeles

        // Devolvemos el panel con las estadísticas de los trabajadores
        return panel;
    }

    private JPanel generarEstadisticasVehiculos(ArrayList<Vehiculo> vehiculos) {
        // Inicializamos las variables de estadísticas
        int motos = 0;
        int bicis = 0;
        int patinetes = 0;

        int motosAlquiladas = 0;
        int bicisAlquiladas = 0;
        int patinetesAlquilados = 0;
        int viajesRealizados = 0;

        int fallosMecanicos = 0;
        int recargasBateria = 0;

        int vecesArrastrados = 0;

        double distanciaRecorrida = 0;
        double distanciaRecorridaMediaPorViajeRealizado = 0;

        // Calculamos las estadísticas a partir de la lista de vehículos
        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo instanceof Moto) {
                motos++;
                motosAlquiladas += vehiculo.getVecesAlquilado();
            } else if (vehiculo instanceof Bicicleta) {
                bicis++;
                bicisAlquiladas += vehiculo.getVecesAlquilado();
            } else if (vehiculo instanceof Patinete) {
                patinetes++;
                patinetesAlquilados += vehiculo.getVecesAlquilado();
            }

            viajesRealizados = motosAlquiladas + bicisAlquiladas + patinetesAlquilados;

            fallosMecanicos += vehiculo.getTotalFallosMecanicos();
            recargasBateria += vehiculo.getRecargasBateria();
            vecesArrastrados += vehiculo.getVecesArrastrado();
            distanciaRecorrida += vehiculo.getDistanciaRecorrida();
        }
        
        distanciaRecorridaMediaPorViajeRealizado = (viajesRealizados != 0) ? (distanciaRecorrida / (double) viajesRealizados) : 0.0;

        // Creamos el panel donde mostraremos las estadísticas de los vehículos
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // Organizar en vertical

        // Añadimos una cabecera
        JLabel labelCabecera = new JLabel("Estadísticas Vehículos");
        UtilidadesMenu.aplicarFuenteCabecera(labelCabecera);
        panel.add(labelCabecera);
        
        // Creamos etiquetas para mostrar las estadísticas de vehículos
        JLabel labelMotos = new JLabel("    Motos: " + motos);
        JLabel labelBicis = new JLabel("    Bicicletas: " + bicis);
        JLabel labelPatinetes = new JLabel("    Patinetes: " + patinetes);
        JLabel labelMotosAlquiladas = new JLabel("    Motos Alquiladas: " + motosAlquiladas);
        JLabel labelBicisAlquiladas = new JLabel("    Bicicletas Alquiladas: " + bicisAlquiladas);
        JLabel labelPatinetesAlquilados = new JLabel("    Patinetes Alquilados: " + patinetesAlquilados);
        JLabel labelViajesRealizados = new JLabel("    Viajes Realizados: " + viajesRealizados);
        JLabel labelFallosMecanicos = new JLabel("    Fallos Mecánicos: " + fallosMecanicos);
        JLabel labelRecargasBateria = new JLabel("    Recargas de Batería: " + recargasBateria);
        JLabel labelVecesArrastrados = new JLabel("    Veces Arrastrados: " + vecesArrastrados);
        JLabel labelDistanciaRecorrida = new JLabel("    Distancia Recorrida: " + String.format("%.2f", distanciaRecorrida) + " km");
        JLabel labelDistanciaRecorridaMediaPorViajeRealizado = new JLabel("    Distancia Media por Viaje Realizado: " + String.format("%.2f", distanciaRecorridaMediaPorViajeRealizado) + " km/viaje");
        
        // Aplicar la fuente a todas las etiquetas
        UtilidadesMenu.aplicarFuenteTexto(labelMotos);
        UtilidadesMenu.aplicarFuenteTexto(labelBicis);
        UtilidadesMenu.aplicarFuenteTexto(labelPatinetes);
        UtilidadesMenu.aplicarFuenteTexto(labelMotosAlquiladas);
        UtilidadesMenu.aplicarFuenteTexto(labelBicisAlquiladas);
        UtilidadesMenu.aplicarFuenteTexto(labelPatinetesAlquilados);
        UtilidadesMenu.aplicarFuenteTexto(labelViajesRealizados);
        UtilidadesMenu.aplicarFuenteTexto(labelFallosMecanicos);
        UtilidadesMenu.aplicarFuenteTexto(labelRecargasBateria);
        UtilidadesMenu.aplicarFuenteTexto(labelVecesArrastrados);
        UtilidadesMenu.aplicarFuenteTexto(labelDistanciaRecorrida);
        UtilidadesMenu.aplicarFuenteTexto(labelDistanciaRecorridaMediaPorViajeRealizado);

        // Añadimos las etiquetas al panel
        panel.add(labelMotos);
        panel.add(labelBicis);
        panel.add(labelPatinetes);
        panel.add(Box.createVerticalStrut(10));  // Añadir espaciado vertical entre secciones
        panel.add(labelMotosAlquiladas);
        panel.add(labelBicisAlquiladas);
        panel.add(labelPatinetesAlquilados);
        panel.add(labelViajesRealizados);
        panel.add(Box.createVerticalStrut(10));  // Añadir espaciado vertical entre secciones
        panel.add(labelFallosMecanicos);
        panel.add(labelRecargasBateria);
        panel.add(labelVecesArrastrados);
        panel.add(labelDistanciaRecorrida);
        panel.add(labelDistanciaRecorridaMediaPorViajeRealizado);
        panel.add(Box.createVerticalStrut(10));  // Espaciado de 10 píxeles

        // Devolvemos el panel con las estadísticas de los vehículos
        return panel;
    }
    
    public void iniciarMenuTarifas() {
        Menu menu = new Menu();
        menu.nombre = "Menú Tarifas";
    
        JDialog dialogo = menu.crearNuevoDialogo();
        JPanel panel = menu.crearPanel();
    
        JLabel labelDias = new JLabel("Introduce el número de días que transcurren entre cobros:");
        UtilidadesMenu.aplicarFuenteTexto(labelDias);
        JTextField campoDias = new JTextField(10);
    
        JLabel labelTasas = new JLabel("Introduce el precio de las tasas en euros:");
        UtilidadesMenu.aplicarFuenteTexto(labelTasas);
        JTextField campoTasas = new JTextField(10);
    
        JButton botonConfirmar = new JButton("Confirmar");
    
        panel.add(labelDias);
        panel.add(campoDias);
        panel.add(Box.createVerticalStrut(10)); // Espaciado
        panel.add(labelTasas);
        panel.add(campoTasas);
        panel.add(Box.createVerticalStrut(10));
        
        // CONSULTAR VEHÍCULOS DISPONIBLES
        String nombreBoton = "Aceptar";
        menu.botones.put(nombreBoton, new Boton(nombreBoton, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int dias = Integer.parseInt(campoDias.getText().trim());
                    double tasas = Double.parseDouble(campoTasas.getText().trim());
        
                    if (dias <= 0 || tasas < 0) {
                        UtilidadesMenu.showMensajeError(dialogo, "Los valores deben ser positivos.");
                        return;
                    }
                    
                    if (dias > 365) {
                        UtilidadesMenu.showMensajeError(dialogo, "Los días entre cobros no pueden superar el año (365).");
                        return;
                    }
                    
                    if (tasas > 1000) {
                        UtilidadesMenu.showMensajeError(dialogo, "El precio de las tasas no puede superar los 1000€.");
                        return;
                    }
        
                    ciudad.tiempo.setDiasEntrePagos(dias);
                    ciudad.dinero.setPrecioTasas(tasas);
                    UtilidadesMenu.showMensajeExito(dialogo, "Tarifas actualizadas correctamente.");
                    dialogo.dispose(); // Cierra el diálogo después de confirmar
        
                } catch (NumberFormatException ex) {
                    UtilidadesMenu.showMensajeError(dialogo, "Introduce valores numéricos válidos.");
                }
            }
        }));
    
        agregarBotones(menu.botones, panel);
    
        dialogo.add(panel);
        dialogo.setVisible(true);
    }
    
    public void iniciarMenuTrasladoVehiculo() {
        Menu menu = new Menu();
        menu.nombre = "Menú de traslado de vehículo";
        
        JDialog dialogo = menu.crearNuevoDialogo();
        JPanel panel = menu.crearPanel();
    
        // Se selecciona el tipo de vehículo
        Class<?> claseVehiculo = utilidadesMenu.seleccionarClase("vehiculo");
        
        // Verificar si el valor es nulo
        if (claseVehiculo == null) {
            dialogo.dispose(); // Cerrar el diálogo si el valor es nulo
            return; // Salir del método
        }
    
        // Se selecciona su índice
        int indice = utilidadesMenu.seleccionarIndice();
        
        // Verificar si el índice no es válido (-1 indica un valor no seleccionado en el ejemplo previo)
        if (indice == -1) {
            dialogo.dispose(); // Cerrar el diálogo si no se seleccionó un índice válido
            return; // Salir del método
        }
    
        Vehiculo vehiculoSeleccionado = (Vehiculo) ciudad.encontrarEntidad(claseVehiculo, indice);
        
        // Verificar si el vehículo es nulo
        if (vehiculoSeleccionado == null) {
            dialogo.dispose(); // Cerrar el diálogo si no se encontró un vehículo
            return; // Salir del método
        }
    
        // Se selecciona la ubicación de destino
        Ubicacion ubicacionDestino = utilidadesMenu.seleccionarUbicacion();
        
        // Verificar si la ubicación es nula
        if (ubicacionDestino == null) {
            dialogo.dispose(); // Cerrar el diálogo si no se seleccionó una ubicación válida
            return; // Salir del método
        }
    
        // Si todos los valores son válidos, se procede con el traslado del vehículo
        Trabajador trabajadorAccedido = (Trabajador) personaAccedida; 
        trabajadorAccedido.activarModoTraslado();
        trabajadorAccedido.setUbicacionTraslado(ubicacionDestino);
        trabajadorAccedido.setEntidadAsignada(ciudad, vehiculoSeleccionado);
    }
    
    public void mostrarFacturasTrabajador() {
        Menu menu = new Menu();
        menu.nombre = "Facturas Trabajador " + personaAccedida.toSimpleString();
    
        JFrame frame = menu.crearNuevaVentana();
        JPanel panel = menu.crearPanel();

        frame.add(agregarScroll(panel));
        frame.setVisible(true);
    
        Trabajador trabajadorAccedido = (Trabajador) personaAccedida;
    
        // Etiqueta de "No hay facturas"
        JLabel mensajeVacio = new JLabel("Todavía no hay ninguna factura.");
        mensajeVacio.setHorizontalAlignment(SwingConstants.CENTER);
        mensajeVacio.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    
        actualizarFacturasMostradas(panel, trabajadorAccedido, mensajeVacio);
    
        // Timer que actualiza cada segundo
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarFacturasMostradas(panel, trabajadorAccedido, mensajeVacio);
            }
        });
    
        timer.start();
    
        // Cierra el timer cuando se cierre el diálogo
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                timer.stop();
            }
        });
    }
    
    // Método que actualiza la lista de facturas
    private void actualizarFacturasMostradas(JPanel panel, Trabajador trabajadorAccedido, JLabel mensajeVacio) {
        panel.removeAll(); // Limpia el panel antes de actualizar
    
        if (trabajadorAccedido.registroInfoFacturas.isEmpty()) {
            panel.add(mensajeVacio);
        } else {
            for (InfoFactura factura : trabajadorAccedido.registroInfoFacturas) {
                JLabel facturaLabel = new JLabel("<html>" + factura.toString().replace("\n", "<br>") + "</html>");
                facturaLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                panel.add(facturaLabel);
                panel.add(Box.createVerticalStrut(10)); // Espaciado entre facturas
            }
        }
    
        panel.revalidate();
        panel.repaint();
    }
    
    public void visualizarEstadoPromociones() {
        Menu menu = new Menu();
        menu.nombre = "Estado Promociones";
        frame.setTitle(menu.nombre);
        JFrame frame = menu.crearNuevaVentana();
    
        // Crear el panel para el submenú
        JPanel panel = menu.crearPanel();
    
        // Método que actualiza el contenido del panel
        Runnable actualizarEstado = () -> {
            // Limpiar el panel antes de agregar la nueva información
            panel.removeAll();
    
            // Se itera sobre todos los usuarios
            List<Entidad> entidades = ciudad.obtenerEntidadesPorClase(Usuario.class);
            entidades = ciudad.ordenarEntidadesPorId(entidades);
    
            // Si no hay usuarios, mostrar un mensaje
            if (entidades.isEmpty()) {
                JLabel labelNoHayUsuarios = new JLabel("De momento no hay ningún usuario.");
                UtilidadesMenu.aplicarFuenteTexto(labelNoHayUsuarios);
                panel.add(labelNoHayUsuarios);
            } else {
                // Dentro del bucle que recorre los usuarios
                for (Entidad entidad : entidades) {
                    Usuario usuario = (Usuario) entidad;
                
                    // Crear un JPanel para el usuario con su nombre e ID
                    JPanel panelUsuario = new JPanel();
                    panelUsuario.setLayout(new BoxLayout(panelUsuario, BoxLayout.Y_AXIS));
                
                    JLabel labelUser = new JLabel("Usuario: " + usuario.getNombre() + " (ID: " + usuario.getId() + ")");
                    UtilidadesMenu.aplicarFuenteTexto(labelUser);
                    panelUsuario.add(labelUser);
                
                    // Si el usuario ya es premium, mostrar mensaje y saltar a la siguiente iteración
                    if (usuario.isPremium) {
                        JLabel labelPremium = new JLabel("  Este usuario ya es premium.");
                        UtilidadesMenu.aplicarFuenteTextoConBold(labelPremium);
                        panelUsuario.add(labelPremium);
                    } else {
                        // Variables para las condiciones de promoción
                        int vehiculosUltimoMes = 0;
                        int vehiculosTresMesesSeguidos = 0;
                        HashSet<Class<? extends Vehiculo>> tiposVehiculosUsados = new HashSet<>();
                
                        // Se itera sobre su registro de alquileres
                        for (InfoAlquiler infoAlquiler : usuario.registroInfoAlquileres) {
                            if (simulacion.tiempo.esUltimoMes(infoAlquiler.getMesAlquiler())) {
                                vehiculosUltimoMes++;
                            }
                            if (simulacion.tiempo.esMesReciente(infoAlquiler.getMesAlquiler(), 3)) {
                                vehiculosTresMesesSeguidos++;
                            }
                            if (simulacion.tiempo.esUltimosSeisMeses(infoAlquiler.getMesAlquiler())) {
                                tiposVehiculosUsados.add(infoAlquiler.getClaseVehiculoAlquilado());
                            }
                        }
                
                        boolean puedePromocionar = false;
                
                        JLabel lblVehiculosUltimoMes = new JLabel();
                        JLabel lblVehiculosTresMesesSeguidos = new JLabel();
                        JLabel lblTiposVehiculosUsados = new JLabel();
                
                        UtilidadesMenu.aplicarFuenteTexto(lblVehiculosUltimoMes);
                        UtilidadesMenu.aplicarFuenteTexto(lblVehiculosTresMesesSeguidos);
                        UtilidadesMenu.aplicarFuenteTexto(lblTiposVehiculosUsados);
                
                        if (vehiculosUltimoMes >= 15) {
                            lblVehiculosUltimoMes.setText("    Cumple con la promoción: " + vehiculosUltimoMes + "/15 vehículos alquilados en el último mes");
                            puedePromocionar = true;
                        } else {
                            lblVehiculosUltimoMes.setText("    Vehículos alquilados en el último mes: " + vehiculosUltimoMes + "/15");
                        }
                
                        if (vehiculosTresMesesSeguidos >= 30) {
                            lblVehiculosTresMesesSeguidos.setText("    Cumple con la promoción: " + vehiculosTresMesesSeguidos + "/30 vehículos en 3 meses (10 por mes)");
                            puedePromocionar = true;
                        } else {
                            int vehiculosPromedioPorMes = vehiculosTresMesesSeguidos / 3;
                            lblVehiculosTresMesesSeguidos.setText("    Vehículos por mes en los últimos 3 meses: " + vehiculosPromedioPorMes + "/10 (Promedio por mes)");
                        }
                
                        int totalTiposVehiculo = Vehiculo.obtenerTotalTiposVehiculo();
                        if (tiposVehiculosUsados.size() == totalTiposVehiculo) {
                            lblTiposVehiculosUsados.setText("    Cumple con la promoción: " + tiposVehiculosUsados.size() + "/" + totalTiposVehiculo + " tipos de vehículos usados en los últimos 6 meses");
                            puedePromocionar = true;
                        } else {
                            lblTiposVehiculosUsados.setText("    Tipos de vehículos usados en los últimos 6 meses: " + tiposVehiculosUsados.size() + "/" + totalTiposVehiculo);
                        }
                
                        panelUsuario.add(lblVehiculosUltimoMes);
                        panelUsuario.add(lblVehiculosTresMesesSeguidos);
                        panelUsuario.add(lblTiposVehiculosUsados);
                
                        if (puedePromocionar) {
                            JButton botonPromocionar = new JButton("Promocionar Usuario");
                            botonPromocionar.addActionListener(e -> {
                                usuario.promocionarUsuario();
                                UtilidadesMenu.showMensajeExito(null, "Usuario promocionado con éxito");
                            });
                            panelUsuario.add(botonPromocionar);
                        } else {
                            JLabel labelUsuarioNoPromocionable = new JLabel("  Este usuario no es promocionable.");
                            UtilidadesMenu.aplicarFuenteTextoConBold(labelUsuarioNoPromocionable);
                            panelUsuario.add(labelUsuarioNoPromocionable);
                        }
                    }
                
                    panel.add(panelUsuario);
                    panel.add(Box.createVerticalStrut(10));  // Añadir espacio de 10 píxeles
                }
            }
    
            // Actualizar la interfaz
            panel.revalidate();
            panel.repaint();
        };
    
        // Ejecutar la actualización inicial
        actualizarEstado.run();
    
        // Añadir el panel al JFrame
        frame.add(agregarScroll(panel));
        frame.setVisible(true);
    
        // Actualización cada segundo
        Timer timer = new Timer(1000, e -> actualizarEstado.run());
        timer.start();
    }

    public void iniciarGestorEntidades() {
        Menu menu = new Menu();
        menu.nombre = "Gestor Entidades";
        JFrame frame = menu.crearNuevaVentana();
        
        // Crear el panel para el submenú
        JPanel panel = menu.crearPanel();
        panel.setBackground(Color.BLUE);
        
        // Crear y añadir los botones con sus funcionalidades
        menu.botones.put("AgregarUsuarioNormal", new Boton("Agregar Usuario Normal", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                simulacion.agregarUsuarioNormal(); // Asegúrate de que este método está bien definido en la clase 'simulacion'
            }
        }));
        menu.botones.put("AgregarUsuarioPremium", new Boton("Agregar Usuario Premium", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                simulacion.agregarUsuarioPremium(); // Asegúrate de que este método está bien definido en la clase 'simulacion'
            }
        }));
        menu.botones.put("AgregarTecnicoMantenimiento", new Boton("Agregar Técnico Mantenimiento", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                simulacion.agregarTecnicoMantenimiento(); // Asegúrate de que este método está bien definido en la clase 'simulacion'
            }
        }));
        menu.botones.put("AgregarMecanico", new Boton("Agregar Mecánico", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                simulacion.agregarMecanico(); // Asegúrate de que este método está bien definido en la clase 'simulacion'
            }
        }));
        
        // Crear y añadir los botones con sus funcionalidades
        menu.botones.put("AgregarMoto", new Boton("Agregar Moto", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                simulacion.agregarMoto(); // Asegúrate de que este método está bien definido en la clase 'simulacion'
            }
        }));
        menu.botones.put("AgregarBase", new Boton("Agregar Base", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Crear una nueva ventana de diálogo para pedir las cantidades de bicicletas y patinetes
                Menu menu = new Menu();
                menu.nombre = "Agregar Base con Bicicletas y Patinetes";
                JDialog dialogo = menu.crearNuevoDialogo();
                
                // Crear el panel para el submenú
                JPanel panel = menu.crearPanel();
                
                // Crear los campos de texto para ingresar el número de bicicletas y patinetes
                JTextField bicicletasField = new JTextField();
                JTextField patinetesField = new JTextField();
                
                // Etiquetas para los campos
                panel.add(new JLabel("Número de Bicicletas:"));
                panel.add(bicicletasField);
                panel.add(new JLabel("Número de Patinetes:"));
                panel.add(patinetesField);
                
                // Botón para confirmar y cerrar el diálogo
                JButton aceptarButton = new JButton("Aceptar");
                aceptarButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            // Obtener los valores de las bicicletas y patinetes
                            int numBicicletas = Integer.parseInt(bicicletasField.getText());
                            int numPatinetes = Integer.parseInt(patinetesField.getText());
                            
                            // Llamar a simulacion.agregarBase con los valores
                            ciudad.agregarBase(simulacion, numBicicletas, numPatinetes);
                            
                            // Cerrar el diálogo
                            dialogo.dispose();
                        } catch (NumberFormatException ex) {
                            // Manejar el error si los campos no tienen valores numéricos válidos
                            UtilidadesMenu.showMensajeError(dialogo, "Por favor ingrese números válidos.");
                        }
                    }
                });
                // Botón de cancelar para cerrar el diálogo sin hacer nada
                JButton cancelarButton = new JButton("Cancelar");
                cancelarButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dialogo.dispose();  // Cerrar el diálogo sin hacer nada
                    }
                });
                
                // Añadir los botones al diálogo
                panel.add(aceptarButton);
                panel.add(cancelarButton);
                
                dialogo.add(agregarScroll(panel));
                
                dialogo.setVisible(true);
            }
        }));
        
        menu.botones.put("AgregarGrupoEntidades", new Boton("Agregar Grupo Entidades", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                simulacion.agregarGrupoEntidades(); // Asegúrate de que este método está bien definido en la clase 'simulacion'
            }
        }));

        // Agregar los botones al panel
        agregarBotones(menu.botones, panel);
        
        // Añadir el panel al JFrame
        frame.add(panel);
        
        frame.setVisible(true);
    }
    
    private void agregarOpcionesUsuarioNormal() {
        String nombreBoton;
        
        // CONSULTAR VEHÍCULOS DISPONIBLES
        nombreBoton = "Consultar Vehículos Disponibles";
        botones.put(nombreBoton, new Boton(nombreBoton, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                utilidadesMenu.mostrarInfo(ciudad, UtilidadesMenu.TipoInfoMostrada.VEHICULOS);
            }
        }));

        // ALQUILAR VEHÍCULOS
        nombreBoton = "Alquilar Vehículo";
        botones.put(nombreBoton, new Boton(nombreBoton, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Class<?> claseVehiculo = utilidadesMenu.seleccionarClase("vehiculo");
                
                if (claseVehiculo != null) {
                
                    Entidad entidadPorAlquilar = ciudad.encontrarEntidadUsableMasCercana(personaAccedida, claseVehiculo);
                    
                    // Comprobar si se ha encontrado una entidad
                    if (entidadPorAlquilar == null) {
                        UtilidadesMenu.showMensajeError(frame, "No se ha podido encontrar ninguna moto disponible.");
                        return;
                    }
                    
                    personaAccedida.planearTrayecto(ciudad, entidadPorAlquilar.getUbicacion(), entidadPorAlquilar);
                }
            }
        }));
        
        nombreBoton = "Alertar Fallo Mecánico";
        botones.put(nombreBoton, new Boton(nombreBoton, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Class<?> claseEntidad = utilidadesMenu.seleccionarClase("entidad");
                
                if (claseEntidad != null) {
                    int indice = utilidadesMenu.seleccionarIndice();
                    
                    Entidad entidad = ciudad.encontrarEntidad(claseEntidad, indice);
                    
                    if (entidad == null) {
                        UtilidadesMenu.showMensajeError(frame, "No se ha encontrado la entidad indicada.");
                        return;
                    }
                    
                    ((Usuario)personaAccedida).alertarFalloMecanico(entidad);
                }
                
            }
        }));
        
        nombreBoton = "Alquileres realizados";
        botones.put(nombreBoton, new Boton(nombreBoton, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarAlquileresUsuario();
            }
        }));
        
        nombreBoton = "Consultar / Recargar Saldo";
        botones.put(nombreBoton, new Boton(nombreBoton, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Menu menu = new Menu();
                menu.nombre = "Gestor Entidades";
                JFrame frame = menu.crearNuevaVentana();
                
                // Crear el panel para el submenú
                JPanel panel = menu.crearPanel();
                
                // Crear un JLabel para mostrar el saldo
                JLabel saldoLabel = new JLabel("Saldo disponible: " + ((Usuario)personaAccedida).getSaldo() + " €");
                panel.add(saldoLabel);
                
                String nombreBoton = "Recargar saldo";
                menu.botones.put(nombreBoton, new Boton(nombreBoton, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Menu menu = new Menu();
                        menu.nombre = "Recargar saldo";
                        
                        // Crear un nuevo diálogo para la recarga de saldo
                        JDialog dialogo = menu.crearNuevoDialogo();
                        dialogo.setTitle("Recargar Saldo");
                        
                        // Crear un panel para añadir los elementos
                        JPanel panel = new JPanel();
                        dialogo.add(panel);
                        
                        // Crear el input para la cantidad a recargar
                        JLabel label = new JLabel("Introduce la cantidad a recargar:");
                        JTextField inputCantidad = new JTextField(10);  // Campo de texto para introducir la cantidad
                        
                        panel.add(label);
                        panel.add(inputCantidad);
                        
                        // Botón de aceptar
                        JButton aceptarButton = new JButton("Aceptar");
                        aceptarButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    // Obtener la cantidad introducida
                                    double cantidad = Double.parseDouble(inputCantidad.getText());
                                    
                                    // Verificar que la cantidad sea mayor que 0
                                    if (cantidad <= 0) {
                                        UtilidadesMenu.showMensajeError(dialogo, "Introduce una cantidad mayor a 0");
                                    } else {
                                        // Lógica para recargar el saldo
                                        ((Usuario)personaAccedida).recargarSaldo(cantidad);
                                        
                                        // Actualizar el JLabel del saldo
                                        saldoLabel.setText("Saldo disponible: " + ((Usuario)personaAccedida).getSaldo() + " €");
                                        
                                        dialogo.dispose();  // Cerrar el diálogo después de la recarga
                                    }
                                } catch (NumberFormatException ex) {
                                    // Manejar el error si no se introduce un número válido
                                    UtilidadesMenu.showMensajeError(dialogo, "Introduce una cantidad válida");
                                }
                            }
                        });
                        
                        // Botón de cancelar
                        JButton cancelarButton = new JButton("Cancelar");
                        cancelarButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                dialogo.dispose();  // Cerrar el diálogo si se cancela la operación
                            }
                        });
                        
                        // Añadir los botones al panel
                        panel.add(aceptarButton);
                        panel.add(cancelarButton);
                        
                        // Ajustar el tamaño del diálogo y hacerlo visible
                        dialogo.pack();
                        dialogo.setVisible(true);
                    }
                }));
        
                // Agregar los botones al panel
                agregarBotones(menu.botones, panel);
                
                // Añadir el panel al JFrame
                frame.add(agregarScroll(panel));
                
                frame.setVisible(true);
            }
        }));
        
        nombreBoton = "Consultar Moto Cercana";
        botones.put(nombreBoton, new Boton(nombreBoton, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Menu menu = new Menu();
                menu.nombre = "Consultar Moto Cercana";
                JFrame frame = menu.crearNuevaVentana();
        
                // Crear el panel para el submenú
                JPanel panel = menu.crearPanel();
        
                // Buscar la moto más cercana disponible
                Moto moto = (Moto) ciudad.encontrarEntidadUsableMasCercana(personaAccedida, Moto.class);
                if (moto == null) {
                    // Mostrar mensaje de error si no se encuentra ninguna moto disponible
                    JLabel errorLabel = new JLabel("No se ha encontrado ninguna moto disponible.");
                    panel.add(errorLabel);
                } else {
                    // Mostrar la información de la moto
                    JLabel motoLabel = new JLabel("Moto cercana disponible: " + moto.toString());
                    panel.add(motoLabel);
        
                    // Botón para alquilar la moto
                    JButton alquilarButton = new JButton("Alquilar moto");
                    alquilarButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            personaAccedida.planearTrayecto(ciudad, moto.getUbicacion(), moto);
                            frame.dispose();
                        }
                    });
        
                    panel.add(alquilarButton);
                }
        
                // Agregar los botones al panel
                agregarBotones(menu.botones, panel);
        
                // Añadir el panel al JFrame
                frame.add(agregarScroll(panel));
                frame.setVisible(true);
            }
        }));
    }
    
    public void mostrarAlquileresUsuario() {
        Menu menu = new Menu();
        menu.nombre = "Alquileres Usuario " + personaAccedida.toSimpleString();
    
        JFrame frame = menu.crearNuevaVentana();
        JPanel panel = menu.crearPanel();

        frame.add(agregarScroll(panel));
        frame.setVisible(true);
    
        Usuario usuarioAccedido = (Usuario) personaAccedida;
    
        // Etiqueta de "No hay facturas"
        JLabel mensajeVacio = new JLabel("Todavía no hay ningún alquiler almacenado.");
        mensajeVacio.setHorizontalAlignment(SwingConstants.CENTER);
        mensajeVacio.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    
        actualizarAlquileresMostrados(panel, usuarioAccedido, mensajeVacio);
    
        // Timer que actualiza cada segundo
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarAlquileresMostrados(panel, usuarioAccedido, mensajeVacio);
            }
        });
    
        timer.start();
    
        // Cierra el timer cuando se cierre el diálogo
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                timer.stop();
            }
        });
    }
    
    // Método que actualiza la lista de facturas
    private void actualizarAlquileresMostrados(JPanel panel, Usuario usuarioAccedido, JLabel mensajeVacio) {
        panel.removeAll(); // Limpia el panel antes de actualizar
    
        if (usuarioAccedido.registroInfoAlquileres.isEmpty()) {
            panel.add(mensajeVacio);
        } else {
            for (InfoAlquiler alquiler : usuarioAccedido.registroInfoAlquileres) {
                JLabel facturaLabel = new JLabel("<html>" + alquiler.toString().replace("\n", "<br>") + "</html>");
                facturaLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                panel.add(facturaLabel);
                panel.add(Box.createVerticalStrut(10)); // Espaciado entre facturas
            }
        }
    
        panel.revalidate();
        panel.repaint();
    }

    private void agregarOpcionesComunesTrabajadores() {
        String nombreBoton;
    
        nombreBoton = "Consultar Entidad Asignada";
        botones.put(nombreBoton, new Boton(nombreBoton, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Menu menu = new Menu();
                JLabel entidadAsignadaLabel;
                menu.nombre = "Consultar Entidad Asignada";
                JFrame frame = menu.crearNuevaVentana();
    
                // Crear el panel para el submenú
                JPanel panel = menu.crearPanel();
    
                // Crear un JLabel para mostrar la entidad asignada
                Entidad entidadAsignada = ((Trabajador) personaAccedida).getEntidadAsignada();
                
                if (entidadAsignada == null) {
                    entidadAsignadaLabel = new JLabel("El trabajador no tiene ninguna entidad asignada");
                } else {
                    entidadAsignadaLabel = new JLabel("Entidad asignada: " + entidadAsignada.toString());
                }
                panel.add(entidadAsignadaLabel);
    
                // Añadir el panel al JFrame
                frame.add(agregarScroll(panel));
                frame.setVisible(true);
    
                // Configurar el Timer para actualizar el texto del JLabel cada 1 segundo (1000 milisegundos)
                Timer timer = new Timer(1000, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        // Obtener la entidad asignada actualizada
                        Entidad entidadActualizada = ((Trabajador) personaAccedida).getEntidadAsignada();
                        
                        if (entidadActualizada == null) {
                            entidadAsignadaLabel.setText("El trabajador no tiene ninguna entidad asignada");
                        } else {
                            entidadAsignadaLabel.setText("Entidad asignada: " + entidadActualizada.toString());
                        }
                    }
                });
    
                // Iniciar el Timer
                timer.start();
            }
        }));
        
        nombreBoton = "Asignar Entidad";
        botones.put(nombreBoton, new Boton(nombreBoton, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Trabajador trabajador = (Trabajador) personaAccedida;

                // Comprobar si el trabajador ya tiene una entidad asignada
                if (trabajador.getEntidadAsignada() != null) {
                    trabajador.terminarTrabajo(false);
                }

                // Seleccionamos la clase de vehículo
                Class<?> claseEntidad = utilidadesMenu.seleccionarClase("entidad");

                if (claseEntidad == null) {
                    UtilidadesMenu.showMensajeError(null, "No se ha seleccionado ninguna clase de entidad.");
                    return;
                }

                // Intentar encontrar una entidad con fallo mecánico de la clase seleccionada
                Entidad entidadPorAsignar = ciudad.encontrarEntidadConFalloMecanico(claseEntidad);

                if (entidadPorAsignar == null) {
                    // Mostrar mensaje si no se encontró ninguna entidad con fallo mecánico
                    UtilidadesMenu.showMensajeError(null, "No se encontró ninguna entidad con fallo mecánico del tipo seleccionado.");
                    return;
                } else {
                    // Intentar asignar el vehículo al trabajador
                    trabajador.setEntidadAsignada(ciudad, entidadPorAsignar);
                    UtilidadesMenu.showMensajeExito(null, "Entidad asignada correctamente.");
                }
            }
        }));
        
        nombreBoton = "Generar Facturas";
        botones.put(nombreBoton, new Boton(nombreBoton, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarFacturasTrabajador();
            }
        }));
        
        nombreBoton = "Trasladar Vehículo";
        botones.put(nombreBoton, new Boton(nombreBoton, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                iniciarMenuTrasladoVehiculo();
            }
        }));
    }
}
