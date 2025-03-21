/**
 * Inicialización de la aplicación
 */
document.addEventListener('DOMContentLoaded', function() {
    // Establecer conexión WebSocket
    initWebSocket();
    
    // Configurar eventos
    document.getElementById('solicitudForm').addEventListener('submit', handleFormSubmit);
    document.getElementById('verHistorico').addEventListener('click', loadHistorico);
});

/**
 * Inicializa la conexión WebSocket
 */
function initWebSocket() {
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);
    
    stompClient.connect({}, function (frame) {
        console.log('Conexión WebSocket establecida');
        stompClient.subscribe('/topic/solicitudes', function (response) {
            const solicitud = JSON.parse(response.body);
            actualizarSolicitud(solicitud);
        });
    });
}

/**
 * Maneja el envío del formulario
 */
function handleFormSubmit(e) {
    e.preventDefault();
    
    const solicitud = {
        nombreCliente: document.getElementById('nombreCliente').value,
        descripcion: document.getElementById('descripcion').value,
        cantidad: parseInt(document.getElementById('cantidad').value)
    };
    
    // Enviar solicitud al servidor
    fetch('/api/solicitudes', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(solicitud)
    })
    .then(response => response.json())
    .then(data => {
        console.log('Solicitud enviada:', data);
        mostrarNuevaSolicitud(data);
        document.getElementById('solicitudForm').reset();
    })
    .catch(error => console.error('Error:', error));
}

/**
 * Actualiza una solicitud existente o muestra una nueva
 */
function actualizarSolicitud(solicitud) {
    // Buscar si ya existe una solicitud con este ID
    let solicitudElement = document.querySelector(`[data-id="${solicitud.id}"]`);
    
    if (solicitudElement) {
        // Actualizar la existente
        solicitudElement.className = `card solicitud-card ${solicitud.estado}`;
        solicitudElement.querySelector('.estado').textContent = `Estado: ${solicitud.estado}`;
    } else {
        // Crear una nueva
        mostrarNuevaSolicitud(solicitud);
    }
}

/**
 * Muestra una nueva solicitud en la interfaz
 */
function mostrarNuevaSolicitud(solicitud) {
    const container = document.getElementById('solicitudesContainer');
    const solicitudElement = document.createElement('div');
    solicitudElement.className = `card solicitud-card ${solicitud.estado}`;
    solicitudElement.setAttribute('data-id', solicitud.id);
    solicitudElement.innerHTML = `
        <div class="card-body">
            <h5 class="card-title">${solicitud.nombreCliente}</h5>
            <h6 class="card-subtitle mb-2 text-muted">ID: ${solicitud.id}</h6>
            <p class="card-text">${solicitud.descripcion}</p>
            <p class="card-text">ID Petición: ${solicitud.cantidad}</p>
            <p class="card-text"><strong class="estado">Estado: ${solicitud.estado}</strong></p>
        </div>
    `;
    container.insertBefore(solicitudElement, container.firstChild);
}

/**
 * Carga el histórico de solicitudes
 */
function loadHistorico() {
    const loadingSpinner = document.getElementById('loadingSpinner');
    const container = document.getElementById('solicitudesContainer');
    
    // Mostrar spinner de carga
    loadingSpinner.style.display = 'inline-block';
    
    // Limpiar el contenedor actual
    container.innerHTML = '';
    
    console.log('Iniciando petición de histórico...');
    
    // Intentar vía API frontend primero
    fetch('/api/solicitudes/historico')
        .then(response => {
            console.log('Respuesta de /api/solicitudes/historico:', response);
            if (!response.ok) {
                throw new Error('Error al obtener el histórico de solicitudes');
            }
            return response.json();
        })
        .then(solicitudes => {
            // Ocultar spinner
            loadingSpinner.style.display = 'none';
            
            console.log('Solicitudes recibidas:', solicitudes);
            
            if (!solicitudes || solicitudes.length === 0) {
                console.log('No se recibieron solicitudes o array vacío, intentando directamente con el servicio CTRL');
                
                // Segundo intento: conexión directa al servicio CTRL
                return fetch('http://localhost:8082/ctrl/solicitudes')
                    .then(response => {
                        console.log('Respuesta directa de CTRL:', response);
                        if (!response.ok) {
                            throw new Error('Error en llamada directa al servicio CTRL');
                        }
                        return response.json();
                    })
                    .then(solicitudesCtrl => {
                        console.log('Solicitudes recibidas directamente de CTRL:', solicitudesCtrl);
                        if (!solicitudesCtrl || solicitudesCtrl.length === 0) {
                            container.innerHTML = '<div class="alert alert-info">No hay solicitudes históricas disponibles</div>';
                            return [];
                        }
                        
                        // Mostrar las solicitudes
                        solicitudesCtrl.forEach(solicitud => {
                            mostrarNuevaSolicitud(solicitud);
                        });
                        return solicitudesCtrl;
                    })
                    .catch(error => {
                        console.error('Error en llamada directa a CTRL:', error);
                        container.innerHTML = '<div class="alert alert-info">No hay solicitudes históricas disponibles</div>';
                        return [];
                    });
            }
            
            // Mostrar las solicitudes ordenadas por fecha (más recientes primero)
            solicitudes.sort((a, b) => {
                return new Date(b.fechaCreacion) - new Date(a.fechaCreacion);
            }).forEach(solicitud => {
                mostrarNuevaSolicitud(solicitud);
            });
            
            return solicitudes;
        })
        .catch(error => {
            console.error('Error:', error);
            loadingSpinner.style.display = 'none';
            container.innerHTML = `<div class="alert alert-danger">Error al cargar el histórico: ${error.message}</div>`;
        });
} 