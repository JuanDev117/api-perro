# CanisCare - Frontend & Base de Datos

Sistema web de gestión veterinaria **CanisCare**.

## 🚀 Despliegue y Ejecución con Docker

Este proyecto incluye la configuración completa de Docker para:
1. **Frontend Web**: Servidor Nginx ultraligero (Alpine) con inyección de variables en tiempo de ejecución.
2. **Base de Datos PostgreSQL**: Motor relacional (PostgreSQL 16 Alpine) con volumen persistente y script de inicialización con tablas y datos de prueba.

---

### 1. Requisitos
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (con Docker Engine + Docker Compose)

---

### 2. Ejecución completa con Docker Compose

1. Inicia todos los servicios (Frontend + Base de Datos):
   ```bash
   docker compose up -d --build
   ```

2. **Accesos:**
   - **Frontend**: [http://localhost:8080](http://localhost:8080)
   - **Base de Datos**: `localhost:5432` (Base de datos: `neondb`, Usuario: `neondb_owner`, Contraseña: `npg_5EchUnxOC9Te`)

3. Para ver los logs o detener los servicios:
   ```bash
   docker compose logs -f
   docker compose down
   ```

---

### 3. Ejecutar ÚNICAMENTE la Base de Datos con Docker

Si estás desarrollando el backend o frontend localmente y solo necesitas la base de datos:

**Con Docker Compose:**
```bash
docker compose up -d caniscare-db
```

**O directamente con Docker CLI:**
```bash
docker run -d \
  --name caniscare-db \
  -p 5432:5432 \
  -e POSTGRES_DB=neondb \
  -e POSTGRES_USER=neondb_owner \
  -e POSTGRES_PASSWORD=npg_5EchUnxOC9Te \
  -v caniscare_db_data:/var/lib/postgresql/data \
  -v ${PWD}/init-db:/docker-entrypoint-initdb.d:ro \
  postgres:16-alpine
```

---

### 4. Variables de Entorno

| Variable | Descripción | Valor por Defecto |
| :--- | :--- | :--- |
| `PORT` | Puerto expuesto para el Frontend | `8080` |
| `API_BASE_URL` | URL base del backend REST | `http://localhost:8081` |
| `DB_PORT` | Puerto expuesto para PostgreSQL | `5432` |
| `DB_NAME` | Nombre de la base de datos | `neondb` |
| `DB_USER` | Usuario administrador de PostgreSQL | `neondb_owner` |
| `DB_PASSWORD` | Contraseña del usuario PostgreSQL | `npg_5EchUnxOC9Te` |

---

### 5. Conexión desde Spring Boot / Backend

**Conexión a base de datos local (Docker):**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/neondb
spring.datasource.username=neondb_owner
spring.datasource.password=npg_5EchUnxOC9Te
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

**Conexión a Neon DB en la nube:**
```properties
spring.datasource.url=jdbc:postgresql://ep-square-dream-ax1lux4e-pooler.c-4.us-east-2.aws.neon.tech/neondb?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_5EchUnxOC9Te
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

---

### 6. Despliegue en Render

El repositorio incluye el archivo de configuración `render.yaml` para desplegar el frontend directamente como servicio web en Render.
