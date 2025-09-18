package com.fortunato.sistema.logica.servicio;

import com.fortunato.sistema.entidad.Producto;
import com.fortunato.sistema.interfaz.IControladorProducto;
import com.fortunato.sistema.config.EntityManagerFactory;
import jakarta.persistence.EntityManager;  

import java.util.List;

public class ControladorProducto implements IControladorProducto {

    private EntityManager em() {
        return EntityManagerFactory.getInstance().createEntityManager();  // ← Cambiar esta línea
    }
    
    @Override
    public void crearProducto(Producto producto) {
        // TODO: Implementar
    }
    
    @Override
    public Producto buscarProducto(Long id) {
        // TODO: Implementar
        return null;
    }
    
    @Override
    public List<Producto> listarProductos() {
        // TODO: Implementar
        return null;
    }
    
    @Override
    public void actualizarProducto(Producto producto) {
        // TODO: Implementar
    }
    
    @Override
    public void eliminarProducto(Long id) {
        // TODO: Implementar
    }
}
