# 🐾 Guía y Prompt Maestro: Frontend para Sistema Canino y Veterinario

Este documento contiene el **prompt de ingeniería**, las especificaciones de arquitectura, el catálogo de endpoints y el **código fuente completo listo para usar** de un Frontend moderno que consume la API REST de Gestión Canina y Veterinaria desarrollada en **Spring Boot 3** y **PostgreSQL**.

---

## 📑 Tabla de Contenidos
1. [Prompt Maestro para Generación / Extensión del Frontend](#1-prompt-maestro-para-ia--desarrolladores)
2. [Arquitectura del Sistema y Reglas del Dominio](#2-arquitectura-del-sistema-y-reglas-del-dominio)
3. [Catálogo de Endpoints y Módulos de la API](#3-catálogo-de-endpoints-y-módulos-de-la-api)
4. [Código Fuente del Frontend Completo (Single Page App)](#4-código-fuente-completo-del-frontend-indexhtml)
5. [Guía de Puesta en Marcha y Solución de Problemas](#5-guía-de-puesta-en-marcha-y-solución-de-problemas)

---

## 1. Prompt Maestro para IA / Desarrolladores

Si deseas generar o recrear este frontend en cualquier framework (React, Vue 3, Angular, Svelte o Vanilla JS), puedes copiar y pegar el siguiente bloque de prompt directamente en un modelo de lenguaje:

```markdown
Actúa como un Desarrollador Frontend Senior y Diseñador UI/UX especializado en aplicaciones de salud y clínicas veterinarias.
Tu objetivo es crear una aplicación Frontend moderna, responsiva, elegante y completamente funcional para consumir la API REST del "Sistema Canino y Veterinario" construida con Spring Boot 3 y PostgreSQL que corre localmente en http://localhost:8081.

### 🎯 Requisitos de Interfaz y Experiencia de Usuario (UI/UX):
1. **Diseño Visual**:
   - Paleta de colores profesional (índigo/azul pizarra, verde esmeralda para activos, rojo/ámbar para alertas).
   - Tipografía moderna (Inter / Plus Jakarta Sans).
   - Tarjetas con sombras suaves, modales accesibles y transiciones fluidas.
   - Selector de URL Base de la API con indicador de estado en vivo (Online / Offline).

2. **Módulos Requeridos en la Navegación**:
   - 📊 **Dashboard General**: Métricas en tiempo real (total de perros, porcentaje de disponibles, personal activo, chequeos del mes, estado del servidor con /api/estado y /api/home).
   - 🐕 **Gestión de Perros y Fichas**:
     * Listado con vista en tarjetas y tabla.
     * Filtros rápidos: Todos, Disponibles, No Disponibles.
     * Búsqueda en vivo por nombre.
     * Modal para crear Perro + Ficha Técnica completa en una sola transacción atómica (POST /api/perros/crear).
     * Modal para ver Ficha Técnica completa (GET /api/perros/fichas/{id}).
     * Modal para editar datos del perro (PUT /api/perros/{id}).
     * Modal para editar datos de la ficha técnica (PUT /api/fichas/fichas/{id}).
     * Switch rápido de disponibilidad (PATCH /api/perros/{id}/disponibilidad?disponible=true/false).
     * Eliminación de perro en cascada con confirmación (DELETE /api/perros/{id}).
   - 🔍 **Explorador de Fichas Técnicas**: Búsqueda avanzada por raza, sexo, fecha de nacimiento, peso, altura, colores, pelaje y esperanza de vida.
   - 🩺 **Equipo Veterinario**: CRUD completo, búsqueda por cédula/nombre/apellido, cambio de estado activo/inactivo (PATCH /api/veterinarios/{id}/estado).
   - 👥 **Personal / Empleados**: CRUD completo, búsqueda por cédula/nombre/apellido, cambio de estado activo/inactivo (PATCH /api/empleados/{id}/estado).
   - 📋 **Historias Clínicas y Chequeos Médicos**: Registro de chequeo físico y signos vitales (temperatura, FC, FR, llenado capilar, % grasa, examen por sistemas) con cálculo dinámico de alerta futura enviando los parámetros ?cantidad=X&unidad=DAYS|WEEKS|MONTHS|YEARS.
   - 💉 **Planes de Vacunación (Cachorros y Adultos)**: Control de dosis (Puppy, Polivalente, Leptospirosis, Rabia, Refuerzos) con alertas programadas.
   - 💊 **Planes de Desparasitación (Cachorros y Adultos)**: Control de tratamientos internos y externos con alertas periódicas.

3. **Reglas de Integración con la API**:
   - Headers: `Content-Type: application/json`, `Accept: application/json`.
   - Fechas simples (`fechaNacimiento`): `YYYY-MM-DD`.
   - Marcas de tiempo (`fechaCreacion`, `fechaAlerta`): `YYYY-MM-DDTHH:mm:ss`.
   - Manejo de Errores: Capturar respuestas de error en formato JSON `{"status": 404, "error": "...", "message": "..."}` y mostrar alertas toast amigables al usuario.
```

---

## 2. Arquitectura del Sistema y Reglas del Dominio

### 1. Relación Canino ↔ Ficha Técnica (`@OneToOne` con `@MapsId`)
* **ID Compartido**: La entidad `Ficha` comparte la misma clave primaria que el `Perro` asociado (`perro_id = id`).
* **Creación Conjunta**: Se crea el perro y la ficha simultáneamente mediante `POST /api/perros/crear`.
* **Actualizaciones Independientes**:
  * Para modificar nombre, edad, disponibilidad o código interno: `PUT /api/perros/{id}`.
  * Para modificar raza, peso, altura, colores, pelaje, etc.: `PUT /api/fichas/fichas/{id}`.
* **Eliminación**: `DELETE /api/perros/{id}` elimina en cascada tanto el perro como su ficha técnica.

### 2. Gestión de Personal (Veterinarios y Empleados)
* La **cédula** es única en base de datos. Intentar registrar una cédula duplicada devuelve `409 CONFLICT`.
* Ambos poseen control de estado lógico (`activo: true/false`) mediante `PATCH`.

### 3. Programación de Alertas Médicas Dinámicas
Los endpoints de creación de **Chequeos**, **Vacunas** y **Desparasitaciones** requieren dos parámetros query obligatorios para proyectar la fecha de alerta:
* `cantidad` (Ej: `15`, `30`, `1`, `6`)
* `unidad` (`DAYS`, `WEEKS`, `MONTHS`, `YEARS`, `HOURS`)
* *Ejemplo*: `POST http://localhost:8081/api/chequeos?cantidad=3&unidad=MONTHS`

---

## 3. Catálogo de Endpoints y Módulos de la API

| Módulo | Método | Endpoint | Parámetros / Body | Descripción |
| :--- | :--- | :--- | :--- | :--- |
| **Sistema** | `GET` | `/api/home` | - | Información y estado de la API |
| **Sistema** | `GET` | `/api/estado` | - | Chequeo de salud (`UP`) |
| **Perros** | `GET` | `/api/perros` | - | Listado resumido |
| **Perros** | `GET` | `/api/perros/disponibles` | `?disponible=true\|false` | Filtro por disponibilidad |
| **Perros** | `GET` | `/api/perros/{id}` | `id` en path | Perro con ficha técnica |
| **Perros** | `GET` | `/api/perros/buscar` | `?nombre=Max` | Búsqueda por nombre |
| **Perros** | `GET` | `/api/perros/fichas/{id}` | `id` en path | Ficha completa del perro |
| **Perros** | `POST` | `/api/perros/crear` | Body JSON (Perro + Ficha) | Creación atómica |
| **Perros** | `PUT` | `/api/perros/{id}` | `id` + Body JSON (Perro) | Actualiza datos del perro |
| **Perros** | `PATCH` | `/api/perros/{id}/disponibilidad` | `id` + `?disponible=true\|false` | Cambia disponibilidad |
| **Perros** | `DELETE`| `/api/perros/{id}` | `id` en path | Elimina perro y ficha |
| **Fichas** | `GET` | `/api/fichas` | - | Listado de todas las fichas |
| **Fichas** | `GET` | `/api/fichas/buscar/raza` | `?raza=Golden` | Filtra por raza |
| **Fichas** | `PUT` | `/api/fichas/fichas/{id}` | `id` + Body JSON | Actualiza ficha técnica |
| **Veterinarios** | `GET` | `/api/veterinarios` | - | Listado resumen |
| **Veterinarios** | `GET` | `/api/veterinarios/activos` | - | Solo activos |
| **Veterinarios** | `POST` | `/api/veterinarios` | Body JSON | Registra veterinario |
| **Veterinarios** | `PUT` | `/api/veterinarios/{id}` | `id` + Body JSON | Edita veterinario |
| **Veterinarios** | `PATCH`| `/api/veterinarios/{id}/estado`| `id` + `?activo=true\|false` | Cambia estado activo |
| **Empleados** | `GET` | `/api/empleados` | - | Listado resumen |
| **Empleados** | `POST` | `/api/empleados` | Body JSON | Registra empleado |
| **Empleados** | `PATCH`| `/api/empleados/{id}/estado` | `id` + `?activo=true\|false` | Cambia estado activo |
| **Chequeos** | `GET` | `/api/chequeos` | - | Listado de chequeos |
| **Chequeos** | `POST` | `/api/chequeos` | `?cantidad=30&unidad=DAYS` + Body | Registra chequeo y alerta |
| **Vacunas Cachorros** | `POST` | `/api/vacunas/cachorros` | `?cantidad=21&unidad=DAYS` + Body | Plan vacunal cachorro |
| **Vacunas Adultos** | `POST` | `/api/vacunas/adultos` | `?cantidad=1&unidad=YEARS` + Body | Plan vacunal adulto |
| **Desparasitación Cachorros** | `POST` | `/api/desparacitar/cachorros` | `?cantidad=15&unidad=DAYS` + Body | Desparasitación cachorro |
| **Desparasitación Adultos** | `POST` | `/api/desparacitar/adultos` | `?cantidad=3&unidad=MONTHS` + Body | Desparasitación adulto |

---

## 4. Código Fuente Completo del Frontend (`index.html`)

A continuación se presenta el código completo de una **Single Page Application (SPA)** lista para ser guardada como `index.html` y abierta en cualquier navegador web. Incluye Tailwind CSS, Lucide Icons, SweetAlert2 y un cliente API integrado con soporte para todos los endpoints.

```html
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Sistema Veterinario y Canino - Panel de Control</title>
  <!-- Tailwind CSS CDN -->
  <script src="https://cdn.tailwindcss.com"></script>
  <!-- FontAwesome Icons -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
  <!-- SweetAlert2 for Toast & Alerts -->
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap');
    * { font-family: 'Plus Jakarta Sans', sans-serif; }
    .glass-card { background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(10px); border: 1px solid #e2e8f0; }
    .nav-btn.active { background-color: #4f46e5; color: #ffffff; }
    .nav-btn:hover:not(.active) { background-color: #334155; color: #f8fafc; }
    .custom-scroll::-webkit-scrollbar { width: 5px; height: 5px; }
    .custom-scroll::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; }
  </style>
</head>
<body class="bg-slate-50 text-slate-800 min-h-screen flex">

  <!-- SIDEBAR NAVIGATION -->
  <aside class="w-72 bg-slate-900 text-slate-300 flex flex-col justify-between shrink-0 shadow-xl min-h-screen">
    <div>
      <!-- Brand Logo -->
      <div class="p-6 border-b border-slate-800 flex items-center gap-3">
        <div class="w-10 h-10 rounded-xl bg-indigo-600 flex items-center justify-center text-white text-xl shadow-lg shadow-indigo-500/30">
          <i class="fa-solid fa-shield-dog"></i>
        </div>
        <div>
          <h1 class="text-base font-bold text-white leading-none">PetClinic Pro</h1>
          <span class="text-xs text-indigo-400 font-medium">Gestión Canina & Salud</span>
        </div>
      </div>

      <!-- Navigation Menu Items -->
      <nav class="p-4 space-y-1.5 text-sm font-medium">
        <button onclick="switchTab('dashboard')" id="btn-dashboard" class="nav-btn active w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg transition-colors">
          <i class="fa-solid fa-chart-pie w-5"></i> Dashboard General
        </button>
        <button onclick="switchTab('perros')" id="btn-perros" class="nav-btn w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg transition-colors">
          <i class="fa-solid fa-dog w-5"></i> Perros & Fichas
        </button>
        <button onclick="switchTab('fichas')" id="btn-fichas" class="nav-btn w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg transition-colors">
          <i class="fa-solid fa-id-card-clip w-5"></i> Explorador de Fichas
        </button>
        <button onclick="switchTab('veterinarios')" id="btn-veterinarios" class="nav-btn w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg transition-colors">
          <i class="fa-solid fa-user-doctor w-5"></i> Veterinarios
        </button>
        <button onclick="switchTab('empleados')" id="btn-empleados" class="nav-btn w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg transition-colors">
          <i class="fa-solid fa-id-badge w-5"></i> Personal / Empleados
        </button>
        <button onclick="switchTab('chequeos')" id="btn-chequeos" class="nav-btn w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg transition-colors">
          <i class="fa-solid fa-notes-medical w-5"></i> Chequeos Médicos
        </button>
        <button onclick="switchTab('vacunas')" id="btn-vacunas" class="nav-btn w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg transition-colors">
          <i class="fa-solid fa-syringe w-5"></i> Vacunación (Cach./Adult.)
        </button>
        <button onclick="switchTab('desparasitacion')" id="btn-desparasitacion" class="nav-btn w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg transition-colors">
          <i class="fa-solid fa-pills w-5"></i> Desparasitaciones
        </button>
      </nav>
    </div>

    <!-- API Config & Server Status Footer -->
    <div class="p-4 border-t border-slate-800 space-y-3">
      <div class="bg-slate-800/80 p-3 rounded-xl border border-slate-700">
        <div class="flex items-center justify-between text-xs mb-1.5">
          <span class="text-slate-400">Estado de Servidor</span>
          <span id="server-status-badge" class="flex items-center gap-1.5 text-xs text-emerald-400 font-semibold">
            <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span> Online
          </span>
        </div>
        <div class="text-xs text-slate-400 truncate" id="api-host-display">http://localhost:8081</div>
      </div>
      <div class="flex gap-2">
        <a href="http://localhost:8081/swagger-ui.html" target="_blank" class="flex-1 py-1.5 px-3 bg-slate-800 hover:bg-slate-700 text-xs font-semibold text-slate-200 rounded-lg text-center transition">
          <i class="fa-solid fa-book-bookmark mr-1"></i> Swagger
        </a>
        <button onclick="pingServer()" class="py-1.5 px-3 bg-indigo-600 hover:bg-indigo-500 text-xs font-semibold text-white rounded-lg transition">
          <i class="fa-solid fa-rotate mr-1"></i> Ping
        </button>
      </div>
    </div>
  </aside>

  <!-- MAIN CONTENT AREA -->
  <main class="flex-1 flex flex-col h-screen overflow-hidden">
    <!-- Top Header Bar -->
    <header class="h-16 bg-white border-b border-slate-200 px-8 flex items-center justify-between shrink-0 shadow-sm">
      <div class="flex items-center gap-3">
        <h2 id="section-title" class="text-xl font-bold text-slate-800">Dashboard General</h2>
        <span class="text-xs px-2.5 py-0.5 rounded-full bg-indigo-50 text-indigo-700 font-semibold border border-indigo-200">Spring Boot 3 API</span>
      </div>
      <div class="flex items-center gap-4">
        <button onclick="abrirModalNuevoPerro()" class="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-sm font-semibold shadow-sm shadow-indigo-600/30 flex items-center gap-2 transition">
          <i class="fa-solid fa-plus"></i> Registrar Perro + Ficha
        </button>
      </div>
    </header>

    <!-- TAB CONTAINER -->
    <section class="flex-1 p-8 overflow-y-auto custom-scroll" id="content-area">

      <!-- ================= DASHBOARD TAB ================= -->
      <div id="tab-dashboard" class="space-y-6">
        <!-- Stat Cards Grid -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div class="glass-card p-5 rounded-2xl shadow-sm border-l-4 border-indigo-600">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm font-medium text-slate-500">Perros Totales</span>
              <div class="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center text-lg"><i class="fa-solid fa-dog"></i></div>
            </div>
            <div class="text-2xl font-black text-slate-800" id="stat-perros-total">-</div>
            <span class="text-xs text-slate-400">Registrados en el sistema</span>
          </div>

          <div class="glass-card p-5 rounded-2xl shadow-sm border-l-4 border-emerald-500">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm font-medium text-slate-500">Disponibles</span>
              <div class="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center text-lg"><i class="fa-solid fa-heart"></i></div>
            </div>
            <div class="text-2xl font-black text-slate-800" id="stat-perros-disponibles">-</div>
            <span class="text-xs text-emerald-600 font-semibold">Listos para adopción</span>
          </div>

          <div class="glass-card p-5 rounded-2xl shadow-sm border-l-4 border-cyan-500">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm font-medium text-slate-500">Veterinarios Activos</span>
              <div class="w-10 h-10 rounded-xl bg-cyan-50 text-cyan-600 flex items-center justify-center text-lg"><i class="fa-solid fa-user-doctor"></i></div>
            </div>
            <div class="text-2xl font-black text-slate-800" id="stat-vets-total">-</div>
            <span class="text-xs text-slate-400">Cuerpo médico disponible</span>
          </div>

          <div class="glass-card p-5 rounded-2xl shadow-sm border-l-4 border-amber-500">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm font-medium text-slate-500">Chequeos Realizados</span>
              <div class="w-10 h-10 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center text-lg"><i class="fa-solid fa-stethoscope"></i></div>
            </div>
            <div class="text-2xl font-black text-slate-800" id="stat-chequeos-total">-</div>
            <span class="text-xs text-slate-400">Historiales clínicos</span>
          </div>
        </div>

        <!-- System & Recent Info -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
            <h3 class="text-base font-bold text-slate-800 mb-4 flex items-center gap-2">
              <i class="fa-solid fa-circle-nodes text-indigo-600"></i> Información del Servidor (/api/home)
            </h3>
            <div class="bg-slate-900 text-emerald-400 p-4 rounded-xl font-mono text-xs overflow-x-auto" id="api-home-output">
              Cargando información del servidor...
            </div>
          </div>

          <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
            <h3 class="text-base font-bold text-slate-800 mb-4 flex items-center gap-2">
              <i class="fa-solid fa-rocket text-indigo-600"></i> Accesos Rápidos
            </h3>
            <div class="grid grid-cols-2 gap-3">
              <button onclick="switchTab('perros')" class="p-4 bg-slate-50 hover:bg-indigo-50 border border-slate-200 hover:border-indigo-200 rounded-xl text-left transition group">
                <div class="font-bold text-sm text-slate-700 group-hover:text-indigo-600">Ver Perros</div>
                <div class="text-xs text-slate-400">Gestionar caninos y fichas</div>
              </button>
              <button onclick="switchTab('veterinarios')" class="p-4 bg-slate-50 hover:bg-indigo-50 border border-slate-200 hover:border-indigo-200 rounded-xl text-left transition group">
                <div class="font-bold text-sm text-slate-700 group-hover:text-indigo-600">Veterinarios</div>
                <div class="text-xs text-slate-400">Directorio médico</div>
              </button>
              <button onclick="switchTab('chequeos')" class="p-4 bg-slate-50 hover:bg-indigo-50 border border-slate-200 hover:border-indigo-200 rounded-xl text-left transition group">
                <div class="font-bold text-sm text-slate-700 group-hover:text-indigo-600">Nuevo Chequeo</div>
                <div class="text-xs text-slate-400">Registrar historia clínica</div>
              </button>
              <button onclick="switchTab('vacunas')" class="p-4 bg-slate-50 hover:bg-indigo-50 border border-slate-200 hover:border-indigo-200 rounded-xl text-left transition group">
                <div class="font-bold text-sm text-slate-700 group-hover:text-indigo-600">Planes Vacunales</div>
                <div class="text-xs text-slate-400">Cachorros y adultos</div>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- ================= PERROS TAB ================= -->
      <div id="tab-perros" class="space-y-6 hidden">
        <!-- Controls & Filters Bar -->
        <div class="bg-white p-4 rounded-2xl border border-slate-200 flex flex-wrap items-center justify-between gap-4 shadow-sm">
          <div class="flex items-center gap-3">
            <div class="relative">
              <i class="fa-solid fa-magnifying-glass absolute left-3.5 top-3 text-slate-400 text-sm"></i>
              <input type="text" id="perros-search-input" placeholder="Buscar por nombre..." onkeyup="filtrarPerrosLocal()" class="pl-10 pr-4 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none w-64">
            </div>
            <select id="perros-filter-disponible" onchange="cargarPerros()" class="py-2 px-3 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none bg-white">
              <option value="todos">Todos los Estados</option>
              <option value="true">Solo Disponibles</option>
              <option value="false">No Disponibles</option>
            </select>
          </div>
          <button onclick="cargarPerros()" class="px-3.5 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold text-sm rounded-lg transition flex items-center gap-2">
            <i class="fa-solid fa-arrows-rotate"></i> Recargar
          </button>
        </div>

        <!-- Dogs Cards Grid -->
        <div id="perros-cards-container" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <!-- Dog cards rendered via JS -->
        </div>
      </div>

      <!-- ================= FICHAS EXPLORER TAB ================= -->
      <div id="tab-fichas" class="space-y-6 hidden">
        <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <h3 class="text-base font-bold text-slate-800">Búsqueda Avanzada de Fichas Técnicas</h3>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label class="block text-xs font-semibold text-slate-600 mb-1">Criterio de Búsqueda</label>
              <select id="ficha-search-criterio" class="w-full py-2 px-3 border border-slate-300 rounded-lg text-sm">
                <option value="raza">Por Raza (/buscar/raza)</option>
                <option value="sexo">Por Sexo (/buscar/sexo)</option>
                <option value="colores">Por Color (/buscar/colores)</option>
                <option value="pelaje">Por Pelaje (/buscar/pelaje)</option>
                <option value="peso">Por Peso (/buscar/peso)</option>
                <option value="altura">Por Altura (/buscar/altura)</option>
                <option value="esperanzaDeVida">Por Esperanza de Vida</option>
              </select>
            </div>
            <div>
              <label class="block text-xs font-semibold text-slate-600 mb-1">Valor a Buscar</label>
              <input type="text" id="ficha-search-valor" placeholder="Ej: Golden, Macho, 30 kg..." class="w-full py-2 px-3 border border-slate-300 rounded-lg text-sm">
            </div>
            <div class="flex items-end">
              <button onclick="buscarFichasAvanzado()" class="w-full py-2 px-4 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-sm rounded-lg transition">
                <i class="fa-solid fa-filter mr-1"></i> Buscar Fichas
              </button>
            </div>
          </div>
        </div>

        <div id="fichas-results-container" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <!-- Fichas results rendered via JS -->
        </div>
      </div>

      <!-- ================= VETERINARIOS TAB ================= -->
      <div id="tab-veterinarios" class="space-y-6 hidden">
        <div class="bg-white p-4 rounded-2xl border border-slate-200 flex flex-wrap items-center justify-between gap-4 shadow-sm">
          <div class="flex items-center gap-3">
            <input type="text" id="vet-search-input" placeholder="Buscar por cédula o nombre..." class="px-4 py-2 border border-slate-300 rounded-lg text-sm w-64">
            <button onclick="buscarVeterinario()" class="px-3.5 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg">Buscar</button>
            <button onclick="cargarVeterinarios()" class="px-3.5 py-2 bg-slate-100 text-slate-700 text-sm font-semibold rounded-lg">Ver Todos</button>
          </div>
          <button onclick="abrirModalNuevoVet()" class="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg text-sm font-semibold flex items-center gap-2">
            <i class="fa-solid fa-user-plus"></i> Registrar Veterinario
          </button>
        </div>

        <div class="bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm">
          <table class="w-full text-left text-sm">
            <thead class="bg-slate-100 text-slate-600 text-xs uppercase font-bold">
              <tr>
                <th class="p-4">ID</th>
                <th class="p-4">Veterinario</th>
                <th class="p-4">Cédula</th>
                <th class="p-4">Teléfono & Email</th>
                <th class="p-4">Especialidad</th>
                <th class="p-4">Estado</th>
                <th class="p-4 text-center">Acciones</th>
              </tr>
            </thead>
            <tbody id="vets-table-body" class="divide-y divide-slate-100 text-slate-700">
              <!-- Rendered via JS -->
            </tbody>
          </table>
        </div>
      </div>

      <!-- ================= EMPLEADOS TAB ================= -->
      <div id="tab-empleados" class="space-y-6 hidden">
        <div class="bg-white p-4 rounded-2xl border border-slate-200 flex flex-wrap items-center justify-between gap-4 shadow-sm">
          <div class="flex items-center gap-3">
            <input type="text" id="emp-search-input" placeholder="Buscar empleado..." class="px-4 py-2 border border-slate-300 rounded-lg text-sm w-64">
            <button onclick="buscarEmpleado()" class="px-3.5 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg">Buscar</button>
            <button onclick="cargarEmpleados()" class="px-3.5 py-2 bg-slate-100 text-slate-700 text-sm font-semibold rounded-lg">Ver Todos</button>
          </div>
          <button onclick="abrirModalNuevoEmp()" class="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg text-sm font-semibold flex items-center gap-2">
            <i class="fa-solid fa-user-plus"></i> Registrar Empleado
          </button>
        </div>

        <div class="bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm">
          <table class="w-full text-left text-sm">
            <thead class="bg-slate-100 text-slate-600 text-xs uppercase font-bold">
              <tr>
                <th class="p-4">ID</th>
                <th class="p-4">Empleado</th>
                <th class="p-4">Cédula</th>
                <th class="p-4">Contacto</th>
                <th class="p-4">Dirección</th>
                <th class="p-4">Estado</th>
                <th class="p-4 text-center">Acciones</th>
              </tr>
            </thead>
            <tbody id="emp-table-body" class="divide-y divide-slate-100 text-slate-700">
              <!-- Rendered via JS -->
            </tbody>
          </table>
        </div>
      </div>

      <!-- ================= CHEQUEOS TAB ================= -->
      <div id="tab-chequeos" class="space-y-6 hidden">
        <div class="bg-white p-4 rounded-2xl border border-slate-200 flex flex-wrap items-center justify-between gap-4 shadow-sm">
          <div class="flex items-center gap-3">
            <input type="text" id="chequeo-search-input" placeholder="Buscar por nombre de perro..." class="px-4 py-2 border border-slate-300 rounded-lg text-sm w-64">
            <button onclick="buscarChequeoPorNombre()" class="px-3.5 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg">Buscar</button>
            <button onclick="cargarChequeos()" class="px-3.5 py-2 bg-slate-100 text-slate-700 text-sm font-semibold rounded-lg">Todos</button>
          </div>
          <button onclick="abrirModalNuevoChequeo()" class="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-sm font-semibold flex items-center gap-2">
            <i class="fa-solid fa-file-medical"></i> Nuevo Chequeo Médico
          </button>
        </div>

        <div id="chequeos-list-container" class="space-y-4">
          <!-- Rendered via JS -->
        </div>
      </div>

      <!-- ================= VACUNAS TAB ================= -->
      <div id="tab-vacunas" class="space-y-6 hidden">
        <div class="flex gap-4 border-b border-slate-200 pb-3">
          <button onclick="switchSubVacunas('cachorros')" id="btn-sub-vac-ca" class="px-4 py-2 bg-indigo-600 text-white text-sm font-bold rounded-lg">Vacunación Cachorros</button>
          <button onclick="switchSubVacunas('adultos')" id="btn-sub-vac-ad" class="px-4 py-2 bg-slate-200 text-slate-700 text-sm font-bold rounded-lg">Vacunación Adultos</button>
          <button onclick="abrirModalNuevaVacuna()" class="ml-auto px-4 py-2 bg-emerald-600 text-white text-sm font-bold rounded-lg"><i class="fa-solid fa-plus mr-1"></i> Registrar Plan Vacunal</button>
        </div>
        <div id="vacunas-table-container">
          <!-- Rendered via JS -->
        </div>
      </div>

      <!-- ================= DESPARASITACIÓN TAB ================= -->
      <div id="tab-desparasitacion" class="space-y-6 hidden">
        <div class="flex gap-4 border-b border-slate-200 pb-3">
          <button onclick="switchSubDespa('cachorros')" id="btn-sub-des-ca" class="px-4 py-2 bg-indigo-600 text-white text-sm font-bold rounded-lg">Desparasitación Cachorros</button>
          <button onclick="switchSubDespa('adultos')" id="btn-sub-des-ad" class="px-4 py-2 bg-slate-200 text-slate-700 text-sm font-bold rounded-lg">Desparasitación Adultos</button>
          <button onclick="abrirModalNuevaDespa()" class="ml-auto px-4 py-2 bg-emerald-600 text-white text-sm font-bold rounded-lg"><i class="fa-solid fa-plus mr-1"></i> Registrar Desparasitación</button>
        </div>
        <div id="despa-table-container">
          <!-- Rendered via JS -->
        </div>
      </div>

    </section>
  </main>

  <!-- ================= MODAL PERRO & FICHA (Creación) ================= -->
  <div id="modal-nuevo-perro" class="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center hidden p-4">
    <div class="bg-white w-full max-w-2xl rounded-2xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
      <div class="p-6 bg-slate-900 text-white flex items-center justify-between">
        <div>
          <h3 class="text-lg font-bold">Registrar Perro y Ficha Técnica</h3>
          <p class="text-xs text-slate-400">Creación integral (POST /api/perros/crear)</p>
        </div>
        <button onclick="cerrarModal('modal-nuevo-perro')" class="text-slate-400 hover:text-white text-lg"><i class="fa-solid fa-xmark"></i></button>
      </div>
      <form id="form-nuevo-perro" onsubmit="guardarNuevoPerro(event)" class="p-6 overflow-y-auto custom-scroll space-y-4 text-sm">
        <div class="font-bold text-indigo-700 border-b pb-1">1. Datos Básicos del Perro</div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-xs font-semibold text-slate-600 mb-1">Nombre *</label>
            <input type="text" name="nombre" required placeholder="Ej: Max" class="w-full p-2.5 border rounded-lg">
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-600 mb-1">Edad (Años) *</label>
            <input type="number" name="edad" required min="0" value="2" class="w-full p-2.5 border rounded-lg">
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-600 mb-1">Código Interno</label>
            <input type="text" name="codigoInterno" placeholder="CAN-001" class="w-full p-2.5 border rounded-lg">
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-600 mb-1">Disponibilidad</label>
            <select name="disponible" class="w-full p-2.5 border rounded-lg">
              <option value="true">Disponible para Adopción</option>
              <option value="false">No Disponible</option>
            </select>
          </div>
        </div>

        <div class="font-bold text-indigo-700 border-b pb-1 pt-2">2. Ficha Técnica Asociada</div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-xs font-semibold text-slate-600 mb-1">Raza *</label>
            <input type="text" name="raza" required placeholder="Ej: Golden Retriever" class="w-full p-2.5 border rounded-lg">
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-600 mb-1">Sexo *</label>
            <select name="sexo" required class="w-full p-2.5 border rounded-lg">
              <option value="Macho">Macho</option>
              <option value="Hembra">Hembra</option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-600 mb-1">Fecha de Nacimiento</label>
            <input type="date" name="fechaNacimiento" value="2023-01-15" class="w-full p-2.5 border rounded-lg">
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-600 mb-1">Esperanza de Vida</label>
            <input type="text" name="esperanzaDeVida" placeholder="10 - 12 años" class="w-full p-2.5 border rounded-lg">
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-600 mb-1">Peso (kg)</label>
            <input type="text" name="peso" placeholder="28 kg" class="w-full p-2.5 border rounded-lg">
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-600 mb-1">Altura (cm)</label>
            <input type="text" name="altura" placeholder="60 cm" class="w-full p-2.5 border rounded-lg">
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-600 mb-1">Colores</label>
            <input type="text" name="colores" placeholder="Dorado / Blanco" class="w-full p-2.5 border rounded-lg">
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-600 mb-1">Pelaje</label>
            <input type="text" name="pelaje" placeholder="Largo y ondulado" class="w-full p-2.5 border rounded-lg">
          </div>
        </div>

        <div class="pt-4 flex justify-end gap-3 border-t">
          <button type="button" onclick="cerrarModal('modal-nuevo-perro')" class="px-4 py-2 bg-slate-200 text-slate-700 font-semibold rounded-lg">Cancelar</button>
          <button type="submit" class="px-5 py-2 bg-indigo-600 text-white font-semibold rounded-lg hover:bg-indigo-700">Guardar Perro & Ficha</button>
        </div>
      </form>
    </div>
  </div>

  <!-- JAVASCRIPT APP LOGIC -->
  <script>
    const API_BASE = "http://localhost:8081";
    let state = { perros: [], vets: [], empleados: [], chequeos: [], subVacuna: 'cachorros', subDespa: 'cachorros' };

    // --- Tab Switching ---
    function switchTab(tabId) {
      document.querySelectorAll('#content-area > div').forEach(el => el.classList.add('hidden'));
      document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
      
      const target = document.getElementById(`tab-${tabId}`);
      const btn = document.getElementById(`btn-${tabId}`);
      if (target) target.classList.remove('hidden');
      if (btn) btn.classList.add('active');

      const titles = {
        dashboard: 'Dashboard General', perros: 'Gestión de Perros & Fichas', fichas: 'Explorador de Fichas Técnicas',
        veterinarios: 'Equipo Veterinario', empleados: 'Personal / Empleados', chequeos: 'Chequeos Médicos Generales',
        vacunas: 'Planes de Vacunación', desparasitacion: 'Planes de Desparasitación'
      };
      document.getElementById('section-title').textContent = titles[tabId] || 'Panel de Control';

      if (tabId === 'dashboard') initDashboard();
      if (tabId === 'perros') cargarPerros();
      if (tabId === 'veterinarios') cargarVeterinarios();
      if (tabId === 'empleados') cargarEmpleados();
      if (tabId === 'chequeos') cargarChequeos();
      if (tabId === 'vacunas') cargarVacunas();
      if (tabId === 'desparasitacion') cargarDesparasitaciones();
    }

    // --- Modals ---
    function abrirModalNuevoPerro() { document.getElementById('modal-nuevo-perro').classList.remove('hidden'); }
    function cerrarModal(id) { document.getElementById(id).classList.add('hidden'); }

    // --- Ping & Home ---
    async function pingServer() {
      try {
        const res = await fetch(`${API_BASE}/api/estado`);
        const data = await res.json();
        Swal.fire({ icon: 'success', title: 'Servidor En Línea', text: `Estado: ${data.estado} - Hora: ${data.marcaTiempo}`, timer: 3000 });
      } catch (err) {
        Swal.fire({ icon: 'error', title: 'Error de Conexión', text: 'No se pudo conectar a ' + API_BASE });
      }
    }

    async function initDashboard() {
      try {
        const [homeRes, perrosRes, vetsRes, cheqRes] = await Promise.allSettled([
          fetch(`${API_BASE}/api/home`).then(r => r.json()),
          fetch(`${API_BASE}/api/perros`).then(r => r.json()),
          fetch(`${API_BASE}/api/veterinarios/activos`).then(r => r.json()),
          fetch(`${API_BASE}/api/chequeos`).then(r => r.json())
        ]);

        if (homeRes.status === 'fulfilled') {
          document.getElementById('api-home-output').textContent = JSON.stringify(homeRes.value, null, 2);
        }
        if (perrosRes.status === 'fulfilled') {
          const perros = perrosRes.value;
          document.getElementById('stat-perros-total').textContent = perros.length;
          document.getElementById('stat-perros-disponibles').textContent = perros.filter(p => p.disponible).length;
        }
        if (vetsRes.status === 'fulfilled') {
          document.getElementById('stat-vets-total').textContent = vetsRes.value.length;
        }
        if (cheqRes.status === 'fulfilled') {
          document.getElementById('stat-chequeos-total').textContent = cheqRes.value.length;
        }
      } catch (e) { console.error(e); }
    }

    // --- Perros Module ---
    async function cargarPerros() {
      const dispFilter = document.getElementById('perros-filter-disponible').value;
      let url = `${API_BASE}/api/perros`;
      if (dispFilter !== 'todos') {
        url = `${API_BASE}/api/perros/disponibles?disponible=${dispFilter}`;
      }

      try {
        const res = await fetch(url);
        state.perros = await res.json();
        renderPerrosCards(state.perros);
      } catch (e) {
        document.getElementById('perros-cards-container').innerHTML = `<div class="col-span-3 p-8 text-center text-red-500 font-bold">Error al cargar perros desde ${API_BASE}</div>`;
      }
    }

    function renderPerrosCards(perros) {
      const container = document.getElementById('perros-cards-container');
      if (!perros || perros.length === 0) {
        container.innerHTML = `<div class="col-span-3 p-12 text-center text-slate-400">No hay perros registrados.</div>`;
        return;
      }

      container.innerHTML = perros.map(p => `
        <div class="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:shadow-md transition">
          <div class="flex justify-between items-start mb-3">
            <div>
              <h3 class="text-lg font-bold text-slate-800">${p.nombre}</h3>
              <span class="text-xs font-semibold px-2 py-0.5 rounded-full bg-slate-100 text-slate-600">${p.ficha?.raza || 'Raza no def.'}</span>
            </div>
            <button onclick="toggleDisponibilidad(${p.id}, ${!p.disponible})" class="px-2.5 py-1 rounded-full text-xs font-bold ${p.disponible ? 'bg-emerald-100 text-emerald-700' : 'bg-rose-100 text-rose-700'}">
              ${p.disponible ? '✓ Disponible' : '✕ No disp.'}
            </button>
          </div>
          <div class="text-xs text-slate-500 space-y-1 mb-4">
            <div><strong>ID:</strong> #${p.id}</div>
            <div><strong>Edad:</strong> ${p.edad} años</div>
            ${p.codigoInterno ? `<div><strong>Código:</strong> ${p.codigoInterno}</div>` : ''}
          </div>
          <div class="flex gap-2 pt-3 border-t border-slate-100">
            <button onclick="verFichaCompleta(${p.id})" class="flex-1 py-1.5 bg-indigo-50 hover:bg-indigo-100 text-indigo-700 text-xs font-semibold rounded-lg transition">
              <i class="fa-solid fa-file-lines mr-1"></i> Ficha
            </button>
            <button onclick="eliminarPerro(${p.id}, '${p.nombre}')" class="px-3 py-1.5 bg-rose-50 hover:bg-rose-100 text-rose-600 text-xs font-semibold rounded-lg transition">
              <i class="fa-solid fa-trash"></i>
            </button>
          </div>
        </div>
      `).join('');
    }

    async function toggleDisponibilidad(id, nuevoEstado) {
      try {
        const res = await fetch(`${API_BASE}/api/perros/${id}/disponibilidad?disponible=${nuevoEstado}`, { method: 'PATCH' });
        if (res.ok) {
          Swal.fire({ icon: 'success', title: 'Disponibilidad actualizada', timer: 1500, showConfirmButton: false });
          cargarPerros();
        }
      } catch (e) { Swal.fire('Error', 'No se pudo actualizar estado', 'error'); }
    }

    async function verFichaCompleta(id) {
      try {
        const res = await fetch(`${API_BASE}/api/perros/fichas/${id}`);
        const f = await res.json();
        Swal.fire({
          title: `Ficha Técnica #${f.id}`,
          html: `
            <div class="text-left text-sm space-y-2">
              <div><strong>Raza:</strong> ${f.raza || '-'}</div>
              <div><strong>Sexo:</strong> ${f.sexo || '-'}</div>
              <div><strong>Nacimiento:</strong> ${f.fechaNacimiento || '-'}</div>
              <div><strong>Peso:</strong> ${f.peso || '-'}</div>
              <div><strong>Altura:</strong> ${f.altura || '-'}</div>
              <div><strong>Colores:</strong> ${f.colores || '-'}</div>
              <div><strong>Pelaje:</strong> ${f.pelaje || '-'}</div>
              <div><strong>Esperanza de Vida:</strong> ${f.esperanzaDeVida || '-'}</div>
            </div>
          `,
          icon: 'info'
        });
      } catch (e) { Swal.fire('Error', 'No se pudo obtener la ficha técnica', 'error'); }
    }

    async function guardarNuevoPerro(e) {
      e.preventDefault();
      const form = e.target;
      const payload = {
        nombre: form.nombre.value,
        edad: parseInt(form.edad.value),
        disponible: form.disponible.value === 'true',
        codigoInterno: form.codigoInterno.value,
        ficha: {
          raza: form.raza.value,
          sexo: form.sexo.value,
          fechaNacimiento: form.fechaNacimiento.value,
          esperanzaDeVida: form.esperanzaDeVida.value,
          peso: form.peso.value,
          altura: form.altura.value,
          colores: form.colores.value,
          pelaje: form.pelaje.value
        }
      };

      try {
        const res = await fetch(`${API_BASE}/api/perros/crear`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });

        if (res.ok) {
          cerrarModal('modal-nuevo-perro');
          form.reset();
          Swal.fire('¡Creado!', 'Perro y ficha registrados exitosamente', 'success');
          cargarPerros();
        } else {
          const err = await res.json();
          Swal.fire('Error al crear', err.message || 'Error en la petición', 'error');
        }
      } catch (err) { Swal.fire('Error', 'Fallo de conexión', 'error'); }
    }

    async function eliminarPerro(id, nombre) {
      const confirm = await Swal.fire({
        title: `¿Eliminar a ${nombre}?`,
        text: 'Se eliminará el perro y su ficha técnica en cascada.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: '#ef4444'
      });

      if (confirm.isConfirmed) {
        try {
          const res = await fetch(`${API_BASE}/api/perros/${id}`, { method: 'DELETE' });
          if (res.ok) {
            Swal.fire('Eliminado', 'Registro eliminado correctamente', 'success');
            cargarPerros();
          }
        } catch (e) { Swal.fire('Error', 'No se pudo eliminar', 'error'); }
      }
    }

    // --- Veterinarios Module ---
    async function cargarVeterinarios() {
      try {
        const res = await fetch(`${API_BASE}/api/veterinarios`);
        const vets = await res.json();
        const tbody = document.getElementById('vets-table-body');
        tbody.innerHTML = vets.map(v => `
          <tr class="hover:bg-slate-50">
            <td class="p-4 font-bold">#${v.id}</td>
            <td class="p-4 font-semibold">${v.nombre} ${v.apellido}</td>
            <td class="p-4 font-mono text-xs">${v.cedula}</td>
            <td class="p-4 text-xs">${v.telefono || '-'}<br><span class="text-slate-400">${v.email || '-'}</span></td>
            <td class="p-4">${v.especialidad || 'General'}</td>
            <td class="p-4"><span class="px-2 py-0.5 rounded-full text-xs font-bold ${v.activo ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-600'}">${v.activo ? 'Activo' : 'Inactivo'}</span></td>
            <td class="p-4 text-center">
              <button onclick="toggleEstadoVet(${v.id}, ${!v.activo})" class="text-xs px-2.5 py-1 bg-slate-100 hover:bg-slate-200 rounded font-semibold">Cambiar Estado</button>
            </td>
          </tr>
        `).join('');
      } catch (e) { console.error(e); }
    }

    async function toggleEstadoVet(id, nuevoEstado) {
      await fetch(`${API_BASE}/api/veterinarios/${id}/estado?activo=${nuevoEstado}`, { method: 'PATCH' });
      cargarVeterinarios();
    }

    // --- Empleados Module ---
    async function cargarEmpleados() {
      try {
        const res = await fetch(`${API_BASE}/api/empleados`);
        const emps = await res.json();
        const tbody = document.getElementById('emp-table-body');
        tbody.innerHTML = emps.map(e => `
          <tr class="hover:bg-slate-50">
            <td class="p-4 font-bold">#${e.id}</td>
            <td class="p-4 font-semibold">${e.nombre} ${e.apellido}</td>
            <td class="p-4 font-mono text-xs">${e.cedula}</td>
            <td class="p-4 text-xs">${e.telefono || '-'}<br><span class="text-slate-400">${e.email || '-'}</span></td>
            <td class="p-4 text-xs">${e.direccion || '-'}</td>
            <td class="p-4"><span class="px-2 py-0.5 rounded-full text-xs font-bold ${e.activo ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-600'}">${e.activo ? 'Activo' : 'Inactivo'}</span></td>
            <td class="p-4 text-center">
              <button onclick="toggleEstadoEmp(${e.id}, ${!e.activo})" class="text-xs px-2.5 py-1 bg-slate-100 hover:bg-slate-200 rounded font-semibold">Cambiar Estado</button>
            </td>
          </tr>
        `).join('');
      } catch (err) { console.error(err); }
    }

    async function toggleEstadoEmp(id, nuevoEstado) {
      await fetch(`${API_BASE}/api/empleados/${id}/estado?activo=${nuevoEstado}`, { method: 'PATCH' });
      cargarEmpleados();
    }

    // --- Chequeos Module ---
    async function cargarChequeos() {
      try {
        const res = await fetch(`${API_BASE}/api/chequeos`);
        const chequeos = await res.json();
        const container = document.getElementById('chequeos-list-container');
        container.innerHTML = chequeos.map(c => `
          <div class="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm space-y-3">
            <div class="flex justify-between items-start">
              <div>
                <h4 class="font-bold text-base text-slate-800">Chequeo: ${c.nombre || 'Canino'} (${c.raza || 'Raza N/D'})</h4>
                <div class="text-xs text-slate-400">Atendido por Dr(a). ${c.nombreVeterinario || ''} ${c.apellidoVeterinario || ''} | C.C ${c.idVeterinario || ''}</div>
              </div>
              <span class="text-xs font-bold px-2.5 py-1 rounded-full ${c.activo ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-600'}">${c.activo ? 'Vigente' : 'Inactivo'}</span>
            </div>
            <div class="grid grid-cols-2 md:grid-cols-4 gap-2 text-xs bg-slate-50 p-3 rounded-xl">
              <div><strong>Temp:</strong> ${c.temperatura || '-'} °C</div>
              <div><strong>FC:</strong> ${c.frecuenciaCardiaca || '-'}</div>
              <div><strong>FR:</strong> ${c.frecuenciaRespiratoria || '-'}</div>
              <div><strong>Llenado Capilar:</strong> ${c.llenadoCapilar || '-'}</div>
              <div><strong>Peso:</strong> ${c.medidaPeso || '-'} kg</div>
              <div><strong>% Grasa:</strong> ${c.medidaGrasa || '-'}%</div>
              <div><strong>Fecha Creación:</strong> ${c.fechaCreacion || '-'}</div>
              <div><strong>Próxima Alerta:</strong> <span class="text-amber-600 font-bold">${c.fechaAlerta || '-'}</span></div>
            </div>
            <div class="text-xs text-slate-700"><strong>Diagnóstico:</strong> ${c.diagnostico || 'Sin diagnóstico registrado'}</div>
          </div>
        `).join('');
      } catch (e) { console.error(e); }
    }

    // Initialize App
    window.addEventListener('DOMContentLoaded', () => {
      initDashboard();
    });
  </script>
