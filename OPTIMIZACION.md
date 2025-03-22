# Recomendaciones de optimización para Docker con WSL2

## Cambios aplicados

Se han realizado las siguientes optimizaciones para mejorar el rendimiento del sistema:

1. **Configuración de WSL2**: 
   - Se ha creado un archivo `.wslconfig` en tu directorio de usuario para limitar los recursos que WSL2 utiliza.
   - Memoria limitada a 4GB
   - Procesadores limitados a 2
   - Memoria swap limitada a 2GB

2. **Optimización de Docker Compose**:
   - Se han añadido límites de recursos a todos los servicios:
     - Zookeeper: 0.5 CPU, 512MB RAM
     - Kafka: 1.0 CPU, 1GB RAM
     - MongoDB: 0.7 CPU, 768MB RAM

3. **Optimización del script de inicio**:
   - Limpieza automática de recursos Docker no utilizados
   - Parámetros de memoria optimizados para las aplicaciones Java
   - Mejor secuencia de inicio de servicios

## Recomendaciones adicionales

Para obtener mejor rendimiento, considera hacer estos cambios manualmente en Docker Desktop:

1. **Ajustes en Docker Desktop**:
   - Abre Docker Desktop → Settings → Resources
   - Limita la memoria a 4-6GB (dependiendo de tu RAM total)
   - Limita CPUs a 2-4 (dependiendo de tu CPU)
   - En la pestaña Advanced, activa "Use the new Virtualization framework" y "Enable VirtioFS accelerated directory sharing"

2. **Mantenimiento periódico**:
   - Ejecuta regularmente `docker system prune -a` para limpiar recursos
   - Reinicia Docker Desktop cuando notes que el rendimiento disminuye

3. **Ajustes del sistema**:
   - Asegúrate de que Windows tiene suficiente espacio en disco
   - Desactiva aplicaciones innecesarias en segundo plano
   - Considera usar un SSD para los archivos de Docker

## Solución de problemas de rendimiento

Si sigues experimentando problemas de rendimiento:

1. **Comprueba el uso de recursos**:
   - Usa `docker stats` para monitorizar el uso de recursos de los contenedores
   - Usa el Administrador de tareas para verificar el uso de recursos de Windows

2. **Opciones alternativas**:
   - Cambia de WSL2 a Hyper-V si continúas con problemas (reinstala Docker Desktop seleccionando Hyper-V)
   - Considera usar Docker en una máquina virtual Linux dedicada
   - Para desarrollo, puedes utilizar versiones más ligeras de las imágenes (alpine) 