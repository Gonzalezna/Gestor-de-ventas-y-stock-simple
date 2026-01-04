package com.fortunato.sistema.presentacion;

import com.fortunato.sistema.entidad.Proveedor;
import com.fortunato.sistema.interfaz.IControladorProveedor;
import com.fortunato.sistema.interfaz.Factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Diálogo para actualizar proveedores
 */
public class DialogoActualizarProveedor extends JFrame {
    
    private IControladorProveedor controladorProveedor;
    private JTextField campoNombre, campoTelefono, campoMail;
    private JComboBox<String> campoActivo;
    private JButton botonActualizar;
    private ProveedorPanel panelPrincipal; // Referencia al panel principal para actualizar la tabla
    private Proveedor proveedorOriginal; // Proveedor que se está actualizando
    
    public DialogoActualizarProveedor(ProveedorPanel panelPrincipal, Proveedor proveedor) {
        this.panelPrincipal = panelPrincipal;
        this.proveedorOriginal = proveedor;
        this.controladorProveedor = Factory.getInstancia().getControladorProveedor();
        
        inicializarComponentes();
        configurarVentana();
        configurarLayout();
        cargarDatosProveedor();
    }
    
    private void inicializarComponentes() {
        // Campos de texto
        campoNombre = crearCampoTexto("Nombre del proveedor");
        campoTelefono = crearCampoTexto("Teléfono");
        campoMail = crearCampoTexto("Email");
        
        // Campo activo
        campoActivo = crearCampoActivo();
        
        // Botón
        botonActualizar = crearBoton("Actualizar", new Color(100, 116, 139), e -> actualizarProveedor());
    }
    
