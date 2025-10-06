package com.unsl.sgeu;

import com.unsl.sgeu.models.Empleado;
import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.repositories.EmpleadoRepository;
import com.unsl.sgeu.repositories.PersonaRepository;
import com.unsl.sgeu.repositories.VehiculoRepository;
import com.unsl.sgeu.services.VehiculoService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SgeuApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgeuApplication.class, args);
    }

}


