package com.fortunato.sistema.interfaz;

import com.fortunato.sistema.entidad.Producto;
import com.fortunato.sistema.entidad.Venta;


public interface IControladorDevoluciones {
    void devolverProducto(int cantidadDevuelta,Producto producto, Venta venta);
    void devolucionVenta(Venta venta);
    
}
