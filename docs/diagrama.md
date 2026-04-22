# Diagrama C4 — Tienda Linktic

## Nivel 1 — Contexto del Sistema

```
┌─────────────────────────────────────────────────────────────┐
│                        INTERNET                             │
│                                                             │
│   ┌─────────────┐                                           │
│   │   Usuario   │                                           │
│   │  (Browser)  │                                           │
│   └──────┬──────┘                                           │
│          │ HTTPS                                            │
│          ▼                                                   │
│   ┌─────────────────────────────┐                           │
│   │      Tienda Linktick        │                           │
│   │   Sistema de gestión de     │                           │
│   │   productos e inventario    │                           │
│   └─────────────────────────────┘                           │
└─────────────────────────────────────────────────────────────┘
```

## Nivel 2 — Contenedores

```
┌──────────────────────────────────────────────────────────────────────────┐
│                          Tienda Linktick                                 │
│                                                                          │
│  ┌─────────────────┐                                                     │
│  │   Usuario        │                                                    │
│  │   (Browser)      │                                                    │
│  └────────┬─────────┘                                                    │
│           │ JWT / HTTP                                                   │
│           ▼                                                              │
│  ┌─────────────────────┐          ┌─────────────────────┐                │
│  │    Vue 3 Frontend   │          │                     │                │
│  │    Vite + Pinia     │          │                     │                │
│  │    :5173            │          │                     │                │
│  └────────┬────────────┘          │                     │                │
│           │                       │                     │                │
│     JWT   │          JWT          │                     │                │
│           ▼                       ▼                     │                │
│  ┌─────────────────┐    ┌──────────────────┐            │                │
│  │ Products Service│    │Inventory Service │            │                │
│  │ Spring Boot 3   │    │Spring Boot 3     │            │                │
│  │ :8081           │◄───│:8082             │            │                │
│  └────────┬────────┘    └───────┬──────────┘            │                │
│           │ JPA                 │ JPA                   │                │
│           ▼                     ▼                       │                │
│  ┌─────────────────┐    ┌──────────────────┐            │                │
│  │  PostgreSQL      │    │  PostgreSQL      │           │                │
│  │  products_db     │    │  inventory_db    │           │                │
│  │  :5432           │    │  :5433           │           │                │
│  └─────────────────┘    └──────────────────┘            │                │
│                                                         │                │
└──────────────────────────────────────────────────────────────────────────┘
```

## Comunicación entre servicios

```
Inventory Service ──── WebClient ────► Products Service
                    timeout: 5s
                    retry: 3 veces
                    circuit breaker: Resilience4j
                    header: X-API-KEY
```

## Flujo de una compra

```
Usuario
  │
  │ POST /api/purchases
  ▼
Inventory Service
  │
  ├── 1. Verificar Idempotency-Key (evita doble descuento)
  │
  ├── 2. Validar producto ──► Products Service
  │         └── 404 → ProductNotFoundException
  │         └── timeout → Circuit Breaker → 503
  │
  ├── 3. Bloqueo pesimista (PESSIMISTIC_WRITE)
  │         └── Solo una transacción a la vez por producto
  │
  ├── 4. Validar stock suficiente
  │         └── Insuficiente → 409 Conflict
  │
  ├── 5. Descontar stock
  │
  ├── 6. Guardar Idempotency-Key
  │
  └── 7. Log evento InventoryChanged
            └── 201 Created → PurchaseResponse
```