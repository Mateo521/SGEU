package com.unsl.sgeu.services;

import com.unsl.sgeu.dto.TurnoCreateDTO;
import com.unsl.sgeu.dto.TurnoDTO;
import org.springframework.data.domain.Page;

public interface TurnoService {

    /* ===================== LISTADOS ===================== */
    Page<TurnoDTO> list(Long empleadoId, Long estId, String fecha, Integer page, Integer size);

    Page<TurnoDTO> listRange(Long empleadoId, Long estId, String desde, String hasta, Integer page, Integer size);

    /* ===================== CREAR ===================== */
    TurnoDTO create(TurnoCreateDTO dto);

    /* ===================== ACTUALIZAR ===================== */
    TurnoDTO update(Long id, TurnoCreateDTO dto);

    TurnoDTO finalizar(Long id);

    /* ===================== DELETE / GET ===================== */
    void delete(Long id);

    TurnoDTO get(Long id);
}
