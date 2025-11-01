class QRVehiculoManager {
    constructor() {
        this.currentData = null;
    }

   
    mostrarQR(button) {
        try {
            this.currentData = {
                patente: button.dataset.patente,
                qr: button.dataset.qr,
                modelo: button.dataset.modelo,
                color: button.dataset.color,
                tipo: button.dataset.tipo,
                dni: button.dataset.dni
            };

            console.log('📱 Mostrando QR para:', this.currentData);

            const elementos = [
                'modalPatente', 'modalModelo', 'modalColor', 
                'modalTipo', 'modalDni', 'modalCodigoQr'
            ];
            
            for (const id of elementos) {
                const elemento = document.getElementById(id);
                if (!elemento) {
                    console.error(`Elemento no encontrado: ${id}`);
                    alert(`Error: Elemento ${id} no encontrado en el modal`);
                    return;
                }
            }

            document.getElementById('modalPatente').textContent = this.currentData.patente;
            document.getElementById('modalModelo').textContent = this.currentData.modelo;
            document.getElementById('modalColor').textContent = this.currentData.color;
            document.getElementById('modalTipo').textContent = this.currentData.tipo;
            document.getElementById('modalDni').textContent = this.currentData.dni;
            document.getElementById('modalCodigoQr').textContent = this.currentData.qr;

            const qrImage = document.getElementById('qrImageModal');
            const qrError = document.getElementById('qrError');
            
            if (!qrImage || !qrError) {
                console.error('Elementos de imagen QR no encontrados');
                alert('Error: Elementos de imagen QR no encontrados');
                return;
            }
            
            qrImage.style.display = 'block';
            qrError.style.display = 'none';
            qrImage.src = `qr-codes/qr_${this.currentData.patente}.png`;
            
            qrImage.onload = function() {
                console.log(' Imagen QR cargada correctamente');
            };
            
            qrImage.onerror = function() {
     
                qrImage.style.display = 'none';
                qrError.style.display = 'block';
                qrError.textContent = 'Error al cargar la imagen QR';
            }.bind(this);

            const modal = document.getElementById('qrModal');
            if (modal) {
                modal.classList.remove('hidden');
            } else {
             
                alert('Error: Modal no encontrado');
            }

        } catch (error) {
       
            alert('Error al mostrar el código QR: ' + error.message);
        }
    }

  
    imprimirQR(button) {
        try {
            const data = {
                patente: button.dataset.patente,
                qr: button.dataset.qr,
                modelo: button.dataset.modelo,
                color: button.dataset.color,
                tipo: button.dataset.tipo,
                dni: button.dataset.dni
            };

            console.log('🖨️ Imprimiendo QR para:', data);
            this.imprimirConImagenExistente(data);

        } catch (error) {
           
            alert('Error al imprimir el código QR: ' + error.message);
        }
    }

     
   imprimirQRDesdeImagen() {
    console.log('🖨️ Imprimiendo QR desde imagen recién generada...');
    
 
    const imagenQR = document.querySelector('.qr-image');
    
    if (!imagenQR) {
   
        alert('No se encontró la imagen del código QR para imprimir');
        return;
    }

 
    const patente = imagenQR.dataset.patente || imagenQR.getAttribute('data-patente') || 'Sin patente';
    const codigoQR = imagenQR.dataset.codigo || imagenQR.getAttribute('data-codigo') || '';
    const modelo = imagenQR.dataset.modelo || imagenQR.getAttribute('data-modelo') || 'N/A';
    const color = imagenQR.dataset.color || imagenQR.getAttribute('data-color') || 'N/A';
    const tipo = imagenQR.dataset.tipo || imagenQR.getAttribute('data-tipo') || 'N/A';
    const dni = imagenQR.dataset.dni || imagenQR.getAttribute('data-dni') || 'N/A';
    const nombre = imagenQR.dataset.nombre || imagenQR.getAttribute('data-nombre') || 'N/A';
    
 
    const rutaImagen = imagenQR.dataset.imagen || imagenQR.getAttribute('data-imagen') || imagenQR.src;

    
    console.log('📋 Datos obtenidos de la imagen:');
    console.log('   Patente:', patente);
    console.log('   Código QR:', codigoQR.substring(0, 30) + '...');
    console.log('   Modelo:', modelo);
    console.log('   Color:', color);
    console.log('   Tipo:', tipo);
    console.log('   DNI:', dni);
    console.log('   Nombre:', nombre);
    console.log('   Ruta imagen:', rutaImagen);

 
    if (!patente || patente === 'Sin patente') {
       
        alert('Error: No se pudo obtener la patente del vehículo');
        return;
    }

    const data = {
        patente: patente,
        qr: codigoQR,
        modelo: modelo,
        color: color,
        tipo: tipo,
        dni: dni,
        nombre: nombre
    };

 
    
    
    this.crearDocumentoImpresion(data, rutaImagen);
}


  
    imprimirQRDesdeModal() {
        if (!this.currentData) {
            alert('No hay datos para imprimir');
            return;
        }
        this.imprimirConImagenExistente(this.currentData);
    }

  
    imprimirConImagenExistente(data) {
        const qrImageSrc = `qr-codes/qr_${data.patente}.png`;
        console.log('🖨️ Usando imagen QR:', qrImageSrc);
        this.crearDocumentoImpresion(data, qrImageSrc);
    }

  
    crearDocumentoImpresion(data, qrImageSrc) {
        const printWindow = window.open('', '_blank', 'width=600,height=800');
        
        if (!printWindow) {
            alert('Por favor, permite las ventanas emergentes para imprimir');
            return;
        }
        
        const htmlContent = `
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>QR - ${data.patente}</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    margin: 0;
                    padding: 20px;
                    background: white;
                }
                .qr-card {
                    max-width: 400px;
                    margin: 0 auto;
                    border: 2px solid #333;
                    border-radius: 10px;
                    padding: 20px;
                    text-align: center;
                    background: white;
                }
                .header {
                    background: #1e40af;
                    color: white;
                    padding: 15px;
                    margin: -20px -20px 20px -20px;
                    border-radius: 8px 8px 0 0;
                }
                .patente {
                    font-size: 24px;
                    font-weight: bold;
                    margin-bottom: 5px;
                }
                .subtitle {
                    font-size: 14px;
                    opacity: 0.9;
                }
                .qr-container {
                    margin: 20px 0;
                    padding: 15px;
                    background: #f8f9fa;
                    border-radius: 8px;
                }
                .qr-image {
                    max-width: 250px;
                    height: auto;
                    border: 1px solid #ddd;
                    border-radius: 4px;
                    background: white;
                    padding: 10px;
                }
                .info-grid {
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 10px;
                    margin: 20px 0;
                    text-align: left;
                }
                .info-item {
                    padding: 8px;
                    background: #f1f5f9;
                    border-radius: 5px;
                    border-left: 3px solid #1e40af;
                }
                .info-label {
                    font-weight: bold;
                    color: #1e40af;
                    font-size: 12px;
                    text-transform: uppercase;
                }
                .info-value {
                    color: #333;
                    font-size: 14px;
                    margin-top: 2px;
                }
                .qr-code-text {
                    font-family: 'Courier New', monospace;
                    font-size: 10px;
                    color: #666;
                    word-break: break-all;
                    margin-top: 10px;
                    padding: 8px;
                    background: #f8f9fa;
                    border-radius: 4px;
                }
                .footer {
                    margin-top: 20px;
                    padding-top: 15px;
                    border-top: 1px solid #e5e7eb;
                    font-size: 12px;
                    color: #666;
                }
                @media print {
                    body { margin: 0; padding: 10px; }
                    .qr-card { border: 1px solid #333; }
                }
            </style>
        </head>
        <body>
            <div class="qr-card">
                <div class="header">
                    <div class="patente">${data.patente}</div>
                    <div class="subtitle">Sistema de Gestión de Estacionamiento</div>
                </div>
                
                <div class="qr-container">
                    <img src="${qrImageSrc}" alt="Código QR" class="qr-image" 
                         onerror="this.alt='Error al cargar QR'; this.style.border='2px dashed #ccc';">
                    <div class="qr-code-text">${data.qr}</div>
                </div>
                
                <div class="info-grid">
                    <div class="info-item">
                        <div class="info-label">Modelo</div>
                        <div class="info-value">${data.modelo}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Color</div>
                        <div class="info-value">${data.color}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Tipo</div>
                        <div class="info-value">${data.tipo}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Propietario</div>
                        <div class="info-value">${data.dni}</div>
                    </div>
                </div>
                
                <div class="footer">
                    <div>Generado: ${new Date().toLocaleString('es-AR')}</div>
                    <div>SGEU - Sistema de Gestión de Estacionamiento Universitario</div>
                </div>
            </div>
            
            <script>
                window.onload = function() {
                    console.log('🖨️ Documento de impresión cargado');
                    setTimeout(function() {
                        window.print();
                    }, 1000);
                };
                
                window.onafterprint = function() {
                    window.close();
                };
            </script>
        </body>
        </html>
        `;

        printWindow.document.write(htmlContent);
        printWindow.document.close();
    }

    
    cerrarModal() {
        const modal = document.getElementById('qrModal');
        if (modal) {
            modal.classList.add('hidden');
        }
        this.currentData = null;
    }
}

 
const qrManager = new QRVehiculoManager();

 
function mostrarQR(button) {
    qrManager.mostrarQR(button);
}

function imprimirQR(button) {
    qrManager.imprimirQR(button);
}

function imprimirQRDesdeModal() {
    qrManager.imprimirQRDesdeModal();
}

 
function imprimirQRRecienGenerado() {
    qrManager.imprimirQRDesdeImagen();
}

function cerrarModal() {
    qrManager.cerrarModal();
}

 
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        cerrarModal();
    }
});

document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('qrModal');
    if (modal) {
        modal.addEventListener('click', function(event) {
            if (event.target === this) {
                cerrarModal();
            }
        });
    }
});
