package com.unsl.sgeu.controllers;

import com.unsl.sgeu.services.QRCodeService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class QRImageController {
    
    private final QRCodeService qrCodeService;
    private static final String QR_CODE_IMAGE_PATH = "src/main/resources/static/qr-codes/";
    
    public QRImageController(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }
    
    @GetMapping("/qr-codes/{filename}")
    public ResponseEntity<Resource> obtenerImagenQR(@PathVariable String filename) {
        try {
            Path rutaArchivo = Paths.get(QR_CODE_IMAGE_PATH + filename);
            
            if (!Files.exists(rutaArchivo)) {
                System.err.println(" Archivo no encontrado: " + rutaArchivo.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(rutaArchivo);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            System.err.println(" Error sirviendo imagen: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
 

@GetMapping("/qr-patente/{patente}")
public ResponseEntity<Resource> obtenerQRPorPatente(@PathVariable String patente) {
    try {
        Path directorio = Paths.get(QR_CODE_IMAGE_PATH);
        
        if (!Files.exists(directorio)) {
            System.err.println(" Directorio no existe: " + directorio.toAbsolutePath());
            return ResponseEntity.notFound().build();
        }
        
        // Buscar archivo que empiece con "qr_" + patente
        Optional<Path> archivoEncontrado = Files.list(directorio)
            .filter(path -> {
                String nombre = path.getFileName().toString();
                return nombre.startsWith("qr_" + patente + "_") && nombre.endsWith(".png");
            })
            .findFirst();
        
        if (archivoEncontrado.isEmpty()) {
            System.err.println("No se encontró QR para patente: " + patente);
            return ResponseEntity.notFound().build();
        }
        
        Resource resource = new FileSystemResource(archivoEncontrado.get());
        String filename = archivoEncontrado.get().getFileName().toString();
        
        System.out.println("Sirviendo QR: " + filename + " para patente: " + patente);
        
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
                
    } catch (Exception e) {
        System.err.println("Error buscando QR para patente " + patente + ": " + e.getMessage());
        return ResponseEntity.notFound().build();
    }
}


    @GetMapping("/qr-image/{codigo}")
    public ResponseEntity<byte[]> generarImagenQRAlVuelo(@PathVariable String codigo) {
        try {
            byte[] imagenQR = qrCodeService.generarQRComoBytes(codigo);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(imagenQR.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(imagenQR);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
