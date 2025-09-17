package com.fortunato.sistema.persistencia.entidad;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una caja registradora en el sistema de kiosco
 */
@Entity
@Table(name = "Cajas")
public class Caja {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caja")
    private Long id;
    
    @Column(name = "saldo_inicial", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoInicial = BigDecimal.ZERO;
    
    @Column(name = "saldo_actual", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoActual = BigDecimal.ZERO;
    
    @Column(name = "fecha_apertura")
    private LocalDateTime fechaApertura;
    
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "estado")
    private Boolean estado = false;
    
    @OneToMany(mappedBy = "caja", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Venta> ventas = new ArrayList<>();
    
    // Constructores
    public Caja() {
    }
    
    public Caja(Integer numeroCaja, BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
        this.saldoActual = saldoInicial;
        this.fechaApertura = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }
    
    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }
    
    public BigDecimal getSaldoActual() {
        return saldoActual;
    }
    
    public void setSaldoActual(BigDecimal saldoActual) {
        this.saldoActual = saldoActual;
    }
    
    public Boolean getEstado() {
        return estado;
    }
    
    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }
    
    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }
    
    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }
    
    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }
    
    public List<Venta> getVentas() {
        return ventas;
    }
    
    public void setVentas(List<Venta> ventas) {
        this.ventas = ventas;
    }
    
    // Métodos de negocio
    public boolean estaAbierta() {
        return estado && fechaApertura != null && fechaCierre == null;
    }
    
    public void abrir() {
        this.estado = true;
        this.fechaApertura = LocalDateTime.now();
        this.fechaCierre = null;
        this.saldoActual = this.saldoInicial;
    }
    
    public void cerrar() {
        this.estado = false;
        this.fechaCierre = LocalDateTime.now();
    }
    
    public BigDecimal calcularTotalVentas() {
        return ventas.stream()
                .filter(v -> v.getFecha() != null)
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void agregarVenta(Venta venta) {
        ventas.add(venta);
        venta.setCaja(this);
    }
    
    public BigDecimal getDiferencia() {
        return saldoActual.subtract(saldoInicial).subtract(calcularTotalVentas());
    }
    
    @Override
    public String toString() {
        return "Caja{" +
                "id=" + id +
                ", saldoInicial=" + saldoInicial +
                ", saldoActual=" + saldoActual +
                ", estado=" + estado +
                ", fechaApertura=" + fechaApertura +
                '}';
    }
}

