package com.fortunato.sistema.logica.servicio;

import com.fortunato.sistema.entidad.Producto;
import com.fortunato.sistema.interfaz.IControladorProducto;
import com.fortunato.sistema.config.EntityManagerFactory;
import jakarta.persistence.EntityManager;  

import java.util.List;

public class ControladorProducto implements IControladorProducto {

    private EntityManager em() {
        return EntityManagerFactory.getInstance().createEntityManager();
    }
    
    @Override
    public void crearProducto(Producto producto) {
            EntityManager em = em();
        try{
            em.getTransaction().begin(); //Creamos transaccion
            em.persist(producto); 
            em.getTransaction().commit(); //Commit de la transaccion
        }catch(Exception e){
            em.getTransaction().rollback(); // Si falla hacemos rollback
            throw new RuntimeException("Error al crear el producto", e);
        } finally {
            em.close(); // Cerramos la conexion
        }
    }
    
    @Override
    public Producto buscarProducto(Long id) {
        EntityManager em = em();
        try {
            Producto producto = em.find(Producto.class, id); // Buscamos el producto, no se necesita transaction
            return producto;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar producto", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Producto> listarProductos() {
        EntityManager em = em();
        try {
            List<Producto> productos = em.createQuery("SELECT p FROM Producto p ORDER BY p.nombre", Producto.class).getResultList();
            return productos;
        } catch (Exception e) {
            throw new RuntimeException("Error al listar productos", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void actualizarProducto(Producto producto) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            em.merge(producto);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar producto", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void eliminarProducto(Long id) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            
            //Buscar el producto
            Producto producto = em.find(Producto.class, id);
            
            //Verificar que existe
            if (producto != null) {
                em.remove(producto);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al eliminar producto", e);
        } finally {
            em.close();
        }
    }
}
