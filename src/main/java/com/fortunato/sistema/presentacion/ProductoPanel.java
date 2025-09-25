package com.fortunato.sistema.presentacion;

import com.fortunato.sistema.entidad.Producto;
import com.fortunato.sistema.entidad.Proveedor;
import com.fortunato.sistema.interfaz.IControladorProducto;
import com.fortunato.sistema.interfaz.IControladorProveedor;
import com.fortunato.sistema.interfaz.Factory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

/**
 * Panel para gestionar productos
 */
public class ProductoPanel extends JPanel {
    
    private IControladorProducto controladorProducto;
    private IControladorProveedor controladorProveedor;
    private JTable tablaProductos;
    private DefaultTableModel modelo;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField campoId, campoNombre, campoDescripcion, campoPrecioCompra, campoPrecioVenta, campoStock, campoCategoria;
    private JTextField campoFiltroId, campoFiltroNombre;
    private JComboBox<Proveedor> comboProveedor;
    private JButton botonIngresar, botonActualizar, botonBorrar;
    private Producto productoSeleccionado;
    private JPanel panelFormulario;
    private JPanel panelTabla;
    private JPanel panelBotones;
    
    public ProductoPanel() {
        this.controladorProducto = Factory.getInstancia().getControladorProducto();
        this.controladorProveedor = Factory.getInstancia().getControladorProveedor();
        inicializarComponentes();
        configurarLayout();
        // Cargar datos de prueba automáticamente
        cargarDatosDePrueba();
    }
    
    private void inicializarComponentes() {
        // Campos de texto
        campoId = crearCampoTexto("Código de barras (ID)");
        campoNombre = crearCampoTexto("Nombre del producto");
        campoDescripcion = crearCampoTexto("Descripción");
        campoPrecioCompra = crearCampoTexto("Precio de Compra");
        campoPrecioVenta = crearCampoTexto("Precio de Venta");
        campoStock = crearCampoTexto("Stock");
        campoCategoria = crearCampoTexto("Categoría");
        
        // Campos de filtro
        campoFiltroId = crearCampoTexto("Buscar por ID...");
        campoFiltroNombre = crearCampoTexto("Buscar por nombre...");
        
        // ComboBox para proveedores
        comboProveedor = crearComboProveedor();
        
        // Botones con paleta moderna
        botonIngresar = crearBoton("Ingresar Producto", new Color(37, 99, 235), e -> mostrarFormularioIngreso());
        botonActualizar = crearBoton("Actualizar Producto", new Color(100, 116, 139), e -> mostrarFormularioActualizacion());
        botonBorrar = crearBoton("Borrar Producto", new Color(220, 38, 38), e -> mostrarFormularioBorrado());
        
        // Tabla de productos
        crearTabla();
        
        // Paneles
        panelFormulario = new JPanel();
        panelTabla = new JPanel();
        panelBotones = new JPanel();
    }
    
