package com.unsl.sgeu.services;

import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.repositories.PersonaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    public Persona guardarPersona(Persona persona) {
        return personaRepository.save(persona);
    }

    public boolean existePersona(Long dni) {
        return personaRepository.existsByDni(dni);
    }

    public Persona buscarPorDni(Long dni) {
        return personaRepository.findById(dni).orElse(null);
    }

    /**
     * Busca múltiples personas por sus DNIs (evita N+1 queries).
     * @param dnis Colección de DNIs
     * @return Lista de personas encontradas
     */
    public List<Persona> buscarPorDnis(Collection<Long> dnis) {
        if (dnis == null || dnis.isEmpty()) {
            return Collections.emptyList();
        }
        return personaRepository.findByDniIn(dnis);
    }
}
