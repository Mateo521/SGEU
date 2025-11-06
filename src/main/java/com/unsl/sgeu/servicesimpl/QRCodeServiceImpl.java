package com.unsl.sgeu.servicesimpl;



import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;


import com.unsl.sgeu.services.QRCodeService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.*;



@Service
public class QRCodeServiceImpl implements QRCodeService {

    @Value("${qr.storage.path:/opt/sgeu/qr-codes/}")
    private String qrCodeImagePath;

    @Value("${server.servlet.context-path:/sgeu}")
    private String contextPath;

    private static final int QR_CODE_WIDTH = 300;
    private static final int QR_CODE_HEIGHT = 300;

    @Override
    public String generarImagenQR(String texto, String nombreArchivo) throws WriterException, IOException {

        Path directorio = Paths.get(qrCodeImagePath);
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);

            try {
                //controles para crear imagen en servidor (habilitar permisos de directorio y archivo)
                if (Files.getFileStore(directorio).supportsFileAttributeView("posix")) {
                    var dirPerms = java.nio.file.attribute.PosixFilePermissions.fromString("rwxr-xr-x");
                    Files.setPosixFilePermissions(directorio, dirPerms);
                }
            } catch (UnsupportedOperationException ignored) {
            }
        }

        // generar el QR
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        //genera imagen (matriz de bits)
        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);

        // guardar a archivo
        String nombreCompleto = "qr_" + nombreArchivo + ".png";
        Path rutaArchivo = directorio.resolve(nombreCompleto);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", rutaArchivo);

   
        try {
            if (Files.getFileStore(rutaArchivo).supportsFileAttributeView("posix")) {
                var filePerms = java.nio.file.attribute.PosixFilePermissions.fromString("rw-r--r--");
                Files.setPosixFilePermissions(rutaArchivo, filePerms);
            }
        } catch (UnsupportedOperationException ignored) {
          
        }

        // devuelve URL publica
        return contextPath + "/qr-codes/" + nombreCompleto;
    }

    @Override
    public byte[] generarQRComoBytes(String texto) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        }
    }
}
