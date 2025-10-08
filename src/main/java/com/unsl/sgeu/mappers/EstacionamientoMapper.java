package com.unsl.sgeu.mappers;

import com.unsl.sgeu.dto.EstacionamientoDTO;
import com.unsl.sgeu.models.Estacionamiento;

public class EstacionamientoMapper {

    public static EstacionamientoDTO toDTO(Estacionamiento e) {
        if (e == null) return null;
        return new EstacionamientoDTO(
            e.getIdEst(),           // 👈 actualizado
            e.getNombre(),
            e.getDireccion(),
            e.getCapacidad(),
            e.getEstado()
        );
    }

    public static Estacionamiento toEntity(EstacionamientoDTO dto) {
        if (dto == null) return null;
        Estacionamiento e = new Estacionamiento();
        e.setIdEst(dto.getId());   // 👈 actualizado
        e.setNombre(dto.getNombre());
        e.setDireccion(dto.getDireccion());
        e.setCapacidad(dto.getCapacidad());
        e.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        return e;
    }

    public static void copyToEntity(EstacionamientoDTO dto, Estacionamiento e) {
        if (dto.getNombre()    != null) e.setNombre(dto.getNombre());
        if (dto.getDireccion() != null) e.setDireccion(dto.getDireccion());
        if (dto.getCapacidad() != null) e.setCapacidad(dto.getCapacidad());
        if (dto.getEstado()    != null) e.setEstado(dto.getEstado());
    }
}
