-- Script de inicialización de la base de datos para el sistema de kiosco
-- Este script se ejecuta automáticamente cuando se crea el contenedor de MySQL

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS kiosco_db;

-- Usar la base de datos
USE kiosco_db;

-- Crear usuario específico para la aplicación (opcional, pero buena práctica)
CREATE USER IF NOT EXISTS 'kiosco_user'@'%' IDENTIFIED BY 'kiosco_password';
GRANT ALL PRIVILEGES ON kiosco_db.* TO 'kiosco_user'@'%';

-- Las tablas se crearán automáticamente por Hibernate con hbm2ddl.auto=update
-- Pero podemos crear algunas tablas básicas si queremos

-- Configurar charset UTF-8
ALTER DATABASE kiosco_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Mostrar información de la base de datos
SELECT 'Base de datos kiosco_db creada correctamente' as mensaje;
