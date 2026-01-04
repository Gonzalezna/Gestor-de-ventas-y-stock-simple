package com.fortunato.sistema.logica.servicio;

import com.fortunato.sistema.entidad.Caja;
import com.fortunato.sistema.interfaz.IControladorCaja;
import com.fortunato.sistema.config.EntityManagerFactory;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;

public class ControladorCaja implements IControladorCaja {

    private EntityManager em() {
        return EntityManagerFactory.getInstance().createEntityManager();
    }
    
    @Override
    public void abrirCaja(Caja caja) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            
            // Recargar la caja para que esté gestionada
            Caja cajaGestionada = em.find(Caja.class, caja.getId());
            if (cajaGestionada != null) {
                cajaGestionada.setSaldoInicial(caja.getSaldoInicial());
                cajaGestionada.abrir();
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al abrir caja", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void cerrarCaja(Caja caja) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            
            // Recargar la caja para que esté gestionada
            Caja cajaGestionada = em.find(Caja.class, caja.getId());
            if (cajaGestionada != null) {
                cajaGestionada.cerrar();
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al cerrar caja", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void añadirDineroCaja(Caja caja, BigDecimal dinero) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            caja.añadirDineroCaja(dinero);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al añadir dinero a caja", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void retirarDineroCaja(Caja caja, BigDecimal dinero) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            caja.retirarDineroCaja(dinero);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al retirar dinero de caja", e);
        } finally {
            em.close();
        }
    }

    @Override
    public BigDecimal getDiferencia(Caja caja) {
        EntityManager em = em();
        try {
            return caja.getDiferencia();
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular diferencia", e);
        } finally {
            em.close();
        }
    }

    @Override
    public BigDecimal calcularTotalVentas(Caja caja) {
        EntityManager em = em();
        try {
            return caja.calcularTotalVentas();
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular total de ventas", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Caja crearCaja(Integer numeroCaja, BigDecimal saldoInicial) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            Caja caja = new Caja(numeroCaja, saldoInicial);
            em.persist(caja);
            em.getTransaction().commit();
            return caja;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al crear caja", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Caja buscarCaja(Long id) {
        EntityManager em = em();
        try {
            return em.find(Caja.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar caja", e);
        } finally {
            em.close();
        }
    }

}
