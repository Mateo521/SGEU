package com.unsl.sgeu.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Repository
public class StatsRepositoryImpl implements StatsRepository {

    @PersistenceContext
    private EntityManager em;

    private LocalDateTime toStart(LocalDate d){
        return d == null ? null : LocalDateTime.of(d, LocalTime.MIN);
    }
    private LocalDateTime toEnd(LocalDate d){
        return d == null ? null : LocalDateTime.of(d, LocalTime.MAX);
    }

    @Override
    public List<Map<String, Object>> porcentajeCategorias(LocalDate desde, LocalDate hasta, Long estId) {
    String base = "SELECT p.categoria AS categoria, COUNT(*) AS cantidad FROM registro_estacionamiento r " +
        "JOIN vehiculo v ON v.patente = r.patente JOIN persona p ON p.dni = v.dni_duenio";
    java.util.List<String> conds = new java.util.ArrayList<>();
    if(desde != null) conds.add("r.fecha_hora >= :desde");
    if(hasta != null) conds.add("r.fecha_hora <= :hasta");
    // siempre añadir la condición de estacionamiento con la forma segura
    conds.add("(:estId IS NULL OR r.id_est = :estId)");
    String sql = base + " WHERE " + String.join(" AND ", conds) + " GROUP BY p.categoria";

    Query q = em.createNativeQuery(sql);
    if(desde != null) q.setParameter("desde", toStart(desde));
    if(hasta != null) q.setParameter("hasta", toEnd(hasta));
    q.setParameter("estId", estId);

    @SuppressWarnings("unchecked")
    List<Object[]> rows = (List<Object[]>) q.getResultList();
        long total = 0;
        for(Object[] r: rows) total += ((Number)r[1]).longValue();

        List<Map<String,Object>> res = new ArrayList<>();
        for(Object[] r: rows){
            String cat = r[0] == null ? "Sin categoria" : r[0].toString();
            long cnt = ((Number)r[1]).longValue();
            double pct = total == 0 ? 0.0 : (cnt * 100.0 / total);
            Map<String,Object> m = new HashMap<>();
            m.put("categoria", cat);
            m.put("cantidad", cnt);
            m.put("porcentaje", Math.round(pct*100.0)/100.0);
            res.add(m);
        }
        return res;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> estacionamientoConMasIngresos(LocalDate desde, LocalDate hasta, Long estId) {
        String base = "SELECT e.id_est, e.nombre, e.capacidad, COUNT(r.id_registro) AS ingresos FROM estacionamiento e LEFT JOIN registro_estacionamiento r ON r.id_est = e.id_est";
    java.util.List<String> conds = new java.util.ArrayList<>();
    if(desde != null) conds.add("r.fecha_hora >= :desde");
    if(hasta != null) conds.add("r.fecha_hora <= :hasta");
    conds.add("(:estId IS NULL OR r.id_est = :estId)");
    String sql = base + " WHERE " + String.join(" AND ", conds) + " GROUP BY e.id_est, e.nombre, e.capacidad ORDER BY ingresos DESC LIMIT 1";

    Query q = em.createNativeQuery(sql);
    if(desde != null) q.setParameter("desde", toStart(desde));
    if(hasta != null) q.setParameter("hasta", toEnd(hasta));
    q.setParameter("estId", estId);

    List<Object[]> rows = (List<Object[]>) q.getResultList();
        if(rows.isEmpty()) return Map.of();
        Object[] r = rows.get(0);
        Map<String,Object> m = new HashMap<>();
        m.put("id", ((Number)r[0]).longValue());
        m.put("nombre", r[1]);
        m.put("capacidad", r[2] == null ? 0 : ((Number)r[2]).intValue());
        m.put("ingresos", ((Number)r[3]).longValue());
        double pct = m.get("capacidad") == null || ((int)m.get("capacidad")) == 0 ? 0.0 : ((long)m.get("ingresos")*100.0/((int)m.get("capacidad")));
        m.put("porcentaje_capacidad", Math.round(pct*100.0)/100.0);
        return m;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> diaSemanaMasIngresos(LocalDate desde, LocalDate hasta, Long estId) {
        String base = "SELECT DAYNAME(r.fecha_hora) AS dia, COUNT(*) AS cnt FROM registro_estacionamiento r";
    java.util.List<String> conds = new java.util.ArrayList<>();
    if(desde != null) conds.add("r.fecha_hora >= :desde");
    if(hasta != null) conds.add("r.fecha_hora <= :hasta");
    conds.add("(:estId IS NULL OR r.id_est = :estId)");
    String sql = base + " WHERE " + String.join(" AND ", conds) + " GROUP BY dia ORDER BY cnt DESC";

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

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> horariosPicoIngresos(LocalDate desde, LocalDate hasta, int topN, Long estId) {
        String base = "SELECT HOUR(r.fecha_hora) AS hora, COUNT(*) AS cnt FROM registro_estacionamiento r";
    java.util.List<String> conds = new java.util.ArrayList<>();
    if(desde != null) conds.add("r.fecha_hora >= :desde");
    if(hasta != null) conds.add("r.fecha_hora <= :hasta");
    conds.add("(:estId IS NULL OR r.id_est = :estId)");
    String sql = base + " WHERE " + String.join(" AND ", conds) + " GROUP BY hora ORDER BY cnt DESC" + (topN>0?" LIMIT "+topN:"");

    Query q = em.createNativeQuery(sql);
    if(desde != null) q.setParameter("desde", toStart(desde));
    if(hasta != null) q.setParameter("hasta", toEnd(hasta));
    q.setParameter("estId", estId);

    List<Object[]> rows = (List<Object[]>) q.getResultList();
        List<Map<String,Object>> res = new ArrayList<>();
        for(Object[] r: rows){
            Map<String,Object> m = new HashMap<>();
            m.put("hora", r[0]);
            m.put("cantidad", ((Number)r[1]).longValue());
            res.add(m);
        }
        return res;
    }



    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> porcentajeOcupacionPorEstacionamiento(LocalDate desde, LocalDate hasta, Long estId) {
        String base = "SELECT e.id_est, e.nombre, e.capacidad, COUNT(r.id_registro) AS ingresos FROM estacionamiento e LEFT JOIN registro_estacionamiento r ON r.id_est = e.id_est";
    java.util.List<String> conds = new java.util.ArrayList<>();
    if(desde != null) conds.add("r.fecha_hora >= :desde");
    if(hasta != null) conds.add("r.fecha_hora <= :hasta");
    conds.add("(:estId IS NULL OR r.id_est = :estId)");
    String sql = base + " WHERE " + String.join(" AND ", conds) + " GROUP BY e.id_est, e.nombre, e.capacidad";

    Query q = em.createNativeQuery(sql);
    if(desde != null) q.setParameter("desde", toStart(desde));
    if(hasta != null) q.setParameter("hasta", toEnd(hasta));
    q.setParameter("estId", estId);

    List<Object[]> rows = (List<Object[]>) q.getResultList();
        List<Map<String,Object>> res = new ArrayList<>();
        for(Object[] r: rows){
            long ingresos = ((Number)r[3]).longValue();
            int capacidad = r[2]==null?0:((Number)r[2]).intValue();
            double pct = capacidad==0?0.0:(ingresos*100.0/capacidad);
            Map<String,Object> m = new HashMap<>();
            m.put("id", ((Number)r[0]).longValue());
            m.put("nombre", r[1]);
            m.put("capacidad", capacidad);
            m.put("ingresos", ingresos);
            m.put("porcentaje", Math.round(pct*100.0)/100.0);
            res.add(m);
        }
        return res;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> evolucionIngresosDiarios(LocalDate desde, LocalDate hasta, Long estId) {
        String base = "SELECT DATE(r.fecha_hora) AS dia, COUNT(*) AS cnt FROM registro_estacionamiento r";
    java.util.List<String> conds = new java.util.ArrayList<>();
    if(desde != null) conds.add("r.fecha_hora >= :desde");
    if(hasta != null) conds.add("r.fecha_hora <= :hasta");
    conds.add("(:estId IS NULL OR r.id_est = :estId)");
    String sql = base + " WHERE " + String.join(" AND ", conds) + " GROUP BY dia ORDER BY dia ASC";

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

    @Override
    public List<Map<String, Object>> conteoManualVsQr(LocalDate desde, LocalDate hasta, Long estId) {
        String base = "SELECT COALESCE(r.modo,'MANUAL') AS modo, COUNT(*) AS cnt FROM registro_estacionamiento r";
    java.util.List<String> conds = new java.util.ArrayList<>();
    if(desde != null) conds.add("r.fecha_hora >= :desde");
    if(hasta != null) conds.add("r.fecha_hora <= :hasta");
    conds.add("(:estId IS NULL OR r.id_est = :estId)");
    String sql = base + " WHERE " + String.join(" AND ", conds) + " GROUP BY modo";

    Query q = em.createNativeQuery(sql);
    if(desde != null) q.setParameter("desde", toStart(desde));
    if(hasta != null) q.setParameter("hasta", toEnd(hasta));
    q.setParameter("estId", estId);

    @SuppressWarnings("unchecked")
    List<Object[]> rows = (List<Object[]>) q.getResultList();
        List<Map<String,Object>> res = new ArrayList<>();
        long total = 0;
        for(Object[] r: rows) total += ((Number)r[1]).longValue();
        for(Object[] r: rows){
            Map<String,Object> m = new HashMap<>();
            String modo = r[0]==null?"MANUAL":r[0].toString();
            long cnt = ((Number)r[1]).longValue();
            double pct = total==0?0.0:(cnt*100.0/total);
            m.put("modo", modo);
            m.put("cantidad", cnt);
            m.put("porcentaje", Math.round(pct*100.0)/100.0);
            res.add(m);
        }
        return res;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> distribucionPorTipoVehiculo(LocalDate desde, LocalDate hasta, Long estId) {
        String base = "SELECT COALESCE(t.nombre, v.tipo) AS tipo, COUNT(*) AS cantidad FROM registro_estacionamiento r " +
                "JOIN vehiculo v ON v.patente = r.patente LEFT JOIN vehiculo_tipo t ON t.id_vehiculo_tipo = v.id_vehiculo_tipo";
        java.util.List<String> conds = new java.util.ArrayList<>();
        if(desde != null) conds.add("r.fecha_hora >= :desde");
        if(hasta != null) conds.add("r.fecha_hora <= :hasta");
        conds.add("(:estId IS NULL OR r.id_est = :estId)");
        String sql = base + " WHERE " + String.join(" AND ", conds) + " GROUP BY tipo ORDER BY cantidad DESC";

        Query q = em.createNativeQuery(sql);
        if(desde != null) q.setParameter("desde", toStart(desde));
        if(hasta != null) q.setParameter("hasta", toEnd(hasta));
        q.setParameter("estId", estId);

        List<Object[]> rows = (List<Object[]>) q.getResultList();
        long total = 0;
        for(Object[] r: rows) total += ((Number)r[1]).longValue();

        List<Map<String,Object>> res = new ArrayList<>();
        for(Object[] r: rows){
            String tipo = r[0]==null?"Desconocido":r[0].toString();
            long cnt = ((Number)r[1]).longValue();
            double pct = total==0?0.0:(cnt*100.0/total);
            Map<String,Object> m = new HashMap<>();
            m.put("tipo", tipo);
            m.put("cantidad", cnt);
            m.put("porcentaje", Math.round(pct*100.0)/100.0);
            res.add(m);
        }
        return res;
    }

    
}
