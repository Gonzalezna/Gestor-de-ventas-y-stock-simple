package com.fortunato.sistema.interfaz;

import com.fortunato.sistema.entidad.Producto;
import com.fortunato.sistema.entidad.Caja;
import com.fortunato.sistema.entidad.Venta;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.fortunato.sistema.entidad.DetalleVenta;

public interface IControladorVentas {
    // Métodos del carrito
    void agregarProductoAlCarrito(Producto producto);
    void agregarProductoAlCarrito(Producto producto, int cantidad);
    BigDecimal calcularTotalCarrito();
    void confirmarVenta(Caja caja, BigDecimal montoPagado, Venta.MetodoPago metodoPago);
    void removerProductoDeCarrito(Producto producto);
    void limpiarCarrito();
    BigDecimal procesarPago(BigDecimal pago);
    List<DetalleVenta> obtenerCarrito();
    
    // Métodos de consulta de ventas
    List<Venta> buscarTodasLasVentas();
    List<Venta> buscarVentasPorFechas(LocalDateTime fechaDesde, LocalDateTime fechaHasta);
    Venta buscarVentaPorId(Long id);
    List<Venta> buscarVentasPorProducto(Long idProducto);
    List<Venta> buscarVentasPorMetodoPago(Venta.MetodoPago metodoPago);
}
