package com.fortunato.sistema.presentacion;

import com.fortunato.sistema.entidad.Producto;
import com.fortunato.sistema.entidad.DetalleVenta;
import com.fortunato.sistema.entidad.Caja;
import com.fortunato.sistema.entidad.Venta;
import com.fortunato.sistema.interfaz.IControladorProducto;
import com.fortunato.sistema.interfaz.IControladorVentas;
import com.fortunato.sistema.interfaz.IControladorCaja;
import com.fortunato.sistema.interfaz.Factory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

/**
 * Panel para la caja registradora (punto de venta)
 */
public class CajaPanel extends JPanel {
    
    private IControladorProducto controladorProducto;
    private IControladorVentas controladorVentas;
    private IControladorCaja controladorCaja;
    
    private JTable tablaCarrito;
    private DefaultTableModel modeloCarrito;
    
    private JTextField campoCodigoBarras;
    private JTextField campoCantidad;
    private JLabel labelTotal;
    private JLabel labelCantidadProductos;
    private JLabel labelEstadoCaja;
    private JLabel labelSaldoCaja;
    
    private JButton botonAgregar;
    private JButton botonRemover;
    private JButton botonLimpiar;
    private JButton botonConfirmar;
    private JButton botonAbrirCaja;
    
    private JPanel panelBotones;
    private JPanel panelCarrito;
    private JPanel panelInfo;
    
    private Caja cajaActual;
    
    public CajaPanel() {
        this.controladorProducto = Factory.getInstancia().getControladorProducto();
        this.controladorVentas = Factory.getInstancia().getControladorVentas();
        this.controladorCaja = Factory.getInstancia().getControladorCaja();
        
        inicializarComponentes();
        configurarLayout();
        cargarCaja();
        actualizarTotales();
    }
    
