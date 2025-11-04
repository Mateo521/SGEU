package com.unsl.sgeu.dto;

import java.util.List;

public class PaginaDTO<T> {
    private List<T> content;
    private int number;           
    private int size;             
    private long totalElements;   
    private int totalPages;       
    private int numberOfElements;  
    private boolean first;         
    private boolean last;         

    public PaginaDTO(List<T> content, int number, int size, long totalElements) {
        this.content = content;
        this.number = number;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (size > 0) ? (int) Math.ceil((double) totalElements / size) : 0;
        this.numberOfElements = content.size();
        this.first = (number == 0);
        this.last = (number + 1) * size >= totalElements;
    }

    // Getters
    public List<T> getContent() { return content; }
    public int getNumber() { return number; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public int getNumberOfElements() { return numberOfElements; }
    public boolean isFirst() { return first; }
    public boolean isLast() { return last; }
    
    // Métodos de conveniencia para Thymeleaf
    public boolean hasPrevious() { return number > 0; }
    public boolean hasNext() { return !last; }
    public boolean isEmpty() { return content.isEmpty(); }

   
    public void setContent(List<T> content) { this.content = content; }
    public void setNumber(int number) { this.number = number; }
    public void setSize(int size) { this.size = size; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public void setNumberOfElements(int numberOfElements) { this.numberOfElements = numberOfElements; }
    public void setFirst(boolean first) { this.first = first; }
    public void setLast(boolean last) { this.last = last; }
}
