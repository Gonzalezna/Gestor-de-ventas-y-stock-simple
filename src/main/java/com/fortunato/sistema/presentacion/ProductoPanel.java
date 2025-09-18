package com.fortunato.sistema.presentacion;

import com.fortunato.sistema.entidad.Producto;
import com.fortunato.sistema.entidad.Proveedor;
import com.fortunato.sistema.interfaz.IControladorProducto;
import com.fortunato.sistema.interfaz.IControladorProveedor;
import com.fortunato.sistema.interfaz.Factory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
    private JTextField campoId, campoNombre, campoDescripcion, campoPrecio, campoStock, campoCategoria;
    private JComboBox<Proveedor> comboProveedor;
    private JButton btnIngresar, btnActualizar, btnBorrar, btnListar;
    private Producto productoSeleccionado;
    private JPanel panelFormulario;
    private JPanel panelTabla;
    
    public ProductoPanel() {
        this.controladorProducto = Factory.getInstancia().getControladorProducto();
        this.controladorProveedor = Factory.getInstancia().getControladorProveedor();
        inicializarComponentes();
        configurarLayout();
        cargarProductos();
    }
    
    private void inicializarComponentes() {
        // Campos de texto
        campoId = crearCampoTexto("Código de barras (ID)");
        campoNombre = crearCampoTexto("Nombre del producto");
        campoDescripcion = crearCampoTexto("Descripción");
        campoPrecio = crearCampoTexto("Precio");
        campoStock = crearCampoTexto("Stock");
        campoCategoria = crearCampoTexto("Categoría");
        
        // ComboBox para proveedores
        comboProveedor = crearComboProveedor();
        
        // Botones del submenú con paleta moderna
        btnIngresar = crearBoton("Ingresar Producto", new Color(37, 99, 235), e -> mostrarFormularioIngreso()); // Azul medio - acción principal
        btnActualizar = crearBoton("Actualizar Producto", new Color(100, 116, 139), e -> mostrarFormularioActualizacion()); // Gris azulado - secundario
        btnBorrar = crearBoton("Borrar Producto", new Color(220, 38, 38), e -> mostrarFormularioBorrado()); // Rojo suave - destructivo
        btnListar = crearBoton("Listar Productos", new Color(100, 116, 139), e -> mostrarListaProductos()); // Gris azulado - secundario
        
        // Tabla de productos
        crearTabla();
        
        // Paneles
        panelFormulario = new JPanel();
        panelTabla = new JPanel();
    }
    
    private JTextField crearCampoTexto(String placeholder) {
        JTextField campo = new JTextField(20);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Tipografía consistente
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1), // Borde sutil moderno
            BorderFactory.createEmptyBorder(10, 12, 10, 12) // Padding uniforme
        ));
        campo.setBackground(Color.WHITE);
        campo.setToolTipText(placeholder);
        return campo;
    }
    
    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Tipografía clara, sin negrita excesiva
        label.setForeground(new Color(30, 41, 59)); // Texto principal gris oscuro
        return label;
    }
    
    private JButton crearBoton(String texto, Color color, ActionListener listener) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Tipografía más clara, sin negrita excesiva
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        
        // Bordes redondeados modernos (8px)
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1), // Borde sutil
            BorderFactory.createEmptyBorder(10, 16, 10, 16) // Padding uniforme
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
        String[] columnas = {"ID", "Nombre", "Precio", "Stock", "Categoría", "Activo"};
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
        
        // Listener para selección de fila
        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarProductoSeleccionado();
            }
        });
    }
    
    private void configurarLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249)); // Fondo gris muy claro moderno
        
        // Panel superior con botones del submenú
        JPanel panelSubmenu = crearPanelSubmenu();
        add(panelSubmenu, BorderLayout.NORTH);
        
        // Panel central que cambia según la acción
        panelFormulario = crearPanelFormulario();
        panelTabla = crearPanelTabla();
        
        // Mostrar formulario de ingreso por defecto
        mostrarFormularioIngreso();
    }
    
    private JPanel crearPanelSubmenu() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16)); // Espaciado uniforme
        panel.setBackground(new Color(241, 245, 249)); // Fondo gris muy claro
        
        // Título simplificado
        JLabel titulo = new JLabel("Gestión de Productos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Título principal más prominente
        titulo.setForeground(new Color(30, 41, 59)); // Texto principal gris oscuro
        panel.add(titulo);
        
        // Espaciador más elegante
        panel.add(Box.createHorizontalStrut(40));
        
        // Botones del submenú con espaciado
        panel.add(btnIngresar);
        panel.add(Box.createHorizontalStrut(8));
        panel.add(btnActualizar);
        panel.add(Box.createHorizontalStrut(8));
        panel.add(btnBorrar);
        panel.add(Box.createHorizontalStrut(8));
        panel.add(btnListar);
        
        return panel;
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE); // Fondo blanco limpio
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(24, 24, 24, 24) // Padding generoso
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12); // Espaciado uniforme más generoso
        gbc.anchor = GridBagConstraints.WEST;
        
        // Fila 1
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(crearLabel("Código de barras"), gbc);
        gbc.gridx = 1;
        panel.add(campoId, gbc);
        
        gbc.gridx = 2;
        panel.add(crearLabel("Nombre"), gbc);
        gbc.gridx = 3;
        panel.add(campoNombre, gbc);
        
        // Fila 2
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(crearLabel("Descripción"), gbc);
        gbc.gridx = 1;
        panel.add(campoDescripcion, gbc);
        
        gbc.gridx = 2;
        panel.add(crearLabel("Precio"), gbc);
        gbc.gridx = 3;
        panel.add(campoPrecio, gbc);
        
        // Fila 3
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(crearLabel("Stock"), gbc);
        gbc.gridx = 1;
        panel.add(campoStock, gbc);
        
        gbc.gridx = 2;
        panel.add(crearLabel("Categoría"), gbc);
        gbc.gridx = 3;
        panel.add(campoCategoria, gbc);
        
        // Fila 4
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(crearLabel("Proveedor"), gbc);
        gbc.gridx = 1;
        panel.add(comboProveedor, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SwingApp.COLOR_FONDO);
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Productos"));
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Métodos para mostrar diferentes vistas
    private void mostrarFormularioIngreso() {
        limpiarFormulario();
        removeAll();
        add(crearPanelSubmenu(), BorderLayout.NORTH);
        add(panelFormulario, BorderLayout.CENTER);
        
        // Botón de crear integrado al bloque de acciones
        JButton btnCrear = crearBoton("Crear Producto", new Color(37, 99, 235), e -> crearProducto()); // Azul medio - acción principal
        JPanel panelBoton = new JPanel(new FlowLayout());
        panelBoton.setBackground(SwingApp.COLOR_FONDO);
        panelBoton.add(btnCrear);
        add(panelBoton, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }
    
    private void mostrarFormularioActualizacion() {
        mostrarListaProductos();
        removeAll();
        add(crearPanelSubmenu(), BorderLayout.NORTH);
        add(panelFormulario, BorderLayout.CENTER);
        
        // Botón de actualizar con color secundario
        JButton btnActualizar = crearBoton("Actualizar Producto", new Color(100, 116, 139), e -> actualizarProducto()); // Gris azulado - secundario
        JPanel panelBoton = new JPanel(new FlowLayout());
        panelBoton.setBackground(SwingApp.COLOR_FONDO);
        panelBoton.add(btnActualizar);
        add(panelBoton, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }
    
    private void mostrarFormularioBorrado() {
        mostrarListaProductos();
        removeAll();
        add(crearPanelSubmenu(), BorderLayout.NORTH);
        add(panelFormulario, BorderLayout.CENTER);
        
        // Botón de eliminar con color destructivo
        JButton btnEliminar = crearBoton("Eliminar Producto", new Color(220, 38, 38), e -> eliminarProducto()); // Rojo suave - destructivo
        JPanel panelBoton = new JPanel(new FlowLayout());
        panelBoton.setBackground(SwingApp.COLOR_FONDO);
        panelBoton.add(btnEliminar);
        add(panelBoton, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }
    
    private void mostrarListaProductos() {
        cargarProductos();
        removeAll();
        add(crearPanelSubmenu(), BorderLayout.NORTH);
        add(panelTabla, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    private void cargarProductos() {
        try {
            List<Producto> productos = controladorProducto.listarProductos();
            modelo.setRowCount(0); // Limpiar tabla
            
            for (Producto producto : productos) {
                Object[] fila = {
                    producto.getId(),
                    producto.getNombre(),
                    "$" + producto.getPrecio(),
                    producto.getStock(),
                    producto.getCategoria(),
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
        if (filaSeleccionada >= 0) {
            Long id = (Long) modelo.getValueAt(filaSeleccionada, 0);
            try {
                productoSeleccionado = controladorProducto.buscarProducto(id);
                if (productoSeleccionado != null) {
                    llenarFormulario(productoSeleccionado);
                }
            } catch (Exception e) {
                mostrarError("Error al cargar producto: " + e.getMessage());
            }
        }
    }
    
    private void llenarFormulario(Producto producto) {
        campoId.setText(producto.getId().toString());
        campoNombre.setText(producto.getNombre());
        campoDescripcion.setText(producto.getDescripcion());
        campoPrecio.setText(producto.getPrecio().toString());
        campoStock.setText(producto.getStock().toString());
        campoCategoria.setText(producto.getCategoria());
        
        // Seleccionar proveedor en el combo
        if (producto.getProveedor() != null) {
            comboProveedor.setSelectedItem(producto.getProveedor());
        } else {
            comboProveedor.setSelectedItem(null);
        }
    }
    
    private void crearProducto() {
        try {
            Producto producto = obtenerProductoDelFormulario();
            controladorProducto.crearProducto(producto);
            mostrarExito("Producto creado exitosamente");
            limpiarFormulario();
        } catch (Exception e) {
            mostrarError("Error al crear producto: " + e.getMessage());
        }
    }
    
    private void actualizarProducto() {
        if (productoSeleccionado == null) {
            mostrarError("Seleccione un producto para actualizar");
            return;
        }
        
        try {
            Producto producto = obtenerProductoDelFormulario();
            producto.setId(productoSeleccionado.getId());
            controladorProducto.actualizarProducto(producto);
            mostrarExito("Producto actualizado exitosamente");
            limpiarFormulario();
        } catch (Exception e) {
            mostrarError("Error al actualizar producto: " + e.getMessage());
        }
    }
    
    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            mostrarError("Seleccione un producto para eliminar");
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea eliminar el producto '" + productoSeleccionado.getNombre() + "'?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                controladorProducto.eliminarProducto(productoSeleccionado.getId());
                mostrarExito("Producto eliminado exitosamente");
                limpiarFormulario();
            } catch (Exception e) {
                mostrarError("Error al eliminar producto: " + e.getMessage());
            }
        }
    }
    
    private Producto obtenerProductoDelFormulario() {
        Producto producto = new Producto();
        
        try {
            producto.setId(Long.parseLong(campoId.getText().trim()));
            producto.setPrecio(new BigDecimal(campoPrecio.getText().trim()));
            producto.setStock(Integer.parseInt(campoStock.getText().trim()));
            
            // Proveedor del ComboBox (puede ser null)
            Proveedor proveedorSeleccionado = (Proveedor) comboProveedor.getSelectedItem();
            producto.setProveedor(proveedorSeleccionado);
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Los campos ID, precio y stock deben ser números válidos");
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
        campoPrecio.setText("");
        campoStock.setText("");
        campoCategoria.setText("");
        comboProveedor.setSelectedItem(null);
        productoSeleccionado = null;
        if (tablaProductos != null) {
            tablaProductos.clearSelection();
        }
    }
    
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}