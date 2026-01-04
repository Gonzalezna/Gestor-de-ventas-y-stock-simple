package com.fortunato.sistema.interfaz;

import com.fortunato.sistema.logica.servicio.ControladorProducto;
import com.fortunato.sistema.logica.servicio.ControladorProveedor;
import com.fortunato.sistema.logica.servicio.ControladorCaja;
import com.fortunato.sistema.logica.servicio.ControladorVentas;
import com.fortunato.sistema.logica.servicio.ControladorDevoluciones;

public class Factory {
    private static Factory instancia;
    private final IControladorProducto producto;
    private final IControladorProveedor proveedor;
    private final IControladorCaja caja;
    private final IControladorVentas ventas;
    private final IControladorDevoluciones devoluciones;

    private Factory() {
        this.producto = new ControladorProducto();
        this.proveedor = new ControladorProveedor();
        this.caja = new ControladorCaja();
        this.ventas = new ControladorVentas();
        this.devoluciones = new ControladorDevoluciones();
    }

    public static synchronized Factory getInstancia() {
		if (instancia == null) instancia = new Factory();
		return instancia;
	}

    public IControladorProducto getControladorProducto() { return producto; }
    public IControladorProveedor getControladorProveedor() { return proveedor; }
    public IControladorCaja getControladorCaja() { return caja; }
    public IControladorVentas getControladorVentas() { return ventas; }
    public IControladorDevoluciones getControladorDevoluciones() { return devoluciones; }
}
