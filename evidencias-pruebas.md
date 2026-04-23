# Evidencia de Pruebas

## Backend — Products Service

### Comando

```bash
cd products-service
./mvnw test
```

### Resultado

```
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
```

## Backend — Inventory Service

### Comando

```bash
cd inventory-service
./mvnw test
```

### Resultado

```
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
```

## Frontend — Unitarios

### Comando

```bash
cd frontend
npm run test:unit
```

### Resultado

```
 Test Files  3 passed (3)
      Tests  10 passed (10)
   Duration  2.42s (transform 264ms, setup 0ms, import 900ms, tests 53ms, environment 5.47s)
```

## Frontend — E2E Cypress

### Comando

**Prerequisito:** El primer producto debe tener stock > 0.
Setear con: POST http://localhost:8082/api/inventory

```bash
npx cypress open --config baseUrl=http://localhost:5173
```

### Flujos probados

- ✅ Lista productos correctamente
- ✅ Navega al detalle del primer producto
- ✅ Compra exitosa muestra feedback
- ✅ Stock insuficiente muestra error claro
