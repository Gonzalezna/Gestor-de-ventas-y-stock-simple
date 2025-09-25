package com.fortunato.sistema.presentacion;

import com.fortunato.sistema.entidad.Producto;
import com.fortunato.sistema.entidad.Proveedor;
import com.fortunato.sistema.interfaz.IControladorProducto;
import com.fortunato.sistema.interfaz.IControladorProveedor;
import com.fortunato.sistema.interfaz.Factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Diálogo para ingresar productos
 */
public class DialogoNuevoProducto extends JFrame {
    
    private IControladorProducto controladorProducto;
    private IControladorProveedor controladorProveedor;
    private JTextField campoId, campoNombre, campoDescripcion, campoPrecioCompra, campoPrecioVenta, campoStock, campoCategoria;
    private CampoTextoConSugerencias campoProveedor;
    private List<Proveedor> todosLosProveedores;
    private Proveedor proveedorSeleccionado;
    private JButton botonCrear;
    private ProductoPanel panelPrincipal; // Referencia al panel principal para actualizar la tabla
    
    public DialogoNuevoProducto(ProductoPanel panelPrincipal) {
        this.panelPrincipal = panelPrincipal;
        this.controladorProducto = Factory.getInstancia().getControladorProducto();
        this.controladorProveedor = Factory.getInstancia().getControladorProveedor();
        
        inicializarComponentes();
        configurarVentana();
        configurarLayout();
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
        
        // Campo de autocompletado para proveedores
        campoProveedor = crearCampoProveedor();
        
        
        todosLosProveedores = new java.util.ArrayList<>();
        proveedorSeleccionado = null;
        
        // Cargar proveedores
        cargarProveedores();
        
        // Botón
        botonCrear = crearBoton("Crear Producto", new Color(37, 99, 235), e -> crearProducto());
    }
    
