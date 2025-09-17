# Configuración para MySQL Workbench

## Conexión a la base de datos Docker

### Parámetros de conexión:

- **Connection Name**: `Kiosco Docker`
- **Connection Method**: `Standard (TCP/IP)`
- **Hostname**: `localhost`
- **Port**: `3306`
- **Username**: `kiosco_user`
- **Password**: `kiosco_password`
- **Default Schema**: `kiosco_db`

### Conexión como root (administrador):

- **Connection Name**: `Kiosco Docker (Root)`
- **Connection Method**: `Standard (TCP/IP)`
- **Hostname**: `localhost`
- **Port**: `3306`
- **Username**: `root`
- **Password**: `root_password`

## Pasos para conectar:

1. **Ejecutar Docker:**
```bash
docker-compose up -d mysql
```

2. **Esperar que MySQL esté listo:**
```bash
docker-compose logs mysql
```

3. **En MySQL Workbench:**
   - File → New Connection
   - Usar los parámetros de arriba
   - Test Connection
   - OK

## Verificar conexión:

```sql
-- Verificar que la base de datos existe
SHOW DATABASES;

-- Usar la base de datos
USE kiosco_db;

-- Ver tablas (después de ejecutar la aplicación Java)
SHOW TABLES;
```

## Comandos útiles:

```bash
# Solo ejecutar MySQL (sin la aplicación Java)
docker-compose up -d mysql

# Ver logs de MySQL
docker-compose logs -f mysql

# Acceder por línea de comandos
docker exec -it kiosco-mysql mysql -u kiosco_user -p kiosco_db

# Parar MySQL
docker-compose stop mysql
```
