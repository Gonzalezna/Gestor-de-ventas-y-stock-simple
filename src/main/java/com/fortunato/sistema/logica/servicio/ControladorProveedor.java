package com.fortunato.sistema.logica.servicio;

import com.fortunato.sistema.entidad.Proveedor;
import com.fortunato.sistema.interfaz.IControladorProveedor;
import com.fortunato.sistema.config.EntityManagerFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ControladorProveedor implements IControladorProveedor {

    private EntityManager em() {
        return EntityManagerFactory.getInstance().createEntityManager();
    }
    
    @Override
    public List<Proveedor> listarProveedores() {
        EntityManager em = em();
        try {
            List<Proveedor> proveedores = em.createQuery("SELECT p FROM Proveedor p ORDER BY p.nombre", Proveedor.class).getResultList();
            return proveedores;
        } catch (Exception e) {
            throw new RuntimeException("Error al listar proveedores", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Proveedor buscarProveedor(Long id) {
        EntityManager em = em();
        try {
            Proveedor proveedor = em.find(Proveedor.class, id);
            return proveedor;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar proveedor", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void crearProveedor(Proveedor proveedor) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            em.persist(proveedor);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al crear proveedor", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void actualizarProveedor(Proveedor proveedor) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            em.merge(proveedor);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al actualizar proveedor", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void eliminarProveedor(Long id) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            Proveedor proveedor = em.find(Proveedor.class, id);
            if (proveedor != null) {
                em.remove(proveedor);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar proveedor", e);
        } finally {
            em.close();
        }
    }
}
