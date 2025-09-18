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
            emf = Persistence.createEntityManagerFactory("kiosco-persistence-unit");
        }
        return emf.createEntityManager();
    }
}