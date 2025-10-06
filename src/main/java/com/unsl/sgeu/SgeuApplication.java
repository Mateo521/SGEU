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

@SpringBootApplication
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

            // ===== 2) Persona =====
            Long dni = 12345678L;
            Persona persona = personaRepo.findById(dni).orElseGet(() -> {
                Persona p = new Persona();
                p.setDni(dni);
                p.setNombre("Ana");
                p.setTelefono("266-123456");
                p.setEmail("ana@uni.edu");
                p.setCategoria(catEstudiante);
                return personaRepo.save(p);
            });
            if (persona.getCategoria() == null) {
                persona.setCategoria(catEstudiante);
                personaRepo.save(persona);
            }

            // ===== 3) Vehículo =====
            String patente = "AB321CD";
            Vehiculo vehiculo = vehiculoRepo.findById(patente).orElseGet(() -> {
                Vehiculo v = new Vehiculo();
                v.setPatente(patente);
                // Usa el nombre correcto de tu método de QR:
                String qr = vehiculoService.generarCodigoQR(patente); // <--- si tu método se llama generarCodigoQR o generarCodigoQrUnico, cambialo aquí
                v.setCodigoQr(qr);
                v.setModelo("Ford Focus");
                v.setColor("Verde");
                v.setVehiculoTipo(tipoAuto); // o v.setIdVehiculoTipo(tipoAuto.getId());
                return vehiculoRepo.save(v);
            });

            // ===== 4) Vincular Persona ↔ Vehículo =====
            personaVehiculoService.vincular(persona.getDni(), vehiculo.getPatente());

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
