package com.unsl.sgeu.services;

public class VehiculoOperacionException extends RuntimeException {
    public VehiculoOperacionException(String message) {
        super(message);
    }
    public VehiculoOperacionException(String message, Throwable cause) {
        super(message, cause);
    }
}
