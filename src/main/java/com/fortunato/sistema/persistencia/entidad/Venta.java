package com.fortunato.sistema.persistencia.entidad;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una venta en el sistema de kiosco
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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoVenta estado = EstadoVenta.PENDIENTE;
    
    @Column(name = "observaciones", length = 255)
    private String observaciones;
    
    // Enum para los estados de venta
    public enum EstadoVenta {
        PENDIENTE, COMPLETADA, CANCELADA
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
