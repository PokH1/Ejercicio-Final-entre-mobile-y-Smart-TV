$ErrorActionPreference = "Stop"

$adbCandidates = @(
    (Get-Command adb -ErrorAction SilentlyContinue | Select-Object -ExpandProperty Source -ErrorAction SilentlyContinue),
    "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe",
    "$env:ANDROID_HOME\platform-tools\adb.exe"
) | Where-Object { $_ -and (Test-Path $_) }

$adb = $adbCandidates | Select-Object -First 1
if (-not $adb) {
    throw "No se encontró adb. Abre Android Studio o agrega Android SDK platform-tools al PATH."
}

$devices = & $adb devices | Select-Object -Skip 1 | ForEach-Object {
    if ($_ -match "^(emulator-\d+)\s+device$") { $Matches[1] }
} | Where-Object { $_ }

if (-not $devices) {
    throw "No hay emuladores Android encendidos. Inicia primero el teléfono y la TV."
}

$tvSerial = $devices | Where-Object {
    $features = & $adb -s $_ shell pm list features 2>$null
    $packagePath = & $adb -s $_ shell pm path com.utselva.supermercadoturno.tv 2>$null
    $hasTvApp = $LASTEXITCODE -eq 0 -and $packagePath -match "package:"
    $isTvDevice = $features -match "android.hardware.type.television|android.software.leanback_only"
    $hasTvApp -and $isTvDevice
} | Select-Object -First 1

if (-not $tvSerial) {
    throw "No encontré la app TV instalada en un emulador Android TV. Ejecuta la configuración 'tv' en el dispositivo Television y vuelve a correr este script."
}

# El puerto host sólo puede apuntar a un dispositivo. Elimina reglas antiguas,
# incluso si la app TV quedó instalada por accidente en un teléfono.
foreach ($device in $devices) {
    & $adb -s $device forward --remove tcp:8080 2>$null
}
& $adb -s $tvSerial forward tcp:8080 tcp:8080
if ($LASTEXITCODE -ne 0) {
    throw "No se pudo reenviar el puerto 8080 al emulador TV $tvSerial."
}

Write-Host "Conexión local preparada." -ForegroundColor Green
Write-Host "TV: $tvSerial (puerto 8080 reenviado)"
Write-Host "El móvil debe usar: ws://10.0.2.2:8080/orders"
