package com.fortunato.sistema.interfaz;

import java.util.List;
import com.fortunato.sistema.entidad.Proveedor;

public interface IControladorProveedor {
    List<Proveedor> listarProveedores();
    Proveedor buscarProveedor(Long id);
    void crearProveedor(Proveedor proveedor);
    void actualizarProveedor(Proveedor proveedor);
    void eliminarProveedor(Long id);
}
