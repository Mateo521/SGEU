package com.unsl.sgeu.services;


import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.repositories.PersonaRepository;
import com.unsl.sgeu.repositories.PersonaRepositoryImpl;

import org.springframework.stereotype.Service;

@Service
public class PersonaService {
    
    private final PersonaRepositoryImpl personaRepository;
    
    public PersonaService(PersonaRepositoryImpl personaRepository) {
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
}
