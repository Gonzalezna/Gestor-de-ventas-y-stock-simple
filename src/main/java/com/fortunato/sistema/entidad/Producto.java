package com.fortunato.sistema.entidad;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un producto en el sistema de kiosco
 */
@Entity
@Table(name = "productos")
public class Producto {
    
    @Id
    @PrimaryKeyJoinColumn
    @Column(name = "id_producto")
    private Long id; // Este será el código de barras
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "descripcion", length = 255)
    private String descripcion;
    
    @Column(name = "precio de compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "precio de venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;
    
    @Column(name = "stock", nullable = false)
    private Integer stock;
    
    @Column(name = "categoria", length = 50)
    private String categoria;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor", nullable = true)
    private Proveedor proveedor;
    
    // Constructores
    public Producto() {
        this.fechaCreacion = LocalDateTime.now();
    }
    
    public Producto(Long id, String nombre, String descripcion, BigDecimal precioCompra, BigDecimal precioVenta, Integer stock, String categoria) {
        this();
        this.id = id; // Código de barras
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.categoria = categoria;
    }
    
    // Métodos de callback
    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }
    
    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }
    
    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }
    
    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }
    
    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
    
    public Proveedor getProveedor() {
        return proveedor;
    }
    
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
    
    // Métodos de negocio
    public boolean estaDisponible() {
        return activo && stock > 0;
    }
    
    public boolean necesitaReposicion() {
        return stock <= 3;
    }
    
    public void reducirStock(int cantidad) {
        if (stock >= cantidad) {
            stock -= cantidad;
        } else {
            throw new IllegalArgumentException("Stock insuficiente");
        }
    }
    
    public void aumentarStock(int cantidad) {
        stock += cantidad;
    }
    
    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precioCompra=" + precioCompra +
                ", precioVenta=" + precioVenta +
                ", stock=" + stock +
                ", categoria='" + categoria + '\'' +
                ", activo=" + activo +
                '}';
    }
}
