package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Categoria;
import java.util.*;

public interface CategoriaRepository {
    List<Categoria> findAll();
    Optional<Categoria> findById(short id);
    Optional<Categoria> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
    Categoria save(Categoria categoria);
    void deleteById(short id);
}
