package com.unsl.sgeu;

import com.unsl.sgeu.repositories.EmpleadoRepository;
import com.unsl.sgeu.repositories.PersonaRepository;
import com.unsl.sgeu.repositories.VehiculoRepository;
import com.unsl.sgeu.services.CategoriaService;
import com.unsl.sgeu.services.PersonaVehiculoService;
import com.unsl.sgeu.services.VehiculoTipoService;
import com.unsl.sgeu.services.VehiculoService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class SgeuApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
        System.out.println("Zona horaria configurada: " + TimeZone.getDefault().getID());
    }

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
        SpringApplication.run(SgeuApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(
            PersonaRepository personaRepo,
            VehiculoRepository vehiculoRepo,
            EmpleadoRepository empleadoRepo,
            CategoriaService categoriaService,
            VehiculoTipoService vehiculoTipoService,
            PersonaVehiculoService personaVehiculoService,
            VehiculoService vehiculoService
    ) {
        return args -> {
            System.out.println("Iniciando seed: Categoría, Tipo, Persona, Vehículo, vínculo y Empleado admin...");
            System.out.println("Seed listo: Categoría, Tipo, Persona, Vehículo, vínculo y Empleado admin.");
        };
    }
}