    private void configurarVentana() {
        setTitle("Actualizar Proveedor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        
        // Tamaño para la ventana - mismo estándar que DialogoActualizarProducto
        setPreferredSize(new Dimension(1200, 800));
        setMinimumSize(new Dimension(1200, 800));
        pack();
        
        // Centrar la ventana en la pantalla (después de pack())
        setLocationRelativeTo(null);
        
        // MouseListener global para cerrar listas al hacer clic fuera de campos
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                // Forzar pérdida de foco de todos los campos para cerrar listas
                // (No aplicable para proveedores, pero mantenemos consistencia)
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel titulo = new JLabel("Actualizar Proveedor");
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
        
        // Fila 1 - Nombre y Teléfono
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(crearEtiqueta("Nombre del proveedor"), gbc);
        gbc.gridx = 1;
        panel.add(campoNombre, gbc);
        
        gbc.gridx = 2;
        panel.add(crearEtiqueta("Teléfono"), gbc);
        gbc.gridx = 3;
        panel.add(campoTelefono, gbc);
        
        // Fila 2 - Email y Activo
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(crearEtiqueta("Email"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(campoMail, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(crearEtiqueta("Activo"), gbc);
        gbc.gridx = 3;
        panel.add(campoActivo, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        panel.add(botonActualizar);
        
        return panel;
    }
    
    private void cargarDatosProveedor() {
        if (proveedorOriginal != null) {
            campoNombre.setText(proveedorOriginal.getNombre());
            campoTelefono.setText(String.valueOf(proveedorOriginal.getTelefono()));
            campoMail.setText(proveedorOriginal.getMail());
            campoActivo.setSelectedIndex(proveedorOriginal.getActivo() ? 0 : 1);
        }
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
        campo.setPreferredSize(new Dimension(250, 34));
        campo.setMinimumSize(new Dimension(250, 34));
        campo.setMaximumSize(new Dimension(250, 34));
        
        // Aplicar validación para el campo de teléfono
        if (placeholder.contains("Teléfono")) {
            configurarTelefono(campo);
            configurarMaximoCaracteres(campo, 20); // Máximo 20 caracteres (para números internacionales)
        }
        
        // Aplicar validación para el campo de email
        if (placeholder.contains("Email")) {
            configurarValidacionEmail(campo);
        }
        
        // Aplicar límite de caracteres para el campo de nombre
        if (placeholder.contains("Nombre")) {
            configurarMaximoCaracteres(campo, 50); // Máximo 50 caracteres
        }
        
        return campo;
    }
    
    private JComboBox<String> crearCampoActivo() {
        JComboBox<String> campo = new JComboBox<>(new String[]{"Si", "No"});
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setForeground(new Color(30, 41, 59));
        campo.setBackground(Color.WHITE);
        campo.setSelectedIndex(0);
        campo.setPreferredSize(new Dimension(80, 34));
        campo.setMinimumSize(new Dimension(80, 34));
        campo.setMaximumSize(new Dimension(80, 34));
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
        boton.setPreferredSize(new Dimension(180, 40));
        boton.setMinimumSize(new Dimension(180, 40));
        boton.setMaximumSize(new Dimension(180, 40));
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
                boton.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });
        
        return boton;
    }
    
    private void actualizarProveedor() {
        try {
            String nombre = campoNombre.getText().trim();
            String telefonoStr = campoTelefono.getText().trim();
            String mail = campoMail.getText().trim();
            
            // Validaciones
            if (nombre.isEmpty()) {
                mostrarError("El nombre del proveedor es obligatorio.");
                return;
            }
            
            if (telefonoStr.isEmpty()) {
                mostrarError("El teléfono del proveedor es obligatorio.");
                return;
            }
            
            // Validar email solo si no está vacío
            if (!mail.isEmpty() && !esEmailValido(mail)) {
                mostrarError("El formato del email no es válido. Debe contener @ y tener formato válido.");
                return;
            }
            
            // Validar que el teléfono no esté vacío
            if (telefonoStr.trim().isEmpty()) {
                mostrarError("El teléfono del proveedor es obligatorio.");
                return;
            }
            
            // Actualizar datos del proveedor (convertir teléfono a número sin espacios ni +)
            String telefonoLimpio = telefonoStr.replaceAll("[^0-9]", "");
            long telefono = Long.parseLong(telefonoLimpio);
            proveedorOriginal.setNombre(nombre);
            proveedorOriginal.setTelefono(telefono);
            proveedorOriginal.setMail(mail);
            proveedorOriginal.setActivo(campoActivo.getSelectedIndex() == 0);
            
            // Guardar en la base de datos
            controladorProveedor.actualizarProveedor(proveedorOriginal);
            
            // Mostrar mensaje de éxito
            mostrarExito("Proveedor actualizado exitosamente.");
            
            // Actualizar la tabla en el panel principal
            if (panelPrincipal != null) {
                panelPrincipal.actualizarTabla();
            }
            
            // Cerrar ventana
            dispose();
        } catch (Exception e) {
            System.err.println("ERROR al actualizar proveedor: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al actualizar proveedor: " + e.getMessage());
        }
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
    
    private void configurarTelefono(JTextField campo) {
        campo.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Permitir dígitos, espacios, + y backspace
                if (!Character.isDigit(c) && c != ' ' && c != '+' && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume(); // Bloquea la tecla
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
    
    private void configurarValidacionEmail(JTextField campo) {
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validarEmail(campo);
            }
        });
    }
    
    private void validarEmail(JTextField campo) {
        String email = campo.getText().trim();
        
        // Si está vacío, es válido (proveedor sin email)
        if (email.isEmpty()) {
            campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
            ));
            return;
        }
        
        // Validar formato de email
        if (esEmailValido(email)) {
            // Email válido - borde normal
            campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
            ));
        } else {
            // Email inválido - borde rojo
            campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 2),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
            ));
            campo.setToolTipText("Email inválido. Debe contener @ y tener formato válido");
        }
    }
    
    private boolean esEmailValido(String email) {
        // Debe contener @
        if (!email.contains("@")) {
            return false;
        }
        
        String[] partes = email.split("@");
        if (partes.length != 2) {
            return false;
        }
        
        String parteLocal = partes[0];
        String parteDominio = partes[1];
        
        // Al menos 3 caracteres antes del @
        if (parteLocal.length() < 3) {
            return false;
        }
        
        // Debe tener al menos un punto después del @
        if (!parteDominio.contains(".")) {
            return false;
        }
        
        // Al menos 2 caracteres después del último punto
        String[] dominioPartes = parteDominio.split("\\.");
        if (dominioPartes.length < 2 || dominioPartes[dominioPartes.length - 1].length() < 2) {
            return false;
        }
        
        return true;
    }
    
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
