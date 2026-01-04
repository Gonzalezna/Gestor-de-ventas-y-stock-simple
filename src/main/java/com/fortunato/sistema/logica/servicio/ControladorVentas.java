package com.fortunato.sistema.logica.servicio;

import com.fortunato.sistema.interfaz.IControladorVentas;

import java.util.ArrayList;

import com.fortunato.sistema.config.EntityManagerFactory;
import jakarta.persistence.EntityManager;
import com.fortunato.sistema.entidad.Producto;
import com.fortunato.sistema.entidad.Caja;
import com.fortunato.sistema.entidad.DetalleVenta;
import com.fortunato.sistema.entidad.Venta;
import java.math.BigDecimal;
import java.util.List;

public class ControladorVentas implements IControladorVentas {

    private List<DetalleVenta> carrito = new ArrayList<>();

    private EntityManager em() {
        return EntityManagerFactory.getInstance().createEntityManager();
    }

    public void agregarProductoAlCarrito(Producto producto) {
        agregarProductoAlCarrito(producto, 1);
    }
    
    @Override
    public void agregarProductoAlCarrito(Producto producto, int cantidad) {
        if(producto.stockActual() == 0){
            throw new IllegalArgumentException("No hay stock de este producto");
        }
        
        if(cantidad <= 0){
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        
        // Buscar si el producto ya está en el carrito
        DetalleVenta detalleExistente = null;
        int cantidadEnCarrito = 0;
        
        for(DetalleVenta detalle : carrito){
            if(detalle.getProducto().getId().equals(producto.getId())){
                detalleExistente = detalle;
                cantidadEnCarrito = detalle.getCantidad();
                break;
            }
        }
        
        // Validar que la cantidad total (en carrito + nueva) no supere el stock ANTES de agregar
        int cantidadTotal = cantidadEnCarrito + cantidad;
        if(cantidadTotal > producto.stockActual()){
            throw new IllegalArgumentException(
                String.format("Stock insuficiente. Disponible: %d, En carrito: %d, Solicitado: %d", 
                    producto.stockActual(), cantidadEnCarrito, cantidad)
            );
        }
        
        // Si llegamos aquí, la cantidad es válida, proceder a agregar
        if(detalleExistente != null){
            // Producto ya existe en el carrito, actualizar cantidad
            detalleExistente.setCantidad(cantidadTotal);
        } else {
            // Producto nuevo en el carrito
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setPrecioUnitario(producto.getPrecioVenta());
            detalle.setCantidad(cantidad);
            carrito.add(detalle);
        }
    }
    
    @Override
    public BigDecimal calcularTotalCarrito() {
        BigDecimal total = BigDecimal.ZERO;
        for(DetalleVenta detalle : carrito){
            total = total.add(detalle.getSubtotal());
        }
        return total;
    }

    @Override
    public void confirmarVenta(Caja caja, BigDecimal montoPagado, Venta.MetodoPago metodoPago){
        Venta venta = new Venta();
        EntityManager em = em();
        try{
            em.getTransaction().begin();
            // Recargar la caja para asegurar que esté gestionada por el EntityManager
            Caja cajaGestionada = em.find(Caja.class, caja.getId());
            if(cajaGestionada == null) {
                throw new IllegalArgumentException("La caja no existe");
            }
            
            // Validar que el monto pagado sea suficiente
            if(montoPagado.compareTo(calcularTotalCarrito()) < 0) {
                throw new IllegalArgumentException("El monto pagado es insuficiente");
            }
            
            venta.setCaja(cajaGestionada);
            for(DetalleVenta detalle : carrito){
                venta.agregarDetalle(detalle);
                detalle.reducirStockDV();
                em.merge(detalle.getProducto());
            }
            
            // Calcular el vuelto
            BigDecimal vuelto = montoPagado.subtract(venta.getTotal());
            venta.setMontoPagado(montoPagado);
            venta.setVuelto(vuelto);
            venta.setMetodoPago(metodoPago);
            
            // Solo añadir dinero a la caja si el pago es en efectivo
            // Si es débito, no se mueve dinero físico en la caja
            if(metodoPago == Venta.MetodoPago.EFECTIVO) {
                cajaGestionada.añadirDineroCaja(venta.getTotal());
            }
            
            em.persist(venta);
            venta.setEstado(Venta.EstadoVenta.COMPLETADA);
            carrito.clear();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al confirmar venta", e);
        } finally {
            em.close();
        }
    }
    

    @Override
    public void removerProductoDeCarrito(Producto producto) {
        for(int i = 0; i < carrito.size(); i++){
            if(carrito.get(i).getProducto().getId().equals(producto.getId())){
            carrito.get(i).setCantidad(carrito.get(i).getCantidad() - 1);
            if(carrito.get(i).getCantidad() == 0){
                carrito.remove(i);
            }
            break;
            }
        }
    }

    @Override
    public void limpiarCarrito() {
        carrito.clear();
    }

    @Override
    public BigDecimal procesarPago(BigDecimal pago) {
        BigDecimal total = calcularTotalCarrito();
        return pago.subtract(total);
    }

    @Override
    public List<DetalleVenta> obtenerCarrito() {
        return new ArrayList<>(carrito);
    }
    
    // ========== MÉTODOS DE CONSULTA DE VENTAS ==========
    
    @Override
    public List<Venta> buscarTodasLasVentas() {
        EntityManager em = em();
        try {
            return em.createQuery("SELECT v FROM Venta v ORDER BY v.fecha DESC", Venta.class)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar todas las ventas", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Venta> buscarVentasPorFechas(java.time.LocalDateTime fechaDesde, java.time.LocalDateTime fechaHasta) {
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT v FROM Venta v WHERE v.fecha BETWEEN :fechaDesde AND :fechaHasta ORDER BY v.fecha DESC", 
                    Venta.class)
                    .setParameter("fechaDesde", fechaDesde)
                    .setParameter("fechaHasta", fechaHasta)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar ventas por fechas", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Venta buscarVentaPorId(Long id) {
        EntityManager em = em();
        try {
            Venta venta = em.find(Venta.class, id);
            if (venta != null) {
                // Forzar la carga de los detalles
                venta.getDetalles().size();
            }
            return venta;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar venta por ID", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Venta> buscarVentasPorProducto(Long idProducto) {
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT DISTINCT v FROM Venta v JOIN v.detalles d WHERE d.producto.id = :idProducto ORDER BY v.fecha DESC", 
                    Venta.class)
                    .setParameter("idProducto", idProducto)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar ventas por producto", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Venta> buscarVentasPorMetodoPago(Venta.MetodoPago metodoPago) {
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT v FROM Venta v WHERE v.metodoPago = :metodoPago ORDER BY v.fecha DESC", 
                    Venta.class)
                    .setParameter("metodoPago", metodoPago)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar ventas por método de pago", e);
        } finally {
            em.close();
        }
    }

}




