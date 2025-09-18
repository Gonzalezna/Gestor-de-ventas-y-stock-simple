package com.fortunato.sistema.presentacion;

import javax.swing.*;

/**
 * Clase de prueba para la interfaz Swing
 */
public class TestSwing {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new SwingApp().setVisible(true);
            } catch (Exception e) {
                System.err.println("Error al iniciar la aplicación: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
