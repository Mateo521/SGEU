package com.unsl.sgeu;

import com.unsl.sgeu.repositories.EmpleadoRepository;
import com.unsl.sgeu.repositories.PersonaRepository;
import com.unsl.sgeu.repositories.VehiculoRepository;
import com.unsl.sgeu.services.CategoriaService;
import com.unsl.sgeu.services.VehiculoService;
import com.unsl.sgeu.services.VehiculoTipoService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class SgeuApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgeuApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(
            PersonaRepository personaRepo,
            VehiculoRepository vehiculoRepo,
            EmpleadoRepository empleadoRepo,
            CategoriaService categoriaService,
            VehiculoTipoService vehiculoTipoService,
            // PersonaVehiculoService personaVehiculoService,
            VehiculoService vehiculoService
    ) {
        return args -> {
            System.out.println("⏳ Iniciando seed: Categoría, Tipo, Persona, Vehículo, vínculo y Empleado admin...");
            System.out.println("✅ Seed listo: Categoría, Tipo, Persona, Vehículo, vínculo y Empleado admin.");
        };
    }
}
