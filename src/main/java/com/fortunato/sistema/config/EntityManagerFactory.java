package com.fortunato.sistema.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

public class EntityManagerFactory {
    private static EntityManagerFactory instance;
    private jakarta.persistence.EntityManagerFactory emf;
    
    private EntityManagerFactory() {
        // Constructor privado
    }
    
    public static EntityManagerFactory getInstance() {
        if (instance == null) {
            instance = new EntityManagerFactory();
        }
        return instance;
    }
    
    public EntityManager createEntityManager() {
        if (emf == null) {
            try {
                emf = Persistence.createEntityManagerFactory("kiosco-persistence-unit");
                System.out.println("EntityManagerFactory creado exitosamente");
            } catch (Exception e) {
                System.err.println("Error al crear EntityManagerFactory: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("No se pudo crear EntityManagerFactory", e);
            }
        }
        return emf.createEntityManager();
    }
    
    public boolean testConnection() {
        EntityManager em = null;
        try {
            em = createEntityManager();
            em.createNativeQuery("SELECT 1").getSingleResult();
            System.out.println("Conexión a la base de datos exitosa");
            return true;
        } catch (Exception e) {
            System.err.println("Error de conexión a la base de datos: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}