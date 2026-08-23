# Mercado La Selva - turnos móvil y Smart TV

Solución Android con dos aplicaciones Jetpack Compose. El cliente arma su pedido en el móvil; la Smart TV recibe y presenta la lista de productos, asigna indicadores `Cliente #1`, `Cliente #2`, etc., y llama automáticamente al siguiente cada 15 segundos. Cuando llega su turno, el teléfono muestra una advertencia y una notificación para pasar a Caja 1.

## Módulos

- `Movile/` (`:mobile`): catálogo, carrito, envío, espera y notificación.
- `TV/` (`:tv`): aplicación Android TV, servidor WebSocket y pantalla de cola.
- `shared/` (`:shared`): modelos JSON compartidos.
- `docs/`: protocolo y diagrama de contexto.

## Configuración de red

### Dos emuladores en la misma computadora

Los emuladores no pueden comunicarse entre sí usando el `10.0.2.15` que muestra la TV, porque cada emulador tiene su propia red virtual. El proyecto móvil ya usa `ws://10.0.2.2:8080/orders`, que representa a la computadora anfitriona.

1. Inicia los emuladores de teléfono y TV.
2. Ejecuta primero la app `tv`.
3. Desde PowerShell, en la raíz del proyecto, ejecuta:

```powershell
.\scripts\connect-emulators.ps1
```

4. Vuelve a ejecutar `mobile` y confirma un pedido.

El script detecta cuál emulador tiene instalada la app TV y reenvía el puerto `8080` de la computadora hacia ese emulador. Debe ejecutarse nuevamente si reinicias el emulador TV.

### Dispositivos físicos

1. Conecta el teléfono y la TV a la misma red Wi-Fi.
2. Ejecuta `tv`. Su panel izquierdo muestra una URL como `ws://192.168.1.25:8080/orders`.
3. Copia esa dirección en `ORDER_SERVICE_URL` de `Movile/build.gradle.kts` y recompila `mobile`.
4. Acepta el permiso de notificaciones en el móvil (Android 13 o posterior).
5. Comprueba que el firewall o aislamiento de clientes no bloquee el puerto TCP 8080.

Esta práctica usa `ws://` sólo en la red local; una publicación real debe usar `wss://` y autenticación.

## Compilación y prueba

Abre la raíz en Android Studio y ejecuta primero `tv` en Android TV (API 26+) y después `mobile` en un teléfono (API 26+).

```powershell
.\gradlew.bat :mobile:testDebugUnitTest :tv:testDebugUnitTest :mobile:assembleDebug :tv:assembleDebug
```

Los APK quedan en `Movile/build/outputs/apk/debug/mobile-debug.apk` y `TV/build/outputs/apk/debug/tv-debug.apk`.

## Flujo de demostración

1. Agrega productos en el móvil, escribe el nombre y confirma.
2. La TV muestra indicador, cliente, productos, cantidades y total.
3. El móvil muestra su indicador y conserva la conexión.
4. Cada 15 segundos la TV llama al primer cliente y lo muestra en “Turno en caja”.
5. Ese cliente recibe una notificación y la pantalla “¡ES TU TURNO!”.

Consulta `docs/TV_PROTOCOL.md` y `docs/DIAGRAMA_CONTEXTO.md`.
