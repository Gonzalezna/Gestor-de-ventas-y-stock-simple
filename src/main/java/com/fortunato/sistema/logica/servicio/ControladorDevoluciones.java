package com.fortunato.sistema.logica.servicio;

import com.fortunato.sistema.entidad.Producto;
import com.fortunato.sistema.entidad.Venta;
import com.fortunato.sistema.entidad.Caja;
import com.fortunato.sistema.interfaz.IControladorDevoluciones;
import com.fortunato.sistema.config.EntityManagerFactory;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;


public class ControladorDevoluciones implements IControladorDevoluciones {
    
    private EntityManager em() {
        return EntityManagerFactory.getInstance().createEntityManager();
    }

    @Override
    public void devolverProducto(int cantidadDevuelta,Producto producto, Venta venta) {
        EntityManager em = em();
        if(venta.getEstado() != Venta.EstadoVenta.COMPLETADA){
            throw new IllegalArgumentException("Solo se pueden devolver productos de ventas completadas");
        }
        if(cantidadDevuelta <= 0){
            throw new IllegalArgumentException("La cantidad de productos devueltos debe ser mayor a 0");
        }
        if(venta.getProductoVendido(producto) == 0){
            throw new IllegalArgumentException("El producto no ha sido vendido");
        } 
        try {
            em.getTransaction().begin();
            
            // Recargar entidades para que estén gestionadas
            Producto productoGestionado = em.find(Producto.class, producto.getId());
            Venta ventaGestionada = em.find(Venta.class, venta.getId());
            Caja cajaGestionada = em.find(Caja.class, ventaGestionada.getCaja().getId());
            
            if(cantidadDevuelta <= (ventaGestionada.getProductoVendido(productoGestionado))){
                productoGestionado.aumentarStock(cantidadDevuelta);
                cajaGestionada.retirarDineroCaja(ventaGestionada.getPrecioVenta(productoGestionado).multiply(BigDecimal.valueOf(cantidadDevuelta)));
            }else{
                throw new IllegalArgumentException("La cantidad de productos devueltos excede los productos vendidos");
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al devolver producto", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void devolucionVenta(Venta ventaOriginal) {
        EntityManager em = em();
        
        // Validaciones
        if(ventaOriginal.getEstado() != Venta.EstadoVenta.COMPLETADA){
            throw new IllegalArgumentException("Solo se pueden devolver ventas completadas");
        }
        
        try {
            em.getTransaction().begin();
            
            // Recargar la venta original y la caja
            Venta ventaGestionada = em.find(Venta.class, ventaOriginal.getId());
            Caja cajaGestionada = em.find(Caja.class, ventaGestionada.getCaja().getId());
            
            // Crear una nueva venta de tipo DEVOLUCION
            Venta devolucion = new Venta();
            devolucion.setTipo(Venta.TipoVenta.DEVOLUCION);
            devolucion.setVentaOriginal(ventaGestionada);
            devolucion.setCaja(cajaGestionada);
            devolucion.setEstado(Venta.EstadoVenta.COMPLETADA);
            
            // Copiar todos los detalles con cantidades negativas
            for(com.fortunato.sistema.entidad.DetalleVenta detalleOriginal : ventaGestionada.getDetalles()){
                com.fortunato.sistema.entidad.DetalleVenta detalleDevolucion = new com.fortunato.sistema.entidad.DetalleVenta();
                detalleDevolucion.setVenta(devolucion);
                detalleDevolucion.setProducto(detalleOriginal.getProducto());
                detalleDevolucion.setCantidad(-detalleOriginal.getCantidad()); // Cantidad negativa
                detalleDevolucion.setPrecioUnitario(detalleOriginal.getPrecioUnitario());
                
                // Devolver stock al producto
                detalleOriginal.getProducto().aumentarStock(detalleOriginal.getCantidad());
                em.merge(detalleOriginal.getProducto());
                
                devolucion.getDetalles().add(detalleDevolucion);
            }
            
            // Recalcular totales (serán negativos)
            devolucion.recalcularTotales();
            
            // Retirar dinero de la caja (reembolso)
            cajaGestionada.retirarDineroCaja(ventaGestionada.getTotal());
            
            // Persistir la devolución
            em.persist(devolucion);
            
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al devolver venta", e);
        } finally {
            em.close();
        }
    }

}
