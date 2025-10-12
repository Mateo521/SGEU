package com.unsl.sgeu;

import com.unsl.sgeu.models.Categoria;
import com.unsl.sgeu.models.Empleado;
import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.models.Rol;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.models.VehiculoTipo;
import com.unsl.sgeu.repositories.EmpleadoRepository;
import com.unsl.sgeu.repositories.PersonaRepository;
import com.unsl.sgeu.repositories.VehiculoRepository;
import com.unsl.sgeu.services.CategoriaService;
import com.unsl.sgeu.services.PersonaVehiculoService;
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
            PersonaVehiculoService personaVehiculoService,
            VehiculoService vehiculoService
    ) {
        return args -> {
            // ===== 1) Catálogo =====
            Categoria catEstudiante = categoriaService.getOrCreateByNombre("estudiante");
            VehiculoTipo tipoAuto   = vehiculoTipoService.getOrCreateByNombre("auto");

        
     
         
           
            // ===== 5) Empleado admin =====
            String user = "admin";
            Empleado admin = empleadoRepo.findByNombreUsuario(user);
            if (admin == null) {
                Empleado e = new Empleado();
                e.setNombre("Gero");
                e.setApellido("Arias");
                e.setCorreo("gero@example.com");
                e.setNombreUsuario(user);
                e.setContrasenia("admin123"); // TODO: en producción encriptar
                e.setRol(Rol.Administrador);          // <--- enum, no int
                empleadoRepo.save(e);
            }

            System.out.println("✅ Seed listo: Categoría, Tipo, Persona, Vehículo, vínculo y Empleado admin.");
        };
    }
}
