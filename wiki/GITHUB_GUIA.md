# Guía de uso de GitHub para el Sistema de Solicitudes Distribuido

## Índice
- [Configuración inicial](#configuración-inicial)
- [Clonar el repositorio](#clonar-el-repositorio)
- [Trabajar con el código](#trabajar-con-el-código)
- [Sincronizar cambios](#sincronizar-cambios)
- [Trabajar con ramas](#trabajar-con-ramas)
- [Colaboración en equipo](#colaboración-en-equipo)
- [Comandos útiles](#comandos-útiles)
- [Solución de problemas comunes](#solución-de-problemas-comunes)

## Configuración inicial

### Instalar Git
1. Descarga Git desde [git-scm.com](https://git-scm.com/downloads)
2. Durante la instalación, acepta las opciones predeterminadas

### Configurar credenciales
```bash
git config --global user.name "Tu Nombre"
git config --global user.email "tu.email@ejemplo.com"
```

### Crear token de acceso personal
1. Ve a GitHub → Settings → Developer settings → Personal access tokens
2. Genera un nuevo token con permisos "repo"
3. Guarda el token en un lugar seguro (se usará como contraseña)

## Clonar el repositorio

### Clonar desde GitHub
```bash
# Reemplaza USERNAME con tu nombre de usuario
git clone https://github.com/inaki76/demo-sistema-distribuido.git
cd demo-sistema-distribuido
```

### Verificar configuración remota
```bash
git remote -v
# Debería mostrar: origin https://github.com/inaki76/demo-sistema-distribuido.git (fetch/push)
```

## Trabajar con el código

### Ver estado actual
```bash
git status
```

### Añadir archivos nuevos o modificados
```bash
# Añadir un archivo específico
git add ruta/al/archivo.java

# Añadir todos los archivos modificados
git add .

# Añadir todos los cambios (incluyendo eliminaciones)
git add --all
```

### Crear un commit
```bash
git commit -m "Descripción clara de los cambios realizados"
```

## Sincronizar cambios

### Actualizar tu repositorio local
```bash
# Obtener cambios sin aplicarlos
git fetch

# Obtener y aplicar cambios
git pull origin main
```

### Subir cambios a GitHub
```bash
git push origin main
```

### Sincronizar después de borrar archivos
```bash
git add --all
git commit -m "Eliminar archivos innecesarios"
git push origin main
```

## Trabajar con ramas

### Crear una nueva rama
```bash
# Crear y cambiar a una nueva rama
git checkout -b nombre-nueva-rama
```

### Cambiar entre ramas
```bash
git checkout main
git checkout nombre-otra-rama
```

### Subir una rama nueva a GitHub
```bash
git push -u origin nombre-nueva-rama
```

### Fusionar ramas
```bash
# Primero cambia a la rama destino
git checkout main

# Luego fusiona desde otra rama
git merge nombre-otra-rama
```

## Colaboración en equipo

### Actualizar fork desde el repositorio original
```bash
# Añadir el repositorio original como remoto
git remote add upstream https://github.com/inaki76/demo-sistema-distribuido.git

# Obtener cambios del repositorio original
git fetch upstream

# Fusionar cambios a tu rama local
git merge upstream/main
```

### Crear un Pull Request
1. Sube tus cambios a tu fork: `git push origin tu-rama`
2. Ve a GitHub y navega a tu fork
3. Haz clic en "Pull request"
4. Selecciona la rama base y la rama con cambios
5. Describe tus cambios y crea el Pull Request

## Comandos útiles

### Ver historial de cambios
```bash
# Ver historial completo
git log

# Ver historial resumido
git log --oneline

# Ver historial con gráfico
git log --graph --oneline --all
```

### Deshacer cambios
```bash
# Deshacer cambios en archivos no añadidos al staging
git checkout -- nombre-archivo

# Deshacer archivos en staging
git reset HEAD nombre-archivo

# Deshacer el último commit (mantiene cambios)
git reset --soft HEAD~1

# Deshacer el último commit (elimina cambios)
git reset --hard HEAD~1
```

### Etiquetar versiones
```bash
# Crear etiqueta
git tag v1.0.0

# Crear etiqueta con mensaje
git tag -a v1.0.0 -m "Versión 1.0.0 estable"

# Subir etiquetas a GitHub
git push origin --tags
```

## Solución de problemas comunes

### Error al hacer push
Si recibes error al hacer push porque hay cambios remotos:
```bash
git pull --rebase origin main
git push origin main
```

### Conflictos de fusión
Si hay conflictos durante un merge:
1. Abre los archivos marcados con conflicto
2. Busca las secciones marcadas con `<<<<<<<`, `=======`, `>>>>>>>`
3. Edita los archivos para resolver los conflictos
4. Añade los archivos resueltos: `git add .`
5. Continúa el merge: `git merge --continue`

### Recuperar archivo eliminado
```bash
# Encuentra el último commit que afectó al archivo
git log -- ruta/al/archivo-eliminado

# Recupera el archivo
git checkout COMMIT_HASH -- ruta/al/archivo-eliminado
```

---

Si necesitas ayuda adicional, consulta la [documentación oficial de Git](https://git-scm.com/doc) o [GitHub Docs](https://docs.github.com/es). 