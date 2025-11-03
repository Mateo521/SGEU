package com.unsl.sgeu.mappers;

import com.unsl.sgeu.dto.PersonaDTO;
import com.unsl.sgeu.models.Persona;
import org.springframework.stereotype.Component;

@Component
public class PersonaMapper {

    public Persona toEntity(PersonaDTO dto) {
        Persona p = new Persona();
        p.setDni(dto.getDni());
        p.setNombre(dto.getNombre());
        p.setTelefono(dto.getTelefono());
        p.setEmail(dto.getEmail());
        p.setCategoria(dto.getCategoriaNombre());
        p.setIdCategoria(mapearCategoriaAId(dto.getCategoriaNombre()));
        return p;
    }

    private Integer mapearCategoriaAId(String categoria) {
        if (categoria == null) return null;
        switch (categoria.toLowerCase()) {
            case "docente": return 1;
            case "no_docente": return 2;
            case "estudiante": return 3;
            case "visitante": return 4;
            default: return null;
        }
    }
}
