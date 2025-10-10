package com.unsl.sgeu.services;

import com.unsl.sgeu.dto.EstacionamientoDTO;

import java.util.List;

public interface EstacionamientoService {


    

    EstacionamientoDTO crear(EstacionamientoDTO dto);

    EstacionamientoDTO editar(Long id, EstacionamientoDTO dto);

    EstacionamientoDTO obtener(Long id);

    List<EstacionamientoDTO> listarTodos();     // si querés solo activos, podés separar

    List<EstacionamientoDTO> listarActivos();

    List<EstacionamientoDTO> listarDesactivados();

    void cambiarEstado(Long id, boolean estado); // true=activar, false=desactivar

    void eliminarFisico(Long id); // opcional (borrado físico)
}
