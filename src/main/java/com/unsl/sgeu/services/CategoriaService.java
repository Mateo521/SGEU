package com.unsl.sgeu.services;
import java.util.List;

import org.springframework.stereotype.Service;

import com.unsl.sgeu.*;
import com.unsl.sgeu.models.Categoria;
import com.unsl.sgeu.repositories.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria getById(Short id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    public Categoria getOrCreateByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
        return categoriaRepository.findByNombreIgnoreCase(nombre.trim())
                .orElseGet(() -> categoriaRepository.save(new Categoria(nombre.trim())));
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }
}
