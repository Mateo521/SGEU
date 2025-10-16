package com.unsl.sgeu.services;

import com.unsl.sgeu.dto.EstacionamientoDTO;

import java.util.List;

public interface EstacionamientoService {


    

    EstacionamientoDTO crear(EstacionamientoDTO dto);

    EstacionamientoDTO editar(Long id, EstacionamientoDTO dto);

    EstacionamientoDTO obtener(Long id);

    List<EstacionamientoDTO> listarTodos();     

    List<EstacionamientoDTO> listarActivos();

    List<EstacionamientoDTO> listarDesactivados();

    void cambiarEstado(Long id, boolean estado);  

    void eliminarFisico(Long id); // opcional (borrado físico)

 
    List<Long> obtenerIdsPorEmpleado(Long empleadoId);
    
  
    List<EstacionamientoDTO> obtenerPorEmpleado(Long empleadoId);
    
 
    boolean empleadoTieneAcceso(Long empleadoId, Long estacionamientoId);

}
