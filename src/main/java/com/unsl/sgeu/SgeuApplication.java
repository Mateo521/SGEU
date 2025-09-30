package com.unsl.sgeu;

import com.unsl.sgeu.models.Empleado;
import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.repositories.EmpleadoRepository;
import com.unsl.sgeu.repositories.PersonaRepository;
import com.unsl.sgeu.repositories.VehiculoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SgeuApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgeuApplication.class, args);
    }

    // Se ejecuta una vez al iniciar: crea datos de ejemplo si no existen
    @Bean
    CommandLineRunner seedData(
            PersonaRepository personaRepo,
            VehiculoRepository vehiculoRepo,
            EmpleadoRepository empleadoRepo
    ) {
        return args -> {
            // ===== Persona =====
            Long dni = 12345678L; // usá el tipo que tengas como @Id en Persona
            Persona persona = personaRepo.findById(dni).orElseGet(() -> {
                Persona p = new Persona();
                p.setDni(dni);
                p.setNombre("Ana");
                p.setTelefono("266-123456");
                p.setEmail("ana@uni.edu");
                p.setCategoria("alumno");
                return personaRepo.save(p);
            });

            // ===== Vehículo (PK = patente) =====
            String patente = "AB123CD";
            vehiculoRepo.findById(patente).orElseGet(() -> {
                Vehiculo v = new Vehiculo();
                v.setPatente(patente);
                v.setCodigoQr("qr-001");
                v.setModelo("Etios");
                v.setColor("Rojo");
                v.setTipo("Auto");
                v.setDuenio(persona); // relación con la persona creada
                return vehiculoRepo.save(v);
            });

            // ===== Empleado =====
            String user = "admin";
            Empleado empleado = empleadoRepo.findByNombreUsuario(user);
            if (empleado == null) {
                Empleado e = new Empleado();
                e.setNombre("Gero");
                e.setApellido("Arias");
                e.setCorreo("gero@example.com");
                e.setNombreUsuario(user);
                e.setContrasenia("admin123"); // luego encriptamos
                e.setCargo("Administrador");
                empleadoRepo.save(e);
            }

            System.out.println("✅ Datos de ejemplo listos: Persona, Vehículo y Empleado.");
        };
    }
}
