package com.unsl.sgeu.servicesimpl;

import com.unsl.sgeu.dto.EstacionamientoDTO;
import com.unsl.sgeu.mappers.EstacionamientoMapper;
import com.unsl.sgeu.models.Estacionamiento;
import com.unsl.sgeu.repositories.EstacionamientoRepository;
import com.unsl.sgeu.services.EstacionamientoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.unsl.sgeu.repositories.TurnoRepository;  

import java.util.List;

import static com.unsl.sgeu.mappers.EstacionamientoMapper.toDTO;

@Service
@Transactional
public class EstacionamientoServiceImpl implements EstacionamientoService {

    private final EstacionamientoRepository repo;
   private final TurnoRepository turnoRepository;

      public EstacionamientoServiceImpl(EstacionamientoRepository repo, TurnoRepository turnoRepository) {
        this.repo = repo;
        this.turnoRepository = turnoRepository;
    }

    @Override
    public EstacionamientoDTO crear(EstacionamientoDTO dto) {
        // (Opcional) validar unicidad por nombre
        // if (repo.existsByNombreIgnoreCase(dto.getNombre())) throw new IllegalArgumentException("Ya existe un estacionamiento con ese nombre");

        Estacionamiento e = EstacionamientoMapper.toEntity(dto);
        // garantizar estado por defecto
        if (e.getEstado() == null) e.setEstado(true);
        e = repo.save(e);
        return toDTO(e);
    }

    @Override
    public EstacionamientoDTO editar(Long id, EstacionamientoDTO dto) {
        Estacionamiento e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estacionamiento no encontrado"));

        EstacionamientoMapper.copyToEntity(dto, e);
        e = repo.save(e);
        return toDTO(e);
    }

    @Override
    @Transactional(readOnly = true)
    public EstacionamientoDTO obtener(Long id) {
        Estacionamiento e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estacionamiento no encontrado"));
        return toDTO(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstacionamientoDTO> listarTodos() {
        return repo.findAll().stream().map(EstacionamientoMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstacionamientoDTO> listarActivos() {
        return repo.findByEstadoTrue().stream().map(EstacionamientoMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstacionamientoDTO> listarDesactivados() {
        return repo.findByEstadoFalse().stream().map(EstacionamientoMapper::toDTO).toList();
    }

    @Override
    public void cambiarEstado(Long id, boolean estado) {
        Estacionamiento e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estacionamiento no encontrado"));
        e.setEstado(estado);
        repo.save(e);
    }

    @Override
    public void eliminarFisico(Long id) {
        if (!repo.existsById(id)) throw new EntityNotFoundException("Estacionamiento no encontrado");
        repo.deleteById(id);
    }



      @Override
    @Transactional(readOnly = true)
    public List<Long> obtenerIdsPorEmpleado(Long empleadoId) {
        try {
            
            List<Long> ids = turnoRepository.findEstacionamientoIdsByEmpleadoId(empleadoId);
            
            return ids;
            
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Lista vacía en caso de error
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstacionamientoDTO> obtenerPorEmpleado(Long empleadoId) {
        try {
            
            List<Estacionamiento> estacionamientos = turnoRepository.findEstacionamientosByEmpleadoId(empleadoId);
            
            List<EstacionamientoDTO> dtos = estacionamientos.stream()
                    .map(EstacionamientoMapper::toDTO)
                    .toList();
                    
            return dtos;
                    
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean empleadoTieneAcceso(Long empleadoId, Long estacionamientoId) {
        try {
            boolean tieneAcceso = turnoRepository.existsByEmpleadoAndEstacionamiento(empleadoId, estacionamientoId);
            return tieneAcceso;
        } catch (Exception e) {
            System.err.println("Error verificando acceso: " + e.getMessage());
            return false;
        }
    }
    
    
}
