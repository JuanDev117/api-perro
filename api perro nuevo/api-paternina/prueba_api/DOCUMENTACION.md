# Documentación y Guía Completa de la API - Sistema Canino y Veterinario

API REST desarrollada con **Spring Boot 3**, **Spring Data JPA** y **PostgreSQL** para la gestión integral de caninos, fichas técnicas, equipo veterinario/administrativo, chequeos médicos y esquemas de vacunación/desparasitación.

---

## 📌 Tabla de Contenidos
1. [Información General del Servidor](#información-general-del-servidor)
2. [Stack Tecnológico](#stack-tecnológico)
3. [Reglas del Dominio y Arquitectura](#reglas-del-dominio-y-arquitectura)
4. [Configuración para Pruebas (Postman / cURL / Swagger)](#configuración-para-pruebas)
5. [Catálogo Completo de Endpoints](#catálogo-completo-de-endpoints)
   - [1. Sistema e Información General (`/api`)](#1-sistema-e-información-general-api)
   - [2. Gestión de Perros (`/api/perros`)](#2-gestión-de-perros-apiperros)
   - [3. Gestión de Fichas Técnicas (`/api/fichas`)](#3-gestión-de-fichas-técnicas-apifichas)
   - [4. Gestión de Veterinarios (`/api/veterinarios`)](#4-gestión-de-veterinarios-apiveterinarios)
   - [5. Gestión de Empleados (`/api/empleados`)](#5-gestión-de-empleados-apiempleados)
   - [6. Chequeos Médicos Generales (`/api/chequeos`)](#6-chequeos-médicos-generales-apichequeos)
   - [7. Plan de Vacunación - Cachorros (`/api/vacunas/cachorros`)](#7-plan-de-vacunación---cachorros-apivacunascachorros)
   - [8. Plan de Vacunación - Adultos (`/api/vacunas/adultos`)](#8-plan-de-vacunación---adultos-apivacunasadultos)
   - [9. Plan de Desparasitación - Cachorros (`/api/desparacitar/cachorros`)](#9-plan-de-desparasitación---cachorros-apidesparacitarcachorros)
   - [10. Plan de Desparasitación - Adultos (`/api/desparacitar/adultos`)](#10-plan-de-desparasitación---adultos-apidesparacitaradultos)
6. [Manejo Global de Errores](#manejo-global-de-errores)
7. [Solución de Problemas Frecuentes](#solución-de-problemas-frecuentes)

---

## Información General del Servidor

* **Host Local**: `http://localhost:8081`
* **Swagger UI (Documentación interactiva)**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
* **Especificación OpenAPI (JSON)**: [http://localhost:8081/api-docs](http://localhost:8081/api-docs)
* **Base de Datos**: PostgreSQL en la nube (Neon DB)

---

## Stack Tecnológico

* **Lenguaje**: Java 21 LTS
* **Framework**: Spring Boot 3.3.5
* **Persistencia**: Spring Data JPA / Hibernate
* **Base de Datos**: PostgreSQL
* **Validación**: Jakarta Bean Validation (`@Valid`, `@NotBlank`, `@NotNull`, `@Size`, `@Positive`, `@Min`)
* **Documentación API**: Springdoc OpenAPI / Swagger UI 2.6.0
* **Utilidades**: Project Lombok

---

## Reglas del Dominio y Arquitectura

### 1. Relación Canino ↔ Ficha Técnica (`@OneToOne` con `@MapsId`)
* **Clave Primaria Compartida**: La entidad `Ficha` es el lado dueño de la relación con clave foránea `perro_id` (`NOT NULL` y `UNIQUE`). Gracias a la anotación `@MapsId`, el `id` de la ficha es **exactamente el mismo** que el `id` del perro asociado.
* **Creación Conjunta Obligatoria**: La creación de un perro y su ficha se realiza en un único paso mediante `POST /api/perros/crear`. De esta forma se garantiza la integridad referencial y se evita la existencia de fichas huérfanas.
* **Cascada**: La eliminación o actualización de un perro propaga sus operaciones hacia la ficha técnica asociada (`CascadeType.ALL`).
* **Listado Optimizado**: Al consultar `GET /api/perros`, se retorna una vista resumida (`ResumenPerroDTO`) que incluye solo el `id` y `raza` de la ficha técnica para optimizar el ancho de banda y evitar transferencias innecesarias de datos.

```
+-----------------------------------+          1 : 1          +-----------------------------------+
|               Ficha               | <---------------------- |               Perro               |
+-----------------------------------+    (FK/PK perro_id)     +-----------------------------------+
| id (PK compartida = perro.id)     |                         | id (PK Auto-incremental)          |
| raza                              |                         | nombre                            |
| sexo                              |                         | edad                              |
| fechaNacimiento                   |                         | disponible                        |
| esperanzaDeVida                   |                         | codigoInterno                     |
| peso                              |                         | ficha (1:1 mappedBy="perro")      |
| altura                            |                         +-----------------------------------+
| colores                           |
| pelaje                            |
| perro_id (FK / PK)                |
+-----------------------------------+
```

### 2. Gestión de Personal (Veterinarios y Empleados)
* Ambos registros poseen control de estado lógico (`activo: true/false`) mediante operaciones `PATCH`.
* La **cédula** es un campo obligatorio y único a nivel de base de datos (`unique = true`). Duplicados generarán un error `409 CONFLICT`.

### 3. Módulos Médicos y Cronogramas de Alertas
* Los registros de chequeo general, planes de vacunación y desparasitación calculan de forma dinámica su **fecha de alerta (`fechaAlerta`)** sumando la `cantidad` y `unidad` de tiempo (`DAYS`, `WEEKS`, `MONTHS`, `YEARS`) a partir de la fecha de creación.

---

## Configuración para Pruebas

### En Postman / Thunder Client / Insomnia
1. **Headers requeridos** para `POST`, `PUT`, `PATCH`:
   ```http
   Content-Type: application/json
   Accept: application/json
   ```
2. **Body**: Seleccionar **`raw`** con formato **`JSON`**.
3. **Formato de Fechas**:
   * Fechas simples (`fechaNacimiento`): Estándar ISO `yyyy-MM-dd` (Ej: `2023-05-15`).
   * Marcas de tiempo (`fechaCreacion`, `fechaAlerta`): Formato ISO `yyyy-MM-dd'T'HH:mm:ss` (Ej: `2026-08-18T10:30:00`).

---

## Catálogo Completo de Endpoints

---

### 1. Sistema e Información General (`/api`)

| Método | Endpoint | Descripción | Respuesta Exitosa |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/home` | Retorna información de la API, versión y estado | `200 OK` |
| `GET` | `/api/indice` | Retorna el directorio de rutas disponibles | `200 OK` |
| `GET` | `/api/estado` | Chequeo de salud del servicio (`UP`) y marca de tiempo | `200 OK` |

#### Ejemplo: `GET /api/home`
```json
{
  "api": "Perros API",
  "version": "1.0.0",
  "curso": "Lenguaje de programación 3 - IF0122",
  "estado": "en línea",
  "documentacion": "/swagger-ui.html"
}
```

---

### 2. Gestión de Perros (`/api/perros`)

| Método | Endpoint | Parámetros | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/perros` | - | Lista todos los perros con ficha reducida |
| `GET` | `/api/perros/disponibles` | `disponible=true\|false` *(query)* | Filtra perros según disponibilidad |
| `GET` | `/api/perros/{id}` | `id` *(path)* | Obtiene un perro con su ficha técnica completa |
| `GET` | `/api/perros/buscar` | `nombre=Firulais` *(query)* | Busca un perro por su nombre exacto |
| `GET` | `/api/perros/fichas/{id}` | `id` *(path)* | Obtiene directamente la ficha completa asociada al perro |
| `POST` | `/api/perros/crear` | Body JSON | Crea un nuevo perro junto con su ficha técnica completa |
| `PUT` | `/api/perros/{id}` | `id` *(path)* + Body JSON | Actualiza los datos propios del perro (sin alterar la ficha) |
| `PATCH`| `/api/perros/{id}/disponibilidad` | `id` *(path)*, `disponible=true\|false` *(query)* | Modifica únicamente el estado de disponibilidad |
| `DELETE`| `/api/perros/{id}` | `id` *(path)* | Elimina el perro y en cascada su ficha técnica |

#### Ejemplo de Creación: `POST /api/perros/crear`
* **URL**: `http://localhost:8081/api/perros/crear`
* **Body (JSON)**:
```json
{
  "nombre": "Max",
  "edad": 3,
  "disponible": true,
  "codigoInterno": "CAN-001",
  "ficha": {
    "raza": "Golden Retriever",
    "sexo": "Macho",
    "fechaNacimiento": "2023-04-12",
    "esperanzaDeVida": "10 - 12 anios",
    "peso": "30 kg",
    "altura": "60 cm",
    "colores": "Dorado claro",
    "pelaje": "Largo y ondulado"
  }
}
```
* **Respuesta (`201 Created`)**:
```json
{
  "id": 1,
  "nombre": "Max",
  "edad": 3,
  "disponible": true,
  "codigoInterno": "CAN-001",
  "ficha": {
    "id": 1,
    "raza": "Golden Retriever",
    "sexo": "Macho",
    "fechaNacimiento": "2023-04-12",
    "esperanzaDeVida": "10 - 12 anios",
    "peso": "30 kg",
    "altura": "60 cm",
    "colores": "Dorado claro",
    "pelaje": "Largo y ondulado"
  }
}
```

#### Ejemplo de Listado: `GET /api/perros`
* **Respuesta (`200 OK`)**:
```json
[
  {
    "id": 1,
    "nombre": "Max",
    "edad": 3,
    "disponible": true,
    "ficha": {
      "id": 1,
      "raza": "Golden Retriever"
    }
  }
]
```

#### Ejemplo de Actualización: `PUT /api/perros/1`
* **Body (JSON)**:
```json
{
  "nombre": "Maximus",
  "edad": 4,
  "disponible": false,
  "codigoInterno": "CAN-001-ACT"
}
```

---

### 3. Gestión de Fichas Técnicas (`/api/fichas`)

| Método | Endpoint | Parámetros | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/fichas` | - | Lista todas las fichas técnicas completas |
| `GET` | `/api/fichas/{id}` | `id` *(path)* | Consulta una ficha técnica por su ID (mismo ID del perro) |
| `GET` | `/api/fichas/buscar/raza` | `raza=Golden` *(query)* | Filtra fichas técnicas por raza |
| `GET` | `/api/fichas/buscar/sexo` | `sexo=Macho` *(query)* | Filtra fichas técnicas por sexo |
| `GET` | `/api/fichas/buscar/fechaNacimiento` | `fechaNacimiento=2023-04-12` *(query)* | Filtra por fecha de nacimiento |
| `GET` | `/api/fichas/buscar/esperanzaDeVida` | `esperanzaDeVida=10-12` *(query)* | Filtra por rango de esperanza de vida |
| `GET` | `/api/fichas/buscar/peso` | `peso=30` *(query)* | Filtra por peso |
| `GET` | `/api/fichas/buscar/altura` | `altura=60` *(query)* | Filtra por altura |
| `GET` | `/api/fichas/buscar/colores` | `colores=Dorado` *(query)* | Filtra por coloración |
| `GET` | `/api/fichas/buscar/pelaje` | `pelaje=Largo` *(query)* | Filtra por tipo de pelaje |
| `PUT` | `/api/fichas/fichas/{id}` | `id` *(path)* + Body JSON | Actualiza los datos de la ficha técnica |
| `DELETE`| `/api/fichas/{id}` | `id` *(path)* | Elimina una ficha técnica individualmente |

> ℹ️ **Nota**: No existe `POST /api/fichas`. La ficha se crea de forma obligatoria y acoplada junto al perro mediante `POST /api/perros/crear`.

#### Ejemplo: `PUT /api/fichas/fichas/1`
* **Body (JSON)**:
```json
{
  "raza": "Golden Retriever Americano",
  "sexo": "Macho",
  "fechaNacimiento": "2023-04-12",
  "esperanzaDeVida": "11 - 13 anios",
  "peso": "32 kg",
  "altura": "62 cm",
  "colores": "Dorado oscuro",
  "pelaje": "Largo, denso y brillante"
}
```

---

### 4. Gestión de Veterinarios (`/api/veterinarios`)

| Método | Endpoint | Parámetros | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/veterinarios` | - | Lista veterinarios en formato resumen |
| `GET` | `/api/veterinarios/activos` | - | Lista veterinarios con estado activo |
| `GET` | `/api/veterinarios/buscar/cedula` | `cedula=12345678` *(query)* | Busca veterinario por su documento de identidad |
| `GET` | `/api/veterinarios/buscar/nombre` | `nombre=Carlos` *(query)* | Busca veterinario por nombre |
| `GET` | `/api/veterinarios/buscar/apellido` | `apellido=Perez` *(query)* | Busca veterinario por apellido |
| `POST` | `/api/veterinarios` | Body JSON | Registra un nuevo veterinario |
| `PUT` | `/api/veterinarios/{id}` | `id` *(path)* + Body JSON | Actualiza datos completos del veterinario |
| `PATCH`| `/api/veterinarios/{id}/estado` | `id` *(path)*, `activo=true\|false` *(query)* | Activa o desactiva un veterinario |
| `DELETE`| `/api/veterinarios/{id}` | `id` *(path)* | Elimina un veterinario por ID |

#### Ejemplo de Creación: `POST /api/veterinarios`
* **Body (JSON)**:
```json
{
  "nombre": "Carlos",
  "apellido": "Perez",
  "cedula": "1098765432",
  "telefono": "3001234567",
  "email": "carlos.perez@veterinaria.com",
  "direccion": "Calle 45 # 12-34",
  "especialidad": "Cirugía y Medicina Interna Canina",
  "activo": true
}
```

---

### 5. Gestión de Empleados (`/api/empleados`)

| Método | Endpoint | Parámetros | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/empleados` | - | Lista empleados en formato resumen |
| `GET` | `/api/empleados/activos` | - | Lista empleados con estado activo |
| `GET` | `/api/empleados/buscar/cedula` | `cedula=98765432` *(query)* | Busca empleado por cédula |
| `GET` | `/api/empleados/buscar/nombre` | `nombre=Maria` *(query)* | Busca empleado por nombre |
| `GET` | `/api/empleados/buscar/apellido` | `apellido=Gomez` *(query)* | Busca empleado por apellido |
| `POST` | `/api/empleados` | Body JSON | Registra un nuevo empleado |
| `PUT` | `/api/empleados/{id}` | `id` *(path)* + Body JSON | Actualiza datos de un empleado |
| `PATCH`| `/api/empleados/{id}/estado` | `id` *(path)*, `activo=true\|false` *(query)* | Activa o desactiva un empleado |
| `DELETE`| `/api/empleados/{id}` | `id` *(path)* | Elimina un empleado por ID |

#### Ejemplo de Creación: `POST /api/empleados`
* **Body (JSON)**:
```json
{
  "nombre": "Maria",
  "apellido": "Gomez",
  "cedula": "52987654",
  "telefono": "3109876543",
  "email": "maria.gomez@clinica.com",
  "direccion": "Carrera 7 # 80-20",
  "activo": true
}
```

---

### 6. Chequeos Médicos Generales (`/api/chequeos`)

| Método | Endpoint | Parámetros | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/chequeos` | - | Lista todos los chequeos médicos |
| `GET` | `/api/chequeos/activos` | - | Lista chequeos médicos activos |
| `GET` | `/api/chequeos/fecha` | `fechaCreacion=2026-08-18T10:00:00` *(query)* | Busca chequeos por fecha de creación |
| `GET` | `/api/chequeos/buscar/nombre`| `nombre=Max` *(query)* | Busca chequeo por nombre del perro |
| `POST` | `/api/chequeos` | `cantidad=30`, `unidad=DAYS` *(query)* + Body JSON | Registra chequeo y programa fecha de alerta |
| `POST` | `/api/chequeos/alerta` | `cantidad=15`, `unidad=DAYS` *(query)* | Programa alerta general periódica |
| `PATCH`| `/api/chequeos/{id}/estado` | `id` *(path)*, `activo=true\|false` *(query)* | Modifica el estado del registro |

*Valores válidos para `unidad`*: `DAYS`, `WEEKS`, `MONTHS`, `YEARS`, `HOURS`.

#### Ejemplo de Registro: `POST /api/chequeos?cantidad=3&unidad=MONTHS`
* **Body (JSON)**:
```json
{
  "fechaCreacion": "2026-08-18T10:00:00",
  "idVeterinario": "1098765432",
  "nombreVeterinario": "Carlos",
  "apellidoVeterinario": "Perez",
  "nombre": "Max",
  "raza": "Golden Retriever",
  "sexo": "Macho",
  "temperatura": 38.5,
  "frecuenciaCardiaca": "95 lpm",
  "frecuenciaRespiratoria": "24 rpm",
  "llenadoCapilar": "2 segundos",
  "medidaPeso": 30.5,
  "medidaGrasa": 14.2,
  "sistemaTegumentario": "Normal, sin lesiones",
  "cabeza": "Ojos limpios, oídos sin secreción",
  "gangliosLinfaticos": "Normales, no reactivos",
  "auscultacion": "Sonidos cardiopulmonares normales",
  "palpacionAbdominal": "Blando e indoloro",
  "movilidadReflejos": "Normales",
  "observación": "Paciente en excelente estado",
  "diagnostico": "Chequeo de rutina satisfactorio",
  "activo": true
}
```

---

### 7. Plan de Vacunación - Cachorros (`/api/vacunas/cachorros`)

| Método | Endpoint | Parámetros | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/vacunas/cachorros` | - | Lista planes de vacunación de cachorros |
| `GET` | `/api/vacunas/cachorros/activos` | - | Lista planes activos de cachorros |
| `GET` | `/api/vacunas/cachorros/fecha` | `fechaCreacion=...` *(query)* | Filtra por fecha de creación |
| `GET` | `/api/vacunas/cachorros/buscar/nombre` | `nombre=Toby` *(query)* | Busca por nombre del perro |
| `POST` | `/api/vacunas/cachorros` | `cantidad=21`, `unidad=DAYS` *(query)* + Body JSON | Crea nuevo plan con cálculo de alerta |
| `POST` | `/api/vacunas/cachorros/alerta` | `cantidad=21`, `unidad=DAYS` *(query)* | Programa alerta de vacunación |
| `PATCH`| `/api/vacunas/cachorros/{id}/estado` | `id` *(path)*, `activo=true\|false` *(query)* | Cambia estado del plan |

#### Ejemplo de Creación: `POST /api/vacunas/cachorros?cantidad=21&unidad=DAYS`
* **Body (JSON)**:
```json
{
  "fechaCreacion": "2026-08-18T09:00:00",
  "idVeterinario": "1098765432",
  "nombreVeterinario": "Carlos",
  "apellidoVeterinario": "Perez",
  "raza": "Beagle",
  "nombre": "Toby",
  "sexo": "Macho",
  "edad": 2,
  "puppy": "Aplicada (Dosis 1)",
  "polivalente": "Pendiente",
  "polivalenteDos": "Pendiente",
  "leptospirosis": "Pendiente",
  "rabia": "Pendiente",
  "observación": "Primera dosis Puppy administrada sin reacciones adversas",
  "activo": true
}
```

---

### 8. Plan de Vacunación - Adultos (`/api/vacunas/adultos`)

| Método | Endpoint | Parámetros | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/vacunas/adultos` | - | Lista planes de vacunación de adultos |
| `GET` | `/api/vacunas/adultos/activos` | - | Lista planes activos |
| `GET` | `/api/vacunas/adultos/fecha` | `fechaCreacion=...` *(query)* | Filtra por fecha |
| `GET` | `/api/vacunas/adultos/buscar/nombre` | `nombre=Max` *(query)* | Busca por nombre del perro |
| `POST` | `/api/vacunas/adultos` | `cantidad=1`, `unidad=YEARS` *(query)* + Body JSON | Crea plan de refuerzo anual |
| `POST` | `/api/vacunas/adultos/alerta` | `cantidad=1`, `unidad=YEARS` *(query)* | Programa alerta de revacunación |
| `PATCH`| `/api/vacunas/adultos/{id}/estado` | `id` *(path)*, `activo=true\|false` *(query)* | Cambia estado del plan |

#### Ejemplo de Creación: `POST /api/vacunas/adultos?cantidad=1&unidad=YEARS`
* **Body (JSON)**:
```json
{
  "fechaCreacion": "2026-08-18T09:00:00",
  "idVeterinario": "1098765432",
  "nombreVeterinario": "Carlos",
  "apellidoVeterinario": "Perez",
  "raza": "Golden Retriever",
  "nombre": "Max",
  "sexo": "Macho",
  "edad": 4,
  "polivalente": "Refuerzo Anual Aplicado",
  "leptospirosis": "Aplicada",
  "rabia": "Aplicada",
  "observación": "Vacunación anual al día",
  "activo": true
}
```

---

### 9. Plan de Desparasitación - Cachorros (`/api/desparacitar/cachorros`)

| Método | Endpoint | Parámetros | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/desparacitar/cachorros` | - | Lista registros de desparasitación de cachorros |
| `GET` | `/api/desparacitar/cachorros/activos` | - | Lista registros activos |
| `GET` | `/api/desparacitar/cachorros/fecha` | `fechaCreacion=...` *(query)* | Filtra por fecha |
| `GET` | `/api/desparacitar/cachorros/buscar/nombre` | `nombre=Toby` *(query)* | Busca por nombre del perro |
| `POST` | `/api/desparacitar/cachorros` | `cantidad=15`, `unidad=DAYS` *(query)* + Body JSON | Registra desparasitación y calcula alerta |
| `POST` | `/api/desparacitar/cachorros/alerta` | `cantidad=15`, `unidad=DAYS` *(query)* | Programa alerta de desparasitación |
| `PATCH`| `/api/desparacitar/cachorros/{id}/estado` | `id` *(path)*, `activo=true\|false` *(query)* | Cambia estado del registro |

#### Ejemplo: `POST /api/desparacitar/cachorros?cantidad=15&unidad=DAYS`
* **Body (JSON)**:
```json
{
  "fechaCreacion": "2026-08-18T09:00:00",
  "idVeterinario": "1098765432",
  "nombreVeterinario": "Carlos",
  "apellidoVeterinario": "Perez",
  "raza": "Beagle",
  "nombre": "Toby",
  "sexo": "Macho",
  "edad": 1,
  "dosSemanas": "Aplicada suspensión oral",
  "quinceDias": "Pendiente",
  "tresMeses": "Pendiente",
  "observación": "Primera toma realizada",
  "activo": true
}
```

---

### 10. Plan de Desparasitación - Adultos (`/api/desparacitar/adultos`)

| Método | Endpoint | Parámetros | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/desparacitar/adultos` | - | Lista registros de desparasitación de adultos |
| `GET` | `/api/desparacitar/adultos/activos` | - | Lista registros activos |
| `GET` | `/api/desparacitar/adultos/fecha` | `fechaCreacion=...` *(query)* | Filtra por fecha |
| `GET` | `/api/desparacitar/adultos/buscar/nombre` | `nombre=Max` *(query)* | Busca por nombre del perro |
| `POST` | `/api/desparacitar/adultos` | `cantidad=3`, `unidad=MONTHS` *(query)* + Body JSON | Registra desparasitación y calcula alerta |
| `POST` | `/api/desparacitar/adultos/alerta` | `cantidad=3`, `unidad=MONTHS` *(query)* | Programa alerta de desparasitación |
| `PATCH`| `/api/desparacitar/adultos/{id}/estado` | `id` *(path)*, `activo=true\|false` *(query)* | Cambia estado del registro |

#### Ejemplo: `POST /api/desparacitar/adultos?cantidad=3&unidad=MONTHS`
* **Body (JSON)**:
```json
{
  "fechaCreacion": "2026-08-18T09:00:00",
  "idVeterinario": "1098765432",
  "nombreVeterinario": "Carlos",
  "apellidoVeterinario": "Perez",
  "raza": "Golden Retriever",
  "nombre": "Max",
  "sexo": "Macho",
  "edad": 4,
  "desparacitarInterno": "Tableta antiparasitaria amplia gama",
  "desparacitarExterno": "Pipeta repelente pulgas y garrapatas",
  "observación": "Tratamiento completo administrado",
  "activo": true
}
```

---

## Manejo Global de Errores

La API implementa un manejador global centralizado (`@RestControllerAdvice`) que estandariza todas las respuestas de error en la estructura `ErrorResponse`:

```json
{
  "status": 404,
  "error": "Recurso No Encontrado",
  "message": "No existe perro con ID: 99",
  "timestamp": "2026-08-18T22:45:00.123456"
}
```

### Códigos de Estado HTTP Utilizados

| Código HTTP | Significado | Escenario |
| :--- | :--- | :--- |
| `200 OK` | Operación exitosa | Consultas `GET`, actualizaciones `PUT` y `PATCH`. |
| `201 CREATED` | Recurso creado exitosamente | Creación exitosa en endpoints `POST`. |
| `204 NO CONTENT` | Sin contenido | Eliminación exitosa en endpoints `DELETE`. |
| `400 BAD REQUEST` | Petición inválida | Error de validación de campos obligatorios (`@Valid`), tipos o formatos de fecha incorrectos. |
| `404 NOT FOUND` | Recurso no encontrado | Búsqueda por ID, nombre o cédula de un elemento inexistente (`RecursoNoEncontradoException`). |
| `409 CONFLICT` | Conflicto / Recurso duplicado | Violación de restricción única (ej. cédula o código ya existente) (`RecursoDuplicadoException`). |
| `500 INTERNAL SERVER ERROR` | Error no controlado | Fallo inesperado en el servidor o base de datos. |

---

## Solución de Problemas Frecuentes

### 1. Error: `null value in column "perro_id" of relation "ficha" violates not-null constraint`
* **Causa**: Se intentó persistir una `Ficha` sin asociarle su entidad `Perro`.
* **Solución**: Usar el endpoint `POST /api/perros/crear` enviando el perro con el objeto `ficha` anidado dentro del JSON.

### 2. Error: `Content-Type 'text/plain;charset=UTF-8' is not supported`
* **Causa**: En el cliente HTTP (Postman, Insomnia) no se especificó la cabecera `Content-Type: application/json` o el cuerpo se envió como texto plano.
* **Solución**: En Postman, ir a **Body** -> seleccionar **raw** -> cambiar el desplegable de `Text` a **`JSON`**.

### 3. Error de conversión de fecha (`DateTimeParseException` / `400 Bad Request`)
* **Causa**: Enviar fechas en formato `dd/MM/yyyy` o con separadores no estándar.
* **Solución**:
  * Para campos de fecha como `fechaNacimiento`: usar formato ISO `yyyy-MM-dd` (Ej: `2024-03-25`).
  * Para marcas temporales como `fechaCreacion`: usar formato ISO `yyyy-MM-dd'T'HH:mm:ss` (Ej: `2026-08-18T14:30:00`).

### 4. Error al ejecutar `PUT` sobre una ficha técnica
* **Causa**: La ruta para actualizar una ficha técnica en el controlador es `PUT /api/fichas/fichas/{id}`.
* **Solución**: Asegurarse de enviar la URL completa `http://localhost:8081/api/fichas/fichas/{id}`.

### 5. Error 409 al registrar empleado o veterinario
* **Causa**: La `cedula` ingresada ya pertenece a otro registro en la base de datos.
* **Solución**: Verificar la cédula o consultar el registro existente con `GET /api/veterinarios/buscar/cedula?cedula=...` o `GET /api/empleados/buscar/cedula?cedula=...`.

---

## Cómo Probar Directamente con Swagger UI

1. Asegúrate de que la aplicación esté en ejecución (`mvn spring-boot:run` o ejecutando `PruebaApiApplication.java`).
2. Abre tu navegador en [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html).
3. Verás todos los controladores organizados por módulos:
   * **`perro-controller`**: Para gestionar caninos y creación anidada de fichas.
   * **`ficha-controller`**: Para consultas y filtros avanzados sobre fichas técnicas.
   * **`veterinario-controller`** y **`empleado-controller`**: Para personal de la clínica.
   * **`chequeo-g-controller`**: Para historias clínicas de chequeos generales.
   * **`plan-v-ca-controller`** y **`plan-v-ad-controller`**: Para planes de vacunación.
   * **`despa-ca-controller`** y **`despa-ad-controller`**: Para planes de desparasitación.
   * **`home-controller`**: Para estado del sistema e índices.
4. Selecciona cualquier endpoint, presiona el botón **"Try it out"**, ajusta los parámetros o cuerpo de prueba y haz clic en **"Execute"** para ver la respuesta inmediata en vivo.
