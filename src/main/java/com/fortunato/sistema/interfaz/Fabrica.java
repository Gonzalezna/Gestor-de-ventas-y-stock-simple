package com.fortunato.sistema.interfaz;

import com.fortunato.sistema.logica.servicio.ControladorProducto;

public class Fabrica {
    private static Fabrica instancia;
    private final IControladorProducto productos;

    private Fabrica() {
        this.productos = new ControladorProducto();
    }

    public static synchronized Fabrica getInstancia() {
		if (instancia == null) instancia = new Fabrica();
		return instancia;
	}

    public IControladorProducto getControladorProducto() { return productos; }
}
