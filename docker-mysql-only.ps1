# Script para ejecutar solo MySQL en Docker (para usar con Workbench)
# Ejecutar en PowerShell

Write-Host "🐳 Script MySQL Docker para Workbench" -ForegroundColor Green
Write-Host ""

function Show-Menu {
    Write-Host "Selecciona una opción:" -ForegroundColor Yellow
    Write-Host "1. Ejecutar solo MySQL" -ForegroundColor Cyan
    Write-Host "2. Ver logs de MySQL" -ForegroundColor Cyan
    Write-Host "3. Acceder a MySQL por línea de comandos" -ForegroundColor Cyan
    Write-Host "4. Parar MySQL" -ForegroundColor Cyan
    Write-Host "5. Verificar estado de MySQL" -ForegroundColor Cyan
    Write-Host "6. Mostrar configuración para Workbench" -ForegroundColor Cyan
    Write-Host "7. Salir" -ForegroundColor Gray
    Write-Host ""
}

function Start-MySQL {
    Write-Host "🚀 Iniciando MySQL en Docker..." -ForegroundColor Green
    docker-compose up -d mysql
    
    Write-Host ""
    Write-Host "⏳ Esperando que MySQL esté listo..." -ForegroundColor Yellow
    
    # Esperar hasta que MySQL esté listo
    do {
        Start-Sleep -Seconds 3
        $status = docker-compose ps mysql
        if ($status -like "*Up*") {
            Write-Host "✅ MySQL está corriendo!" -ForegroundColor Green
            break
        }
    } while ($true)
    
    Write-Host ""
    Write-Host "📋 Configuración para MySQL Workbench:" -ForegroundColor Cyan
    Write-Host "   Host: localhost" -ForegroundColor White
    Write-Host "   Puerto: 3306" -ForegroundColor White
    Write-Host "   Usuario: kiosco_user" -ForegroundColor White
    Write-Host "   Contraseña: kiosco_password" -ForegroundColor White
    Write-Host "   Base de datos: kiosco_db" -ForegroundColor White
}

function Show-MySQL-Logs {
    Write-Host "📋 Mostrando logs de MySQL..." -ForegroundColor Cyan
    docker-compose logs -f mysql
}

function Access-MySQL-CLI {
    Write-Host "🔗 Accediendo a MySQL por línea de comandos..." -ForegroundColor Cyan
    docker exec -it kiosco-mysql mysql -u kiosco_user -p kiosco_db
}

function Stop-MySQL {
    Write-Host "⏹️ Parando MySQL..." -ForegroundColor Yellow
    docker-compose stop mysql
    Write-Host "✅ MySQL parado." -ForegroundColor Green
}

function Check-MySQL-Status {
    Write-Host "📊 Estado de MySQL:" -ForegroundColor Cyan
    docker-compose ps mysql
    Write-Host ""
    Write-Host "📋 Información del contenedor:" -ForegroundColor Cyan
    docker inspect kiosco-mysql --format="Status: {{.State.Status}} | Health: {{.State.Health.Status}}"
}

function Show-Workbench-Config {
    Write-Host "🔧 Configuración para MySQL Workbench:" -ForegroundColor Green
    Write-Host ""
    Write-Host "Connection Name: Kiosco Docker" -ForegroundColor Yellow
    Write-Host "Hostname: localhost" -ForegroundColor White
    Write-Host "Port: 3306" -ForegroundColor White
    Write-Host "Username: kiosco_user" -ForegroundColor White
    Write-Host "Password: kiosco_password" -ForegroundColor White
    Write-Host "Default Schema: kiosco_db" -ForegroundColor White
    Write-Host ""
    Write-Host "🔧 Configuración Root (Administrador):" -ForegroundColor Yellow
    Write-Host "Connection Name: Kiosco Docker (Root)" -ForegroundColor White
    Write-Host "Username: root" -ForegroundColor White
    Write-Host "Password: root_password" -ForegroundColor White
    Write-Host ""
    Write-Host "📝 Pasos en Workbench:" -ForegroundColor Cyan
    Write-Host "1. File → New Connection" -ForegroundColor White
    Write-Host "2. Usar los parámetros de arriba" -ForegroundColor White
    Write-Host "3. Test Connection" -ForegroundColor White
    Write-Host "4. OK" -ForegroundColor White
}

# Menú principal
do {
    Show-Menu
    $choice = Read-Host "Ingresa tu opción (1-7)"
    
    switch ($choice) {
        "1" { Start-MySQL }
        "2" { Show-MySQL-Logs }
        "3" { Access-MySQL-CLI }
        "4" { Stop-MySQL }
        "5" { Check-MySQL-Status }
        "6" { Show-Workbench-Config }
        "7" { 
            Write-Host "👋 ¡Hasta luego!" -ForegroundColor Green
            exit 
        }
        default { 
            Write-Host "❌ Opción inválida. Intenta de nuevo." -ForegroundColor Red 
        }
    }
    
    if ($choice -ne "7") {
        Write-Host ""
        Write-Host "Presiona Enter para continuar..." -ForegroundColor Gray
        Read-Host
        Clear-Host
    }
} while ($choice -ne "7")
