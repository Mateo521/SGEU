package com.unsl.sgeu.dto;

import java.util.List;

public class StatsResponseDTO {
    private List<CategoriaStatsDTO> categorias;
    private EstacionamientoStatsDTO estacionamientoTop;
    private List<EvolucionDTO> evolucion;
    private List<ModoConteoDTO> modoConteo;
    private List<HorarioDTO> horariosPico;
    private Double promedioEstancia;
    // arrays para graficas
    private List<String> categoriasLabels;
    private List<Number> categoriasData;
    private List<String> ocupacionLabels;
    private List<Number> ocupacionData;
    // lista detallada de ocupacion por estacionamiento (nombre, capacidad, ingresos, porcentaje)
    private List<EstacionamientoStatsDTO> porcentajeOcupacion;
    private List<String> evolucionLabels;
    private List<Number> evolucionData;

    public StatsResponseDTO() {}

    // getters / setters
    public List<CategoriaStatsDTO> getCategorias() { return categorias; }
    public void setCategorias(List<CategoriaStatsDTO> categorias) { this.categorias = categorias; }
    public EstacionamientoStatsDTO getEstacionamientoTop() { return estacionamientoTop; }
    public void setEstacionamientoTop(EstacionamientoStatsDTO estacionamientoTop) { this.estacionamientoTop = estacionamientoTop; }
    public List<EvolucionDTO> getEvolucion() { return evolucion; }
    public void setEvolucion(List<EvolucionDTO> evolucion) { this.evolucion = evolucion; }
    public List<ModoConteoDTO> getModoConteo() { return modoConteo; }
    public void setModoConteo(List<ModoConteoDTO> modoConteo) { this.modoConteo = modoConteo; }
    public List<HorarioDTO> getHorariosPico() { return horariosPico; }
    public void setHorariosPico(List<HorarioDTO> horariosPico) { this.horariosPico = horariosPico; }
    public Double getPromedioEstancia() { return promedioEstancia; }
    public void setPromedioEstancia(Double promedioEstancia) { this.promedioEstancia = promedioEstancia; }
    public List<String> getCategoriasLabels() { return categoriasLabels; }
    public void setCategoriasLabels(List<String> categoriasLabels) { this.categoriasLabels = categoriasLabels; }
    public List<Number> getCategoriasData() { return categoriasData; }
    public void setCategoriasData(List<Number> categoriasData) { this.categoriasData = categoriasData; }
    public List<String> getOcupacionLabels() { return ocupacionLabels; }
    public void setOcupacionLabels(List<String> ocupacionLabels) { this.ocupacionLabels = ocupacionLabels; }
    public List<Number> getOcupacionData() { return ocupacionData; }
    public void setOcupacionData(List<Number> ocupacionData) { this.ocupacionData = ocupacionData; }
    public List<EstacionamientoStatsDTO> getPorcentajeOcupacion() { return porcentajeOcupacion; }
    public void setPorcentajeOcupacion(List<EstacionamientoStatsDTO> porcentajeOcupacion) { this.porcentajeOcupacion = porcentajeOcupacion; }
    public List<String> getEvolucionLabels() { return evolucionLabels; }
    public void setEvolucionLabels(List<String> evolucionLabels) { this.evolucionLabels = evolucionLabels; }
    public List<Number> getEvolucionData() { return evolucionData; }
    public void setEvolucionData(List<Number> evolucionData) { this.evolucionData = evolucionData; }
}
