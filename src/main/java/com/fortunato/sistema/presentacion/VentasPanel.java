package com.fortunato.sistema.presentacion;

import com.fortunato.sistema.entidad.Venta;
import com.fortunato.sistema.entidad.DetalleVenta;
import com.fortunato.sistema.entidad.Producto;
import com.fortunato.sistema.interfaz.IControladorVentas;
import com.fortunato.sistema.interfaz.IControladorProducto;
import com.fortunato.sistema.interfaz.Factory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel para consultar el historial de ventas
 */
public class VentasPanel extends JPanel {
    
    private IControladorVentas controladorVentas;
    private IControladorProducto controladorProducto;
    
    private JTable tablaVentas;
    private DefaultTableModel modeloTabla;
    
    // Filtros
    private JTextField campoIdVenta;
    private JTextField campoFechaDesde;
    private JTextField campoFechaHasta;
    private JComboBox<String> comboProducto;
    private JComboBox<String> comboMetodoPago;
    
    private JButton botonBuscar;
    private JButton botonLimpiar;
    private JButton botonVerDetalles;
    
    private JLabel labelTotalVentas;
    private JLabel labelMontoTotal;
    
    public VentasPanel() {
        this.controladorVentas = Factory.getInstancia().getControladorVentas();
        this.controladorProducto = Factory.getInstancia().getControladorProducto();
        
        inicializarComponentes();
        configurarLayout();
        cargarDatos();
    }
    
    private void inicializarComponentes() {
        // Campos de filtro
        campoIdVenta = crearCampoTexto("ID de Venta");
        campoFechaDesde = crearCampoTexto("dd/MM/yyyy");
        campoFechaHasta = crearCampoTexto("dd/MM/yyyy");
        
        // Combo de productos
        comboProducto = new JComboBox<>();
        comboProducto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboProducto.addItem("Todos los productos");
        cargarProductosEnCombo();
        
        // Combo de método de pago
        comboMetodoPago = new JComboBox<>();
        comboMetodoPago.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboMetodoPago.addItem("Todos los métodos");
        comboMetodoPago.addItem("Efectivo");
        comboMetodoPago.addItem("Débito");
        
        // Botones
        botonBuscar = crearBoton("Buscar", new Color(37, 99, 235), e -> buscarVentas());
        botonLimpiar = crearBoton("Limpiar Filtros", new Color(100, 116, 139), e -> limpiarFiltros());
        botonVerDetalles = crearBoton("Ver Detalles", new Color(22, 163, 74), e -> verDetallesVenta());
        
        // Labels de resumen
        labelTotalVentas = crearLabelInfo("Total de ventas: 0");
        labelMontoTotal = crearLabelInfo("Monto total: $0.00");
        
        // Tabla de ventas
        crearTablaVentas();
    }
    
