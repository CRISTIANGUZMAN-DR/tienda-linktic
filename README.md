# Tienda Linktic — Full Stack (Java Spring Boot + Vue 3)

Sistema de gestión de productos e inventario con 2 microservicios, comunicación resiliente, autenticación JWT y frontend en Vue 3.

---

## 🏗️ Arquitectura

┌─────────────────┐ HTTP + API Key ┌──────────────────────┐
│ products-service│ ◄────────────────────► │ inventory-service │
│ :8081 │ │ :8082 │
└─────────────────┘ └──────────────────────┘
▲ ▲
│ JWT │ JWT
└──────────────────┬────────────────────────┘
│
┌──────────────┐
│ Vue 3 app │
│ :5173 │
└──────────────┘

---

## 🚀 Cómo correr el proyecto

### Requisitos

- Docker Desktop instalado y corriendo
- Node.js 18+
- Git

### 1. Clonar el repositorio

```bash
git clone https://github.com/CRISTIANGUZMAN-DR/tienda-linktic.git
cd tienda-linktic
```

### 2. Levantar el backend con Docker Compose

```bash
docker-compose up --build
```

Esto levanta automáticamente:

- PostgreSQL para Products (`localhost:5432`)
- PostgreSQL para Inventory (`localhost:5433`)
- Products Service (`localhost:8081`)
- Inventory Service (`localhost:8082`)

### 3. Levantar el frontend

```bash
cd frontend
npm install
npm run dev
```

Abre http://localhost:5173 en el navegador.

### 4. Login

Usuario: admin
Contraseña: admin

---

## 🔑 Variables de entorno

| Variable                     | Descripción                                    | Valor por defecto                                     |
| ---------------------------- | ---------------------------------------------- | ----------------------------------------------------- |
| `SPRING_DATASOURCE_URL`      | URL de la base de datos                        | `jdbc:postgresql://products-db:5432/products_db`      |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la BD                               | `admin`                                               |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la BD                            | `secret`                                              |
| `JWT_SECRET`                 | Clave secreta para firmar JWT                  | `mi-clave-super-secreta-para-jwt-debe-ser-larga-2024` |
| `JWT_EXPIRATION`             | Expiración del JWT en ms                       | `86400000` (24h)                                      |
| `PRODUCTS_SERVICE_URL`       | URL del Products Service (usado por Inventory) | `http://products-service:8081`                        |

---

## 📡 Endpoints principales

### Products Service — `http://localhost:8081`

| Método   | Endpoint             | Descripción                          |
| -------- | -------------------- | ------------------------------------ |
| `POST`   | `/auth/login`        | Login — retorna JWT                  |
| `GET`    | `/api/products`      | Listar productos (paginado, filtros) |
| `GET`    | `/api/products/{id}` | Obtener producto por ID              |
| `POST`   | `/api/products`      | Crear producto                       |
| `PUT`    | `/api/products/{id}` | Actualizar producto                  |
| `DELETE` | `/api/products/{id}` | Eliminar producto                    |
| `GET`    | `/swagger-ui.html`   | Documentación Swagger                |

### Inventory Service — `http://localhost:8082`

| Método | Endpoint                     | Descripción                 |
| ------ | ---------------------------- | --------------------------- |
| `GET`  | `/api/inventory/{productId}` | Consultar inventario        |
| `POST` | `/api/inventory`             | Setear stock de un producto |
| `POST` | `/api/purchases`             | Realizar compra             |
| `GET`  | `/swagger-ui.html`           | Documentación Swagger       |

---

## 🔧 Ejemplos curl

### Login

```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin"}'
```

### Crear producto

```bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "sku": "PROD-001",
    "name": "Camiseta Azul",
    "price": 29.99,
    "status": "ACTIVE"
  }'
```

### Setear inventario

```bash
curl -X POST http://localhost:8082/api/inventory \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "productId": "{ID_DEL_PRODUCTO}",
    "available": 100
  }'
```

### Realizar compra

```bash
curl -X POST http://localhost:8082/api/purchases \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Idempotency-Key: unique-key-001" \
  -d '{
    "productId": "{ID_DEL_PRODUCTO}",
    "quantity": 2
  }'
```

---

## 🧪 Correr los tests

### Backend — Products Service

```bash
cd products-service
./mvnw test
```

### Backend — Inventory Service

```bash
cd inventory-service
./mvnw test
```

### Frontend — Unitarios

```bash
cd frontend
npm run test:unit
```

### Frontend — E2E (requiere backend corriendo)

```bash
cd frontend
npm run dev       # terminal 1
npx cypress open --config baseUrl=http://localhost:5173  # terminal 2
```

---

## 🛡️ Decisiones técnicas

### Concurrencia

Se usa **bloqueo pesimista** (`PESSIMISTIC_WRITE`) en el repositorio de inventario. Cuando dos compras llegan simultáneamente, la BD serializa el acceso fila por fila, garantizando que el stock nunca quede negativo.

### Idempotencia

Cada compra acepta un header `Idempotency-Key`. Si la misma key llega dos veces, el sistema detecta la duplicación y retorna el resultado anterior sin descontar stock nuevamente. Esto protege contra dobles cobros por reintentos del cliente.

### Resiliencia

La comunicación entre Inventory → Products usa **Resilience4j** con:

- 3 reintentos con espera de 500ms entre ellos
- Circuit breaker que se abre tras 50% de fallos en ventana de 5 llamadas
- Fallback claro: si Products no responde, el frontend recibe `503 Service Unavailable` con mensaje descriptivo

### Base de datos

Cada microservicio tiene su **propia base de datos PostgreSQL** (patrón Database per Service), garantizando independencia de despliegue y datos.

### JWT

Autenticación stateless con JWT firmado con HMAC-SHA384. El token expira en 24 horas. Ambos servicios validan el token independientemente con la misma clave secreta.

---

## 📊 Cobertura de tests

| Módulo               | Tests  | Tipo                       |
| -------------------- | ------ | -------------------------- |
| Products Service     | 6      | Unitarios (Mockito)        |
| Products Service     | 5      | Integración (MockMvc + H2) |
| Inventory Service    | 5      | Unitarios (Mockito)        |
| Inventory Service    | 5      | Integración (MockMvc + H2) |
| Frontend stores      | 7      | Unitarios (Vitest)         |
| Frontend componentes | 3      | Unitarios (Vitest)         |
| Frontend E2E         | 4      | E2E (Cypress)              |
| **Total**            | **35** |                            |
