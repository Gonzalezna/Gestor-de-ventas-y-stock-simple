package com.fortunato.sistema.interfaz;

import java.util.List;

import com.fortunato.sistema.entidad.Producto;

public interface IControladorProducto {
    void crearProducto(Producto producto);
    Producto buscarProducto(Long id);
    List<Producto> listarProductos();
    void actualizarProducto(Producto producto);
    void eliminarProducto(Long id);
}