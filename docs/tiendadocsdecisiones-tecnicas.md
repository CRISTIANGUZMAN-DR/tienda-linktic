# Decisiones Técnicas

## 1. Base de datos — Database per Service

Cada microservicio tiene su propia base de datos PostgreSQL independiente.
Esto garantiza que un cambio de esquema en Products no afecta Inventory,
y permite escalar cada servicio de forma independiente.

## 2. Concurrencia — Bloqueo Pesimista

Se eligió `PESSIMISTIC_WRITE` sobre optimista porque:

- Las compras son operaciones críticas que no pueden fallar silenciosamente
- El bloqueo optimista requiere reintentos en la capa de aplicación
- Con pesimista, la BD serializa el acceso y garantiza consistencia

## 3. Idempotencia — Tabla de claves

Cada compra acepta un `Idempotency-Key` en el header. Se persiste en una
tabla dedicada. Si la misma key llega dos veces, se retorna el resultado
anterior sin ejecutar la lógica de negocio nuevamente.
Protege contra: reintentos del cliente, fallos de red, dobles clicks.

## 4. Resiliencia — Resilience4j

Se eligió Resilience4j sobre alternativas porque:

- Es el estándar de facto en Spring Boot 3
- Integración nativa con anotaciones
- Circuit breaker + retry + timeout en una sola librería

Configuración:

- Retry: 3 intentos, 500ms entre ellos
- Circuit breaker: se abre con 50% de fallos en ventana de 5 llamadas
- Open state: 10 segundos antes de pasar a half-open

## 5. JWT — Stateless

Se eligió JWT sobre sesiones porque:

- Los microservicios no comparten estado
- Cada servicio valida el token independientemente
- No requiere base de datos de sesiones compartida

## 6. WebClient sobre RestTemplate

Se eligió WebClient porque:

- Es el cliente HTTP recomendado en Spring Boot 3
- RestTemplate está en modo mantenimiento
- Soporte nativo para timeout y configuración reactiva

## 7. Frontend — Pinia sobre Vuex

Se eligió Pinia porque:

- Es el store oficial recomendado para Vue 3
- API más simple y con mejor soporte TypeScript
- Devtools integrado

## 8. Caché simple en frontend

Se implementó caché en memoria en el store de productos con TTL de 1 minuto.
Evita llamadas innecesarias al backend al navegar entre páginas.