    private void inicializarComponentes() {
        // Campos de texto
        campoCodigoBarras = crearCampoTexto("Escanear o ingresar código de barras");
        campoCantidad = crearCampoTexto("1");
        
        // Labels de información
        labelTotal = crearLabelTotal();
        labelCantidadProductos = crearLabelInfo("Productos: 0");
        labelEstadoCaja = crearLabelInfo("Caja: Cerrada");
        labelSaldoCaja = crearLabelInfo("Saldo en caja: $0.00");
        
        // Botones con paleta moderna
        botonAgregar = crearBoton("Agregar al Carrito", new Color(37, 99, 235), e -> agregarProductoAlCarrito());
        botonRemover = crearBoton("Remover Seleccionado", new Color(220, 38, 38), e -> removerProductoDelCarrito());
        botonLimpiar = crearBoton("Limpiar Carrito", new Color(100, 116, 139), e -> limpiarCarrito());
        botonConfirmar = crearBoton("Confirmar Venta", new Color(22, 163, 74), e -> confirmarVenta());
        botonAbrirCaja = crearBotonPequeno("Abrir Caja", new Color(16, 185, 129), e -> abrirCaja());
        
        // Tabla del carrito
        crearTablaCarrito();
        
        // Configurar evento Enter en campo código de barras
        campoCodigoBarras.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    agregarProductoAlCarrito();
                }
            }
        });
        
        // Solo permitir números en cantidad
        campoCantidad.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
    }
    
    private void configurarLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(241, 245, 249)); // Fondo gris muy claro moderno
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior con título
        JPanel panelTitulo = crearPanelTitulo();
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel principal con layout horizontal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 0));
        panelPrincipal.setBackground(new Color(241, 245, 249));
        
        // Panel de botones a la izquierda
        panelBotones = crearPanelBotones();
        panelPrincipal.add(panelBotones, BorderLayout.WEST);
        
        // Panel de carrito en el centro
        panelCarrito = crearPanelCarrito();
        panelPrincipal.add(panelCarrito, BorderLayout.CENTER);
        
        add(panelPrincipal, BorderLayout.CENTER);
        
        // Panel de información en la parte inferior
        panelInfo = crearPanelInformacion();
        add(panelInfo, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titulo = new JLabel("Punto de Venta");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(30, 41, 59));
        
        JPanel panelEstadoCaja = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelEstadoCaja.setBackground(new Color(241, 245, 249));
        panelEstadoCaja.add(labelEstadoCaja);
        panelEstadoCaja.add(botonAbrirCaja);
        
        panel.add(titulo, BorderLayout.WEST);
        panel.add(panelEstadoCaja, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Título del panel
        JLabel titulo = new JLabel("Buscar Producto");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(new Color(30, 41, 59));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(15));
        
        // Campo código de barras
        panel.add(crearCampoConLabel("Código de Barras:", campoCodigoBarras));
        panel.add(Box.createVerticalStrut(15));
        
        // Campo cantidad sin botones
        panel.add(crearCampoConLabel("Cantidad:", campoCantidad));
        panel.add(Box.createVerticalStrut(20));
        
        // Botones
        botonAgregar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(botonAgregar);
        panel.add(Box.createVerticalStrut(10));
        
        botonRemover.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(botonRemover);
        panel.add(Box.createVerticalStrut(10));
        
        botonLimpiar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(botonLimpiar);
        panel.add(Box.createVerticalStrut(20));
        
        // Separador
        JSeparator separador = new JSeparator();
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separador.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(separador);
        panel.add(Box.createVerticalStrut(20));
        
        botonConfirmar.setAlignmentX(Component.LEFT_ALIGNMENT);
        botonConfirmar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        botonConfirmar.setPreferredSize(new Dimension(220, 50));
        panel.add(botonConfirmar);
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Título
        JLabel titulo = new JLabel("Carrito de Compras");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(new Color(30, 41, 59));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(titulo, BorderLayout.NORTH);
        
        // Tabla
        JScrollPane scrollPane = new JScrollPane(tablaCarrito);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelInformacion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        
        // Panel izquierdo con cantidad de productos (alineado a la izquierda)
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(labelCantidadProductos, gbc);
        
        // Panel central con saldo de caja (centrado)
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(labelSaldoCaja, gbc);
        
        // Panel derecho con total (alineado a la derecha)
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(labelTotal, gbc);
        
        return panel;
    }
    
    private JPanel crearCampoConLabel(String labelText, JTextField campo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(30, 41, 59));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(220, 38));
        campo.setPreferredSize(new Dimension(220, 38));
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(campo);
        
        return panel;
    }
    
    private JTextField crearCampoTexto(String placeholder) {
        JTextField campo = new JTextField(20);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        campo.setBackground(Color.WHITE);
        campo.setToolTipText(placeholder);
        return campo;
    }
    
    private JLabel crearLabelTotal() {
        JLabel label = new JLabel("Total: $0.00");
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(new Color(22, 163, 74)); // Verde
        return label;
    }
    
    private JLabel crearLabelInfo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(100, 116, 139));
        return label;
    }
    
    private JButton crearBoton(String texto, Color color, ActionListener listener) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        
        boton.setPreferredSize(new Dimension(220, 40));
        boton.setMinimumSize(new Dimension(220, 40));
        boton.setMaximumSize(new Dimension(220, 40));
        
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addActionListener(listener);
        
        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });
        
        return boton;
    }
    
    private JButton crearBotonPequeno(String texto, Color color, ActionListener listener) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        
        boton.setPreferredSize(new Dimension(90, 32));
        boton.setMinimumSize(new Dimension(90, 32));
        boton.setMaximumSize(new Dimension(90, 32));
        
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addActionListener(listener);
        
        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });
        
        return boton;
    }
    
    private void crearTablaCarrito() {
        String[] columnas = {"Código", "Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        modeloCarrito = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaCarrito.setRowHeight(40); // Aumentado para los botones
        tablaCarrito.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaCarrito.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaCarrito.getTableHeader().setForeground(new Color(30, 41, 59));
        tablaCarrito.setSelectionBackground(new Color(219, 234, 254));
        tablaCarrito.setSelectionForeground(new Color(30, 41, 59));
        tablaCarrito.setGridColor(new Color(226, 232, 240));
        
        // Alinear columnas numéricas a la derecha
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaCarrito.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Precio
        tablaCarrito.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // Subtotal
        
        // Renderer personalizado para la columna de cantidad con botones
        tablaCarrito.getColumnModel().getColumn(2).setCellRenderer(new CantidadCellRenderer());
        
        // Ajustar anchos de columnas
        tablaCarrito.getColumnModel().getColumn(0).setPreferredWidth(100); // Código
        tablaCarrito.getColumnModel().getColumn(1).setPreferredWidth(300); // Producto
        tablaCarrito.getColumnModel().getColumn(2).setPreferredWidth(120); // Cantidad con botones
        tablaCarrito.getColumnModel().getColumn(3).setPreferredWidth(100); // Precio
        tablaCarrito.getColumnModel().getColumn(4).setPreferredWidth(120); // Subtotal
        
        // Agregar listener para clicks en los botones de cantidad
        tablaCarrito.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = tablaCarrito.columnAtPoint(e.getPoint());
                int row = tablaCarrito.rowAtPoint(e.getPoint());
                
                if (column == 2 && row >= 0) { // Columna de cantidad
                    Rectangle cellRect = tablaCarrito.getCellRect(row, column, false);
                    int clickX = e.getX() - cellRect.x;
                    int cellWidth = cellRect.width;
                    
                    // Los elementos están: [cantidad][▲][▼] alineados a la derecha
                    // Calculamos las posiciones desde la derecha
                    int botonWidth = 24;
                    int botonMenosStart = cellWidth - botonWidth - 3;
                    int botonMasStart = botonMenosStart - botonWidth - 3;
                    
                    if (clickX >= botonMasStart && clickX < botonMenosStart) { // Botón aumentar (▲)
                        aumentarCantidadEnFila(row);
                    } else if (clickX >= botonMenosStart) { // Botón disminuir (▼)
                        disminuirCantidadEnFila(row);
                    }
                }
            }
        });
    }
    
    // Renderer personalizado para mostrar cantidad con flechas
    private class CantidadCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 5));
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            
            // Label con la cantidad
            JLabel lblCantidad = new JLabel(value != null ? value.toString() : "0");
            lblCantidad.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblCantidad.setHorizontalAlignment(SwingConstants.CENTER);
            lblCantidad.setPreferredSize(new Dimension(35, 28));
            
            // Botón disminuir (▼)
            JLabel btnMenos = new JLabel("▼");
            btnMenos.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnMenos.setForeground(Color.WHITE);
            btnMenos.setHorizontalAlignment(SwingConstants.CENTER);
            btnMenos.setPreferredSize(new Dimension(24, 24));
            btnMenos.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnMenos.setOpaque(true);
            btnMenos.setBackground(new Color(239, 68, 68));
            btnMenos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 38, 38), 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            
            // Botón aumentar (▲)
            JLabel btnMas = new JLabel("▲");
            btnMas.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnMas.setForeground(Color.WHITE);
            btnMas.setHorizontalAlignment(SwingConstants.CENTER);
            btnMas.setPreferredSize(new Dimension(24, 24));
            btnMas.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnMas.setOpaque(true);
            btnMas.setBackground(new Color(59, 130, 246));
            btnMas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(37, 99, 235), 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            
            panel.add(lblCantidad);
            panel.add(btnMas);
            panel.add(btnMenos);
            
            return panel;
        }
    }
    
    // ========== MÉTODOS DE LÓGICA ==========
    
    private void cargarCaja() {
        try {
            // Intentar cargar la Caja #1
            cajaActual = controladorCaja.buscarCaja(1L);
            
            if (cajaActual == null) {
                // No existe, crear la Caja #1 en la base de datos
                cajaActual = controladorCaja.crearCaja(1, BigDecimal.ZERO);
            }
            
            actualizarEstadoCaja();
        } catch (Exception e) {
            mostrarError("Error al cargar la caja: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void actualizarEstadoCaja() {
        if (cajaActual != null && cajaActual.estaAbierta()) {
            labelEstadoCaja.setText("Caja: Abierta");
            labelEstadoCaja.setForeground(new Color(22, 163, 74)); // Verde
            botonAbrirCaja.setText("Cerrar Caja");
            botonAbrirCaja.setBackground(new Color(220, 38, 38)); // Rojo
            actualizarSaldoCaja();
        } else {
            labelEstadoCaja.setText("Caja: Cerrada");
            labelEstadoCaja.setForeground(new Color(220, 38, 38)); // Rojo
            botonAbrirCaja.setText("Abrir Caja");
            botonAbrirCaja.setBackground(new Color(16, 185, 129)); // Verde
            labelSaldoCaja.setText("Saldo en caja: $0.00");
            labelSaldoCaja.setForeground(new Color(100, 116, 139));
        }
    }
    
    private void actualizarSaldoCaja() {
        if (cajaActual != null && cajaActual.estaAbierta()) {
            BigDecimal saldo = cajaActual.getSaldoActual();
            labelSaldoCaja.setText(String.format("Saldo en caja: $%.2f", saldo));
            labelSaldoCaja.setForeground(new Color(37, 99, 235)); // Azul
            labelSaldoCaja.setFont(new Font("Segoe UI", Font.BOLD, 15));
        } else {
            labelSaldoCaja.setText("Saldo en caja: $0.00");
            labelSaldoCaja.setForeground(new Color(100, 116, 139));
            labelSaldoCaja.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
    }
    
    private void abrirCaja() {
        try {
            if (cajaActual == null) {
                mostrarError("No hay una caja cargada");
                return;
            }
            
            if (cajaActual.estaAbierta()) {
                // Cerrar caja
                int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de que desea cerrar la caja?",
                    "Cerrar Caja",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (confirmacion == JOptionPane.YES_OPTION) {
                    controladorCaja.cerrarCaja(cajaActual);
                    
                    // Recargar la caja desde la BD para tener el estado actualizado
                    cajaActual = controladorCaja.buscarCaja(1L);
                    
                    actualizarEstadoCaja();
                    mostrarExito("Caja cerrada correctamente");
                }
            } else {
                // Abrir caja - Solicitar saldo inicial del día
                String saldoStr = JOptionPane.showInputDialog(
                    this,
                    "Ingrese el saldo inicial de la caja:",
                    "1000.00"
                );
                
                if (saldoStr != null && !saldoStr.trim().isEmpty()) {
                    BigDecimal saldoInicial = new BigDecimal(saldoStr.trim());
                    
                    // Abrir la caja con el saldo inicial
                    cajaActual.setSaldoInicial(saldoInicial);
                    controladorCaja.abrirCaja(cajaActual);
                    
                    // Recargar la caja desde la BD para tener el estado actualizado
                    cajaActual = controladorCaja.buscarCaja(1L);
                    
                    actualizarEstadoCaja();
                    mostrarExito(String.format("Caja abierta con saldo inicial: $%.2f", saldoInicial));
                }
            }
            
        } catch (NumberFormatException e) {
            mostrarError("El saldo inicial debe ser un número válido");
        } catch (Exception e) {
            mostrarError("Error al abrir/cerrar la caja: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void aumentarCantidadEnFila(int fila) {
        try {
            Long codigoProducto = (Long) modeloCarrito.getValueAt(fila, 0);
            Producto producto = controladorProducto.buscarProducto(codigoProducto);
            
            if (producto != null) {
                // Agregar una unidad más al carrito
                controladorVentas.agregarProductoAlCarrito(producto);
                actualizarTablaCarrito();
                actualizarTotales();
            }
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al aumentar cantidad: " + e.getMessage());
        }
    }
    
    private void disminuirCantidadEnFila(int fila) {
        try {
            Long codigoProducto = (Long) modeloCarrito.getValueAt(fila, 0);
            Producto producto = controladorProducto.buscarProducto(codigoProducto);
            
            if (producto != null) {
                // Remover una unidad del carrito
                controladorVentas.removerProductoDeCarrito(producto);
                actualizarTablaCarrito();
                actualizarTotales();
            }
        } catch (Exception e) {
            mostrarError("Error al disminuir cantidad: " + e.getMessage());
        }
    }
    
    private void agregarProductoAlCarrito() {
        try {
            String codigoStr = campoCodigoBarras.getText().trim();
            String cantidadStr = campoCantidad.getText().trim();
            
            if (codigoStr.isEmpty()) {
                mostrarAdvertencia("Por favor, ingrese un código de barras");
                return;
            }
            
            if (cantidadStr.isEmpty()) {
                cantidadStr = "1";
            }
            
            Long codigo = Long.parseLong(codigoStr);
            int cantidad = Integer.parseInt(cantidadStr);
            
            if (cantidad <= 0) {
                mostrarAdvertencia("La cantidad debe ser mayor a 0");
                return;
            }
            
            // Buscar el producto
            Producto producto = controladorProducto.buscarProducto(codigo);
            
            if (producto == null) {
                mostrarError("Producto no encontrado con código: " + codigo);
                return;
            }
            
            // Agregar al carrito con la cantidad especificada
            controladorVentas.agregarProductoAlCarrito(producto, cantidad);
            
            actualizarTablaCarrito();
            actualizarTotales();
            
            // Limpiar campos
            campoCodigoBarras.setText("");
            campoCantidad.setText("1");
            campoCodigoBarras.requestFocus();
            
            mostrarExito("Producto agregado al carrito");
            
        } catch (NumberFormatException e) {
            mostrarError("Código o cantidad inválidos");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al agregar producto: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void removerProductoDelCarrito() {
        try {
            int filaSeleccionada = tablaCarrito.getSelectedRow();
            
            if (filaSeleccionada == -1) {
                mostrarAdvertencia("Por favor, seleccione un producto del carrito");
                return;
            }
            
            Long codigoProducto = (Long) modeloCarrito.getValueAt(filaSeleccionada, 0);
            
            // Buscar el producto
            Producto producto = controladorProducto.buscarProducto(codigoProducto);
            
            if (producto != null) {
                controladorVentas.removerProductoDeCarrito(producto);
                actualizarTablaCarrito();
                actualizarTotales();
                mostrarExito("Producto removido del carrito");
            }
            
        } catch (Exception e) {
            mostrarError("Error al remover producto: " + e.getMessage());
        }
    }
    
    private void limpiarCarrito() {
        try {
            int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de que desea limpiar el carrito?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                controladorVentas.limpiarCarrito();
                actualizarTablaCarrito();
                actualizarTotales();
                mostrarExito("Carrito limpiado");
            }
            
        } catch (Exception e) {
            mostrarError("Error al limpiar carrito: " + e.getMessage());
        }
    }
    
    private void confirmarVenta() {
        try {
            List<DetalleVenta> carrito = controladorVentas.obtenerCarrito();
            
            if (carrito.isEmpty()) {
                mostrarAdvertencia("El carrito está vacío");
                return;
            }
            
            if (cajaActual == null || !cajaActual.estaAbierta()) {
                mostrarError("La caja debe estar abierta para realizar ventas");
                return;
            }
            
            BigDecimal total = controladorVentas.calcularTotalCarrito();
            
            // Preguntar método de pago
            Object[] opciones = {"Efectivo", "Débito", "Cancelar"};
            int metodoPagoSeleccion = JOptionPane.showOptionDialog(
                this,
                String.format("Total a cobrar: $%.2f\n\n¿Método de pago?", total),
                "Seleccionar Método de Pago",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
            );
            
            // Si el usuario cancela, salir
            if (metodoPagoSeleccion == 2 || metodoPagoSeleccion == JOptionPane.CLOSED_OPTION) {
                return;
            }
            
            boolean esEfectivo = (metodoPagoSeleccion == 0);
            Venta.MetodoPago metodoPago = esEfectivo ? Venta.MetodoPago.EFECTIVO : Venta.MetodoPago.DEBITO;
            
            BigDecimal montoPagado;
            BigDecimal vuelto;
            
            if (esEfectivo) {
                // Pago en efectivo: solicitar el monto con el que paga el cliente
                String montoPagadoStr = JOptionPane.showInputDialog(
                    this,
                    String.format("Total a cobrar: $%.2f\nIngrese el monto con el que paga el cliente:", total),
                    "Pago en Efectivo",
                    JOptionPane.QUESTION_MESSAGE
                );
                
                // Si el usuario cancela, salir
                if (montoPagadoStr == null || montoPagadoStr.trim().isEmpty()) {
                    return;
                }
                
                try {
                    montoPagado = new BigDecimal(montoPagadoStr.trim());
                    
                    // Validar que el monto pagado sea suficiente
                    if (montoPagado.compareTo(total) < 0) {
                        mostrarError("El monto pagado es insuficiente");
                        return;
                    }
                    
                    // Calcular el vuelto
                    vuelto = montoPagado.subtract(total);
                    
                } catch (NumberFormatException e) {
                    mostrarError("El monto ingresado no es válido");
                    return;
                }
            } else {
                // Pago con débito: monto exacto, sin vuelto
                montoPagado = total;
                vuelto = BigDecimal.ZERO;
            }
            
            // Mostrar confirmación
            String mensaje;
            if (esEfectivo) {
                mensaje = String.format(
                    "Método de pago: EFECTIVO\n\nTotal: $%.2f\nMonto pagado: $%.2f\nVuelto: $%.2f\n\n¿Confirmar venta?",
                    total, montoPagado, vuelto
                );
            } else {
                mensaje = String.format(
                    "Método de pago: DÉBITO\n\nTotal: $%.2f\n\n¿Confirmar venta?\n\n(No se registrará dinero físico en la caja)",
                    total
                );
            }
            
            int confirmacion = JOptionPane.showConfirmDialog(
                this,
                mensaje,
                "Confirmar Venta",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
                if (confirmacion == JOptionPane.YES_OPTION) {
                    // Confirmar la venta con el monto pagado y método de pago
                    controladorVentas.confirmarVenta(cajaActual, montoPagado, metodoPago);
                    
                    // Recargar la caja para actualizar el saldo
                    cajaActual = controladorCaja.buscarCaja(cajaActual.getId());
                    actualizarSaldoCaja();
                    
                    // Mostrar mensaje de éxito
                    String mensajeExito;
                    if (esEfectivo) {
                        mensajeExito = String.format(
                            "¡Venta confirmada!\n\nMétodo: EFECTIVO\nTotal: $%.2f\nMonto pagado: $%.2f\nVuelto a entregar: $%.2f",
                            total, montoPagado, vuelto
                        );
                    } else {
                        mensajeExito = String.format(
                            "¡Venta confirmada!\n\nMétodo: DÉBITO\nTotal: $%.2f",
                            total
                        );
                    }
                    
                    mostrarExito(mensajeExito);
                    
                    actualizarTablaCarrito();
                    actualizarTotales();
                    campoCodigoBarras.requestFocus();
                }
            
        } catch (Exception e) {
            mostrarError("Error al confirmar venta: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void actualizarTablaCarrito() {
        // Limpiar tabla
        modeloCarrito.setRowCount(0);
        
        try {
            List<DetalleVenta> carrito = controladorVentas.obtenerCarrito();
            
            for (DetalleVenta detalle : carrito) {
                Object[] fila = {
                    detalle.getProducto().getId(),
                    detalle.getProducto().getNombre(),
                    detalle.getCantidad(),
                    String.format("$%.2f", detalle.getPrecioUnitario()),
                    String.format("$%.2f", detalle.getSubtotal())
                };
                modeloCarrito.addRow(fila);
            }
            
        } catch (Exception e) {
            mostrarError("Error al actualizar carrito: " + e.getMessage());
        }
    }
    
    private void actualizarTotales() {
        try {
            BigDecimal total = controladorVentas.calcularTotalCarrito();
            List<DetalleVenta> carrito = controladorVentas.obtenerCarrito();
            
            int cantidadTotal = carrito.stream()
                .mapToInt(DetalleVenta::getCantidad)
                .sum();
            
            labelTotal.setText(String.format("Total: $%.2f", total));
            labelCantidadProductos.setText(String.format("Productos: %d", cantidadTotal));
            
        } catch (Exception e) {
            labelTotal.setText("Total: $0.00");
            labelCantidadProductos.setText("Productos: 0");
        }
    }
    
    // ========== MÉTODOS DE UI ==========
    
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
}

