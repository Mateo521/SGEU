package com.unsl.sgeu.services;

import com.unsl.sgeu.dto.*;
import com.unsl.sgeu.models.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehiculoService {

    // Altas/Edición
    VehiculoRegistroResultadoDTO registrarNuevoVehiculo(RegistroVehiculoFormDTO form);
    void actualizarVehiculo(String patenteOriginal, RegistroVehiculoFormDTO form);

    // Consultas básicas
    List<Vehiculo> obtenerTodos();
    boolean existePatente(String patente);
    Vehiculo guardarVehiculo(Vehiculo vehiculo);
    Vehiculo buscarPorPatente(String patente);
    Vehiculo buscarPorCodigoQr(String codigoQr);

    //busquedas y filtros
    List<Vehiculo> buscarVehiculosPorPatente(String patente);
    List<Vehiculo> obtenerPorEstacionamiento(Long idEstacionamiento);
    List<Vehiculo> buscarEnEstacionamiento(Long idEstacionamiento, String patente);
    List<Vehiculo> obtenerPorEstacionamientos(List<Long> idsEstacionamientos);
    List<Vehiculo> buscarEnEstacionamientos(List<Long> idsEstacionamientos, String patente);
    List<Vehiculo> obtenerPorGuardia(Long guardiaId);
    List<Vehiculo> buscarPorPatenteYGuardia(String patente, Long guardiaId);

    //Eliminacion
    ResultadoEliminacion eliminarVehiculo(String patente);
    ResultadoEliminacion eliminarVehiculoConHistorial(String patente);

    //QR y estado
    String generarCodigoQR(String patente);
    VehiculoQRResponseDTO obtenerDatosQR(String codigoQR, EstacionamientoDTO estacionamiento);
   // byte[] obtenerImagenQR(String patente);

    //
    long contarVehiculosEnEstacionamiento(Long idEstacionamiento);
    long contarVehiculosEnEstacionamientos(List<Long> idsEstacionamientos);
    String obtenerEstacionamientoOrigenVehiculo(String patente, Long guardiaId);

    // Paginacion
    Page<Vehiculo> obtenerTodosPaginado(Pageable pageable);
    Page<Vehiculo> buscarVehiculosPorPatentePaginado(String patente, Pageable pageable);
    Page<Vehiculo> buscarPorPatenteYGuardiaPaginado(String patente, Long guardiaId, Pageable pageable);
}
