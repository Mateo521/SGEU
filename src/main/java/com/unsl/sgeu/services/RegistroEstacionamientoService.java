package com.unsl.sgeu.services;

import com.unsl.sgeu.repositories.RegistroEstacionamientoRepository;
import com.unsl.sgeu.repositories.VehiculoRepository;
import com.unsl.sgeu.services.VehiculoService;
import com.unsl.sgeu.dto.EstacionamientoDTO;
import com.unsl.sgeu.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.transaction.annotation.Propagation;
import com.unsl.sgeu.dto.AccionEstacionamientoResultDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SessionAttributes
@Service
public interface RegistroEstacionamientoService {

  

    public RegistroEstacionamiento registrarEntrada(String patente, EstacionamientoDTO est, int modo);

    public RegistroEstacionamiento registrarSalida(String patente, EstacionamientoDTO est, int modo) ;


    public boolean esPar(String patente, EstacionamientoDTO est) ;

    public List<String> obtenerPatentesAdentroMasDeCuatroHoras(EstacionamientoDTO est) ;

   
    
    public void eliminarRegistrosPorPatente(String patente);

      public AccionEstacionamientoResultDTO procesarAccion(String patente, String accion, EstacionamientoDTO estacionamiento, int modo) ;


    public boolean vehiculoTieneRegistros(String patente);

    public boolean vehiculoEstaEstacionado(String patente);

    public long contarRegistrosPorPatente(String patente);

    public EstadoVehiculo obtenerEstadoActualVehiculo(String patente);

    public List<RegistroEstacionamiento> obtenerVehiculosActualmenteEnEstacionamiento(Long idEstacionamiento);

     public List<RegistroEstacionamiento> obtenerEgresosDelDia(Long idEstacionamiento);

    public List<RegistroEstacionamiento> obtenerIngresosDelDia(Long idEstacionamiento);

    public EstadoVehiculo obtenerEstadoDetallado(String patente);

    public String generarMensajeError(String patente);

   public boolean estacionamientoIsFull (EstacionamientoDTO est);

}
