@echo off
echo Iniciando servicios de la aplicación Demo Kafka...
echo NOTA: Este script iniciará los servicios Java, pero necesitarás Kafka y MongoDB para un funcionamiento completo.

echo.
echo 1. Intentando iniciar el servicio INGR...
start "INGR-SERVICE" java -jar ingr-service/target/ingr-service-1.0-SNAPSHOT.jar
timeout /t 5

echo.
echo 2. Intentando iniciar el servicio CTRL...
start "CTRL-SERVICE" java -jar ctrl-service/target/ctrl-service-1.0-SNAPSHOT.jar
timeout /t 5

echo.
echo 3. Intentando iniciar el servicio FRONTEND...
start "FRONTEND-SERVICE" java -jar frontend-service/target/frontend-service-1.0-SNAPSHOT.jar

echo.
echo Todos los servicios se han iniciado. Por favor, revisa las ventanas de cada servicio para detectar posibles errores.
echo Recuerda que sin Kafka y MongoDB, los servicios podrían no funcionar correctamente.
echo.
echo Para instalar Docker y ejecutar el sistema completo:
echo 1. Descarga Docker Desktop desde https://www.docker.com/products/docker-desktop/
echo 2. Instala Docker Desktop
echo 3. Ejecuta "docker-compose up -d" en este directorio para iniciar Kafka y MongoDB
echo.
pause 