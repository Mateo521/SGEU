package com.unsl.sgeu.services;

import com.google.zxing.WriterException;
import java.io.IOException;

public interface QRCodeService {

    /**
     * Genera un archivo PNG con el QR en el directorio configurado y devuelve una URL pública.
     * @param texto contenido del QR
     * @param nombreArchivo nombre base del archivo (sin extensión)
     * @return URL pública para acceder a la imagen
     */
    String generarImagenQR(String texto, String nombreArchivo) throws WriterException, IOException;

    /**
     * Genera un QR en memoria y lo devuelve como bytes PNG.
     * @param texto contenido del QR
     * @return bytes del PNG
     */
    byte[] generarQRComoBytes(String texto) throws WriterException, IOException;
}
