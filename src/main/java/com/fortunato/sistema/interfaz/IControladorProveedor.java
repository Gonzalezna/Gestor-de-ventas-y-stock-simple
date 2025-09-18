package com.fortunato.sistema.interfaz;

import java.util.List;
import com.fortunato.sistema.entidad.Proveedor;

public interface IControladorProveedor {
    List<Proveedor> listarProveedores();
    Proveedor buscarProveedor(Long id);
}
