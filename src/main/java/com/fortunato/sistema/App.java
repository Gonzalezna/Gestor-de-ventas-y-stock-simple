package com.fortunato.sistema;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.fortunato.sistema.entidad.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Clase principal para probar el sistema de gestión de kiosco
 */
public class App {
    
    private static SessionFactory sessionFactory;
    
    public static void main(String[] args) {
        try {
            // Configurar Hibernate
            configurarHibernate();
            
            // Crear datos de prueba
            crearDatosDePrueba();
            
            // Realizar operaciones de ejemplo
            realizarOperacionesEjemplo();
            
        } catch (Exception e) {
            System.err.println("Error en la aplicación: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }
    
    private static void configurarHibernate() {
        try {
            Configuration configuration = new Configuration();
            
            // Verificar si estamos en Docker
            String dbHost = System.getenv("DB_HOST");
            if (dbHost != null && dbHost.equals("mysql")) {
                // Estamos en Docker, usar configuración específica
                configuration.configure("hibernate-docker.cfg.xml");
                System.out.println("🐳 Configurando Hibernate para Docker...");
            } else {
                // Configuración local
                configuration.configure("hibernate.cfg.xml");
                System.out.println("💻 Configurando Hibernate para entorno local...");
            }
            
            sessionFactory = configuration.buildSessionFactory();
            System.out.println("✅ Hibernate configurado correctamente");
        } catch (Exception e) {
            System.err.println("Error configurando Hibernate: " + e.getMessage());
            throw e;
        }
    }
    
    private static void crearDatosDePrueba() {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            
            // Crear productos de prueba
            Producto producto1 = new Producto("Coca Cola 500ml", "Bebida gaseosa", 
                new BigDecimal("150.00"), 50, "Bebidas");
            producto1.setCodigoBarras("1234567890123");
            producto1.setStockMinimo(10);
            
            Producto producto2 = new Producto("Galletas Oreo", "Galletas rellenas de crema", 
                new BigDecimal("80.00"), 30, "Golosinas");
            producto2.setCodigoBarras("2345678901234");
            producto2.setStockMinimo(5);
            
            Producto producto3 = new Producto("Cigarrillos Marlboro", "Cigarrillos", 
                new BigDecimal("450.00"), 20, "Tabaco");
            producto3.setCodigoBarras("3456789012345");
            producto3.setStockMinimo(3);
            
            session.persist(producto1);
            session.persist(producto2);
            session.persist(producto3);
            
            // Crear caja de prueba
            Caja caja = new Caja(1, new BigDecimal("1000.00"));
            session.persist(caja);
            session.getTransaction().commit();
            System.out.println("✅ Datos de prueba creados correctamente");
            
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error creando datos de prueba: " + e.getMessage());
            throw e;
        } finally {
            session.close();
        }
    }
    
    private static void realizarOperacionesEjemplo() {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            
            // Buscar productos
            List<Producto> productos = session.createQuery("FROM Producto", Producto.class).list();
            System.out.println("\n📦 Productos disponibles:");
            productos.forEach(System.out::println);
            
            // Buscar caja
            Caja caja = session.createQuery("FROM Caja WHERE numeroCaja = 1", Caja.class).uniqueResult();
            System.out.println("\n💰 Caja encontrada: " + caja);
            
            // Crear una venta de ejemplo
            Venta venta = new Venta(caja);
            
            // Agregar productos a la venta
            Producto cocaCola = productos.get(0);
            Producto oreo = productos.get(1);
            
            DetalleVenta detalle1 = new DetalleVenta(venta, cocaCola, 2);
            DetalleVenta detalle2 = new DetalleVenta(venta, oreo, 3);
            
            venta.agregarDetalle(detalle1);
            venta.agregarDetalle(detalle2);
            
            // Completar la venta
            venta.completar();
            
            session.persist(venta);
            
            // Actualizar stock de productos
            cocaCola.reducirStock(2);
            oreo.reducirStock(3);
            
            session.merge(cocaCola);
            session.merge(oreo);
            
            session.getTransaction().commit();
            
            System.out.println("\n🛒 Venta realizada:");
            System.out.println("Número de venta: " + venta.getNumeroVenta());
            System.out.println("Total: $" + venta.getTotal());
            System.out.println("Cantidad de productos vendidos: " + venta.getCantidadTotalProductos());
            
            // Mostrar detalles de la venta
            System.out.println("\n📋 Detalles de la venta:");
            venta.getDetalles().forEach(System.out::println);
            
            System.out.println("\n📊 Stock actualizado:");
            System.out.println("Coca Cola - Stock: " + cocaCola.getStock());
            System.out.println("Oreo - Stock: " + oreo.getStock());
            
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error realizando operaciones: " + e.getMessage());
            throw e;
        } finally {
            session.close();
        }
    }
}
