package com.fortunato.sistema.presentacion;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Campo de texto con lista de sugerencias fija debajo
 * El campo funciona normalmente, la lista solo muestra sugerencias
 */
public class CampoTextoConSugerencias extends JPanel {
    private JTextField campoTexto;
    private JList<String> listaSugerencias;
    private DefaultListModel<String> modeloLista;
    private List<String> todosLosItems;
    private JScrollPane panelScroll;

    public CampoTextoConSugerencias(List<String> items) {
        this.todosLosItems = new ArrayList<>(items);
        inicializarComponentes();
        configurarListeners();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        
        // Campo de texto normal
        campoTexto = new JTextField();
        campoTexto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoTexto.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Lista de sugerencias
        modeloLista = new DefaultListModel<>();
        listaSugerencias = new JList<>(modeloLista);
        listaSugerencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaSugerencias.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        listaSugerencias.setBackground(Color.WHITE);
        listaSugerencias.setSelectionBackground(new Color(59, 130, 246));
        listaSugerencias.setSelectionForeground(Color.WHITE);
        listaSugerencias.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        listaSugerencias.setVisibleRowCount(5); // Mostrar al menos 5 filas
        
        // Scroll para la lista
        panelScroll = new JScrollPane(listaSugerencias);
        panelScroll.setPreferredSize(new Dimension(0, 120));
        panelScroll.setMinimumSize(new Dimension(0, 120));
        panelScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        panelScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panelScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Agregar componentes
        add(campoTexto, BorderLayout.NORTH);
        add(panelScroll, BorderLayout.CENTER);
        
        // Inicialmente ocultar la lista
        panelScroll.setVisible(false);
    }

