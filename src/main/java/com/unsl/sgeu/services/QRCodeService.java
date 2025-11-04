package com.unsl.sgeu.services;

import com.google.zxing.WriterException;
import java.io.IOException;

public interface QRCodeService {

 
    
    String generarImagenQR(String texto, String nombreArchivo) throws WriterException, IOException;

    
    
    byte[] generarQRComoBytes(String texto) throws WriterException, IOException;
}