    private JTextField crearCampoTexto(String placeholder) {
        JTextField campo = new JTextField(20);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14) // Padding izquierdo aumentado para letras como 'j'
        ));
        campo.setBackground(Color.WHITE);
        campo.setToolTipText(placeholder);
        campo.setPreferredSize(new Dimension(200, 34)); // Altura aumentada para mejor legibilidad
        campo.setMinimumSize(new Dimension(200, 34));
        campo.setMaximumSize(new Dimension(200, 34));
        return campo;
    }
    
    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Tipografía clara, sin negrita excesiva
        label.setForeground(new Color(30, 41, 59)); // Texto principal gris oscuro
        return label;
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
    
    private JComboBox<Proveedor> crearComboProveedor() {
        JComboBox<Proveedor> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Tipografía consistente
        combo.setPreferredSize(new Dimension(200, 38)); // Altura consistente con campos de texto
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Cargar proveedores
        cargarProveedores(combo);
        
        return combo;
    }
    
    private void cargarProveedores(JComboBox<Proveedor> combo) {
        try {
            combo.removeAllItems();
            // Agregar opción "Sin proveedor"
            combo.addItem(null);
            
            List<Proveedor> proveedores = controladorProveedor.listarProveedores();
            for (Proveedor proveedor : proveedores) {
                combo.addItem(proveedor);
            }
        } catch (Exception e) {
            mostrarError("Error al cargar proveedores: " + e.getMessage());
        }
    }
    
    private void crearTabla() {
        String[] columnas = {"ID", "Nombre", "Precio Compra", "Precio Venta", "Stock", "Categoría", "Proveedor", "Activo"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
        };
        
        tablaProductos = new JTable(modelo);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaProductos.setRowHeight(30);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaProductos.getTableHeader().setBackground(SwingApp.COLOR_SECUNDARIO);
        
        // Configurar el filtro de tabla
        sorter = new TableRowSorter<>(modelo);
        tablaProductos.setRowSorter(sorter);
        
        // Listener para selección de fila
        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarProductoSeleccionado();
            }
        });
        
        // Listeners para los campos de filtro
        javax.swing.event.DocumentListener listenerFiltro = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        };
        
        campoFiltroId.getDocument().addDocumentListener(listenerFiltro);
        campoFiltroNombre.getDocument().addDocumentListener(listenerFiltro);
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
        
        JLabel titulo = new JLabel("Gestión de Productos");
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
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Fila 1
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormularioInterno.add(crearEtiqueta("Código de barras"), gbc);
        gbc.gridx = 1;
        panelFormularioInterno.add(campoId, gbc);
        
        gbc.gridx = 2;
        panelFormularioInterno.add(crearEtiqueta("Nombre"), gbc);
        gbc.gridx = 3;
        panelFormularioInterno.add(campoNombre, gbc);
        
        // Fila 2
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormularioInterno.add(crearEtiqueta("Descripción"), gbc);
        gbc.gridx = 1;
        panelFormularioInterno.add(campoDescripcion, gbc);
        
        gbc.gridx = 2;
        panelFormularioInterno.add(crearEtiqueta("Precio de Compra"), gbc);
        gbc.gridx = 3;
        panelFormularioInterno.add(campoPrecioCompra, gbc);
        
        // Fila 2.5 - Precio de Venta
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormularioInterno.add(crearEtiqueta("Precio de Venta"), gbc);
        gbc.gridx = 1;
        panelFormularioInterno.add(campoPrecioVenta, gbc);
        
        // Fila 3
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormularioInterno.add(crearEtiqueta("Stock"), gbc);
        gbc.gridx = 1;
        panelFormularioInterno.add(campoStock, gbc);
        
        gbc.gridx = 2;
        panelFormularioInterno.add(crearEtiqueta("Categoría"), gbc);
        gbc.gridx = 3;
        panelFormularioInterno.add(campoCategoria, gbc);
        
        // Fila 4
        gbc.gridx = 0; gbc.gridy = 3;
        panelFormularioInterno.add(crearEtiqueta("Proveedor"), gbc);
        gbc.gridx = 1;
        panelFormularioInterno.add(comboProveedor, gbc);
        
        panel.add(panelFormularioInterno, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Productos"));
        
        // Configurar la tabla para que se redimensione automáticamente
        tablaProductos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Ajustar el ancho de las columnas proporcionalmente
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(200); // Nombre
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(80);  // Precio
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(60);  // Stock
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(120); // Categoría
        tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(120); // Proveedor
        tablaProductos.getColumnModel().getColumn(6).setPreferredWidth(60);  // Activo
        
        // El BorderLayout se encarga de que ocupe todo el espacio disponible
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Método para filtrar la tabla
    private void filtrar() {
        String filtroId = campoFiltroId.getText().trim();
        String filtroNombre = campoFiltroNombre.getText().trim();
        
        // Si ambos filtros están vacíos, mostrar todos los productos
        if (filtroId.isEmpty() && filtroNombre.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        
        // Crear filtros para cada columna
        java.util.List<RowFilter<Object, Object>> filtros = new java.util.ArrayList<>();
        
        // Filtro por ID (columna 0)
        if (!filtroId.isEmpty()) {
            filtros.add(RowFilter.regexFilter("(?i)" + filtroId, 0));
        }
        
        // Filtro por Nombre (columna 1)
        if (!filtroNombre.isEmpty()) {
            filtros.add(RowFilter.regexFilter("(?i)" + filtroNombre, 1));
        }
        
        // Aplicar los filtros (AND lógico - ambos deben cumplirse)
        if (filtros.size() == 1) {
            sorter.setRowFilter(filtros.get(0));
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filtros));
        }
    }
    
    // Método para mostrar/ocultar el formulario
    private void mostrarTabla() {
        panelFormulario.setVisible(false);
        revalidate();
        repaint();
    }
    
    // Métodos para mostrar diferentes vistas
    private void mostrarFormularioIngreso() {
        // Abrir nueva ventana para ingresar producto
        DialogoNuevoProducto ventana = new DialogoNuevoProducto(this);
        ventana.setVisible(true);
    }
    
    private void mostrarFormularioActualizacion() {
        System.out.println("=== MOSTRAR FORMULARIO ACTUALIZACIÓN ===");
        System.out.println("Producto seleccionado: " + (productoSeleccionado != null ? productoSeleccionado.getNombre() : "null"));
        
        if (productoSeleccionado == null) {
            mostrarError("Seleccione un producto de la lista para actualizar");
            return;
        }
        
        try {
            // Abrir ventana de actualización
            System.out.println("Creando ventana de actualización...");
            DialogoActualizarProducto ventanaActualizar = new DialogoActualizarProducto(this, productoSeleccionado);
            System.out.println("Ventana creada, mostrando...");
            ventanaActualizar.setVisible(true);
            System.out.println("Ventana mostrada exitosamente");
        } catch (Exception e) {
            System.err.println("Error al crear ventana de actualización: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al abrir ventana de actualización: " + e.getMessage());
        }
    }
    
    private void mostrarFormularioBorrado() {
        if (productoSeleccionado == null) {
            mostrarError("Seleccione un producto de la lista para eliminar");
            return;
        }
        
        // Abrir ventana de confirmación de borrado
        DialogoConfirmarBorrado ventanaBorrado = new DialogoConfirmarBorrado(this, productoSeleccionado);
        ventanaBorrado.setVisible(true);
    }
    
    private void hacerFormularioSoloLectura() {
        campoId.setEditable(false);
        campoNombre.setEditable(false);
        campoDescripcion.setEditable(false);
        campoPrecioCompra.setEditable(false);
        campoPrecioVenta.setEditable(false);
        campoStock.setEditable(false);
        campoCategoria.setEditable(false);
        comboProveedor.setEnabled(false);
    }
    
    private void hacerFormularioEditable() {
        campoId.setEditable(true);
        campoNombre.setEditable(true);
        campoDescripcion.setEditable(true);
        campoPrecioCompra.setEditable(true);
        campoPrecioVenta.setEditable(true);
        campoStock.setEditable(true);
        campoCategoria.setEditable(true);
        comboProveedor.setEnabled(true);
    }
    
    private void cargarProductos() {
        try {
            List<Producto> productos = controladorProducto.listarProductos();
            modelo.setRowCount(0); // Limpiar tabla
            
            for (Producto producto : productos) {
                Object[] fila = {
                    producto.getId(),
                    producto.getNombre(),
                    "$" + producto.getPrecioCompra(),
                    "$" + producto.getPrecioVenta(),
                    producto.getStock(),
                    producto.getCategoria(),
                    producto.getProveedor() != null ? producto.getProveedor().getNombre() : "Sin proveedor",
                    producto.getActivo() ? "Sí" : "No"
                };
                modelo.addRow(fila);
            }
        } catch (Exception e) {
            mostrarError("Error al cargar productos: " + e.getMessage());
        }
    }
    
    private void cargarProductoSeleccionado() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        System.out.println("=== CARGAR PRODUCTO SELECCIONADO ===");
        System.out.println("Fila seleccionada: " + filaSeleccionada);
        
        if (filaSeleccionada >= 0) {
            Long id = (Long) modelo.getValueAt(filaSeleccionada, 0);
            System.out.println("ID del producto seleccionado: " + id);
            try {
                productoSeleccionado = controladorProducto.buscarProducto(id);
                System.out.println("Producto cargado: " + (productoSeleccionado != null ? productoSeleccionado.getNombre() : "null"));
                if (productoSeleccionado != null) {
                    llenarFormulario(productoSeleccionado);
                }
            } catch (Exception e) {
                System.err.println("Error al cargar producto: " + e.getMessage());
                e.printStackTrace();
                mostrarError("Error al cargar producto: " + e.getMessage());
            }
        } else {
            System.out.println("No hay fila seleccionada");
        }
    }
    
    private void llenarFormulario(Producto producto) {
        campoId.setText(producto.getId().toString());
        campoNombre.setText(producto.getNombre());
        campoDescripcion.setText(producto.getDescripcion());
        campoPrecioCompra.setText(producto.getPrecioCompra().toString());
        campoPrecioVenta.setText(producto.getPrecioVenta().toString());
        campoStock.setText(producto.getStock().toString());
        campoCategoria.setText(producto.getCategoria());
        
        // Seleccionar proveedor en el combo
        if (producto.getProveedor() != null) {
            comboProveedor.setSelectedItem(producto.getProveedor());
        } else {
            comboProveedor.setSelectedItem(null);
        }
    }
    
    
    
    private Producto obtenerProductoDelFormulario() {
        Producto producto = new Producto();
        
        try {
            producto.setId(Long.parseLong(campoId.getText().trim()));
            producto.setPrecioCompra(new BigDecimal(campoPrecioCompra.getText().trim()));
            producto.setPrecioVenta(new BigDecimal(campoPrecioVenta.getText().trim()));
            producto.setStock(Integer.parseInt(campoStock.getText().trim()));
            
            // Proveedor del ComboBox (puede ser null)
            Proveedor proveedorSeleccionado = (Proveedor) comboProveedor.getSelectedItem();
            producto.setProveedor(proveedorSeleccionado);
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Los campos ID, precios y stock deben ser números válidos");
        }
        
        producto.setNombre(campoNombre.getText().trim());
        producto.setDescripcion(campoDescripcion.getText().trim());
        producto.setCategoria(campoCategoria.getText().trim());
        
        return producto;
    }
    
    private void limpiarFormulario() {
        campoId.setText("");
        campoNombre.setText("");
        campoDescripcion.setText("");
        campoPrecioCompra.setText("");
        campoPrecioVenta.setText("");
        campoStock.setText("");
        campoCategoria.setText("");
        comboProveedor.setSelectedItem(null);
        productoSeleccionado = null;
        hacerFormularioEditable(); // Restaurar edición
        if (tablaProductos != null) {
            tablaProductos.clearSelection();
        }
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Método público para actualizar la tabla desde ventanas externas
    public void actualizarTabla() {
        cargarProductos();
    }
    
    // Método para cargar datos de prueba automáticamente
    private void cargarDatosDePrueba() {
        try {
            // Probar conexión a la base de datos
            System.out.println("=== DIAGNÓSTICO DE CONEXIÓN ===");
            boolean conexionOK = com.fortunato.sistema.config.EntityManagerFactory.getInstance().testConnection();
            if (!conexionOK) {
                System.err.println("ERROR: No se puede conectar a la base de datos");
                return;
            }
            
            // Verificar si ya existen datos
            List<Proveedor> proveedoresExistentes = controladorProveedor.listarProveedores();
            List<Producto> productosExistentes = controladorProducto.listarProductos();
            
            // Solo cargar productos si no hay productos (proveedores pueden existir)
            if (productosExistentes.isEmpty()) {
                System.out.println("Cargando datos de prueba automáticamente...");
                
                // Solo crear proveedores si no existen
                if (proveedoresExistentes.isEmpty()) {
                    cargarProveedoresDePrueba();
                }
                
                // Crear productos de ejemplo
                cargarProductosDePrueba();
                
                // Cargar la tabla con los nuevos datos
                cargarProductos();
                
                System.out.println("Datos de prueba cargados exitosamente!");
            } else {
                // Si ya hay datos, solo cargar la tabla
                cargarProductos();
            }
        } catch (Exception e) {
            System.err.println("Error al cargar datos de prueba: " + e.getMessage());
            // En caso de error, intentar cargar la tabla de todas formas
            try {
                cargarProductos();
            } catch (Exception ex) {
                System.err.println("Error al cargar productos: " + ex.getMessage());
            }
        }
    }
    
    private void cargarProveedoresDePrueba() {
        // Crear proveedores de ejemplo
        List<Proveedor> proveedores = new java.util.ArrayList<>();
        Proveedor cocaCola = new Proveedor("Coca-Cola", 1234567890L);
        cocaCola.setMail("contacto@coca-cola.com");
        proveedores.add(cocaCola);
        
        Proveedor pepsi = new Proveedor("Pepsi", 998765432L);
        pepsi.setMail("contacto@pepsi.com");
        proveedores.add(pepsi);
        
        Proveedor nestle = new Proveedor("Nestlé", 1122334455L);
        nestle.setMail("contacto@nestle.com");
        proveedores.add(nestle);
        
        Proveedor unilever = new Proveedor("Unilever", 567788990L);
        unilever.setMail("contacto@unilever.com");
        proveedores.add(unilever);
        
        Proveedor mars = new Proveedor("Mars", 998877665L);
        mars.setMail("contacto@mars.com");
        proveedores.add(mars);
        
        Proveedor kraft = new Proveedor("Kraft", 112233445L);
        kraft.setMail("contacto@kraft.com");
        proveedores.add(kraft);
        
        Proveedor generalMills = new Proveedor("General Mills", 556677889L);
        generalMills.setMail("contacto@generalmills.com");
        proveedores.add(generalMills);
        
        Proveedor kelloggs = new Proveedor("Kellogg's", 998877665L);
        kelloggs.setMail("contacto@kelloggs.com");
        proveedores.add(kelloggs);
        
        // Guardar proveedores
        for (Proveedor proveedor : proveedores) {
            controladorProveedor.crearProveedor(proveedor);
        }
        
        System.out.println("Proveedores cargados: " + proveedores.size());
    }
    
    private void cargarProductosDePrueba() {
        // Obtener proveedores para asignar a los productos
        List<Proveedor> proveedores = controladorProveedor.listarProveedores();
        if (proveedores.isEmpty()) {
            throw new RuntimeException("No hay proveedores disponibles.");
        }
        
        // Crear productos de ejemplo
        List<Producto> productos = new java.util.ArrayList<>();
        
        // Productos de Coca-Cola
        productos.add(crearProducto(1001L, "Coca-Cola 500ml", "Bebida gaseosa sabor cola", new BigDecimal("600"), new BigDecimal("850"), 50, "Bebidas", proveedores.get(0)));
        productos.add(crearProducto(1002L, "Coca-Cola 1L", "Bebida gaseosa sabor cola", new BigDecimal("800"), new BigDecimal("1200"), 30, "Bebidas", proveedores.get(0)));
        productos.add(crearProducto(1003L, "Sprite 500ml", "Bebida gaseosa sabor lima-limón", new BigDecimal("600"), new BigDecimal("850"), 40, "Bebidas", proveedores.get(0)));
        
        // Productos de Pepsi
        productos.add(crearProducto(2001L, "Pepsi 500ml", "Bebida gaseosa sabor cola", new BigDecimal("550"), new BigDecimal("800"), 45, "Bebidas", proveedores.get(1)));
        productos.add(crearProducto(2002L, "7Up 500ml", "Bebida gaseosa sabor lima-limón", new BigDecimal("550"), new BigDecimal("800"), 35, "Bebidas", proveedores.get(1)));
        
        // Productos de Nestlé
        productos.add(crearProducto(3001L, "KitKat", "Chocolate con galleta", new BigDecimal("300"), new BigDecimal("450"), 60, "Dulces", proveedores.get(2)));
        productos.add(crearProducto(3002L, "Crunch", "Chocolate con arroz inflado", new BigDecimal("280"), new BigDecimal("420"), 55, "Dulces", proveedores.get(2)));
        productos.add(crearProducto(3003L, "Nescafé 50g", "Café instantáneo", new BigDecimal("1200"), new BigDecimal("1800"), 20, "Café", proveedores.get(2)));
        
        // Productos de Unilever (índice 3)
        productos.add(crearProducto(4001L, "Alfajor Jorgito", "Alfajor de chocolate", new BigDecimal("120"), new BigDecimal("180"), 80, "Dulces", proveedores.get(3)));
        productos.add(crearProducto(4002L, "Bon o Bon", "Bombón de chocolate", new BigDecimal("80"), new BigDecimal("120"), 100, "Dulces", proveedores.get(3)));
        productos.add(crearProducto(4003L, "Mogul", "Caramelo de chocolate", new BigDecimal("60"), new BigDecimal("95"), 90, "Dulces", proveedores.get(3)));
        
        // Productos adicionales de Coca Cola
        productos.add(crearProducto(5001L, "Fanta 500ml", "Bebida gaseosa sabor naranja", new BigDecimal("600"), new BigDecimal("850"), 35, "Bebidas", proveedores.get(0)));
        productos.add(crearProducto(5002L, "Coca-Cola Zero 500ml", "Bebida gaseosa sin azúcar", new BigDecimal("600"), new BigDecimal("850"), 40, "Bebidas", proveedores.get(0)));
        
        // Productos adicionales de Pepsi
        productos.add(crearProducto(6001L, "Pepsi Max 500ml", "Bebida gaseosa sin azúcar", new BigDecimal("550"), new BigDecimal("800"), 30, "Bebidas", proveedores.get(1)));
        
        // Productos adicionales de Nestlé
        productos.add(crearProducto(7001L, "Nesquik", "Bebida en polvo sabor chocolate", new BigDecimal("800"), new BigDecimal("1200"), 25, "Bebidas", proveedores.get(2)));
        
        // Guardar productos
        for (Producto producto : productos) {
            controladorProducto.crearProducto(producto);
        }
        
        System.out.println("Productos cargados: " + productos.size());
    }
    
    // Método auxiliar para crear un producto
    private Producto crearProducto(Long id, String nombre, String descripcion, BigDecimal precioCompra, 
                                 BigDecimal precioVenta, int stock, String categoria, Proveedor proveedor) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecioCompra(precioCompra);
        producto.setPrecioVenta(precioVenta);
        producto.setStock(stock);
        producto.setCategoria(categoria);
        producto.setProveedor(proveedor);
        return producto;
    }
}