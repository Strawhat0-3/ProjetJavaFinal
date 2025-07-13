package org.example.projetjavafinal.dao;

import org.example.projetjavafinal.dao.GenericDAO;
import org.example.projetjavafinal.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

public abstract class GenericDAOImpl<T> implements GenericDAO<T> {
    private final Class<T> entityClass;

    // CORRECTION : Appel direct à la méthode statique
    protected SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }

    @SuppressWarnings("unchecked")
    public GenericDAOImpl() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    protected <R> R executeInTransaction(TransactionOperation<R> operation) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            R result = operation.execute(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    protected void executeInTransactionWithoutResult(VoidTransactionOperation operation) {
        executeInTransaction(session -> {
            operation.execute(session);
            return null;
        });
    }

    @Override
    public T save(T entity) {
        return executeInTransaction(session -> {
            session.persist(entity);
            return entity;
        });
    }

    @Override
    public Optional<T> findById(Long id) {
        try (Session session = getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(entityClass, id));
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("from " + entityClass.getName(), entityClass).list();
        }
    }

    @Override
    public void delete(T entity) {
        executeInTransactionWithoutResult(session -> session.remove(entity));
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }

    @Override
    public void update(T entity) {
        executeInTransactionWithoutResult(session -> session.merge(entity));
    }

    @FunctionalInterface
    protected interface TransactionOperation<R> {
        R execute(Session session);
    }

    @FunctionalInterface
    protected interface VoidTransactionOperation {
        void execute(Session session);
    }
}