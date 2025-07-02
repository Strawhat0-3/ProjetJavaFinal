package org.example.projetjavafinal.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDAO<T> {
    T save(T entity);
    Optional<T> findById(Long id);
    List<T> findAll();
    void delete(T entity);
    void deleteById(Long id);
    void update(T entity);
}