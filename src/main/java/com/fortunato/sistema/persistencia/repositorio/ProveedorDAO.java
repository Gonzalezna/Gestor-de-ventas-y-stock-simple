package com.fortunato.sistema.persistencia.repositorio;

import com.fortunato.sistema.persistencia.entidad.Proveedor;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de persistencia para Proveedor
 */
public interface ProveedorDAO {
    
    /**
     * Guardar un proveedor
     * @param proveedor El proveedor a guardar
     * @return El proveedor guardado
     */
    Proveedor guardar(Proveedor proveedor);
    
    /**
     * Buscar un proveedor por ID
     * @param id El ID del proveedor
     * @return Optional con el proveedor si existe
     */
    Optional<Proveedor> buscarPorId(Long id);
    
    /**
     * Buscar un proveedor por nombre
     * @param nombre El nombre del proveedor
     * @return Optional con el proveedor si existe
     */
    Optional<Proveedor> buscarPorNombre(String nombre);
    
    /**
     * Obtener todos los proveedores
     * @return Lista de todos los proveedores
     */
    List<Proveedor> obtenerTodos();
    
    /**
     * Obtener proveedores activos
     * @return Lista de proveedores activos
     */
    List<Proveedor> obtenerActivos();
    
    /**
     * Buscar proveedores por nombre (búsqueda parcial)
     * @param nombre El nombre o parte del nombre
     * @return Lista de proveedores que coinciden
     */
    List<Proveedor> buscarPorNombreParcial(String nombre);
    
    /**
     * Buscar proveedores por contacto
     * @param contacto El contacto o parte del contacto
     * @return Lista de proveedores que coinciden
     */
    List<Proveedor> buscarPorContacto(String contacto);
    
    /**
     * Actualizar un proveedor
     * @param proveedor El proveedor actualizado
     * @return El proveedor actualizado
     */
    Proveedor actualizar(Proveedor proveedor);
    
    /**
     * Eliminar un proveedor
     * @param id El ID del proveedor a eliminar
     */
    void eliminar(Long id);
    
    /**
     * Verificar si existe un proveedor con el nombre dado
     * @param nombre El nombre del proveedor
     * @return true si existe, false en caso contrario
     */
    boolean existePorNombre(String nombre);
    
    /**
     * Contar proveedores activos
     * @return El número de proveedores activos
     */
    long contarActivos();
    
    /**
     * Contar total de proveedores
     * @return El número total de proveedores
     */
    long contarTotal();
}