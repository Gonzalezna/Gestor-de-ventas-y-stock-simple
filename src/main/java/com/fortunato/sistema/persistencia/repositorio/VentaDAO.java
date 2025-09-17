package com.fortunato.sistema.persistencia.repositorio;

import com.fortunato.sistema.persistencia.entidad.DetalleVenta;
import com.fortunato.sistema.persistencia.entidad.Producto;
import com.fortunato.sistema.persistencia.entidad.Venta;
import com.fortunato.sistema.persistencia.entidad.Caja;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de persistencia para Venta
 */
public interface VentaDAO {
    
    /**
     * Guardar una venta
     * @param venta La venta a guardar
     * @return La venta guardada
     */
    Venta guardar(Venta venta);
    
    /**
     * Buscar una venta por ID
     * @param id El ID de la venta
     * @return Optional con la venta si existe
     */
    Optional<Venta> buscarPorId(Long id);
    
    /**
     * Buscar una venta por número de venta
     * @param numeroVenta El número de venta
     * @return Optional con la venta si existe
     */
    Optional<Venta> buscarPorNumeroVenta(String numeroVenta);
    
    /**
     * Obtener todas las ventas
     * @return Lista de todas las ventas
     */
    List<Venta> obtenerTodas();
    
    /**
     * Obtener ventas por caja
     * @param caja La caja
     * @return Lista de ventas de la caja
     */
    List<Venta> obtenerPorCaja(Caja caja);
    
    /**
     * Obtener ventas por rango de fechas
     * @param fechaInicio La fecha de inicio
     * @param fechaFin La fecha de fin
     * @return Lista de ventas en el rango
     */
    List<Venta> obtenerPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    /**
     * Obtener ventas por estado
     * @param estado El estado de la venta
     * @return Lista de ventas con el estado
     */
    List<Venta> obtenerPorEstado(Venta.EstadoVenta estado);
    
    /**
     * Obtener ventas completadas
     * @return Lista de ventas completadas
     */
    List<Venta> obtenerCompletadas();
    
    /**
     * Obtener ventas pendientes
     * @return Lista de ventas pendientes
     */
    List<Venta> obtenerPendientes();
    
    /**
     * Obtener ventas canceladas
     * @return Lista de ventas canceladas
     */
    List<Venta> obtenerCanceladas();
    
    /**
     * Actualizar una venta
     * @param venta La venta actualizada
     * @return La venta actualizada
     */
    Venta actualizar(Venta venta);
    
    /**
     * Eliminar una venta
     * @param id El ID de la venta a eliminar
     */
    void eliminar(Long id);
    
    /**
     * Contar ventas por estado
     * @param estado El estado
     * @return El número de ventas con el estado
     */
    long contarPorEstado(Venta.EstadoVenta estado);
    
    /**
     * Contar ventas por caja
     * @param caja La caja
     * @return El número de ventas de la caja
     */
    long contarPorCaja(Caja caja);
    
    /**
     * Obtener la siguiente venta en secuencia para una caja
     * @param caja La caja
     * @return El número de la siguiente venta
     */
    String obtenerSiguienteNumeroVenta(Caja caja);

    //DetalleVenta
    List<DetalleVenta> obtenerDetallesPorVenta(Long ventaId);
    List<DetalleVenta> obtenerDetallesPorProducto(Producto producto);
    List<DetalleVenta> obtenerDetallesPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);
}
