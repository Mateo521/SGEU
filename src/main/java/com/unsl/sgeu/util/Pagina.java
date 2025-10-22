package com.unsl.sgeu.util;

import java.util.List;

public class Pagina<T> {
    private List<T> contenido;
    private int numeroPagina;
    private int tamanoPagina;
    private long totalElementos;
    private int totalPaginas;

    public Pagina(List<T> contenido, int numeroPagina, int tamanoPagina, long totalElementos) {
        this.contenido = contenido;
        this.numeroPagina = numeroPagina;
        this.tamanoPagina = tamanoPagina;
        this.totalElementos = totalElementos;
        this.totalPaginas = (int) Math.ceil((double) totalElementos / tamanoPagina);
    }

    public List<T> getContenido() {
        return contenido;
    }

    public int getNumeroPagina() {
        return numeroPagina;
    }

    public int getTamanoPagina() {
        return tamanoPagina;
    }

    public long getTotalElementos() {
        return totalElementos;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public boolean esPrimera() {
        return numeroPagina == 0;
    }

    public boolean esUltima() {
        return numeroPagina == totalPaginas - 1;
    }

    public int getNumeroElementos() {
        return contenido.size();
    }
}