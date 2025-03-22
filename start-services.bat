@echo off
echo Iniciando servicios de la aplicacion Demo Kafka...
echo 1. Realizando limpieza previa...

REM Limpieza de recursos de Docker
echo Limpiando recursos sin usar de Docker...
docker container prune -f > nul 2>&1
docker volume prune -f > nul 2>&1
docker image prune -f > nul 2>&1

echo 2. Deteniendo contenedores previos...
docker-compose down

echo 3. Iniciando Kafka y MongoDB con recursos optimizados...
echo NOTA: Este proceso puede tardar hasta un minuto. Por favor, espera...
start "DOCKER-SERVICES" cmd /c "docker-compose up"

echo.
echo Esperando a que Kafka este disponible (10 segundos)...
timeout /t 10

echo.
echo Verificando que los contenedores esten ejecutandose...
docker ps

echo.
echo 4. Iniciando servicios Java con memoria optimizada...
echo Iniciando servicio INGR...
start "INGR-SERVICE" cmd /c "java -Xms128m -Xmx256m -jar ingr-service/target/ingr-service-1.0-SNAPSHOT.jar"
timeout /t 3

echo Iniciando servicio CTRL...
start "CTRL-SERVICE" cmd /c "java -Xms128m -Xmx256m -jar ctrl-service/target/ctrl-service-1.0-SNAPSHOT.jar"
timeout /t 3

echo Iniciando servicio FRONTEND...
start "FRONTEND-SERVICE" cmd /c "java -Xms128m -Xmx256m -jar frontend-service/target/frontend-service-1.0-SNAPSHOT.jar"

echo.
echo Todos los servicios han sido iniciados con configuracion optimizada.
echo.
echo IMPORTANTE:
echo - Los contenedores Docker ahora tienen limites de recursos para mejor rendimiento.
echo - Las aplicaciones Java tienen configuracion de memoria optimizada.
echo - Para detener los contenedores Docker: docker-compose down
echo - Para reiniciar todo el sistema, cierra todas las ventanas y ejecuta este script nuevamente.
echo.
pause 