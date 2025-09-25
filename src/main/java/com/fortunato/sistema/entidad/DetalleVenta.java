package com.fortunato.sistema.entidad;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidad que representa un detalle de venta (producto vendido en una venta específica)
 */
@Entity
@Table(name = "detalles_venta")
public class DetalleVenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
    
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    // Constructores
    public DetalleVenta() {
    }
    
    public DetalleVenta(Venta venta, Producto producto, Integer cantidad) {
        this.venta = venta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecioVenta();
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
    
    // Métodos de callback
    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Venta getVenta() {
        return venta;
    }
    
    public void setVenta(Venta venta) {
        this.venta = venta;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    @Override
    public String toString() {
        return "DetalleVenta{" +
                "id=" + id +
                ", producto=" + (producto != null ? producto.getNombre() : "null") +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                '}';
    }
}