    private void configurarListeners() {
        // Mostrar lista al hacer clic en el campo
        campoTexto.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                mostrarListaSugerencias();
            }
        });

        // Escuchar cambios en el texto para filtrar
        campoTexto.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { 
                filtrarSugerencias(); 
            }
            public void removeUpdate(DocumentEvent e) { 
                filtrarSugerencias(); 
            }
            public void changedUpdate(DocumentEvent e) {}
        });

        // Permitir selección de sugerencias con un solo clic
        listaSugerencias.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    seleccionarSugerencia();
                }
            }
        });

        // Navegación con teclado desde el campo de texto
        campoTexto.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && listaSugerencias.getModel().getSize() > 0) {
                    listaSugerencias.requestFocus();
                    listaSugerencias.setSelectedIndex(0);
                }
            }
        });

        // Navegación con teclado en la lista
        listaSugerencias.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    seleccionarSugerencia();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && listaSugerencias.getSelectedIndex() == 0) {
                    campoTexto.requestFocus();
                }
            }
        });

        // FocusListener en el campo de texto para cerrar la lista cuando pierde el foco
        campoTexto.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                // Solo cerrar si el foco NO va a la lista o al scroll
                Component opposite = e.getOppositeComponent();
                if (opposite != listaSugerencias && opposite != panelScroll) {
                    SwingUtilities.invokeLater(() -> {
                        panelScroll.setVisible(false);
                    });
                }
            }
        });

        // FocusListener en la lista para mantenerla abierta cuando tiene foco
        listaSugerencias.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                // Mantener la lista visible cuando la lista obtiene foco
                panelScroll.setVisible(true);
            }
        });

        // MouseListener global para cerrar la lista al hacer clic fuera
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // Solo cerrar si el clic NO es en el campo de texto, la lista o el scroll
                if (e.getSource() != campoTexto && e.getSource() != listaSugerencias && e.getSource() != panelScroll) {
                    // Forzar pérdida de foco del campo para cerrar la lista
                    if (campoTexto.hasFocus()) {
                        campoTexto.transferFocus();
                    }
                }
            }
        });
    }

    private void mostrarListaSugerencias() {
        System.out.println("Mostrando lista de sugerencias...");
        // Cargar todas las sugerencias y mostrar la lista
        cargarTodasLasSugerencias();
        System.out.println("Items en la lista: " + modeloLista.getSize());
        if (!modeloLista.isEmpty()) {
            listaSugerencias.setSelectedIndex(0);
            panelScroll.setVisible(true);
            // Forzar actualización del layout
            revalidate();
            repaint();
            System.out.println("Lista visible: " + panelScroll.isVisible());
        } else {
            System.out.println("No hay items para mostrar");
        }
    }

    private void filtrarSugerencias() {
        String texto = campoTexto.getText().trim().toLowerCase();
        modeloLista.clear();

        if (texto.isEmpty()) {
            // Si no hay texto, mostrar todos los items
            cargarTodasLasSugerencias();
        } else {
            // Filtrar items que contengan el texto
            for (String item : todosLosItems) {
                if (item.toLowerCase().contains(texto)) {
                    modeloLista.addElement(item);
                }
            }
        }
        
        // Mostrar la lista si hay items, ocultar si no hay
        if (!modeloLista.isEmpty()) {
            listaSugerencias.setSelectedIndex(0);
            panelScroll.setVisible(true);
            // Forzar actualización del layout
            revalidate();
            repaint();
        } else {
            panelScroll.setVisible(false);
        }
    }

    private void cargarTodasLasSugerencias() {
        System.out.println("Cargando todas las sugerencias...");
        modeloLista.clear();
        System.out.println("Total de items disponibles: " + todosLosItems.size());
        for (String item : todosLosItems) {
            modeloLista.addElement(item);
            System.out.println("  - " + item);
        }
        System.out.println("Items cargados en el modelo: " + modeloLista.getSize());
    }

    private void seleccionarSugerencia() {
        String valorSeleccionado = listaSugerencias.getSelectedValue();
        if (valorSeleccionado != null) {
            campoTexto.setText(valorSeleccionado);
            campoTexto.requestFocus();
            // Cerrar la lista al seleccionar
            panelScroll.setVisible(false);
            // Opcional: notificar que se seleccionó algo
            dispararAccionRealizada();
        }
    }

    // Métodos públicos para acceder al campo de texto
    public String getText() {
        return campoTexto.getText();
    }

    public void setText(String text) {
        campoTexto.setText(text);
        // NO llamar filtrarSugerencias aquí - solo si la lista está visible
        if (panelScroll.isVisible()) {
            filtrarSugerencias();
        }
    }

    public void addActionListener(ActionListener listener) {
        campoTexto.addActionListener(listener);
    }

    public void removeActionListener(ActionListener listener) {
        campoTexto.removeActionListener(listener);
    }

    public void setEditable(boolean editable) {
        campoTexto.setEditable(editable);
    }

    public boolean isEditable() {
        return campoTexto.isEditable();
    }

    public void setEnabled(boolean enabled) {
        campoTexto.setEnabled(enabled);
        listaSugerencias.setEnabled(enabled);
    }

    public void requestFocus() {
        campoTexto.requestFocus();
    }

    // Método para actualizar la lista de items
    public void setItems(List<String> newItems) {
        System.out.println("Actualizando items: " + newItems.size());
        this.todosLosItems = new ArrayList<>(newItems);
        // No llamar filtrarSugerencias aquí, solo actualizar la lista
        System.out.println("Items actualizados: " + todosLosItems.size());
    }

    // Método para limpiar el campo
    public void clear() {
        campoTexto.setText("");
        // NO cargar sugerencias aquí - solo limpiar el texto
        panelScroll.setVisible(false);
    }

    // Método para obtener el campo de texto (por si necesitas acceso directo)
    public JTextField getTextField() {
        return campoTexto;
    }

    // Método para obtener la lista de sugerencias
    public JList<String> getSuggestionList() {
        return listaSugerencias;
    }

    // Simular fireActionPerformed para compatibilidad
    private void dispararAccionRealizada() {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "suggestion_selected");
        for (ActionListener listener : campoTexto.getActionListeners()) {
            listener.actionPerformed(event);
        }
    }
}
