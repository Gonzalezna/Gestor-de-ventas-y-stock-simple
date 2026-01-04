package com.fortunato.sistema.presentacion;

import javax.swing.*;
import java.awt.*;

/**
 * Aplicación principal con interfaz gráfica Swing
 * Sistema de Gestión de Kiosco
 */
public class SwingApp extends JFrame {
    
    // Paleta de colores
    public static final Color COLOR_PRIMARIO = new Color(46, 134, 171); // #2E86AB
    public static final Color COLOR_SECUNDARIO = new Color(244, 244, 244); // #F4F4F4
    public static final Color COLOR_EXITO = new Color(40, 167, 69); // #28A745
    public static final Color COLOR_ERROR = new Color(220, 53, 69); // #DC3545
    public static final Color COLOR_TEXTO = new Color(51, 51, 51); // #333333
    public static final Color COLOR_FONDO = Color.WHITE;
    
    public SwingApp() {
        inicializarInterfaz();
        configurarVentana();
        agregarComponentes();
    }
    
    private void inicializarInterfaz() {
        // Configurar colores globales
        UIManager.put("Panel.background", COLOR_FONDO);
        UIManager.put("Button.background", COLOR_PRIMARIO);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("Label.foreground", COLOR_TEXTO);
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 12));
    }
    
    private void configurarVentana() {
        setTitle("Kiosco - Fortunato");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Maximizar la ventana al abrir
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    
    private void agregarComponentes() {
        // Panel principal con BorderLayout
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_FONDO);
        
        // Header con título
        panelPrincipal.add(crearHeader(), BorderLayout.NORTH);
        
        // Panel central con pestañas
        panelPrincipal.add(crearPanelCentral(), BorderLayout.CENTER);
        
        add(panelPrincipal);
    }
    
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARIO);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Título principal
        JLabel titulo = new JLabel("Sistema de Gestión - Fortunato");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.LEFT);
        
        JPanel tituloPanel = new JPanel(new BorderLayout());
        tituloPanel.setOpaque(false);
        tituloPanel.add(titulo, BorderLayout.CENTER);
        
        header.add(tituloPanel, BorderLayout.WEST);
        
        // Logo o información adicional
        JLabel version = new JLabel("v1.0");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setForeground(new Color(255, 255, 255, 150));
        header.add(version, BorderLayout.EAST);
        
        return header;
    }
    
    private JComponent crearPanelCentral() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(COLOR_FONDO);
        
        // Crear paneles
        ProductoPanel productoPanel = new ProductoPanel();
        ProveedorPanel proveedorPanel = new ProveedorPanel();
        CajaPanel cajaPanel = new CajaPanel();
        VentasPanel ventasPanel = new VentasPanel();
        
        // Pestaña de Productos
        tabbedPane.addTab("Productos", null, productoPanel, "Gestionar productos del kiosco");
        
        // Pestaña de Proveedores
        tabbedPane.addTab("Proveedores", null, proveedorPanel, "Gestionar proveedores del kiosco");
        
        // Pestaña de Caja (Punto de Venta)
        tabbedPane.addTab("Caja", null, cajaPanel, "Punto de venta y carrito");
        
        // Pestaña de Ventas (Historial)
        tabbedPane.addTab("Ventas", null, ventasPanel, "Historial y reportes de ventas");
        
        // Listener para recargar datos cuando cambias de pestaña
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            String selectedTitle = tabbedPane.getTitleAt(selectedIndex);
            
            // Recargar tabla de productos cuando se selecciona esa pestaña
            if (selectedTitle.equals("Productos")) {
                productoPanel.recargarTabla();
            }
            // Recargar tabla de proveedores cuando se selecciona esa pestaña
            else if (selectedTitle.equals("Proveedores")) {
                proveedorPanel.recargarTabla();
            }
            // Recargar tabla de ventas cuando se selecciona esa pestaña
            else if (selectedTitle.equals("Ventas")) {
                ventasPanel.recargarTabla();
            }
        });
        
        return tabbedPane;
    }
    
    
    public static void main(String[] args) {
        // Configurar Swing para mejor rendimiento
        SwingUtilities.invokeLater(() -> {
            try {
                new SwingApp().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Error al iniciar la aplicación: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
