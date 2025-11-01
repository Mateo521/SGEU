package com.unsl.sgeu.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


// @Repository marca esta clase como un componente de acceso a datos (DAO manual).
// Spring la detecta automáticamente y le aplica manejo de excepciones (DataAccessException).
@Repository
// Implementa la interfaz StatsRepository, que define los métodos de estadísticas personalizadas.
// A diferencia de los Repository automáticos de Spring Data, acá se escriben las queries manualmente.
public class StatsRepositoryImpl implements StatsRepository {

    // EntityManager es el objeto principal de JPA para interactuar con la base de datos.
    // Permite crear consultas (SQL nativas), ejecutar inserts, updates, deletes, etc.
    // Spring inyecta automáticamente una instancia configurada del EntityManager
    // gracias a la anotación @PersistenceContext.
    @PersistenceContext
    private EntityManager em;
    // El EntityManager reemplaza a los métodos automáticos de JpaRepository (como findAll, save, etc.).
    // Acá se usa para construir y ejecutar las consultas de forma manual.

    // Convierte una fecha LocalDate al inicio del día (00:00:00)
    private LocalDateTime toStart(LocalDate d){
        return d == null ? null : LocalDateTime.of(d, LocalTime.MIN);
    }

    // Convierte una fecha LocalDate al final del día (23:59:59)
    private LocalDateTime toEnd(LocalDate d){
        return d == null ? null : LocalDateTime.of(d, LocalTime.MAX);
    }

    // ==========================================================
    // MÉTODOS PERSONALIZADOS (consultas estadísticas)
    // ==========================================================

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> getCantidadPorCategoria(LocalDate desde, LocalDate hasta, Long estId) {
        // Ejecuta la consulta nativa y devuelve los resultados sin procesar
        String base = "SELECT p.categoria AS categoria, COUNT(*) AS cantidad FROM registro_estacionamiento r " +
                    "JOIN vehiculo v ON v.patente = r.patente JOIN persona p ON p.dni = v.dni_duenio";
        List<String> conds = new ArrayList<>();
        if(desde != null) conds.add("r.fecha_hora >= :desde");
        if(hasta != null) conds.add("r.fecha_hora <= :hasta");
        conds.add("(:estId IS NULL OR r.id_est = :estId)");
        
        String sql = base + " WHERE " + String.join(" AND ", conds) + " GROUP BY p.categoria";

        Query q = em.createNativeQuery(sql);
        if(desde != null) q.setParameter("desde", toStart(desde));
        if(hasta != null) q.setParameter("hasta", toEnd(hasta));
        q.setParameter("estId", estId);

        return (List<Object[]>) q.getResultList();
    }

    // ----------------------------------------------------------

    // @Override
    // @SuppressWarnings("unchecked")
    // public List<Object[]> getIngresosPorEstacionamiento(LocalDate desde, LocalDate hasta, Long estId) {
    //     // Devuelve los ingresos totales por estacionamiento (raw data), sin calcular porcentajes ni transformar
    //     String base = "SELECT e.id_est, e.nombre, e.capacidad, COUNT(r.id_registro) AS ingresos FROM estacionamiento e " +
    //                 "LEFT JOIN registro_estacionamiento r ON r.id_est = e.id_est";
    //     List<String> conds = new ArrayList<>();
    //     if(desde != null) conds.add("r.fecha_hora >= :desde");
    //     if(hasta != null) conds.add("r.fecha_hora <= :hasta");
    //     conds.add("(:estId IS NULL OR r.id_est = :estId)");
        
    //     String sql = base + " WHERE " + String.join(" AND ", conds) + 
    //                 " GROUP BY e.id_est, e.nombre, e.capacidad ORDER BY ingresos DESC";

    //     Query q = em.createNativeQuery(sql);
    //     if(desde != null) q.setParameter("desde", toStart(desde));
    //     if(hasta != null) q.setParameter("hasta", toEnd(hasta));
    //     q.setParameter("estId", estId);

    //     return (List<Object[]>) q.getResultList();
    // }

    // // ----------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> diaSemanaMasIngresos(LocalDate desde, LocalDate hasta, Long estId) {
        // Retorna qué días de la semana se registran más ingresos.
        String base = "SELECT DAYNAME(r.fecha_hora) AS dia, COUNT(*) AS cnt FROM registro_estacionamiento r";
        List<String> conds = new ArrayList<>();
        if(desde != null) conds.add("r.fecha_hora >= :desde");
        if(hasta != null) conds.add("r.fecha_hora <= :hasta");
        conds.add("(:estId IS NULL OR r.id_est = :estId)");
        String sql = base + " WHERE " + String.join(" AND ", conds) + 
                     " GROUP BY dia ORDER BY cnt DESC";

        Query q = em.createNativeQuery(sql);
        if(desde != null) q.setParameter("desde", toStart(desde));
        if(hasta != null) q.setParameter("hasta", toEnd(hasta));
        q.setParameter("estId", estId);

