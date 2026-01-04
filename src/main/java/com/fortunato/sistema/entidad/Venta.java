package com.fortunato.sistema.entidad;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una venta en el sistema del negocio
 */
@Entity
@Table(name = "ventas")
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long id;
    
    @Column(name = "numero_venta", unique = true)
    private String numeroVenta;
    
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caja", nullable = false)
    private Caja caja;
    
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();
    
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @Column(name = "descuento", precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;
    
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    
    @Column(name = "monto_pagado", precision = 10, scale = 2)
    private BigDecimal montoPagado = BigDecimal.ZERO;
    
    @Column(name = "vuelto", precision = 10, scale = 2)
    private BigDecimal vuelto = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago = MetodoPago.EFECTIVO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoVenta estado = EstadoVenta.PENDIENTE;
    
    @Column(name = "observaciones", length = 255)
    private String observaciones;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_venta", nullable = false)
    private TipoVenta tipo = TipoVenta.VENTA;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_original_id")
    private Venta ventaOriginal;
    
    // Enum para los estados de venta
    public enum EstadoVenta {
        PENDIENTE, COMPLETADA, CANCELADA
    }
    
    // Enum para el tipo de venta
    public enum TipoVenta {
        VENTA, DEVOLUCION
    }
    
    // Enum para el método de pago
    public enum MetodoPago {
        EFECTIVO, DEBITO
    }
    
    // Constructores
    public Venta() {
        this.fecha = LocalDateTime.now();
        this.numeroVenta = generarNumeroVenta();
    }
    
    public Venta(Caja caja) {
        this();
        this.caja = caja;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumeroVenta() {
        return numeroVenta;
    }
    
    public void setNumeroVenta(String numeroVenta) {
        this.numeroVenta = numeroVenta;
    }
    
    public LocalDateTime getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
    
    public Caja getCaja() {
        return caja;
    }
    
    public void setCaja(Caja caja) {
        this.caja = caja;
    }
    
    public List<DetalleVenta> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getDescuento() {
        return descuento;
    }
    
    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public BigDecimal getMontoPagado() {
        return montoPagado;
    }
    
    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }
    
    public BigDecimal getVuelto() {
        return vuelto;
    }
    
    public void setVuelto(BigDecimal vuelto) {
        this.vuelto = vuelto;
    }
    
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public EstadoVenta getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public TipoVenta getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoVenta tipo) {
        this.tipo = tipo;
    }
    
    public Venta getVentaOriginal() {
        return ventaOriginal;
    }
    
    public void setVentaOriginal(Venta ventaOriginal) {
        this.ventaOriginal = ventaOriginal;
    }
    
    // Métodos de negocio
    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        detalle.setVenta(this);
        recalcularTotales();
    }
    
    public void removerDetalle(DetalleVenta detalle) {
        detalles.remove(detalle);
        detalle.setVenta(null);
        recalcularTotales();
    }
    
    public void recalcularTotales() {
        // Calcular subtotal sumando todos los detalles
        this.subtotal = detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calcular total (subtotal - descuento)
        this.total = subtotal.subtract(descuento != null ? descuento : BigDecimal.ZERO);
    }
    
    public boolean estaCompletada() {
        return estado == EstadoVenta.COMPLETADA;
    }
    
    public boolean estaCancelada() {
        return estado == EstadoVenta.CANCELADA;
    }
    
    public void completar() {
        if (detalles.isEmpty()) {
            throw new IllegalStateException("No se puede completar una venta sin productos");
        }
        this.estado = EstadoVenta.COMPLETADA;
    }
    
    public void cancelar() {
        this.estado = EstadoVenta.CANCELADA;
    }

    //Devuelve el precio de venta de un producto
    public BigDecimal getPrecioVenta(Producto producto) {
        return detalles.stream()
                .filter(detalle -> detalle.getProducto().getId().equals(producto.getId()))
                .map(DetalleVenta::getPrecioUnitario)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }
    
    //Devuelve los productos de una venta realizada
    public int getProductoVendido(Producto producto) {
        return detalles.stream()
                .filter(detalle -> detalle.getProducto().getId().equals(producto.getId()))
                .mapToInt(DetalleVenta::getCantidad).sum();
    }
    
    //Devuelve la cantidad total de productos en la venta
    public int getCantidadTotalProductos() {
        return detalles.stream()
                .mapToInt(DetalleVenta::getCantidad)
                .sum();
    }
    
    private String generarNumeroVenta() {
        // Generar un número de venta único basado en timestamp
        return "V" + System.currentTimeMillis();
    }
    
    @Override
    public String toString() {
        return "Venta{" +
                "id=" + id +
                ", numeroVenta='" + numeroVenta + '\'' +
                ", fecha=" + fecha +
                ", subtotal=" + subtotal +
                ", descuento=" + descuento +
                ", total=" + total +
                ", estado=" + estado +
                '}';
    }
}