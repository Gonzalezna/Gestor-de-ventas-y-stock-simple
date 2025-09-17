# Scripts de Docker para el Sistema de Gestión de Kiosco
# Ejecutar en PowerShell

Write-Host "🐳 Scripts de Docker para Sistema de Kiosco" -ForegroundColor Green
Write-Host ""

function Show-Menu {
    Write-Host "Selecciona una opción:" -ForegroundColor Yellow
    Write-Host "1. Construir y ejecutar todo el sistema" -ForegroundColor Cyan
    Write-Host "2. Solo ejecutar (sin construir)" -ForegroundColor Cyan
    Write-Host "3. Parar todos los servicios" -ForegroundColor Cyan
    Write-Host "4. Ver logs de la aplicación" -ForegroundColor Cyan
    Write-Host "5. Ver logs de MySQL" -ForegroundColor Cyan
    Write-Host "6. Acceder a la base de datos" -ForegroundColor Cyan
    Write-Host "7. Limpiar todo (contenedores, imágenes, volúmenes)" -ForegroundColor Red
    Write-Host "8. Salir" -ForegroundColor Gray
    Write-Host ""
}

function Build-And-Run {
    Write-Host "🔨 Construyendo y ejecutando el sistema..." -ForegroundColor Green
    docker-compose up --build -d
    Write-Host "✅ Sistema iniciado. Accede a phpMyAdmin en: http://localhost:8080" -ForegroundColor Green
}

function Run-Only {
    Write-Host "▶️ Ejecutando sistema (sin construir)..." -ForegroundColor Green
    docker-compose up -d
    Write-Host "✅ Sistema iniciado." -ForegroundColor Green
}

function Stop-Services {
    Write-Host "⏹️ Parando servicios..." -ForegroundColor Yellow
    docker-compose down
    Write-Host "✅ Servicios parados." -ForegroundColor Green
}

function Show-App-Logs {
    Write-Host "📋 Mostrando logs de la aplicación..." -ForegroundColor Cyan
    docker-compose logs -f kiosco-app
}

function Show-MySQL-Logs {
    Write-Host "🗄️ Mostrando logs de MySQL..." -ForegroundColor Cyan
    docker-compose logs -f mysql
}

function Access-Database {
    Write-Host "🔗 Accediendo a la base de datos..." -ForegroundColor Cyan
    Write-Host "Credenciales:" -ForegroundColor Yellow
    Write-Host "  Host: localhost:3306" -ForegroundColor White
    Write-Host "  Usuario: kiosco_user" -ForegroundColor White
    Write-Host "  Contraseña: kiosco_password" -ForegroundColor White
    Write-Host "  Base de datos: kiosco_db" -ForegroundColor White
    Write-Host ""
    Write-Host "O accede a phpMyAdmin en: http://localhost:8080" -ForegroundColor Green
    docker exec -it kiosco-mysql mysql -u kiosco_user -p kiosco_db
}

function Clean-All {
    Write-Host "🧹 Limpiando todo..." -ForegroundColor Red
    Write-Host "¿Estás seguro? Esto eliminará contenedores, imágenes y volúmenes (y/s/N): " -NoNewline -ForegroundColor Red
    $confirm = Read-Host
    if ($confirm -eq "y" -or $confirm -eq "Y") {
        docker-compose down -v --rmi all
        docker system prune -f
        Write-Host "✅ Limpieza completada." -ForegroundColor Green
    } else {
        Write-Host "❌ Limpieza cancelada." -ForegroundColor Yellow
    }
}

# Menú principal
do {
    Show-Menu
    $choice = Read-Host "Ingresa tu opción (1-8)"
    
    switch ($choice) {
        "1" { Build-And-Run }
        "2" { Run-Only }
        "3" { Stop-Services }
        "4" { Show-App-Logs }
        "5" { Show-MySQL-Logs }
        "6" { Access-Database }
        "7" { Clean-All }
        "8" { 
            Write-Host "👋 ¡Hasta luego!" -ForegroundColor Green
            exit 
        }
        default { 
            Write-Host "❌ Opción inválida. Intenta de nuevo." -ForegroundColor Red 
        }
    }
    
    if ($choice -ne "8") {
        Write-Host ""
        Write-Host "Presiona Enter para continuar..." -ForegroundColor Gray
        Read-Host
        Clear-Host
    }
} while ($choice -ne "8")
