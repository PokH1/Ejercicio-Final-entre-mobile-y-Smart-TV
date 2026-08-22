# Mercado La Selva

Aplicación Android para seleccionar productos de supermercado, enviar el pedido a una Smart TV y recibir el turno en el que estará listo para recogerse.

## Estado del proyecto

- `Movile/`: aplicación móvil funcional desarrollada con Kotlin y Jetpack Compose.
- `TV/`: reservado para la futura aplicación de Smart TV.
- Rama de desarrollo móvil: `mobile`.

## Ejecutar la aplicación

1. Abre la raíz del repositorio en Android Studio.
2. Espera a que Gradle sincronice el proyecto.
3. Selecciona la configuración `mobile` y ejecuta en un dispositivo con Android 8.0 (API 26) o superior.

También puedes compilar desde PowerShell:

```powershell
.\gradlew.bat :mobile:assembleDebug
```

El APK se genera en `Movile/build/outputs/apk/debug/mobile-debug.apk`.

## Configuración interna de la conexión

El móvil usa WebSocket para mantener una comunicación bidireccional. La TV o el servidor asociado debe escuchar, por ejemplo, en:

```text
ws://192.168.1.100:8080/orders
```

La dirección no se muestra al usuario. Antes de compilar, sustituye `192.168.1.100` en `Movile/build.gradle.kts` por la IP local del dispositivo que ejecuta la aplicación de TV. Ambos dispositivos deben estar conectados a la misma red Wi-Fi.

Para probar con el emulador Android y un servidor ejecutándose en la computadora anfitriona usa:

```text
ws://10.0.2.2:8080/orders
```

## Flujo

1. El usuario filtra el catálogo y selecciona cantidades.
2. Revisa el pedido, escribe su nombre y lo confirma.
3. El móvil abre el WebSocket y envía un mensaje `order.create`.
4. La TV registra el pedido y responde con `order.turn`.
5. El móvil presenta el número de turno y el tiempo estimado.

La especificación completa del intercambio está en `docs/TV_PROTOCOL.md`.

## Verificación

```powershell
.\gradlew.bat :mobile:testDebugUnitTest :mobile:assembleDebug
```
