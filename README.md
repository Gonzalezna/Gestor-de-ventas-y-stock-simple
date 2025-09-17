# Sistema de Gestión de Kiosco

Sistema de gestión de kiosco desarrollado en Java con Hibernate para el manejo de productos, ventas y cajas.

## Características

- **Gestión de Productos**: Manejo completo de inventario con control de stock
- **Sistema de Ventas**: Registro de ventas con detalles de productos
- **Control de Caja**: Gestión de caja registradora con apertura y cierre
- **Base de Datos**: Persistencia con Hibernate y MySQL

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/fortunato/sistema/
│   │   ├── modelo/
│   │   │   ├── Producto.java          # Entidad Producto
│   │   │   ├── Venta.java            # Entidad Venta
│   │   │   ├── DetalleVenta.java     # Entidad DetalleVenta
│   │   │   └── Caja.java             # Entidad Caja
│   │   └── SistemaKioscoApp.java     # Clase principal
│   └── resources/
│       └── hibernate.cfg.xml         # Configuración de Hibernate
└── test/
    └── java/com/fortunato/sistema/
```

## Entidades del Sistema

### Producto
- ID único, nombre, descripción, precio
- Control de stock y stock mínimo
- Categoría y código de barras
- Fechas de creación y modificación

### Venta
- Número de venta único, fecha
- Asociación con caja registradora
- Lista de detalles de productos vendidos
- Estados: PENDIENTE, COMPLETADA, CANCELADA

### DetalleVenta
- Producto específico vendido
- Cantidad y precio unitario
- Cálculo automático de subtotal

### Caja
- Número de caja único
- Saldo inicial y actual
- Control de apertura y cierre
- Lista de ventas realizadas

## Configuración

### Base de Datos MySQL

1. Crear la base de datos:
```sql
CREATE DATABASE kiosco_db;
```

2. Actualizar las credenciales en `src/main/resources/hibernate.cfg.xml`:
```xml
<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/kiosco_db</property>
<property name="hibernate.connection.username">tu_usuario</property>
<property name="hibernate.connection.password">tu_contraseña</property>
```

### Compilación y Ejecución


### Con Docker (Recomendado) 🐳

#### **Opción A: Sistema completo con phpMyAdmin**
```bash
# Construir y ejecutar todo
docker-compose up --build -d

# O usar el script interactivo
.\docker-scripts.ps1
```

#### **Opción B: Solo MySQL para usar con Workbench**
```bash
# Solo ejecutar MySQL
docker-compose up -d mysql

# O usar el script específico para MySQL
.\docker-mysql-only.ps1
```

**Configuración para MySQL Workbench:**
- Host: `localhost`
- Puerto: `3306`
- Usuario: `kiosco_user`
- Contraseña: `kiosco_password`
- Base de datos: `kiosco_db`

#### **Comandos útiles:**
```bash
# Ver logs de MySQL
docker-compose logs -f mysql

# Ver logs de la aplicación (si está corriendo)
docker-compose logs -f kiosco-app

# Parar todo
docker-compose down
```

### Ejecución Local 💻

1. **Configurar MySQL localmente** y actualizar `hibernate.cfg.xml`

2. **Compilar el proyecto:**
```bash
mvn compile
```

3. **Ejecutar la aplicación:**
```bash
mvn exec:java -Dexec.mainClass="com.fortunato.sistema.SistemaKioscoApp"
```

## Funcionalidades Implementadas

- ✅ Creación automática de tablas en la base de datos
- ✅ Gestión completa de productos con control de stock
- ✅ Sistema de ventas con múltiples productos
- ✅ Control de caja registradora
- ✅ Cálculos automáticos de totales
- ✅ Validaciones de negocio
- ✅ Fechas automáticas de creación y modificación

## Próximas Mejoras

- [ ] Interfaz gráfica de usuario
- [ ] Reportes de ventas
- [ ] Gestión de usuarios y permisos
- [ ] Integración con sistema de facturación
- [ ] Backup automático de datos

## Arquitectura Docker 🐳

El proyecto incluye una configuración completa de Docker con:

- **Contenedor de aplicación Java**: Compilación y ejecución automática
- **MySQL 8.0**: Base de datos con inicialización automática
- **phpMyAdmin**: Interfaz web para administrar la base de datos
- **Red personalizada**: Comunicación entre contenedores
- **Volúmenes persistentes**: Los datos se mantienen entre reinicios

### Estructura Docker:
```
docker-compose.yml          # Orquestación de servicios
Dockerfile                  # Imagen de la aplicación Java
docker/init.sql            # Script de inicialización de BD
docker-scripts.ps1         # Scripts interactivos (Windows)
```

## Tecnologías Utilizadas

- **Java 11**
- **Hibernate 6.3.1**
- **MySQL 8.0**
- **Docker & Docker Compose**
- **Maven**
- **phpMyAdmin**
- **JUnit 5** (para testing)

## Autor

Proyecto desarrollado por Fortunato - Sistema de Gestión de Kiosco

