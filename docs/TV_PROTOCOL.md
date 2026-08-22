# Protocolo móvil - Smart TV

## Transporte

- WebSocket de texto.
- Ruta sugerida: `/orders`.
- Codificación: UTF-8.
- JSON con versión de protocolo `1`.
- Encabezado enviado por el móvil: `X-Mercado-Protocol: 1`.

Durante desarrollo se admite `ws://` en la red local. Para una implementación pública debe usarse `wss://`.

## Pedido enviado por el móvil

```json
{
  "type": "order.create",
  "protocolVersion": 1,
  "orderId": "MT-A1B2C3D4",
  "customerName": "María López",
  "createdAtEpochMillis": 1787428800000,
  "items": [
    {
      "productId": "fruit-banana",
      "name": "Plátano",
      "unitPrice": 28.9,
      "quantity": 2,
      "subtotal": 57.8
    }
  ],
  "total": 57.8
}
```

La TV debe conservar `orderId` para relacionar la respuesta con el pedido correcto.

## Turno devuelto por la TV

```json
{
  "type": "order.turn",
  "protocolVersion": 1,
  "orderId": "MT-A1B2C3D4",
  "ticketNumber": "A-017",
  "estimatedMinutes": 12,
  "message": "Tu pedido estará listo pronto"
}
```

Al recibir este mensaje, el móvil muestra el turno y el tiempo estimado y limpia el carrito.

## Reglas para la futura aplicación de TV

1. Aceptar conexiones WebSocket en `/orders`.
2. Ignorar campos JSON desconocidos para permitir futuras ampliaciones.
3. Validar que `items` no esté vacío y que las cantidades sean positivas.
4. Mostrar el pedido recibido en pantalla.
5. Asignar un turno único.
6. Responder por la misma conexión con `order.turn`.
7. Si el pedido no es válido, responder con `type: order.error`, el `orderId` cuando exista y un `message` descriptivo.

## Red local

Si no conecta, comprueba que la TV y el teléfono estén en la misma subred, que el puerto elegido esté permitido por el firewall y que el servidor WebSocket escuche en `0.0.0.0`, no únicamente en `localhost`.