    private void configurarVentana() {
        setTitle("Ingresar Producto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        
        // Tamaño para la ventana 
        setPreferredSize(new Dimension(1200, 800));
        setMinimumSize(new Dimension(1200 , 800));
        pack();
        
        // Centrar la ventana en la pantalla (después de pack())
        setLocationRelativeTo(null);
        
        // MouseListener global para cerrar listas al hacer clic fuera de campos
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                // Forzar pérdida de foco de todos los campos para cerrar listas
                if (campoProveedor != null && campoProveedor.getTextField().hasFocus()) {
                    campoProveedor.getTextField().transferFocus();
                }
            }
        });
    }
    
    private void configurarLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));
        
        // Panel superior con título
        JPanel panelTitulo = crearPanelTitulo();
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con formulario
        JPanel panelFormulario = crearPanelFormulario();
        add(panelFormulario, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel titulo = new JLabel("Ingresar Nuevo Producto");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(30, 41, 59));
        panel.add(titulo);
        
        return panel;
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Fila 1 - Código de barras y Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(crearEtiqueta("Código de barras"), gbc);
        gbc.gridx = 1;
        panel.add(campoId, gbc);
        
        gbc.gridx = 2;
        panel.add(crearEtiqueta("Nombre"), gbc);
        gbc.gridx = 3;
        panel.add(campoNombre, gbc);
        
        // Fila 2 - Precio de Compra y Precio de Venta
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(crearEtiqueta("Precio de Compra"), gbc);
        gbc.gridx = 1;
        panel.add(campoPrecioCompra, gbc);
        
        gbc.gridx = 2;
        panel.add(crearEtiqueta("Precio de Venta"), gbc);
        gbc.gridx = 3;
        panel.add(campoPrecioVenta, gbc);
        
        // Fila 3 - Stock y Categoría
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(crearEtiqueta("Stock"), gbc);
        gbc.gridx = 1;
        panel.add(campoStock, gbc);
        
        gbc.gridx = 2;
        panel.add(crearEtiqueta("Categoría"), gbc);
        gbc.gridx = 3;
        panel.add(campoCategoria, gbc);
        
        // Fila 4 - Descripción
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(crearEtiqueta("Descripción"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        panel.add(campoDescripcion, gbc);
        
        // Fila 5 - Proveedor
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(crearEtiqueta("Proveedor"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(crearPanelProveedor(), gbc);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        panel.add(botonCrear);
        
        return panel;
    }
    
    private JPanel crearPanelProveedor() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Campo de autocompletado para proveedores
        panel.add(campoProveedor, BorderLayout.CENTER);
        
        // Configurar tamaño del panel para mostrar campo y lista desplegable
        panel.setPreferredSize(new Dimension(200, 160));
        panel.setMinimumSize(new Dimension(200, 160));
        panel.setMaximumSize(new Dimension(200, 160));
        
        return panel;
    }
    
    private CampoTextoConSugerencias crearCampoProveedor() {
        // Crear lista vacía inicialmente
        List<String> nombresProveedores = new ArrayList<>();
        CampoTextoConSugerencias campo = new CampoTextoConSugerencias(nombresProveedores);
        
        // Configurar tamaño del campo de autocompletado
        campo.setPreferredSize(new Dimension(200, 160));
        campo.setMinimumSize(new Dimension(200, 160));
        campo.setMaximumSize(new Dimension(200, 160));
        campo.setToolTipText("Buscar proveedor...");
        
        // Listener para cuando se selecciona un proveedor
        campo.addActionListener(e -> {
            String nombreSeleccionado = campo.getText().trim();
            if (!nombreSeleccionado.isEmpty()) {
                // Buscar el proveedor por nombre
                buscarProveedorPorNombre(nombreSeleccionado);
            } else {
                proveedorSeleccionado = null;
            }
        });
        
        return campo;
    }
    
    
    private void buscarProveedorPorNombre(String nombre) {
        System.out.println("Buscando proveedor por nombre: " + nombre);
        for (Proveedor proveedor : todosLosProveedores) {
            if (proveedor.getNombre().equals(nombre)) {
                proveedorSeleccionado = proveedor;
                System.out.println("Proveedor encontrado - ID: " + proveedor.getId() + ", Nombre: " + proveedor.getNombre());
                return;
            }
        }
        proveedorSeleccionado = null;
        System.out.println("Proveedor no encontrado: " + nombre);
    }
    
    private JTextField crearCampoTexto(String placeholder) {
        JTextField campo = new JTextField(20);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Mismo tamaño que ProductoPanel
        campo.setPreferredSize(new Dimension(200, 34)); // Misma altura que ProductoPanel
        campo.setMinimumSize(new Dimension(200, 34));
        campo.setMaximumSize(new Dimension(200, 34));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14) // Mismo padding que ProductoPanel
        ));
        campo.setBackground(Color.WHITE);
        campo.setToolTipText(placeholder);
        
        // Aplicar validación según el tipo de campo
        if (placeholder.contains("Código de barras")) {
            configurarSoloNumeros(campo);
            configurarMaximoCaracteres(campo, 13);
        } else if (placeholder.contains("Stock")) {
            configurarSoloNumeros(campo);
            configurarMaximoCaracteres(campo, 4); // Máximo 9999
        } else if (placeholder.contains("Precio de Compra") || placeholder.contains("Precio de Venta")) {
            configurarSoloNumeros(campo);
            configurarMaximoCaracteres(campo, 7); // Máximo 9999999
        } else if (placeholder.contains("Nombre")) {
            configurarMaximoCaracteres(campo, 50); // Máximo 50 caracteres
        } else if (placeholder.contains("Descripción")) {
            configurarMaximoCaracteres(campo, 300); // Máximo 300 caracteres
        } else if (placeholder.contains("Categoría")) {
            configurarMaximoCaracteres(campo, 50); // Máximo 50 caracteres
        }
        
        return campo;
    }
    
    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        etiqueta.setForeground(new Color(30, 41, 59));
        return etiqueta;
    }
    
    private JButton crearBoton(String texto, Color color, ActionListener listener) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setPreferredSize(new Dimension(140, 40));
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addActionListener(listener);
        
        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (color.equals(new Color(37, 99, 235))) {
                    boton.setBackground(new Color(59, 130, 246));
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
    
    private void cargarProveedores() {
        try {
            todosLosProveedores.clear();
            List<Proveedor> proveedores = controladorProveedor.listarProveedores();
            todosLosProveedores.addAll(proveedores);
            System.out.println("Proveedores cargados en DialogoNuevoProducto: " + proveedores.size());
            
            // Crear lista de nombres para el autocompletado
            List<String> nombresProveedores = new ArrayList<>();
            for (Proveedor p : proveedores) {
                nombresProveedores.add(p.getNombre());
                System.out.println("  - ID: " + p.getId() + ", Nombre: " + p.getNombre() + ", Teléfono: " + p.getTelefono() + ", Email: " + p.getMail());
            }
            
            // Actualizar el campo de sugerencias
            campoProveedor.setItems(nombresProveedores);
        } catch (Exception e) {
            // Si hay error de conexión, mostrar proveedores de ejemplo
            System.err.println("Error al cargar proveedores: " + e.getMessage());
            // Agregar proveedores de ejemplo para desarrollo
            agregarProveedoresEjemplo();
        }
    }
    
    private void agregarProveedoresEjemplo() {
        // Proveedores de ejemplo para desarrollo cuando no hay conexión a BD
        todosLosProveedores.clear();
        Proveedor[] proveedoresEjemplo = {
            new Proveedor("Coca-Cola", 1234567890L),
            new Proveedor("Pepsi", 987654321L),
            new Proveedor("Nestlé", 112233445L),
            new Proveedor("Unilever", 556677889L),
            new Proveedor("Mars", 998877665L)
        };
        
        // Configurar emails para los proveedores de ejemplo
        proveedoresEjemplo[0].setMail("contacto@coca-cola.com");
        proveedoresEjemplo[1].setMail("contacto@pepsi.com");
        proveedoresEjemplo[2].setMail("contacto@nestle.com");
        proveedoresEjemplo[3].setMail("contacto@unilever.com");
        proveedoresEjemplo[4].setMail("contacto@mars.com");
        
        // Crear lista de nombres para el autocompletado
        List<String> nombresProveedores = new ArrayList<>();
        for (Proveedor p : proveedoresEjemplo) {
            todosLosProveedores.add(p);
            nombresProveedores.add(p.getNombre());
        }
        
        // Actualizar el campo de sugerencias
        campoProveedor.setItems(nombresProveedores);
    }
    
    private void crearProducto() {
        try {
            System.out.println("=== INICIANDO CREACIÓN DE PRODUCTO ===");
            System.out.println("ID: " + campoId.getText().trim());
            System.out.println("Nombre: " + campoNombre.getText().trim());
            System.out.println("Precio Compra: " + campoPrecioCompra.getText().trim());
            System.out.println("Precio Venta: " + campoPrecioVenta.getText().trim());
            System.out.println("Stock: " + campoStock.getText().trim());
            System.out.println("Proveedor seleccionado: " + (proveedorSeleccionado != null ? proveedorSeleccionado.getNombre() : "null"));
            
            Producto producto = obtenerProductoDelFormulario();
            System.out.println("Producto obtenido del formulario: " + producto.getNombre());
            
            controladorProducto.crearProducto(producto);
            System.out.println("Producto guardado en base de datos exitosamente");
            
            mostrarExito("Producto creado exitosamente");
            limpiarFormulario();
            
            // Actualizar la tabla en el panel principal
            if (panelPrincipal != null) {
                panelPrincipal.actualizarTabla();
                System.out.println("Tabla actualizada");
            }
        } catch (Exception e) {
            System.err.println("ERROR al crear producto: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al crear producto: " + e.getMessage());
        }
    }
    
    
    private Producto obtenerProductoDelFormulario() {
        Producto producto = new Producto();
        
        try {
            String idText = campoId.getText().trim();
            String precioCompraText = campoPrecioCompra.getText().trim();
            String precioVentaText = campoPrecioVenta.getText().trim();
            String stockText = campoStock.getText().trim();
            
            System.out.println("Validando campos numéricos...");
            System.out.println("ID text: '" + idText + "'");
            System.out.println("Precio Compra text: '" + precioCompraText + "'");
            System.out.println("Precio Venta text: '" + precioVentaText + "'");
            System.out.println("Stock text: '" + stockText + "'");
            
            if (idText.isEmpty() || precioCompraText.isEmpty() || precioVentaText.isEmpty() || stockText.isEmpty()) {
                throw new IllegalArgumentException("Los campos ID, precio de compra, precio de venta y stock son obligatorios");
            }
            
            producto.setId(Long.parseLong(idText));
            producto.setPrecioCompra(new BigDecimal(precioCompraText));
            producto.setPrecioVenta(new BigDecimal(precioVentaText));
            producto.setStock(Integer.parseInt(stockText));
            
            producto.setProveedor(this.proveedorSeleccionado);
            System.out.println("Proveedor asignado: " + (this.proveedorSeleccionado != null ? this.proveedorSeleccionado.getNombre() : "null"));
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Los campos ID, precio y stock deben ser números válidos: " + e.getMessage());
        }
        
        String nombre = campoNombre.getText().trim();
        String descripcion = campoDescripcion.getText().trim();
        String categoria = campoCategoria.getText().trim();
        
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setCategoria(categoria);
        producto.setActivo(true); // Los productos nuevos siempre están activos
        
        System.out.println("Producto creado exitosamente: " + producto.getNombre());
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
        campoProveedor.clear();
        proveedorSeleccionado = null;
    }
    
    private void configurarSoloNumeros(JTextField campo) {
        campo.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Solo permitir dígitos y backspace
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume(); // Bloquea la tecla completamente
                }
            }
        });
    }
    
    private void configurarMaximoCaracteres(JTextField campo, int maximo) {
        campo.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                // Si ya tiene el máximo de caracteres, no permitir más
                if (campo.getText().length() >= maximo) {
                    e.consume(); // Bloquea la tecla
                }
            }
        });
    }
    
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
