package com.fortunato.sistema.presentacion;

import com.fortunato.sistema.entidad.Proveedor;
import com.fortunato.sistema.interfaz.IControladorProveedor;
import com.fortunato.sistema.interfaz.Factory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel para gestionar proveedores
 */
public class ProveedorPanel extends JPanel {
    
    private IControladorProveedor controladorProveedor;
    private JTable tablaProveedores;
    private DefaultTableModel modelo;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField campoNombre, campoTelefono, campoMail;
    private JTextField campoFiltroId, campoFiltroNombre;
    private JButton botonIngresar, botonActualizar, botonBorrar;
    private Proveedor proveedorSeleccionado;
    private JPanel panelFormulario;
    private JPanel panelTabla;
    private JPanel panelBotones;
    
    public ProveedorPanel() {
        this.controladorProveedor = Factory.getInstancia().getControladorProveedor();
        inicializarComponentes();
        configurarLayout();
        // Cargar datos de prueba automáticamente
        cargarDatosDePrueba();
    }
    
    private void inicializarComponentes() {
        // Campos de texto
        campoNombre = crearCampoTexto("Nombre del proveedor");
        campoTelefono = crearCampoTexto("Teléfono");
        campoMail = crearCampoTexto("Email");
        
        // Campos de filtro
        campoFiltroId = crearCampoFiltro("Filtrar por ID");
        campoFiltroNombre = crearCampoFiltro("Filtrar por nombre");
        
        // Botones
        botonIngresar = crearBoton("Ingresar Proveedor", new Color(37, 99, 235), e -> mostrarFormularioIngreso());
        botonActualizar = crearBoton("Actualizar Proveedor", new Color(100, 116, 139), e -> mostrarFormularioActualizacion());
        botonBorrar = crearBoton("Borrar Proveedor", new Color(220, 38, 38), e -> mostrarFormularioBorrado());
        
        // Tabla
        crearTabla();
        
        // Paneles
        crearPaneles();
    }
    
