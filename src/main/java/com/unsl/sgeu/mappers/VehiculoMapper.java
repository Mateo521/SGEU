package com.unsl.sgeu.mappers;

import com.unsl.sgeu.dto.VehiculoDTO;
import com.unsl.sgeu.models.Vehiculo;
import org.springframework.stereotype.Component;

@Component
public class VehiculoMapper {

    public Vehiculo toEntity(VehiculoDTO dto, Long dniDuenio, String codigoQr) {
        Vehiculo v = new Vehiculo();
        v.setPatente(dto.getPatente());
        v.setModelo(dto.getModelo());
        v.setColor(dto.getColor());
        v.setTipo(dto.getTipoNombre());
        v.setIdVehiculoTipo(mapearTipoAId(dto.getTipoNombre()));
        v.setCodigoQr(codigoQr);
        v.setDniDuenio(dniDuenio);
        return v;
    }

    private Integer mapearTipoAId(String tipo) {
        if (tipo == null) return null;
        switch (tipo.toLowerCase()) {
            case "auto": return 1;
            case "moto": return 2;
            default: return null;
        }
    }
}
