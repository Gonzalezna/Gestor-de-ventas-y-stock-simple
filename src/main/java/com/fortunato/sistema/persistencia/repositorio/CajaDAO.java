package com.fortunato.sistema.persistencia.repositorio;

import com.fortunato.sistema.persistencia.entidad.Caja;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de persistencia para Caja
 */
public interface CajaDAO {
    
    /**
     * Guardar una caja
     * @param caja La caja a guardar
     * @return La caja guardada
     */
    Caja guardar(Caja caja);
    
    /**
     * Buscar una caja por ID
     * @param id El ID de la caja
     * @return Optional con la caja si existe
     */
    Optional<Caja> buscarPorId(Long id);
    
    /**
     * Buscar una caja por número
     * @param numeroCaja El número de la caja
     * @return Optional con la caja si existe
     */
    Optional<Caja> buscarPorNumero(Integer numeroCaja);
    
    /**
     * Obtener todas las cajas
     * @return Lista de todas las cajas
     */
    List<Caja> obtenerTodas();
    
    /**
     * Obtener cajas activas
     * @return Lista de cajas activas
     */
    List<Caja> obtenerActivas();
    
    /**
     * Obtener cajas abiertas
     * @return Lista de cajas abiertas
     */
    List<Caja> obtenerAbiertas();
    
    /**
     * Obtener cajas cerradas
     * @return Lista de cajas cerradas
     */
    List<Caja> obtenerCerradas();
    
    /**
     * Actualizar una caja
     * @param caja La caja actualizada
     * @return La caja actualizada
     */
    Caja actualizar(Caja caja);
    
    /**
     * Eliminar una caja
     * @param id El ID de la caja a eliminar
     */
    void eliminar(Long id);
    
    /**
     * Verificar si existe una caja con el número dado
     * @param numeroCaja El número de la caja
     * @return true si existe, false en caso contrario
     */
    boolean existePorNumero(Integer numeroCaja);
    
    /**
     * Contar cajas activas
     * @return El número de cajas activas
     */
    long contarActivas();
    
    /**
     * Contar cajas abiertas
     * @return El número de cajas abiertas
     */
    long contarAbiertas();
}
