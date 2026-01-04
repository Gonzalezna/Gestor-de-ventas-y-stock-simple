package com.fortunato.sistema.presentacion;

import com.fortunato.sistema.entidad.Producto;
import com.fortunato.sistema.interfaz.IControladorProducto;
import com.fortunato.sistema.interfaz.Factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Diálogo para confirmar el borrado de productos
 */
public class DialogoConfirmarBorrado extends JFrame {
    
    private IControladorProducto controladorProducto;
    private JButton botonConfirmar;
    private ProductoPanel panelPrincipal;
    private Producto productoABorrar;
    
    public DialogoConfirmarBorrado(ProductoPanel panelPrincipal, Producto producto) {
        this.panelPrincipal = panelPrincipal;
        this.productoABorrar = producto;
        this.controladorProducto = Factory.getInstancia().getControladorProducto();
        
        inicializarComponentes();
        configurarVentana();
        configurarLayout();
    }
    
    private void inicializarComponentes() {
        // Botón
        botonConfirmar = crearBoton("Confirmar", new Color(220, 38, 38), e -> confirmarBorrado());
    }
    
    private void configurarVentana() {
        setTitle("Confirmar Borrado de Producto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Configurar tamaño de la ventana
        setPreferredSize(new Dimension(600, 400));
        setMinimumSize(new Dimension(600, 400));
        setMaximumSize(new Dimension(600, 400));
        pack();
        
        // Centrar la ventana en la pantalla
        setLocationRelativeTo(null);
    }
    
    private void configurarLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));
        
        // Panel superior con título y descripción
        JPanel panelTitulo = crearPanelTitulo();
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con información del producto y campo de confirmación
        JPanel panelCentral = crearPanelCentral();
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel titulo = new JLabel("Confirmar Borrado de Producto");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(220, 38, 38)); // Color rojo para indicar peligro
        panel.add(titulo);
        
        return panel;
    }
    
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        // Panel para el contenido principal
        JPanel contenidoPanel = new JPanel(new BorderLayout());
        contenidoPanel.setBackground(Color.WHITE);
        
        // Pregunta principal
        JLabel etiquetaInfo = new JLabel("¿Está seguro que desea eliminar el siguiente producto?");
        etiquetaInfo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        etiquetaInfo.setForeground(new Color(30, 41, 59));
        etiquetaInfo.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        contenidoPanel.add(etiquetaInfo, BorderLayout.NORTH);
        
        // Detalles del producto (solo ID y nombre)
        String detallesProducto = String.format(
            "<html>" +
            "<div style='font-size: 14px; line-height: 1.6;'>" +
            "<b>ID:</b> %d<br>" +
            "<b>Nombre:</b> %s" +
            "</div>" +
            "</html>",
            productoABorrar.getId(),
            productoABorrar.getNombre()
        );
        JLabel etiquetaDetalles = new JLabel(detallesProducto);
        etiquetaDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        etiquetaDetalles.setForeground(new Color(71, 85, 105));
        etiquetaDetalles.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        contenidoPanel.add(etiquetaDetalles, BorderLayout.CENTER);
        
        panel.add(contenidoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        panel.add(botonConfirmar);
        
        return panel;
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
                if (color.equals(new Color(220, 38, 38))) {
                    boton.setBackground(new Color(185, 28, 28)); // Rojo más oscuro
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
    
    private void confirmarBorrado() {
        try {
            controladorProducto.eliminarProducto(productoABorrar.getId());
            mostrarExito("Producto eliminado exitosamente");
            dispose(); // Cerrar la ventana
            
            // Actualizar la tabla en el panel principal
            if (panelPrincipal != null) {
                panelPrincipal.actualizarTabla();
            }
        } catch (Exception e) {
            System.err.println("ERROR al eliminar producto: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al eliminar producto: " + e.getMessage());
        }
    }
    
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
