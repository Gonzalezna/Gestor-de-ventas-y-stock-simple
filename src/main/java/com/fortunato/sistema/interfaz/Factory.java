package com.fortunato.sistema.interfaz;

import com.fortunato.sistema.logica.servicio.ControladorProducto;
import com.fortunato.sistema.logica.servicio.ControladorProveedor;

public class Factory {
    private static Factory instancia;
    private final IControladorProducto producto;
    private final IControladorProveedor proveedor;

    private Factory() {
        this.producto = new ControladorProducto();
        this.proveedor = new ControladorProveedor();
    }

    public static synchronized Factory getInstancia() {
		if (instancia == null) instancia = new Factory();
		return instancia;
	}

    public IControladorProducto getControladorProducto() { return producto; }
    public IControladorProveedor getControladorProveedor() { return proveedor; }
}
