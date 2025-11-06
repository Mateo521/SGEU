
let personaCargada = false;

function buscarPersona() {
    const dni = document.getElementById('buscarDni').value;
    const resultado = document.getElementById('busquedaResultado');

    if (!dni || dni.length < 7 || dni.length > 8) {
        resultado.className = 'mt-2 text-sm text-red-600 font-semibold';
        resultado.innerHTML = 'Por favor ingresá un DNI válido (7-8 dígitos)';
        return;
    }

    resultado.className = 'mt-2 text-sm text-blue-600 font-semibold animate-pulse';
    resultado.innerHTML = 'Buscando persona...';

    fetch(`/sgeu/api/personas/buscar/${dni}`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else if (response.status === 404) {
                throw new Error('NOT_FOUND');
            } else if (response.status === 401) {
                throw new Error('UNAUTHORIZED');
            } else if (response.status === 403) {
                throw new Error('FORBIDDEN');
            } else {
                throw new Error('ERROR');
            }
        })
        .then(persona => {
            // autocompletar el formulario
            document.getElementById('nombre').value = persona.nombre || '';
            document.getElementById('dni').value = persona.dni || '';
            document.getElementById('telefono').value = persona.telefono || '';
            document.getElementById('email').value = persona.email || '';
            document.getElementById('categoriaNombre').value = persona.categoriaNombre || '';

            // marcar como persona cargada
            personaCargada = true;

            // deshabilitar campos para evitar edicion (excepto email y telefono por si cambiaron)
            document.getElementById('nombre').readOnly = true;
            document.getElementById('dni').readOnly = true;
            const categoriaElement = document.getElementById('categoriaNombre');
            categoriaElement.classList.add('pointer-events-none', 'opacity-60');

            // Y en limpiarFormularioPersona():
            categoriaElement.classList.remove('pointer-events-none', 'opacity-60');

            // cambiar el color de los campos para indicar que estan autocompletados
            ['nombre', 'dni', 'telefono', 'email', 'categoriaNombre'].forEach(id => {
                const element = document.getElementById(id);
                element.classList.remove('bg-slate-50', 'border-slate-200');
                element.classList.add('bg-green-50', 'border-green-400');
            });

            resultado.className = 'mt-2 text-sm text-green-600 font-bold flex items-center gap-2';
            resultado.innerHTML = `
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                </svg>
                Persona encontrada: ${persona.nombre}. Datos cargados automáticamente.
            `;

            // scroll suave hacia el formulario de vehículo
            setTimeout(() => {
                document.getElementById('patente')?.focus();
            }, 500);
        })
        .catch(error => {
            personaCargada = false;

            if (error.message === 'NOT_FOUND') {
                resultado.className = 'mt-2 text-sm text-orange-600 font-semibold flex items-center gap-2';
                resultado.innerHTML = `
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                            d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                    </svg>
                    Persona no encontrada. Complete los datos manualmente.
                `;
            } else if (error.message === 'UNAUTHORIZED') {
                resultado.className = 'mt-2 text-sm text-red-600 font-semibold';
                resultado.innerHTML = 'Sesión expirada. Por favor inicie sesión nuevamente.';
                setTimeout(() => window.location.href = '/login', 2000);
            } else if (error.message === 'FORBIDDEN') {
                resultado.className = 'mt-2 text-sm text-red-600 font-semibold';
                resultado.innerHTML = 'No tiene permisos para realizar esta búsqueda.';
            } else {
                resultado.className = 'mt-2 text-sm text-red-600 font-semibold';
                resultado.innerHTML = 'Error en la búsqueda. Intente nuevamente.';
            }

            limpiarFormularioPersona(false);
        });
}

function limpiarFormularioPersona(limpiarCampos = true) {
    personaCargada = false;

    if (limpiarCampos) {
        document.getElementById('buscarDni').value = '';
        document.getElementById('nombre').value = '';
        document.getElementById('dni').value = '';
        document.getElementById('telefono').value = '';
        document.getElementById('email').value = '';
        document.getElementById('categoriaNombre').value = '';

        const resultado = document.getElementById('busquedaResultado');
        resultado.className = 'mt-2 text-sm text-slate-600';
        resultado.innerHTML = 'Campos limpiados. Puede ingresar nuevos datos.';

        setTimeout(() => {
            resultado.innerHTML = '';
        }, 3000);
    }

    // Habilitar todos los campos
    ['nombre', 'dni', 'telefono', 'email'].forEach(id => {
        const element = document.getElementById(id);
        element.readOnly = false;
        element.classList.remove('bg-green-50', 'border-green-400');
        element.classList.add('bg-slate-50', 'border-slate-200');
    });

    const categoriaElement = document.getElementById('categoriaNombre');
    categoriaElement.disabled = false;
    categoriaElement.classList.remove('bg-green-50', 'border-green-400');
    categoriaElement.classList.add('bg-slate-50', 'border-slate-200');
}

// permitir busqueda con Enter en el campo de busqueda
document.getElementById('buscarDni')?.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        e.preventDefault();
        buscarPersona();
    }
});

//advertir si intenta cambiar datos de persona cargada
['nombre', 'dni', 'categoriaNombre'].forEach(id => {
    document.getElementById(id)?.addEventListener('focus', function () {
        if (personaCargada && this.readOnly) {
            const resultado = document.getElementById('busquedaResultado');
            resultado.className = 'mt-2 text-sm text-orange-600 font-semibold';
            resultado.innerHTML = 'Este campo está bloqueado porque la persona ya existe en el sistema.';
        }
    });
});

