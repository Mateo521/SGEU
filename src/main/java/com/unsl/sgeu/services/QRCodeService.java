package com.unsl.sgeu.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

@Service
public class QRCodeService {

    @Value("${qr.storage.path:/opt/sgeu/qr-codes/}")
    private String qrCodeImagePath;
    
    
    @Value("${server.servlet.context-path:/sgeu}")
    private String contextPath;

    private static final int QR_CODE_WIDTH = 300;
    private static final int QR_CODE_HEIGHT = 300;

    public String generarImagenQR(String texto, String nombreArchivo) throws WriterException, IOException {
        
        Path directorio = Paths.get(qrCodeImagePath);
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
           
            Set<PosixFilePermission> dirPerms = PosixFilePermissions.fromString("rwxr-xr-x");
            Files.setPosixFilePermissions(directorio, dirPerms);
        }

        
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);

        
        String nombreCompleto = "qr_" + nombreArchivo + ".png";
        Path rutaArchivo = directorio.resolve(nombreCompleto);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", rutaArchivo);
        
      
        Set<PosixFilePermission> filePerms = PosixFilePermissions.fromString("rw-r--r--");
        Files.setPosixFilePermissions(rutaArchivo, filePerms);

      
        return contextPath + "/qr-codes/" + nombreCompleto;
    }

    public byte[] generarQRComoBytes(String texto) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }
}