        List<Object[]> rows = (List<Object[]>) q.getResultList();
        List<Map<String,Object>> res = new ArrayList<>();
        for(Object[] r: rows){
            Map<String,Object> m = new HashMap<>();
            m.put("dia", r[0]);
            m.put("cantidad", ((Number)r[1]).longValue());
            res.add(m);
        }
        return res;
    }

    // ----------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> getIngresosPorHora(LocalDate desde, LocalDate hasta, Long estId, int topN) {
        String base = "SELECT HOUR(r.fecha_hora) AS hora, COUNT(*) AS cnt FROM registro_estacionamiento r";
        List<String> conds = new ArrayList<>();
        if(desde != null) conds.add("r.fecha_hora >= :desde");
        if(hasta != null) conds.add("r.fecha_hora <= :hasta");
        conds.add("(:estId IS NULL OR r.id_est = :estId)");
        String sql = base + " WHERE " + String.join(" AND ", conds) + 
                    " GROUP BY hora ORDER BY cnt DESC" + (topN>0?" LIMIT "+topN:"");

        Query q = em.createNativeQuery(sql);
        if(desde != null) q.setParameter("desde", toStart(desde));
        if(hasta != null) q.setParameter("hasta", toEnd(hasta));
        q.setParameter("estId", estId);

        return (List<Object[]>) q.getResultList();
    }

    // ----------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> getIngresosPorEstacionamiento(LocalDate desde, LocalDate hasta, Long estId) {
        // Solo ejecuta la consulta y devuelve los resultados crudos
        String base = "SELECT e.id_est, e.nombre, e.capacidad, COUNT(r.id_registro) AS ingresos FROM estacionamiento e " +
                    "LEFT JOIN registro_estacionamiento r ON r.id_est = e.id_est";
        List<String> conds = new ArrayList<>();
        if(desde != null) conds.add("r.fecha_hora >= :desde");
        if(hasta != null) conds.add("r.fecha_hora <= :hasta");
        conds.add("(:estId IS NULL OR r.id_est = :estId)");
        
        String sql = base + " WHERE e.estado = 1 AND " + String.join(" AND ", conds) +
                    " GROUP BY e.id_est, e.nombre, e.capacidad";

        Query q = em.createNativeQuery(sql);
        if(desde != null) q.setParameter("desde", toStart(desde));
        if(hasta != null) q.setParameter("hasta", toEnd(hasta));
        q.setParameter("estId", estId);

        return (List<Object[]>) q.getResultList();
    }


    // ----------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> getIngresosPorDia(LocalDate desde, LocalDate hasta, Long estId) {
        String base = "SELECT DATE(r.fecha_hora) AS dia, COUNT(*) AS cnt FROM registro_estacionamiento r";
        List<String> conds = new ArrayList<>();
        if(desde != null) conds.add("r.fecha_hora >= :desde");
        if(hasta != null) conds.add("r.fecha_hora <= :hasta");
        conds.add("(:estId IS NULL OR r.id_est = :estId)");
        String sql = base + " WHERE " + String.join(" AND ", conds) + 
                    " GROUP BY dia ORDER BY dia ASC";

        Query q = em.createNativeQuery(sql);
        if(desde != null) q.setParameter("desde", toStart(desde));
        if(hasta != null) q.setParameter("hasta", toEnd(hasta));
        q.setParameter("estId", estId);

        return (List<Object[]>) q.getResultList();
    }


    // ----------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> getConteoManualVsQr(LocalDate desde, LocalDate hasta, Long estId) {
        String base = "SELECT COALESCE(r.modo,'MANUAL') AS modo, COUNT(*) AS cnt FROM registro_estacionamiento r";
        List<String> conds = new ArrayList<>();
        if(desde != null) conds.add("r.fecha_hora >= :desde");
        if(hasta != null) conds.add("r.fecha_hora <= :hasta");
        conds.add("(:estId IS NULL OR r.id_est = :estId)");
        String sql = base + " WHERE " + String.join(" AND ", conds) + " GROUP BY modo";

        Query q = em.createNativeQuery(sql);
        if(desde != null) q.setParameter("desde", toStart(desde));
        if(hasta != null) q.setParameter("hasta", toEnd(hasta));
        q.setParameter("estId", estId);

        return (List<Object[]>) q.getResultList();
    }


    // ----------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> getDistribucionPorTipoVehiculo(LocalDate desde, LocalDate hasta, Long estId) {
        String base = "SELECT COALESCE(t.nombre, v.tipo) AS tipo, COUNT(*) AS cantidad FROM registro_estacionamiento r " +
                    "JOIN vehiculo v ON v.patente = r.patente LEFT JOIN vehiculo_tipo t ON t.id_vehiculo_tipo = v.id_vehiculo_tipo";
        List<String> conds = new ArrayList<>();
        if(desde != null) conds.add("r.fecha_hora >= :desde");
        if(hasta != null) conds.add("r.fecha_hora <= :hasta");
        conds.add("(:estId IS NULL OR r.id_est = :estId)");
        String sql = base + " WHERE " + String.join(" AND ", conds) + " GROUP BY tipo ORDER BY cantidad DESC";

        Query q = em.createNativeQuery(sql);
        if(desde != null) q.setParameter("desde", toStart(desde));
        if(hasta != null) q.setParameter("hasta", toEnd(hasta));
        q.setParameter("estId", estId);

        return (List<Object[]>) q.getResultList();
    }

}