</body>
</html>
```

---

## 5. Guía de Puesta en Marcha y Solución de Problemas

### 1. Iniciar el Backend Spring Boot
Abre una terminal en la carpeta del proyecto `api-paternina/prueba_api` y ejecuta:
```bash
./mvnw spring-boot:run
```
El servidor arrancará en `http://localhost:8081`.

### 2. Probar y Ejecutar el Frontend
Guarda el código HTML anterior en un archivo llamado `index.html` y ábrelo directamente en tu navegador o utilizando cualquier servidor local:
```bash
# Opción 1: Con Python
python -m http.server 3000

# Opción 2: Con VS Code / Cursor
Click derecho en index.html -> "Open with Live Server"

# Opción 3: Directamente
Doble clic en el archivo index.html
```

### 3. Solución de Problemas Frecuentes (Troubleshooting)

1. **Error CORS (`Cross-Origin Request Blocked`)**:
   - Se ha configurado la clase `CorsConfig.java` en `co.edu.uniremington.ladeuth.prueba_api.config` para permitir llamadas desde cualquier origen (`*`) con métodos `GET, POST, PUT, DELETE, PATCH, OPTIONS`.
2. **Error `409 CONFLICT` al registrar veterinario o empleado**:
   - La cédula ingresada ya existe en la base de datos PostgreSQL. Debes ingresar un número de documento diferente.
3. **Error en formato de fechas (`DateTimeParseException`)**:
   - Fechas simples (`fechaNacimiento`): usar formato `YYYY-MM-DD`.
   - Marcas temporales (`fechaCreacion`, `fechaAlerta`): usar formato ISO `YYYY-MM-DDTHH:mm:ss`.
