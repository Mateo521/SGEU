package com.unsl.sgeu.services;

import com.unsl.sgeu.models.VehiculoTipo;
import com.unsl.sgeu.repositories.VehiculoTipoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehiculoTipoService {

    private final VehiculoTipoRepository repo;

    public VehiculoTipoService(VehiculoTipoRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public VehiculoTipo getById(Short id) {
        if (id == null) return null;
        return repo.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public VehiculoTipo getByNombre(String nombre) {
        if (nombre == null) return null;
        String n = nombre.trim();
        if (n.isEmpty()) return null;
        return repo.findByNombreIgnoreCase(n).orElse(null);
    }

    @Transactional
    public VehiculoTipo getOrCreateByNombre(String nombre) {
        if (nombre == null) return null;
        String n = nombre.trim();
        if (n.isEmpty()) return null;
        return repo.findByNombreIgnoreCase(n)
                   .orElseGet(() -> repo.save(new VehiculoTipo(n)));
    }

    @Transactional(readOnly = true)
    public List<VehiculoTipo> listarTodos() {
        return repo.findAll();
    }
}
