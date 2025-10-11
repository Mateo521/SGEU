/**
 * Utilidad para imprimir códigos QR
 * Reutilizable desde cualquier página
 */

class QRPrinter {

    /**
     * Imprime un código QR desde cualquier imagen
     * @param {HTMLElement|string} elemento - Elemento img o selector
     * @param {Object} opciones - Opciones adicionales
     */
    static imprimir(elemento, opciones = {}) {
        let imagenQR;

        // Determinar si es un elemento o un selector
        if (typeof elemento === 'string') {
            imagenQR = document.querySelector(elemento);
        } else {
            imagenQR = elemento;
        }

        if (!imagenQR) {
            console.error('No se encontró la imagen del código QR');
            alert('No se encontró la imagen del código QR');
            return;
        }

        // Obtener datos del QR
        const datosQR = this.extraerDatosQR(imagenQR, opciones);

        // Crear ventana de impresión
        const ventanaImpresion = window.open('', '_blank', 'width=600,height=800');

        if (!ventanaImpresion) {
            alert('No se pudo abrir la ventana de impresión. Verifique que no esté bloqueada por el navegador.');
            return;
        }

        // Generar contenido HTML
        const contenidoHTML = this.generarHTML(datosQR);

        // Escribir contenido y configurar eventos
        ventanaImpresion.document.write(contenidoHTML);
        ventanaImpresion.document.close();

        // Configurar evento de carga
        ventanaImpresion.onload = () => {
            setTimeout(() => {
                if (opciones.imprimirAutomatico) {
                    ventanaImpresion.print();
                } else if (ventanaImpresion.confirm('¿Desea imprimir el código QR ahora?')) {
                    ventanaImpresion.print();
                }
            }, 1000);
        };
    }

    /**
     * Extrae los datos necesarios del elemento QR
     */
 
    static extraerDatosQR(imagenQR, opciones) {
        let patente = 'Sin patente';

        // Método 1: Desde atributos data del elemento
        if (imagenQR.getAttribute('data-patente')) {
            patente = imagenQR.getAttribute('data-patente');
        }
        // Método 2: Desde el atributo alt (ej: "QR de AC494SQ")
        else if (imagenQR.getAttribute('alt')) {
            const altText = imagenQR.getAttribute('alt');
            const match = altText.match(/QR de (.+)/i);
            if (match && match[1]) {
                patente = match[1].trim();
            }
        }
        // Método 3: Buscar en la fila de la tabla
        else if (imagenQR.closest('tr')) {
            const fila = imagenQR.closest('tr');

            // Buscar por clase 'patente'
            const elementoPatente = fila.querySelector('.patente');
            if (elementoPatente) {
                patente = elementoPatente.textContent.trim();
            }
            // Buscar en la primera celda (asumiendo que es la patente)
            else {
                const primeraCelda = fila.querySelector('td:first-child');
                if (primeraCelda && primeraCelda.textContent.trim()) {
                    patente = primeraCelda.textContent.trim();
                }
            }
        }
        // Método 4: Desde opciones pasadas como parámetro
        else if (opciones.patente) {
            patente = opciones.patente;
        }

        // Extraer código QR
        let codigoQR = '';
        if (imagenQR.getAttribute('data-codigo')) {
            codigoQR = imagenQR.getAttribute('data-codigo');
        } else if (imagenQR.getAttribute('alt')) {
            codigoQR = imagenQR.getAttribute('alt');
        } else if (opciones.codigoQR) {
            codigoQR = opciones.codigoQR;
        }

        console.log('🔍 Datos extraídos:', { patente, codigoQR }); // Para debugging

        return {
            src: imagenQR.src || imagenQR.getAttribute('src'),
            patente: patente,
            codigoQR: codigoQR,
            titulo: opciones.titulo || 'Sistema de Gestión de Estacionamiento Universitario',
            subtitulo: opciones.subtitulo || 'Código QR del Vehículo'
        };
    }