    private void crearPaneles() {
        // Panel del formulario
        panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            "Información del Proveedor",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(30, 41, 59)
        ));
        
        // Agregar campos al formulario
        panelFormulario.add(crearCampoConLabel("Nombre:", campoNombre));
        panelFormulario.add(Box.createVerticalStrut(15));
        panelFormulario.add(crearCampoConLabel("Teléfono:", campoTelefono));
        panelFormulario.add(Box.createVerticalStrut(15));
        panelFormulario.add(crearCampoConLabel("Email:", campoMail));
        
        // Panel de botones
        panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.add(botonIngresar);
        panelBotones.add(botonActualizar);
        panelBotones.add(botonBorrar);
        
        // Panel de la tabla
        panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.WHITE);
        panelTabla.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            "Lista de Proveedores",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(30, 41, 59)
        ));
        
        JScrollPane scrollPane = new JScrollPane(tablaProveedores);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        panelTabla.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void configurarLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249)); // Fondo gris muy claro moderno
        
        // Panel superior con título y filtros
        JPanel panelTitulo = crearPanelTitulo();
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel principal con layout horizontal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 0)); // Espaciado horizontal entre componentes
        panelPrincipal.setBackground(new Color(241, 245, 249));
        
        // Panel de botones a la izquierda
        panelBotones = crearPanelBotones();
        panelPrincipal.add(panelBotones, BorderLayout.WEST);
        
        // Separador vertical entre columnas
        JSeparator separador = new JSeparator(JSeparator.VERTICAL);
        separador.setBackground(new Color(226, 232, 240));
        separador.setForeground(new Color(226, 232, 240));
        panelPrincipal.add(separador, BorderLayout.CENTER);
        
        // Panel de tabla a la derecha - ocupa el resto del espacio
        panelTabla = crearPanelTabla();
        panelPrincipal.add(panelTabla, BorderLayout.CENTER);
        
        // Panel de formulario (se muestra/oculta según necesidad)
        panelFormulario = crearPanelFormulario();
        
        add(panelPrincipal, BorderLayout.CENTER);
        add(panelFormulario, BorderLayout.SOUTH);
        
        // Mostrar la tabla por defecto
        mostrarTabla();
    }
    
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        
        // Panel izquierdo con título alineado con la columna de botones
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelTitulo.setBackground(new Color(241, 245, 249));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        
        JLabel titulo = new JLabel("Gestión de Proveedores");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(30, 41, 59));
        panelTitulo.add(titulo);
        
        // Panel derecho con filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelFiltros.setBackground(new Color(241, 245, 249));
        
        // Espaciador para mover los filtros más a la derecha
        panelFiltros.add(Box.createHorizontalStrut(200));
        
        JLabel etiquetaFiltroId = crearEtiqueta("Buscar por ID:");
        JLabel etiquetaFiltroNombre = crearEtiqueta("Buscar por Nombre:");
        
        // Configurar tamaños de los campos de filtro con el mismo estándar
        campoFiltroId.setPreferredSize(new Dimension(100, 34));
        campoFiltroId.setMinimumSize(new Dimension(100, 34));
        campoFiltroId.setMaximumSize(new Dimension(100, 34));
        campoFiltroNombre.setPreferredSize(new Dimension(120, 34));
        campoFiltroNombre.setMinimumSize(new Dimension(120, 34));
        campoFiltroNombre.setMaximumSize(new Dimension(120, 34));
        
        panelFiltros.add(etiquetaFiltroId);
        panelFiltros.add(campoFiltroId);
        panelFiltros.add(Box.createHorizontalStrut(15));
        panelFiltros.add(etiquetaFiltroNombre);
        panelFiltros.add(campoFiltroNombre);
        
        panel.add(panelTitulo, BorderLayout.WEST);
        panel.add(panelFiltros, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setMinimumSize(new Dimension(200, 0));
        
        // Botones en columna vertical alineados arriba
        panel.add(botonIngresar);
        panel.add(Box.createVerticalStrut(15));
        panel.add(botonActualizar);
        panel.add(Box.createVerticalStrut(15));
        panel.add(botonBorrar);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(tablaProveedores);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel interno con el formulario
        JPanel panelFormularioInterno = new JPanel(new GridBagLayout());
        panelFormularioInterno.setBackground(Color.WHITE);
        panelFormularioInterno.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Campo Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormularioInterno.add(crearEtiqueta("Nombre:"), gbc);
        gbc.gridx = 1;
        panelFormularioInterno.add(campoNombre, gbc);
        
        // Campo Teléfono
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormularioInterno.add(crearEtiqueta("Teléfono:"), gbc);
        gbc.gridx = 1;
        panelFormularioInterno.add(campoTelefono, gbc);
        
        // Campo Email
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormularioInterno.add(crearEtiqueta("Email:"), gbc);
        gbc.gridx = 1;
        panelFormularioInterno.add(campoMail, gbc);
        
        panel.add(panelFormularioInterno, BorderLayout.CENTER);
        panel.setVisible(false); // Oculto por defecto
        
        return panel;
    }
    
    private void crearTabla() {
        String[] columnas = {"ID", "Nombre", "Teléfono", "Email", "Activo"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };
        
        tablaProveedores = new JTable(modelo);
        tablaProveedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProveedores.setRowHeight(30);
        tablaProveedores.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaProveedores.setGridColor(new Color(226, 232, 240));
        tablaProveedores.setShowGrid(true);
        tablaProveedores.setIntercellSpacing(new Dimension(0, 0));
        
        // Configurar ancho de columnas
        tablaProveedores.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaProveedores.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        tablaProveedores.getColumnModel().getColumn(1).setPreferredWidth(150); // Nombre
        tablaProveedores.getColumnModel().getColumn(2).setPreferredWidth(120); // Teléfono
        tablaProveedores.getColumnModel().getColumn(3).setPreferredWidth(200); // Email
        tablaProveedores.getColumnModel().getColumn(4).setPreferredWidth(80);   // Activo
        
        // Configurar sorter para filtros
        sorter = new TableRowSorter<>(modelo);
        tablaProveedores.setRowSorter(sorter);
        
        // Listener para selección
        tablaProveedores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tablaProveedores.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    cargarProveedorSeleccionado(filaSeleccionada);
                }
            }
        });
        
        // Configurar filtros
        configurarFiltros();
    }
    
    private void configurarFiltros() {
        // Filtro por ID
        campoFiltroId.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
        });
        
        // Filtro por nombre
        campoFiltroNombre.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
        });
    }
    
    private void aplicarFiltros() {
        String filtroId = campoFiltroId.getText().toLowerCase();
        String filtroNombre = campoFiltroNombre.getText().toLowerCase();
        
        RowFilter<DefaultTableModel, Object> filtro = RowFilter.andFilter(java.util.List.of(
            RowFilter.regexFilter("(?i)" + filtroId, 0), // Columna ID
            RowFilter.regexFilter("(?i)" + filtroNombre, 1) // Columna nombre
        ));
        
        sorter.setRowFilter(filtro);
    }
    
    private void cargarProveedorSeleccionado(int filaSeleccionada) {
        int filaModelo = tablaProveedores.convertRowIndexToModel(filaSeleccionada);
        if (filaModelo >= 0 && filaModelo < modelo.getRowCount()) {
            Long id = (Long) modelo.getValueAt(filaModelo, 0);
            String nombre = (String) modelo.getValueAt(filaModelo, 1);
            Long telefono = (Long) modelo.getValueAt(filaModelo, 2);
            String mail = (String) modelo.getValueAt(filaModelo, 3);
            String activoStr = (String) modelo.getValueAt(filaModelo, 4);
            
            // Crear proveedor temporal para mostrar en el formulario
            proveedorSeleccionado = new Proveedor(nombre, telefono);
            proveedorSeleccionado.setId(id);
            proveedorSeleccionado.setMail(mail);
            proveedorSeleccionado.setActivo("Sí".equals(activoStr));
            
            // Cargar datos en el formulario
            campoNombre.setText(nombre);
            campoTelefono.setText(telefono != null ? telefono.toString() : "");
            campoMail.setText(mail);
        }
    }
    
    private JPanel crearCampoConLabel(String textoLabel, JTextField campo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel label = new JLabel(textoLabel);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(30, 41, 59));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(campo, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTextField crearCampoTexto(String placeholder) {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        campo.setBackground(Color.WHITE);
        campo.setToolTipText(placeholder);
        campo.setPreferredSize(new Dimension(200, 34));
        campo.setMinimumSize(new Dimension(200, 34));
        campo.setMaximumSize(new Dimension(200, 34));
        return campo;
    }
    
    private JTextField crearCampoFiltro(String placeholder) {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        campo.setBackground(Color.WHITE);
        campo.setToolTipText(placeholder);
        campo.setPreferredSize(new Dimension(150, 30));
        return campo;
    }
    
    private JButton crearBoton(String texto, Color color, ActionListener listener) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        
        // Tamaño uniforme y texto centrado
        boton.setPreferredSize(new Dimension(180, 40));
        boton.setMinimumSize(new Dimension(180, 40));
        boton.setMaximumSize(new Dimension(180, 40));
        
        // Bordes redondeados modernos
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        
        // Aplicar bordes redondeados
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addActionListener(listener);
        
        // Efecto hover moderno
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Azul más claro para hover
                if (color.equals(new Color(37, 99, 235))) {
                    boton.setBackground(new Color(59, 130, 246)); // Azul más claro
                } else {
                    boton.setBackground(color.darker());
                }
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });
        
        return boton;
    }
    
    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(30, 41, 59));
        return label;
    }
    
    private void cargarDatosDePrueba() {
        try {
            // Intentar cargar proveedores desde la base de datos
            List<Proveedor> proveedores = controladorProveedor.listarProveedores();
            if (proveedores != null && !proveedores.isEmpty()) {
                // Cargar directamente en la tabla sin llamar a cargarProveedoresEnTabla()
                modelo.setRowCount(0); // Limpiar tabla
                for (Proveedor proveedor : proveedores) {
                    Object[] fila = {
                        proveedor.getId(),
                        proveedor.getNombre(),
                        proveedor.getTelefono(),
                        proveedor.getMail(),
                        proveedor.getActivo() ? "Sí" : "No"
                    };
                    modelo.addRow(fila);
                }
                System.out.println("Proveedores cargados desde BD: " + proveedores.size());
            } else {
                // Si no hay datos en BD, cargar datos de prueba
                cargarProveedoresDePrueba();
            }
        } catch (Exception e) {
            System.err.println("Error al cargar proveedores: " + e.getMessage());
            e.printStackTrace();
            // Cargar datos de prueba en caso de error
            cargarProveedoresDePrueba();
        }
    }
    
    private void cargarProveedoresDePrueba() {
        // Crear proveedores de ejemplo
        List<Proveedor> proveedores = new java.util.ArrayList<>();
        
        Proveedor cocaCola = new Proveedor("Coca-Cola", 1234567890L);
        cocaCola.setMail("contacto@coca-cola.com");
        proveedores.add(cocaCola);
        
        Proveedor pepsi = new Proveedor("Pepsi", 987654321L);
        pepsi.setMail("contacto@pepsi.com");
        proveedores.add(pepsi);
        
        Proveedor nestle = new Proveedor("Nestlé", 1122334455L);
        nestle.setMail("contacto@nestle.com");
        proveedores.add(nestle);
        
        Proveedor unilever = new Proveedor("Unilever", 5566778899L);
        unilever.setMail("contacto@unilever.com");
        proveedores.add(unilever);
        
        Proveedor mars = new Proveedor("Mars", 9988776655L);
        mars.setMail("contacto@mars.com");
        proveedores.add(mars);
        
        // Guardar proveedores en la base de datos
        for (Proveedor proveedor : proveedores) {
            try {
                System.out.println("Creando proveedor: " + proveedor.getNombre());
                controladorProveedor.crearProveedor(proveedor);
                System.out.println("Proveedor creado exitosamente: " + proveedor.getNombre());
            } catch (Exception e) {
                System.err.println("Error al crear proveedor " + proveedor.getNombre() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Cargar la tabla con los proveedores creados
        try {
            System.out.println("Cargando proveedores en tabla...");
            cargarProveedoresEnTabla();
            System.out.println("Proveedores cargados en tabla exitosamente");
        } catch (Exception e) {
            System.err.println("Error al cargar proveedores en tabla: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Proveedores de prueba cargados: " + proveedores.size());
    }
    
    private void cargarProveedoresEnTabla() {
        try {
            List<Proveedor> proveedores = controladorProveedor.listarProveedores();
            modelo.setRowCount(0); // Limpiar tabla
            
            for (Proveedor proveedor : proveedores) {
                Object[] fila = {
                    proveedor.getId(),
                    proveedor.getNombre(),
                    proveedor.getTelefono(),
                    proveedor.getMail(),
                    proveedor.getActivo() ? "Sí" : "No"
                };
                modelo.addRow(fila);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar proveedores en tabla: " + e.getMessage());
        }
    }
    
    private void mostrarTabla() {
        panelFormulario.setVisible(false);
        panelTabla.setVisible(true);
        revalidate();
        repaint();
    }
    
    private void mostrarFormulario() {
        panelTabla.setVisible(false);
        panelFormulario.setVisible(true);
        revalidate();
        repaint();
    }
    
    private void mostrarFormularioIngreso() {
        DialogoNuevoProveedor ventana = new DialogoNuevoProveedor(this);
        ventana.setVisible(true);
    }
    
    private void mostrarFormularioActualizacion() {
        if (proveedorSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un proveedor para actualizar.", 
                "Proveedor no seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        DialogoActualizarProveedor ventana = new DialogoActualizarProveedor(this, proveedorSeleccionado);
        ventana.setVisible(true);
    }
    
    private void mostrarFormularioBorrado() {
        if (proveedorSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un proveedor para borrar.", 
                "Proveedor no seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        DialogoConfirmarBorradoProveedor ventana = new DialogoConfirmarBorradoProveedor(this, proveedorSeleccionado);
        ventana.setVisible(true);
    }
    
    public void actualizarTabla() {
        modelo.setRowCount(0); // Limpiar tabla
        cargarDatosDePrueba(); // Recargar datos
    }
}