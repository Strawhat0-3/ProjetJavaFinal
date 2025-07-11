package org.example.projetjavafinal.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    private static HibernateUtil instance;
    private static SessionFactory sessionFactory;
    
    // Constructeur privé
    private HibernateUtil() {
        buildSessionFactory();
    }
    
    // Méthode getInstance thread-safe
    public static synchronized HibernateUtil getInstance() {
        if (instance == null) {
            instance = new HibernateUtil();
        }
        return instance;
    }
    
    private void buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            throw new RuntimeException("Erreur de création de la SessionFactory.", e);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}