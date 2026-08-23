# Protocolo móvil - Smart TV

## Transporte y configuración

- WebSocket de texto en `ws://IP_DE_LA_TV:8080/orders`.
- JSON UTF-8 con versión de protocolo `1`.
- El móvil envía el encabezado `X-Mercado-Protocol: 1`.
- Ambos equipos deben estar en la misma red Wi-Fi.

La TV muestra su dirección de conexión en pantalla. Para dispositivos físicos, copia esa IP en `ORDER_SERVICE_URL`, dentro de `Movile/build.gradle.kts`. Con dos emuladores se usa `10.0.2.2` y se ejecuta `scripts/connect-emulators.ps1` para reenviar el puerto de la computadora al emulador TV. Nunca se edita `BuildConfig.java`: es un archivo generado desde `Movile/build.gradle.kts`.

## 1. Pedido enviado por el móvil

```json
{
  "type": "order.create",
  "protocolVersion": 1,
  "orderId": "MT-A1B2C3D4",
  "customerName": "María López",
  "createdAtEpochMillis": 1787428800000,
  "items": [{"productId":"fruit-banana","name":"Plátano","unitPrice":28.9,"quantity":2,"subtotal":57.8}],
  "total": 57.8
}
```

## 2. Confirmación de ingreso a la fila

```json
{"type":"order.turn","protocolVersion":1,"orderId":"MT-A1B2C3D4","ticketNumber":"Cliente #1","queuePosition":1,"message":"Cliente #1 registrado. Mantén abierta la aplicación"}
```

El móvil muestra el indicador asignado y mantiene abierto el WebSocket.

## 3. Llamado a caja

```json
{"type":"order.called","protocolVersion":1,"orderId":"MT-A1B2C3D4","ticketNumber":"Cliente #1","checkout":"Caja 1","message":"¡Es tu turno! Pasa a la caja"}
```

La TV llama automáticamente un cliente cada 15 segundos. El móvil cambia a la pantalla de aviso y publica una notificación local de alta prioridad.

## Errores

La TV responde `order.error` si el JSON es inválido, la versión no coincide, faltan datos o existen productos con cantidad no positiva. Los campos desconocidos se ignoran para mantener compatibilidad futura.