    private void configurarLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(241, 245, 249));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior con título y filtros
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        panelSuperior.setBackground(new Color(241, 245, 249));
        
        panelSuperior.add(crearPanelTitulo(), BorderLayout.NORTH);
        panelSuperior.add(crearPanelFiltros(), BorderLayout.CENTER);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central con tabla
        add(crearPanelTabla(), BorderLayout.CENTER);
        
        // Panel inferior con resumen
        add(crearPanelResumen(), BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titulo = new JLabel("Historial de Ventas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(30, 41, 59));
        
        panel.add(titulo, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Título de filtros
        JLabel tituloFiltros = new JLabel("Filtros de Búsqueda");
        tituloFiltros.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tituloFiltros.setForeground(new Color(30, 41, 59));
        tituloFiltros.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(tituloFiltros);
        panel.add(Box.createVerticalStrut(15));
        
        // Primera fila de filtros
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        fila1.setBackground(Color.WHITE);
        fila1.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        fila1.add(crearCampoConLabel("ID Venta:", campoIdVenta, 150));
        fila1.add(crearCampoConLabel("Fecha Desde:", campoFechaDesde, 150));
        fila1.add(crearCampoConLabel("Fecha Hasta:", campoFechaHasta, 150));
        
        panel.add(fila1);
        panel.add(Box.createVerticalStrut(15));
        
        // Segunda fila de filtros
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        fila2.setBackground(Color.WHITE);
        fila2.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        fila2.add(crearComboConLabel("Producto:", comboProducto, 250));
        fila2.add(crearComboConLabel("Método de Pago:", comboMetodoPago, 180));
        
        panel.add(fila2);
        panel.add(Box.createVerticalStrut(20));
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panelBotones.add(botonBuscar);
        panelBotones.add(botonLimpiar);
        panelBotones.add(botonVerDetalles);
        
        panel.add(panelBotones);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titulo = new JLabel("Resultados");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(new Color(30, 41, 59));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(titulo, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(tablaVentas);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelResumen() {
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
        
        // Total de ventas (izquierda)
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        labelTotalVentas.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(labelTotalVentas, gbc);
        
        // Monto total (derecha)
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        labelMontoTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelMontoTotal.setForeground(new Color(22, 163, 74));
        panel.add(labelMontoTotal, gbc);
        
        return panel;
    }
    
    private JPanel crearCampoConLabel(String labelText, JTextField campo, int ancho) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(30, 41, 59));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(ancho, 35));
        campo.setPreferredSize(new Dimension(ancho, 35));
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(campo);
        
        return panel;
    }
    
    private JPanel crearComboConLabel(String labelText, JComboBox<String> combo, int ancho) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(30, 41, 59));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        combo.setMaximumSize(new Dimension(ancho, 35));
        combo.setPreferredSize(new Dimension(ancho, 35));
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(combo);
        
        return panel;
    }
    
    private JTextField crearCampoTexto(String placeholder) {
        JTextField campo = new JTextField(20);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        campo.setBackground(Color.WHITE);
        campo.setToolTipText(placeholder);
        return campo;
    }
    
    private JButton crearBoton(String texto, Color color, java.awt.event.ActionListener listener) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        
        boton.setPreferredSize(new Dimension(150, 38));
        boton.setMinimumSize(new Dimension(150, 38));
        boton.setMaximumSize(new Dimension(150, 38));
        
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
    
    private JLabel crearLabelInfo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(100, 116, 139));
        return label;
    }
    
    private void crearTablaVentas() {
        String[] columnas = {"ID", "Número Venta", "Fecha", "Total", "Método Pago", "Estado", "Productos"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaVentas = new JTable(modeloTabla);
        tablaVentas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaVentas.setRowHeight(35);
        tablaVentas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaVentas.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaVentas.getTableHeader().setForeground(new Color(30, 41, 59));
        tablaVentas.setSelectionBackground(new Color(219, 234, 254));
        tablaVentas.setSelectionForeground(new Color(30, 41, 59));
        tablaVentas.setGridColor(new Color(226, 232, 240));
        
        // Alinear columnas numéricas a la derecha
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaVentas.getColumnModel().getColumn(0).setCellRenderer(rightRenderer); // ID
        tablaVentas.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Total
        
        // Centrar algunas columnas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tablaVentas.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Método
        tablaVentas.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Estado
        tablaVentas.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Productos
        
        // Ajustar anchos de columnas
        tablaVentas.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        tablaVentas.getColumnModel().getColumn(1).setPreferredWidth(150); // Número
        tablaVentas.getColumnModel().getColumn(2).setPreferredWidth(150); // Fecha
        tablaVentas.getColumnModel().getColumn(3).setPreferredWidth(100); // Total
        tablaVentas.getColumnModel().getColumn(4).setPreferredWidth(100); // Método
        tablaVentas.getColumnModel().getColumn(5).setPreferredWidth(100); // Estado
        tablaVentas.getColumnModel().getColumn(6).setPreferredWidth(80);  // Productos
    }
    
    // ========== MÉTODOS DE LÓGICA ==========
    
    private void cargarProductosEnCombo() {
        try {
            List<Producto> productos = controladorProducto.listarProductos();
            for (Producto p : productos) {
                comboProducto.addItem(p.getId() + " - " + p.getNombre());
            }
        } catch (Exception e) {
            mostrarError("Error al cargar productos: " + e.getMessage());
        }
    }
    
    private void cargarDatos() {
        try {
            List<Venta> ventas = controladorVentas.buscarTodasLasVentas();
            mostrarVentasEnTabla(ventas);
        } catch (Exception e) {
            mostrarError("Error al cargar ventas: " + e.getMessage());
        }
    }
    
    private void buscarVentas() {
        try {
            String idVentaStr = campoIdVenta.getText().trim();
            String fechaDesdeStr = campoFechaDesde.getText().trim();
            String fechaHastaStr = campoFechaHasta.getText().trim();
            String productoSeleccionado = (String) comboProducto.getSelectedItem();
            String metodoPagoSeleccionado = (String) comboMetodoPago.getSelectedItem();
            
            List<Venta> ventas = null;
            
            // Filtro por ID
            if (!idVentaStr.isEmpty()) {
                try {
                    Long id = Long.parseLong(idVentaStr);
                    Venta venta = controladorVentas.buscarVentaPorId(id);
                    ventas = venta != null ? List.of(venta) : List.of();
                } catch (NumberFormatException e) {
                    mostrarError("El ID debe ser un número válido");
                    return;
                }
            }
            // Filtro por fechas
            else if (!fechaDesdeStr.isEmpty() && !fechaHastaStr.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                try {
                    LocalDate fechaDesde = LocalDate.parse(fechaDesdeStr, formatter);
                    LocalDate fechaHasta = LocalDate.parse(fechaHastaStr, formatter);
                    
                    LocalDateTime desde = fechaDesde.atStartOfDay();
                    LocalDateTime hasta = fechaHasta.atTime(LocalTime.MAX);
                    
                    ventas = controladorVentas.buscarVentasPorFechas(desde, hasta);
                } catch (Exception e) {
                    mostrarError("Formato de fecha inválido. Use dd/MM/yyyy");
                    return;
                }
            }
            // Filtro por producto
            else if (productoSeleccionado != null && !productoSeleccionado.equals("Todos los productos")) {
                Long idProducto = Long.parseLong(productoSeleccionado.split(" - ")[0]);
                ventas = controladorVentas.buscarVentasPorProducto(idProducto);
            }
            // Filtro por método de pago
            else if (metodoPagoSeleccionado != null && !metodoPagoSeleccionado.equals("Todos los métodos")) {
                Venta.MetodoPago metodo = metodoPagoSeleccionado.equals("Efectivo") 
                    ? Venta.MetodoPago.EFECTIVO 
                    : Venta.MetodoPago.DEBITO;
                ventas = controladorVentas.buscarVentasPorMetodoPago(metodo);
            }
            // Sin filtros: mostrar todas
            else {
                ventas = controladorVentas.buscarTodasLasVentas();
            }
            
            mostrarVentasEnTabla(ventas);
            
        } catch (Exception e) {
            mostrarError("Error al buscar ventas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void mostrarVentasEnTabla(List<Venta> ventas) {
        modeloTabla.setRowCount(0);
        
        BigDecimal montoTotal = BigDecimal.ZERO;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Venta venta : ventas) {
            Object[] fila = {
                venta.getId(),
                venta.getNumeroVenta(),
                venta.getFecha().format(formatter),
                String.format("$%.2f", venta.getTotal()),
                venta.getMetodoPago().toString(),
                venta.getEstado().toString(),
                venta.getCantidadTotalProductos()
            };
            modeloTabla.addRow(fila);
            montoTotal = montoTotal.add(venta.getTotal());
        }
        
        // Actualizar resumen
        labelTotalVentas.setText(String.format("Total de ventas: %d", ventas.size()));
        labelMontoTotal.setText(String.format("Monto total: $%.2f", montoTotal));
    }
    
    private void limpiarFiltros() {
        campoIdVenta.setText("");
        campoFechaDesde.setText("");
        campoFechaHasta.setText("");
        comboProducto.setSelectedIndex(0);
        comboMetodoPago.setSelectedIndex(0);
        cargarDatos();
    }
    
    private void verDetallesVenta() {
        int filaSeleccionada = tablaVentas.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            mostrarAdvertencia("Por favor, seleccione una venta de la tabla");
            return;
        }
        
        try {
            Long idVenta = (Long) modeloTabla.getValueAt(filaSeleccionada, 0);
            Venta venta = controladorVentas.buscarVentaPorId(idVenta);
            
            if (venta != null) {
                mostrarDialogoDetalles(venta);
            }
            
        } catch (Exception e) {
            mostrarError("Error al cargar detalles: " + e.getMessage());
        }
    }
    
    private void mostrarDialogoDetalles(Venta venta) {
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Detalles de Venta", true);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.setSize(700, 500);
        dialogo.setLocationRelativeTo(this);
        
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelContenido.setBackground(Color.WHITE);
        
        // Información general
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        panelContenido.add(crearLabelDetalle("ID Venta:", venta.getId().toString()));
        panelContenido.add(crearLabelDetalle("Número:", venta.getNumeroVenta()));
        panelContenido.add(crearLabelDetalle("Fecha:", venta.getFecha().format(formatter)));
        panelContenido.add(crearLabelDetalle("Estado:", venta.getEstado().toString()));
        panelContenido.add(crearLabelDetalle("Método de Pago:", venta.getMetodoPago().toString()));
        panelContenido.add(crearLabelDetalle("Monto Pagado:", String.format("$%.2f", venta.getMontoPagado())));
        panelContenido.add(crearLabelDetalle("Vuelto:", String.format("$%.2f", venta.getVuelto())));
        panelContenido.add(Box.createVerticalStrut(10));
        
        // Tabla de productos
        String[] columnas = {"Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        DefaultTableModel modeloDetalles = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (DetalleVenta detalle : venta.getDetalles()) {
            Object[] fila = {
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                String.format("$%.2f", detalle.getPrecioUnitario()),
                String.format("$%.2f", detalle.getSubtotal())
            };
            modeloDetalles.addRow(fila);
        }
        
        JTable tablaDetalles = new JTable(modeloDetalles);
        tablaDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaDetalles.setRowHeight(30);
        tablaDetalles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(tablaDetalles);
        scrollPane.setPreferredSize(new Dimension(650, 200));
        panelContenido.add(scrollPane);
        
        panelContenido.add(Box.createVerticalStrut(10));
        panelContenido.add(crearLabelDetalle("TOTAL:", String.format("$%.2f", venta.getTotal()), true));
        
        dialogo.add(new JScrollPane(panelContenido), BorderLayout.CENTER);
        
        // Botón cerrar
        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(Color.WHITE);
        JButton botonCerrar = crearBoton("Cerrar", new Color(100, 116, 139), e -> dialogo.dispose());
        panelBoton.add(botonCerrar);
        dialogo.add(panelBoton, BorderLayout.SOUTH);
        
        dialogo.setVisible(true);
    }
    
    private JPanel crearLabelDetalle(String etiqueta, String valor) {
        return crearLabelDetalle(etiqueta, valor, false);
    }
    
    private JPanel crearLabelDetalle(String etiqueta, String valor, boolean esTotal) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel labelEtiqueta = new JLabel(etiqueta);
        labelEtiqueta.setFont(new Font("Segoe UI", Font.BOLD, esTotal ? 16 : 13));
        labelEtiqueta.setForeground(new Color(30, 41, 59));
        labelEtiqueta.setPreferredSize(new Dimension(150, 25));
        
        JLabel labelValor = new JLabel(valor);
        labelValor.setFont(new Font("Segoe UI", Font.PLAIN, esTotal ? 16 : 13));
        labelValor.setForeground(esTotal ? new Color(22, 163, 74) : new Color(71, 85, 105));
        
        panel.add(labelEtiqueta);
        panel.add(labelValor);
        
        return panel;
    }
    
    public void recargarTabla() {
        cargarDatos();
    }
    
    // ========== MÉTODOS DE UI ==========
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
}

