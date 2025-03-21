# 📱 Guía de Uso

Esta guía proporciona instrucciones detalladas sobre cómo utilizar el Sistema de Solicitudes Distribuido, desde el acceso a la aplicación hasta la consulta del histórico de solicitudes.

## Acceso a la Aplicación

1. Abre tu navegador web (Chrome, Firefox, Safari o Edge)
2. Navega a la URL: `http://localhost:8080`
3. Deberías ver la página principal con un formulario para crear solicitudes y un panel para visualizar solicitudes recibidas

## Interfaz de Usuario

La interfaz principal se compone de tres secciones principales:

![Interfaz de Usuario](./img/ui_overview.png) *(Imagen de referencia, puede variar)*

1. **Formulario de Creación**: Situado en la parte superior
2. **Solicitudes Recibidas**: Panel central que muestra actualizaciones en tiempo real
3. **Histórico de Solicitudes**: Accesible mediante botón en la parte inferior

## Creación de Una Nueva Solicitud

### Paso 1: Completar el formulario

Rellena los siguientes campos:
- **Solicitud**: Introduce el nombre del cliente o identificador de la solicitud
- **Descripción**: Proporciona una descripción detallada de la solicitud
- **ID Petición**: Introduce un número único de identificación (valor numérico)

### Paso 2: Enviar la solicitud

1. Haz clic en el botón "Enviar Solicitud"
2. El sistema procesará la solicitud y mostrará un mensaje de confirmación
3. La solicitud aparecerá en la sección "Solicitudes Recibidas" con estado inicial "CREADA"

### Paso 3: Seguimiento en tiempo real

Observa cómo el estado de tu solicitud se actualiza automáticamente:
1. **CREADA** → Solicitud enviada correctamente
2. **VALIDADA** → La solicitud ha pasado la validación
3. **PROCESADA** → La solicitud ha sido procesada completamente
4. **RECHAZADA** → La solicitud ha sido rechazada por no cumplir criterios de validación

## Visualización de Solicitudes en Tiempo Real

La sección "Solicitudes Recibidas" muestra todas las solicitudes procesadas recientemente:

- Las solicitudes se actualizan automáticamente mediante WebSockets
- Cada solicitud muestra: ID, Nombre, Descripción, Estado y Fechas
- El color de fondo indica el estado:
  - **Verde**: PROCESADA
  - **Amarillo**: VALIDADA
  - **Azul**: CREADA
  - **Rojo**: RECHAZADA

## Consulta de Histórico de Solicitudes

### Paso 1: Acceder al histórico

1. Haz clic en el botón "Ver Histórico de Solicitudes" situado en la parte inferior de la página

### Paso 2: Explorar el histórico

El sistema mostrará una tabla con todas las solicitudes procesadas anteriormente:

![Histórico de Solicitudes](./img/historico.png) *(Imagen de referencia, puede variar)*

La tabla incluye:
- ID de la solicitud
- Nombre del cliente
- Descripción
- Cantidad (ID Petición)
- Estado actual
- Fecha de creación
- Fecha de procesamiento

### Paso 3: Filtrar y buscar (opcional)

Utiliza el campo de búsqueda para filtrar las solicitudes:
1. Introduce texto para buscar en cualquier campo
2. La tabla se actualizará automáticamente mostrando solo las coincidencias

## Ejemplos de Uso

### Ejemplo 1: Solicitud Básica

```
Solicitud: Cliente Ejemplo
Descripción: Pedido de material de oficina
ID Petición: 12345
```

### Ejemplo 2: Solicitud Detallada

```
Solicitud: Departamento de Marketing
Descripción: Solicitud de diseño para nueva campaña promocional. Incluir logotipos actualizados y seguir la guía de marca.
ID Petición: 67890
```

## Ciclo Completo de una Solicitud

1. **Usuario crea solicitud** → Formulario web
2. **Frontend recibe solicitud** → Asigna ID único y fecha
3. **INGR Service valida** → Verifica datos y reglas de negocio
4. **CTRL Service procesa** → Almacena en MongoDB
5. **Notificación en tiempo real** → WebSocket actualiza interfaz
6. **Consulta histórica** → Disponible para futuras referencias

## Atajos de Teclado

| Tecla | Función |
|-------|---------|
| `Alt + N` | Nueva solicitud (limpia el formulario) |
| `Alt + S` | Enviar solicitud (equivale a clic en el botón) |
| `Alt + H` | Ver histórico de solicitudes |
| `Esc` | Cerrar ventana de histórico |

## Buenas Prácticas

1. **Campos descriptivos**: Usa nombres y descripciones claras
2. **ID de Petición único**: Utiliza un sistema para asegurar unicidad
3. **Verificación**: Comprueba que la solicitud aparece correctamente en el sistema
4. **Seguimiento**: Observa las actualizaciones de estado en tiempo real
5. **Documentación**: Registra los IDs de solicitudes importantes para referencia futura

## Solución de Problemas Comunes

| Problema | Posible Causa | Solución |
|----------|---------------|----------|
| La solicitud no aparece | Conexión WebSocket interrumpida | Recarga la página |
| Solicitud en estado "CREADA" sin cambios | Problema en el servicio INGR | Verifica los logs del servicio INGR |
| Solicitud rechazada | Datos inválidos (ej. ID negativo) | Corrige los datos y envía de nuevo |
| No se carga el histórico | Problema en el servicio CTRL | Verifica la conexión a MongoDB y los logs |

## Información Adicional

- Los datos se almacenan persistentemente en MongoDB
- Las solicitudes pueden tardar unos segundos en procesarse completamente
- El sistema mantiene un registro completo de todas las solicitudes
- Las actualizaciones en tiempo real funcionan mejor con una conexión estable a Internet

---

*[Volver al índice principal](WIKI.md)* 