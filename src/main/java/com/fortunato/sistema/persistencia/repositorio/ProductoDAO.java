package com.fortunato.sistema.persistencia.repositorio;

import com.fortunato.sistema.persistencia.entidad.Producto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de persistencia para Producto
 */
public interface ProductoDAO {
    
    /**
     * Guardar un producto
     * @param producto El producto a guardar
     * @return El producto guardado
     */
    Producto guardar(Producto producto);
    
    /**
     * Buscar un producto por ID
     * @param id El ID del producto
     * @return Optional con el producto si existe
     */
    Optional<Producto> buscarPorId(Long id);
    
    /**
     * Buscar un producto por código de barras
     * @param codigoBarras El código de barras
     * @return Optional con el producto si existe
     */
    Optional<Producto> buscarPorCodigoBarras(String codigoBarras);
    
    /**
     * Obtener todos los productos
     * @return Lista de todos los productos
     */
    List<Producto> obtenerTodos();
    
    /**
     * Obtener productos activos
     * @return Lista de productos activos
     */
    List<Producto> obtenerActivos();
    
    /**
     * Obtener productos por categoría
     * @param categoria La categoría
     * @return Lista de productos de la categoría
     */
    List<Producto> obtenerPorCategoria(String categoria);
    
    /**
     * Obtener productos con stock bajo
     * @return Lista de productos que necesitan reposición
     */
    List<Producto> obtenerConStockBajo();
    
    /**
     * Buscar productos por nombre (búsqueda parcial)
     * @param nombre El nombre o parte del nombre
     * @return Lista de productos que coinciden
     */
    List<Producto> buscarPorNombre(String nombre);
    
    /**
     * Obtener productos por rango de precios
     * @param precioMinimo El precio mínimo
     * @param precioMaximo El precio máximo
     * @return Lista de productos en el rango
     */
    List<Producto> obtenerPorRangoPrecios(BigDecimal precioMinimo, BigDecimal precioMaximo);
    
    /**
     * Actualizar un producto
     * @param producto El producto actualizado
     * @return El producto actualizado
     */
    Producto actualizar(Producto producto);
    
    /**
     * Eliminar un producto
     * @param id El ID del producto a eliminar
     */
    void eliminar(Long id);
    
    /**
     * Verificar si existe un producto con el código de barras dado
     * @param codigoBarras El código de barras
     * @return true si existe, false en caso contrario
     */
    boolean existePorCodigoBarras(String codigoBarras);
    
    /**
     * Contar productos por categoría
     * @param categoria La categoría
     * @return El número de productos en la categoría
     */
    long contarPorCategoria(String categoria);
}