    /**
     * Genera el HTML para la impresión
     */
    static generarHTML(datos) {
        return `
<!DOCTYPE html>
<html>
<head>
    <title>Código QR - ${datos.patente}</title>
    <meta charset="UTF-8">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body { 
            text-align: center; 
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
            margin: 20px;
            background: white;
            color: #333;
        }
        
        .qr-container { 
            border: 3px solid #2563eb; 
            border-radius: 12px;
            padding: 30px; 
            margin: 20px auto; 
            max-width: 450px;
            background: white;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        
        .header {
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #e5e7eb;
        }
        
        .titulo {
            font-size: 20px;
            font-weight: bold;
            color: #1f2937;
            margin-bottom: 5px;
        }
        
        .subtitulo {
            font-size: 16px;
            color: #6b7280;
        }
        
        .qr-image { 
            width: 280px; 
            height: 280px; 
            margin: 20px auto;
            display: block;
            border: 2px solid #e5e7eb;
            border-radius: 8px;
            padding: 10px;
            background: white;
        }
        
        .info-section {
            margin-top: 25px;
            padding-top: 20px;
            border-top: 2px solid #e5e7eb;
        }
        
        .info { 
            margin: 8px 0; 
            font-size: 14px;
            color: #4b5563;
        }
        
        .patente { 
            font-size: 24px; 
            font-weight: bold; 
            color: #1f2937;
            background: #f3f4f6;
            padding: 10px;
            border-radius: 6px;
            margin: 15px 0;
            letter-spacing: 2px;
        }
        
        .codigo {
            font-family: 'Courier New', monospace;
            font-size: 12px;
            color: #6b7280;
            word-break: break-all;
            background: #f9fafb;
            padding: 8px;
            border-radius: 4px;
            margin: 10px 0;
        }
        
        .fecha-hora {
            display: flex;
            justify-content: space-between;
            margin-top: 15px;
            font-size: 13px;
        }
        
        .botones {
            margin-top: 30px;
            display: flex;
            gap: 15px;
            justify-content: center;
        }
        
        .btn {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 12px 24px;
            font-size: 16px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 500;
            transition: all 0.2s;
        }
        
        .btn-primary {
            background: #2563eb;
            color: white;
        }
        
        .btn-primary:hover {
            background: #1d4ed8;
        }
        
        .btn-secondary {
            background: #6b7280;
            color: white;
        }
        
        .btn-secondary:hover {
            background: #4b5563;
        }
        
        .footer {
            margin-top: 20px;
            font-size: 11px;
            color: #9ca3af;
            font-style: italic;
        }
        
        @media print {
            body { 
                margin: 0; 
                background: white;
            }
            .no-print { 
                display: none !important; 
            }
            .qr-container {
                box-shadow: none;
                border: 2px solid #000;
                margin: 0;
                max-width: none;
            }
        }
    </style>
</head>
<body>
    <div class="qr-container">
        <div class="header">
            <div class="titulo">${datos.titulo}</div>
            <div class="subtitulo">${datos.subtitulo}</div>
        </div>
        
        <img src="${datos.src}" 
             alt="Código QR" 
             class="qr-image"
             onerror="this.style.display='none'; document.querySelector('.error-msg').style.display='block';">
        
        <div class="error-msg" style="display:none; color:red; margin:20px;">
            ❌ Error al cargar la imagen del código QR
        </div>
        
        <div class="info-section">
            <div class="info patente">📋 ${datos.patente}</div>
            
            ${datos.codigoQR ? `
            <div class="info">
                <strong>Código QR:</strong>
                <div class="codigo">${datos.codigoQR.length > 50 ? datos.codigoQR.substring(0, 50) + '...' : datos.codigoQR}</div>
            </div>
            ` : ''}
            
            <div class="fecha-hora">
                <span>📅 ${new Date().toLocaleDateString('es-AR')}</span>
                <span>🕐 ${new Date().toLocaleTimeString('es-AR')}</span>
            </div>
        </div>
        
        <div class="footer">
            Generado automáticamente por SGEU
        </div>
    </div>
    
    <div class="no-print botones">
        <button onclick="window.print()" class="btn btn-primary">
            <svg width="20" height="20" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"/>
            </svg>
            Imprimir
        </button>
        <button onclick="window.close()" class="btn btn-secondary">
            <svg width="20" height="20" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
            Cerrar
        </button>
    </div>
</body>
</html>`;
    }

    /**
     * Función de conveniencia para imprimir desde botón
     */
    static imprimirDesdeBoton(boton) {
        // Buscar la imagen QR más cercana
        const fila = boton.closest('tr');
        const imagenQR = fila?.querySelector('.qr-image') || fila?.querySelector('img[src*="qr"]');

        if (imagenQR) {
            this.imprimir(imagenQR);
        } else {
            alert('No se encontró el código QR asociado');
        }
    }
}

// Función global para compatibilidad con código existente
function imprimirQR(elemento, opciones) {
    QRPrinter.imprimir(elemento, opciones);
}

// Función específica para botones en tabla
function imprimirQRDesdeTabla(boton) {
    QRPrinter.imprimirDesdeBoton(boton);
}
