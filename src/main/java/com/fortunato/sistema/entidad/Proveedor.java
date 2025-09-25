package com.fortunato.sistema.entidad;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un proveedor en el sistema de kiosco
 */
@Entity
@Table(name = "proveedores")
public class Proveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long id;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "telefono", length = 255)
    private Long telefono;

    @Column(name = "mail", length = 255)
    private String mail;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Producto> productos = new ArrayList<>();
    
    // Constructores
    public Proveedor() {
    }
    
    public Proveedor(String nombre, Long telefono) {
        this();
        this.nombre = nombre;
        this.telefono = telefono;
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
    
    public Long getTelefono() {
        return telefono;
    }
    
    public void setTelefono(Long telefono) {
        this.telefono = telefono;
    }

    public String getMail() {
        return mail;
    }
    
    public void setMail(String mail) {
        this.mail = mail;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public List<Producto> getProductos() {
        return productos;
    }
    
    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
    
    // Métodos de negocio
    public void agregarProducto(Producto producto) {
        productos.add(producto);
        producto.setProveedor(this);
    }
    
    public void removerProducto(Producto producto) {
        productos.remove(producto);
        producto.setProveedor(null);
    }
    
    public int getCantidadProductos() {
        return productos.size();
    }
    
    public boolean tieneProductos() {
        return !productos.isEmpty();
    }
    
    @Override
    public String toString() {
        return nombre != null ? nombre : "Sin proveedor";
    }
}