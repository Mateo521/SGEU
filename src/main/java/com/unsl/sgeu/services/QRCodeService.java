package com.unsl.sgeu.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class QRCodeService {

    private static final String QR_CODE_IMAGE_PATH = "src/main/resources/static/qr-codes/";
    private static final int QR_CODE_WIDTH = 300;
    private static final int QR_CODE_HEIGHT = 300;

    public String generarImagenQR(String texto, String nombreArchivo) throws WriterException, IOException {
        // Crear directorio si no existe
        Path directorio = Paths.get(QR_CODE_IMAGE_PATH);
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
        }

        // Generar QR
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);

        // Guardar imagen
        String nombreCompleto = nombreArchivo + ".png";
        Path rutaArchivo = Paths.get(QR_CODE_IMAGE_PATH + nombreCompleto);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", rutaArchivo);

        // Retornar la URL para acceder desde el navegador
        return "/qr-codes/" + nombreCompleto;
    }

    public byte[] generarQRComoBytes(String texto) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }
}
