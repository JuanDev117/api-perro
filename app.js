const state = {
  tab: "dashboard",
  apiBase: localStorage.getItem("caniscare-api") || "http://localhost:8081",
  role: localStorage.getItem("caniscare-role") || "",
  dogs: [],
  vets: [],
  employees: [],
  checks: [],
  vaccines: [],
  dewormings: [],
  dogFilter: "todos",
  dogView: "cards",
};
const pages = {
  dashboard: ["CENTRO VETERINARIO", "Resumen clinico"],
  perros: ["GESTION DE PERROS", "Perros y fichas"],
  fichas: ["CONSULTA AVANZADA", "Explorador de fichas"],
  veterinarios: ["EQUIPO CLINICO", "Veterinarios"],
  empleados: ["OPERACION", "Personal y empleados"],
  chequeos: ["HISTORIA CLINICA", "Chequeos medicos"],
  vacunas: ["PREVENCION", "Plan de vacunacion"],
  desparasitacion: ["PREVENCION", "Plan de desparasitacion"],
};
const roles = {
  admin: {
    label: "Administrador",
    help: "Control total del refugio, personal, perros y registros clinicos.",
    tabs: [
      "dashboard",
      "perros",
      "fichas",
      "veterinarios",
      "empleados",
      "chequeos",
      "vacunas",
      "desparasitacion",
    ],
    actions: [
      "createDog",
      "editDog",
      "deleteDog",
      "availability",
      "managePeople",
      "medical",
    ],
  },
  veterinario: {
    label: "Veterinario",
    help: "Gestion clinica de perros, fichas, chequeos y tratamientos.",
    tabs: [
      "dashboard",
      "perros",
      "fichas",
      "chequeos",
      "vacunas",
      "desparasitacion",
    ],
    actions: ["createDog", "editDog", "deleteDog", "availability", "medical"],
  },
  empleado: {
    label: "Empleado",
    help: "Consulta de perros, fichas tecnicas y disponibilidad.",
    tabs: ["dashboard", "perros", "fichas"],
    actions: [],
  },
  adoptante: {
    label: "Adoptante",
    help: "Consulta de perros disponibles y lectura de fichas publicas.",
    tabs: ["dashboard", "perros", "fichas"],
    actions: [],
  },
};
const permissionRows = [
  [
    "Ver resumen del sistema",
    ["admin", "veterinario", "empleado", "adoptante"],
  ],
  [
    "Consultar perros y fichas",
    ["admin", "veterinario", "empleado", "adoptante"],
  ],
  ["Registrar perros", ["admin", "veterinario"]],
  ["Editar datos del perro", ["admin", "veterinario"]],
  ["Eliminar perros", ["admin", "veterinario"]],
  ["Cambiar disponibilidad", ["admin", "veterinario"]],
  ["Gestionar veterinarios", ["admin"]],
  ["Gestionar empleados", ["admin"]],
  ["Registrar chequeos", ["admin", "veterinario"]],
  ["Registrar vacunas", ["admin", "veterinario"]],
  ["Registrar desparasitacion", ["admin", "veterinario"]],
];
const $ = (s, root = document) => root.querySelector(s);
const esc = (v) =>
  String(v ?? "-").replace(
    /[&<>\"']/g,
    (c) =>
      ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;",
      })[c],
  );
function icon(name) {
  return `<i data-lucide="${name}"></i>`;
}
function refreshIcons() {
  window.lucide?.createIcons();
}
function roleConfig(role = state.role) {
  return roles[role] || roles.admin;
}
function can(action) {
  return roleConfig().actions.includes(action);
}
function canSee(tab) {
  return roleConfig().tabs.includes(tab);
}
function visibleTabs() {
  return roleConfig().tabs;
}
function isLoggedIn() {
  return Boolean(state.role && roles[state.role]);
}
function actionDenied() {
  toast("Tu rol actual no tiene permiso para realizar esta accion.", "error");
}
function apiUrl(path) {
  return `${state.apiBase.replace(/\/$/, "")}${path}`;
}
async function request(path, options = {}) {
  const response = await fetch(apiUrl(path), {
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
    ...options,
  });
  if (!response.ok) {
    let data = {};
    try {
      data = await response.json();
    } catch {
      try {
        data = { message: await response.text() };
      } catch {}
    }
    throw new Error(
      data.message || data.error || `Error ${response.status} en ${path}`,
    );
  }
  if (response.status === 204) return null;
  const type = response.headers.get("content-type") || "";
  return type.includes("json") ? response.json() : response.text();
}
async function withFullFicha(dog) {
  if (!dog?.id) return dog;
  const fullDog = await request(`/api/perros/${dog.id}`).catch(() => dog);
  if (!fullDog?.ficha?.id) return fullDog;
  const ficha = await request(`/api/fichas/${fullDog.ficha.id}`).catch(
    () => fullDog.ficha,
  );
  return { ...fullDog, ficha };
}
async function loadFullFichas(dogs) {
  if (!Array.isArray(dogs)) return [];
  return Promise.all(dogs.map(withFullFicha));
}
function normalizeSexo(value) {
  const text = String(value || "").toUpperCase();
  if (text === "MACHO" || text === "HEMBRA") return text;
  if (text === "DESCONOCIDO") return text;
  return "";
}
function sexoLabel(value) {
  const normalized = normalizeSexo(value);
  if (normalized === "MACHO") return "Macho";
  if (normalized === "HEMBRA") return "Hembra";
  return "Sin especificar";
}
function toast(message, type = "success") {
  const node = document.createElement("div");
  node.className = `toast ${type}`;
  node.textContent = message;
  $("#toast-region").append(node);
  setTimeout(() => node.remove(), 3600);
}
async function ping() {
  try {
    await request("/api/estado");
    setDbStatus(true);
    return true;
  } catch {
    setDbStatus(false);
    return false;
  }
}
function setDbStatus(online) {
  const node = $("#db-status");
  if (!node) return;
  node.classList.toggle("online", online);
  node.classList.toggle("offline", !online);
  node.title = online ? "Base de datos conectada" : "Base de datos sin conexion";
}
function setPage(tab) {
  if (!canSee(tab)) {
    tab = roleConfig().tabs[0];
  }
  state.tab = tab;
  const [kicker, title] = pages[tab];
  $("#page-kicker").textContent = kicker;
  $("#page-title").textContent = title;
  document.querySelectorAll(".nav-item").forEach((b) => {
    const visible = canSee(b.dataset.tab);
    b.hidden = !visible;
    b.classList.toggle("active", b.dataset.tab === tab);
  });
  syncTopbarAction();
  syncNavGroups();
  render();
  $("#sidebar").classList.remove("open");
}
function syncRoleUI() {
  const config = roleConfig();
  $("#current-role") && ($("#current-role").textContent = config.label);
  document.querySelectorAll(".nav-item").forEach((b) => {
    b.hidden = !canSee(b.dataset.tab);
    b.classList.toggle("active", b.dataset.tab === state.tab);
  });
  syncTopbarAction();
  syncNavGroups();
}
function setRole(role) {
  state.role = roles[role] ? role : "admin";
  localStorage.setItem("caniscare-role", state.role);
  if (state.role === "adoptante") state.dogFilter = "disponibles";
  if (!canSee(state.tab)) state.tab = roleConfig().tabs[0];
  const [kicker, title] = pages[state.tab];
  $("#page-kicker").textContent = kicker;
  $("#page-title").textContent = title;
  syncRoleUI();
  render();
}
function showLogin(show) {
  $("#login-screen").classList.toggle("hidden", !show);
  $(".app-shell").classList.toggle("hidden", show);
}
function login(role) {
  state.role = roles[role] ? role : "adoptante";
  localStorage.setItem("caniscare-role", state.role);
  state.tab = roleConfig().tabs[0];
  showLogin(false);
  setRole(state.role);
  ping();
}
function logout() {
  localStorage.removeItem("caniscare-role");
  state.role = "";
  showLogin(true);
}
function syncNavGroups() {
  document.querySelectorAll(".nav-label").forEach((label) => {
    const items = [];
    let next = label.nextElementSibling;
    while (next && !next.classList.contains("nav-label")) {
      if (next.classList.contains("nav-item")) items.push(next);
      next = next.nextElementSibling;
    }
    label.hidden = items.length > 0 && items.every((item) => item.hidden);
  });
}
function primaryActionForTab() {
  if (["perros", "fichas"].includes(state.tab) && can("createDog")) {
    return ["new-dog", "Registrar perro", "plus"];
  }
  if (
    ["veterinarios", "empleados"].includes(state.tab) &&
    can("managePeople")
  ) {
    return ["new-person", "Registrar integrante", "plus"];
  }
  if (
    ["chequeos", "vacunas", "desparasitacion"].includes(state.tab) &&
    can("medical")
  ) {
    return ["new-medical", "Nuevo registro", "plus"];
  }
  return null;
}
function syncTopbarAction() {
  const button = $("#new-record");
  const action = primaryActionForTab();
  button.hidden = !action;
  if (!action) return;
  button.innerHTML = `${icon(action[2])}<span>${action[1]}</span>`;
  refreshIcons();
}
function heading(title, description, action = "") {
  return `<div class="page-heading"><div><h2>${title}</h2><p class="muted">${description}</p></div>${action}</div>`;
}
function metric(label, value, note, kind, ic) {
  return `<article class="metric ${kind}"><span class="metric-label">${label}</span><span class="metric-icon">${icon(ic)}</span><div class="metric-value">${value}</div><span class="metric-note">${note}</span></article>`;
}
async function loadDashboard() {
  const [dogs, vets, checks] = await Promise.all([
    request("/api/perros").catch(() => []),
    request("/api/veterinarios/activos").catch(() => state.vets),
    request("/api/chequeos").catch(() => state.checks),
  ]);
  state.dogs = await loadFullFichas(dogs);
  state.vets = Array.isArray(vets) ? vets : [];
  state.checks = Array.isArray(checks) ? checks : [];
  const available = state.dogs.filter((d) => d.disponible).length;
  $("#dash-dogs") && ($("#dash-dogs").textContent = state.dogs.length);
  $("#dash-available") && ($("#dash-available").textContent = available);
  $("#dash-vets") && ($("#dash-vets").textContent = state.vets.length);
  $("#dash-checks") && ($("#dash-checks").textContent = state.checks.length);
  refreshIcons();
}
function dashboard() {
  const metrics = [
    metric(
      "Perros registrados",
      '<span id="dash-dogs">...</span>',
      "Perros en el historial",
      "",
      "dog",
    ),
    metric(
      "Disponibles",
      '<span id="dash-available">...</span>',
      "Listos para atencion",
      "yellow",
      "heart",
    ),
  ];
  if (canSee("veterinarios")) {
    metrics.push(
      metric(
        "Veterinarios activos",
        '<span id="dash-vets">...</span>',
        "Equipo clinico",
        "blue",
        "stethoscope",
      ),
    );
  }
  if (canSee("chequeos")) {
    metrics.push(
      metric(
        "Chequeos",
        '<span id="dash-checks">...</span>',
        "Registros clinicos",
        "red",
        "heart-pulse",
      ),
    );
  }
  const activities = [
    `<li><span class="activity-icon">${icon("dog")}</span><div><strong>Perros disponibles</strong><span>Consulta perros y fichas visibles para tu rol.</span></div></li>`,
  ];
  if (can("medical")) {
    activities.push(
      `<li><span class="activity-icon">${icon("syringe")}</span><div><strong>Atencion clinica</strong><span>Registra chequeos, vacunas y desparasitacion.</span></div></li>`,
    );
  }
  if (can("managePeople")) {
    activities.push(
      `<li><span class="activity-icon">${icon("users-round")}</span><div><strong>Gestion de personal</strong><span>Administra veterinarios y empleados del refugio.</span></div></li>`,
    );
  }
  return `${heading("Un vistazo al centro", `Vista activa: ${roleConfig().label}. Solo se muestran opciones autorizadas.`)}<section class="metric-grid">${metrics.join("")}</section><section class="dashboard-grid"><article class="panel"><div class="panel-head"><h3>Opciones disponibles</h3><span class="pill success">${roleConfig().label}</span></div><div class="panel-body"><ul class="activity-list">${activities.join("")}</ul></div></article></section>`;
}
function rolesView() {
  const config = roleConfig();
  const allowedRows = permissionRows.filter(([, allowed]) =>
    allowed.includes(state.role),
  );
  return `${heading("Autorizaciones visibles", `Rol activo: ${config.label}. Solo aparecen las vistas y acciones disponibles para este rol.`)}<section class="role-grid compact">${visibleTabs()
    .map(
      (tab) =>
        `<article class="role-card"><div class="role-card-head"><span class="role-icon">${icon(tab === "dashboard" ? "layout-dashboard" : tab === "roles" ? "shield-check" : tab === "perros" ? "dog" : tab === "fichas" ? "clipboard-list" : tab === "veterinarios" ? "stethoscope" : tab === "empleados" ? "contact-round" : tab === "chequeos" ? "heart-pulse" : tab === "vacunas" ? "syringe" : "pill")}</span><div><h3>${pages[tab][1]}</h3><p>${pages[tab][0]}</p></div></div><button class="small-action" data-action="go-tab" data-tab="${tab}">Abrir vista</button></article>`,
    )
    .join(
      "",
    )}</section><div class="table-wrap permission-table"><table><thead><tr><th>Casos de uso permitidos para ${config.label}</th><th>Estado</th></tr></thead><tbody>${allowedRows
    .map(
      ([useCase]) =>
        `<tr><td><strong>${useCase}</strong></td><td><span class="permission yes">Visible</span></td></tr>`,
    )
    .join("")}</tbody></table></div>`;
}
async function loadDogs() {
  const dogs = await request("/api/perros").catch(() => []);
  state.dogs = await loadFullFichas(dogs);
}
function dogCard(d) {
  const f = d.ficha || {};
  const statusButton = can("availability")
    ? `<button class="pill ${d.disponible ? "success" : "neutral"}" data-action="availability" data-id="${d.id}" data-value="${!d.disponible}">${d.disponible ? "Disponible" : "No disponible"}</button>`
    : `<span class="pill ${d.disponible ? "success" : "neutral"}">${d.disponible ? "Disponible" : "No disponible"}</span>`;
  const actions = [
    `<button data-action="detail" data-id="${d.id}">Ver ficha</button>`,
    can("editDog")
      ? `<button data-action="edit-dog" data-id="${d.id}">Editar</button>`
      : "",
    can("deleteDog")
      ? `<button data-action="delete-dog" data-id="${d.id}">Eliminar</button>`
      : "",
  ].join("");
  return `<article class="dog-card"><div class="dog-top"><span class="dog-avatar">${icon("dog")}</span>${statusButton}</div><h3>${esc(d.nombre)}</h3><p class="dog-meta">${esc(f.raza || "Ficha pendiente")}</p><div class="dog-details"><span><b>${esc(d.edad)}</b> anos</span><span><b>${f.sexo ? esc(sexoLabel(f.sexo)) : "-"}</b></span><span>Codigo <b>${esc(d.codigoInterno || "-")}</b></span><span>Peso <b>${esc(f.peso || "-")}</b></span></div><div class="card-actions">${actions}</div></article>`;
}
function dogTable(dogs) {
  return `<div class="table-wrap"><table><thead><tr><th>Perro</th><th>Raza</th><th>Edad</th><th>Codigo</th><th>Estado</th><th></th></tr></thead><tbody>${dogs.map((d) => `<tr><td><strong>${esc(d.nombre)}</strong><small>${d.ficha?.sexo ? esc(sexoLabel(d.ficha.sexo)) : "Sin ficha"}</small></td><td>${esc(d.ficha?.raza || "-")}</td><td>${esc(d.edad)} anos</td><td>${esc(d.codigoInterno || "-")}</td><td>${can("availability") ? `<button class="pill ${d.disponible ? "success" : "neutral"}" data-action="availability" data-id="${d.id}" data-value="${!d.disponible}">${d.disponible ? "Disponible" : "No disponible"}</button>` : `<span class="pill ${d.disponible ? "success" : "neutral"}">${d.disponible ? "Disponible" : "No disponible"}</span>`}</td><td><div class="table-actions"><button class="small-action" data-action="detail" data-id="${d.id}">Ficha</button>${can("editDog") ? `<button class="small-action" data-action="edit-dog" data-id="${d.id}">Editar</button>` : ""}</div></td></tr>`).join("")}</tbody></table></div>`;
}
function dogs() {
  const list = filteredDogs();
  const filterButtons =
    state.role === "adoptante"
      ? `<button class="filter-btn active" data-filter="disponibles">Disponibles</button>`
      : `<button class="filter-btn ${state.dogFilter === "todos" ? "active" : ""}" data-filter="todos">Todos</button><button class="filter-btn ${state.dogFilter === "disponibles" ? "active" : ""}" data-filter="disponibles">Disponibles</button><button class="filter-btn ${state.dogFilter === "no-disponibles" ? "active" : ""}" data-filter="no-disponibles">No disponibles</button>`;
  return `${heading("Perros", "Busca, filtra y administra cada ficha tecnica.")}<div class="toolbar"><div class="search-wrap">${icon("search")}<input id="dog-search" value="${esc(state.dogSearch || "")}" placeholder="Buscar por nombre o raza" /></div><div class="filters">${filterButtons}<span class="view-toggle"><button class="${state.dogView === "cards" ? "active" : ""}" data-view="cards" title="Vista de tarjetas">${icon("grid-2x2")}</button><button class="${state.dogView === "table" ? "active" : ""}" data-view="table" title="Vista de tabla">${icon("table-properties")}</button></span></div></div><div id="dog-results">${list.length ? (state.dogView === "cards" ? `<section class="dog-grid">${list.map(dogCard).join("")}</section>` : dogTable(list)) : `<div class="empty">${icon("search-x")}<div>No hay perros que coincidan con la consulta.</div></div>`}</div>`;
}
function filteredDogs() {
  let r = [...state.dogs];
  if (state.role === "adoptante") {
    state.dogFilter = "disponibles";
    r = r.filter((d) => d.disponible);
  }
  if (state.dogFilter === "disponibles") r = r.filter((d) => d.disponible);
  if (state.dogFilter === "no-disponibles") r = r.filter((d) => !d.disponible);
  const q = (state.dogSearch || "").toLowerCase();
  return q
    ? r.filter((d) =>
        `${d.nombre} ${d.ficha?.raza || ""}`.toLowerCase().includes(q),
      )
    : r;
}
function fichas() {
  const visibleDogs =
    state.role === "adoptante"
      ? state.dogs.filter((d) => d.disponible)
      : state.dogs;
  const rows = visibleDogs.map((d) => ({ d, f: d.ficha || {} }));
  return `${heading("Explorador de fichas", "Consulta rapida de caracteristicas, medidas e historial base.")}<div class="toolbar"><div class="search-wrap">${icon("search")}<input id="file-search" placeholder="Filtrar por raza, sexo o color" /></div></div><div class="table-wrap"><table><thead><tr><th>Perro</th><th>Raza</th><th>Sexo</th><th>Nacimiento</th><th>Medidas</th><th>Caracteristicas</th></tr></thead><tbody id="file-body">${fileRows(rows)}</tbody></table></div>`;
}
function fileRows(rows) {
  return rows
    .map(
      ({ d, f }) =>
        `<tr><td><strong>${esc(d.nombre)}</strong><small>#${esc(d.id)}</small></td><td>${esc(f.raza)}</td><td>${f.sexo ? esc(sexoLabel(f.sexo)) : "-"}</td><td>${esc(f.fechaNacimiento)}</td><td>${esc(f.peso)} / ${esc(f.altura)}</td><td>${esc(f.colores)}<small>${esc(f.pelaje)}</small></td></tr>`,
    )
    .join("");
}
function people(type) {
  const isVet = type === "veterinarios",
    list = isVet ? state.vets : state.employees,
    label = isVet ? "veterinario" : "empleado";
  return `${heading(isVet ? "Equipo veterinario" : "Personal operativo", isVet ? "Profesionales asignados a la atencion clinica." : "Administra el personal de apoyo y coordinacion.")}<div class="toolbar"><div class="search-wrap">${icon("search")}<input class="person-search" data-type="${type}" placeholder="Buscar por nombre, apellido o cedula" /></div></div><div class="table-wrap"><table><thead><tr><th>Profesional</th><th>Documento</th><th>Contacto</th><th>${isVet ? "Especialidad" : "Direccion"}</th><th>Estado</th><th></th></tr></thead><tbody id="${isVet ? "vets" : "employees"}-body">${personRows(list, isVet)}</tbody></table></div>`;
}
function personRows(list, isVet) {
  if (!list.length)
    return `<tr><td colspan="6"><div class="empty">${icon("users-round")}<div>No hay registros disponibles.</div></div></td></tr>`;
  return list
    .map(
      (p) =>
        `<tr><td><strong>${esc(p.nombre)} ${esc(p.apellido)}</strong><small>#${esc(p.id)}</small></td><td>${esc(p.cedula)}</td><td>${esc(p.telefono || "-")}<small>${esc(p.email || "")}</small></td><td>${esc(isVet ? p.especialidad || "General" : p.direccion || "-")}</td><td>${can("managePeople") ? `<button class="pill ${p.activo !== false ? "success" : "neutral"}" data-action="person-status" data-type="${isVet ? "veterinarios" : "empleados"}" data-id="${p.id}" data-value="${p.activo === false}">${p.activo !== false ? "Activo" : "Inactivo"}</button>` : `<span class="pill ${p.activo !== false ? "success" : "neutral"}">${p.activo !== false ? "Activo" : "Inactivo"}</span>`}</td><td>${can("managePeople") ? `<button class="small-action" data-action="edit-person" data-type="${isVet ? "veterinarios" : "empleados"}" data-id="${p.id}">Editar</button>` : ""}</td></tr>`,
    )
    .join("");
}
function medical(type) {
  const isCheck = type === "chequeos",
    list = isCheck
      ? state.checks
      : type === "vacunas"
        ? state.vaccines
        : state.dewormings;
  const labels = {
    chequeos: [
      "Chequeos medicos",
      "Registra signos vitales y proyecta la proxima alerta.",
      "Nuevo chequeo",
    ],
    vacunas: [
      "Plan de vacunacion",
      "Control de dosis para cachorros y adultos.",
      "Registrar vacuna",
    ],
    desparasitacion: [
      "Plan de desparasitacion",
      "Control de tratamientos internos y externos.",
      "Registrar tratamiento",
    ],
  }[type];
  return `${heading(labels[0], labels[1])}<section class="module-grid"><article class="panel form-panel"><h3>Proxima alerta</h3><p class="muted">Cada registro usa cantidad y unidad para la fecha proyectada.</p><div class="form-grid"><div class="field"><label>Cantidad</label><input value="${isCheck ? 30 : type === "vacunas" ? 1 : 3}" disabled /></div><div class="field"><label>Unidad</label><input value="${isCheck ? "DAYS" : type === "vacunas" ? "YEARS" : "MONTHS"}" disabled /></div></div><div class="notice">Los parametros se envian con cada registro a la API: <b>?cantidad=X&unidad=DAYS|WEEKS|MONTHS|YEARS</b>.</div></article><section><div class="record-list">${medicalRecords(list, type)}</div></section></section>`;
}
function medicalRecords(list, type) {
  if (!list.length)
    return `<div class="empty">${icon(type === "chequeos" ? "heart-pulse" : type === "vacunas" ? "syringe" : "pill")}<div>Aun no hay registros en este modulo.</div></div>`;
  return list
    .map(
      (x) =>
        `<article class="record"><div class="record-head"><div><h3>${esc(x.nombre || x.tipo || "Registro preventivo")}</h3><p>${esc(x.nombrePerro || x.raza || "Perro")}</p></div><span class="pill warning">Alerta ${esc(x.fechaAlerta || "pendiente")}</span></div><div class="record-stats"><span><b>Fecha:</b> ${esc(x.fechaCreacion || x.fecha || "-")}</span>${type === "chequeos" ? `<span><b>Temp:</b> ${esc(x.temperatura || "-")} C</span><span><b>Diagnostico:</b> ${esc(x.diagnostico || "-")}</span>` : `<span><b>Estado:</b> ${esc(x.estado || "Registrado")}</span>`}</div></article>`,
    )
    .join("");
}
async function render() {
  const content = $("#content");
  content.innerHTML = `<div class="empty">${icon("loader-circle")}<div>Cargando modulo...</div></div>`;
  refreshIcons();
  if (["perros", "fichas"].includes(state.tab)) await loadDogs();
  if (state.tab === "veterinarios")
    state.vets = await request("/api/veterinarios").catch(() => state.vets);
  if (state.tab === "empleados")
    state.employees = await request("/api/empleados").catch(
      () => state.employees,
    );
  if (state.tab === "chequeos")
    state.checks = await request("/api/chequeos").catch(() => state.checks);
  content.innerHTML =
    state.tab === "dashboard"
      ? dashboard()
      : state.tab === "perros"
          ? dogs()
          : state.tab === "fichas"
            ? fichas()
            : state.tab === "veterinarios"
              ? people("veterinarios")
              : state.tab === "empleados"
                ? people("empleados")
                : medical(state.tab);
  refreshIcons();
  if (state.tab === "dashboard") loadDashboard();
}
function field(
  name,
  label,
  type = "text",
  value = "",
  full = false,
  required = false,
) {
  return `<div class="field ${full ? "full" : ""}"><label for="f-${name}">${label}${required ? " *" : ""}</label>${type === "textarea" ? `<textarea id="f-${name}" name="${name}" ${required ? "required" : ""}>${esc(value)}</textarea>` : `<input id="f-${name}" name="${name}" type="${type}" value="${esc(value)}" ${required ? "required" : ""} />`}</div>`;
}
function select(name, label, options, value = "", full = false) {
  return `<div class="field ${full ? "full" : ""}"><label for="f-${name}">${label}</label><select id="f-${name}" name="${name}">${options
    .map((option) => {
      const item =
        typeof option === "object"
          ? option
          : { value: String(option), label: String(option) };
      return `<option value="${esc(item.value)}" ${String(item.value) === String(value) ? "selected" : ""}>${esc(item.label)}</option>`;
    })
    .join("")}</select></div>`;
}
function detailItem(label, value) {
  return `<div class="detail-item"><span>${esc(label)}</span><strong>${esc(value || "-")}</strong></div>`;
}
function openModal(kind, data = {}) {
  const form = $("#modal-form");
  const titles = {
    dog: ["PERRO", "Registrar perro y ficha"],
    person: ["EQUIPO", "Datos del integrante"],
    medical: ["HISTORIA CLINICA", "Nuevo registro medico"],
    detail: ["FICHA TECNICA", data.nombre || "Ficha del perro"],
  };
  const [k, t] = titles[kind];
  $("#modal-kicker").textContent = k;
  $("#modal-title").textContent = t;
  if (kind === "detail") {
    const f = data.ficha || data;
    form.innerHTML = `<div class="detail-grid">${detailItem("Raza", f.raza)}${detailItem("Sexo", f.sexo ? sexoLabel(f.sexo) : "")}${detailItem("Fecha de nacimiento", f.fechaNacimiento)}${detailItem("Peso", f.peso)}${detailItem("Altura", f.altura)}${detailItem("Colores", f.colores)}${detailItem("Pelaje", f.pelaje)}${detailItem("Esperanza de vida", f.esperanzaDeVida)}</div><div class="form-footer"><button type="button" class="secondary-btn" data-close>Volver</button>${can("editDog") ? `<button type="button" class="primary-btn" data-action="edit-dog" data-id="${esc(data.id)}">${icon("pencil")} Editar ficha</button>` : ""}</div>`;
    form.dataset.kind = "detail";
  } else if (kind === "dog") {
    const f = data.ficha || {};
    form.innerHTML = `<div class="form-grid">${field("nombre", "Nombre", "text", data.nombre || "", false, true)}${field("edad", "Edad", "number", data.edad || "")}${select("disponible", "Disponibilidad", ["true", "false"], String(data.disponible ?? true))}${field("codigoInterno", "Codigo interno", "text", data.codigoInterno || "")}${field("raza", "Raza", "text", f.raza || "", false, true)}${select("sexo", "Sexo", [{ value: "", label: "Sin especificar" }, { value: "MACHO", label: "Macho" }, { value: "HEMBRA", label: "Hembra" }], normalizeSexo(f.sexo))}${field("fechaNacimiento", "Fecha de nacimiento", "date", f.fechaNacimiento || "")}${field("peso", "Peso", "text", f.peso || "")}${field("altura", "Altura", "text", f.altura || "")}${field("colores", "Colores", "text", f.colores || "")}${field("pelaje", "Pelaje", "text", f.pelaje || "")}${field("esperanzaDeVida", "Esperanza de vida", "text", f.esperanzaDeVida || "")}</div>${modalFooter(data.id ? "Guardar cambios" : "Registrar perro")}`;
    form.dataset.kind = "dog";
    form.dataset.id = data.id || "";
  } else if (kind === "person") {
    const vet = data.type === "veterinarios";
    form.innerHTML = `<div class="form-grid">${field("nombre", "Nombre", "text", data.nombre || "", false, true)}${field("apellido", "Apellido", "text", data.apellido || "", false, true)}${field("cedula", "Cedula", "text", data.cedula || "", false, true)}${field("telefono", "Telefono", "tel", data.telefono || "")}${field("email", "Correo", "email", data.email || "")}${field(vet ? "especialidad" : "direccion", vet ? "Especialidad" : "Direccion", "text", vet ? data.especialidad || "" : data.direccion || "")}</div>${modalFooter(data.id ? "Guardar cambios" : "Registrar integrante")}`;
    form.dataset.kind = "person";
    form.dataset.type = data.type;
    form.dataset.id = data.id || "";
  } else {
    const type = data.type;
    const clinicalFields =
      type === "chequeos"
        ? `${field("temperatura", "Temperatura (C)", "number", "")}${field("frecuenciaCardiaca", "Frecuencia cardiaca", "number", "")}${field("frecuenciaRespiratoria", "Frecuencia respiratoria", "number", "")}${field("diagnostico", "Diagnostico", "textarea", "", true)}`
        : `${select("grupo", "Grupo", ["cachorros", "adultos"])}${field("tipo", type === "vacunas" ? "Vacuna / dosis" : "Tratamiento", "text", "", false, true)}`;
    const defaultAmount = type === "chequeos" ? 30 : type === "vacunas" ? 1 : 3;
    const defaultUnit =
      type === "chequeos" ? "DAYS" : type === "vacunas" ? "YEARS" : "MONTHS";
    form.innerHTML = `<div class="form-grid">${field("idPerro", "ID del perro", "number", "", false, true)}${clinicalFields}${field("cantidad", "Cantidad para alerta", "number", defaultAmount)}${select("unidad", "Unidad", ["DAYS", "WEEKS", "MONTHS", "YEARS"], defaultUnit)}</div>${modalFooter("Guardar registro")}`;
    form.dataset.kind = "medical";
    form.dataset.type = type;
  }
  $("#modal-backdrop").classList.remove("hidden");
  refreshIcons();
}
function modalFooter(label) {
  return `<div class="form-footer"><button type="button" class="secondary-btn" data-close>Cancelar</button><button type="submit" class="primary-btn">${icon("save")} ${label}</button></div>`;
}
function closeModal() {
  $("#modal-backdrop").classList.add("hidden");
}
function formData(form) {
  return Object.fromEntries(new FormData(form).entries());
}
async function submitForm(e) {
  e.preventDefault();
  const form = e.currentTarget,
    data = formData(form),
    kind = form.dataset.kind;
  if (kind === "detail") return closeModal();
  try {
    if (kind === "dog") {
      const raza = data.raza?.trim();
      if (!raza) {
        toast("La raza es obligatoria para guardar la ficha.", "error");
        return;
      }
      const dogPayload = {
        nombre: data.nombre?.trim(),
        edad: Number(data.edad),
        disponible: data.disponible === "true",
        codigoInterno: data.codigoInterno?.trim(),
      };
      const fichaPayload = {
        raza,
        sexo: normalizeSexo(data.sexo) || null,
        fechaNacimiento: data.fechaNacimiento,
        peso: data.peso?.trim(),
        altura: data.altura?.trim(),
        colores: data.colores?.trim(),
        pelaje: data.pelaje?.trim(),
        esperanzaDeVida: data.esperanzaDeVida?.trim(),
      };
      let savedDog;
      if (form.dataset.id) {
        const currentDog = state.dogs.find(
          (dog) => String(dog.id) === String(form.dataset.id),
        );
        savedDog = await request(`/api/perros/${form.dataset.id}`, {
          method: "PUT",
          body: JSON.stringify(dogPayload),
        });
        if (currentDog?.ficha?.id) {
          const savedFicha = await request(
            `/api/fichas/fichas/${currentDog.ficha.id}`,
            {
              method: "PUT",
              body: JSON.stringify({ id: currentDog.ficha.id, ...fichaPayload }),
            },
          );
          savedDog = { ...savedDog, ficha: savedFicha };
        } else {
          toast(
            "El perro fue actualizado, pero la API no expone una ruta para crear ficha en un perro existente sin ficha.",
            "error",
          );
        }
      } else {
        savedDog = await request("/api/perros/crear", {
          method: "POST",
          body: JSON.stringify({ ...dogPayload, ficha: fichaPayload }),
        });
      }
      let savedWithBackendMismatch = false;
      if (savedDog?.id) {
        const nextDog = await withFullFicha(savedDog);
        state.dogs = [
          nextDog,
          ...state.dogs.filter((dog) => String(dog.id) !== String(savedDog.id)),
        ];
        if (
          fichaPayload.sexo &&
          nextDog.ficha?.sexo &&
          normalizeSexo(nextDog.ficha.sexo) !== fichaPayload.sexo
        ) {
          savedWithBackendMismatch = true;
          toast(
            `La API recibio ${sexoLabel(fichaPayload.sexo)}, pero devolvio ${sexoLabel(nextDog.ficha.sexo)}. Revisa el guardado de sexo en el backend.`,
            "error",
          );
        }
      }
      if (!savedWithBackendMismatch) toast("Perro guardado correctamente en la API.");
    }
    if (kind === "person") {
      const type = form.dataset.type,
        endpoint =
          type === "veterinarios" ? "/api/veterinarios" : "/api/empleados";
      if (form.dataset.id)
        await request(`${endpoint}/${form.dataset.id}`, {
          method: "PUT",
          body: JSON.stringify(data),
        });
      else
        await request(endpoint, { method: "POST", body: JSON.stringify(data) });
      toast("Integrante guardado correctamente.");
    }
    if (kind === "medical") {
      const type = form.dataset.type,
        group = data.grupo || "",
        endpoint =
          type === "chequeos"
            ? "/api/chequeos"
            : type === "vacunas"
              ? `/api/vacunas/${group}`
              : `/api/desparacitar/${group}`;
      const payload = { ...data, idPerro: Number(data.idPerro) };
      delete payload.cantidad;
      delete payload.unidad;
      delete payload.grupo;
      await request(
        `${endpoint}?cantidad=${encodeURIComponent(data.cantidad)}&unidad=${encodeURIComponent(data.unidad)}`,
        { method: "POST", body: JSON.stringify(payload) },
      );
      toast("Registro clinico guardado correctamente.");
    }
    closeModal();
    render();
  } catch (err) {
    toast(err.message || "No se pudo guardar el registro.", "error");
  }
}
async function handleAction(e) {
  const b = e.target.closest("[data-action]");
  if (!b) return;
  const a = b.dataset.action,
    id = b.dataset.id;
  if (a === "go-tab") {
    setPage(b.dataset.tab);
    return;
  }
  if (a === "new-dog") {
    if (!can("createDog")) return actionDenied();
    return openModal("dog");
  }
  if (a === "detail") {
    const d = state.dogs.find((x) => String(x.id) === id);
    return openModal("detail", d);
  }
  if (a === "edit-dog") {
    if (!can("editDog")) return actionDenied();
    let d = state.dogs.find((x) => String(x.id) === id);
    if (d?.id) d = await withFullFicha(d);
    return openModal("dog", d);
  }
  if (a === "availability") {
    if (!can("availability")) return actionDenied();
    try {
      await request(
        `/api/perros/${id}/disponibilidad?disponible=${b.dataset.value}`,
        { method: "PATCH" },
      );
      state.dogs.find((x) => String(x.id) === id).disponible =
        b.dataset.value === "true";
      render();
      toast("Disponibilidad actualizada.");
    } catch (err) {
      toast(err.message, "error");
    }
    return;
  }
  if (a === "delete-dog") {
    if (!can("deleteDog")) return actionDenied();
    const d = state.dogs.find((x) => String(x.id) === id);
    if (
      !confirm(`Eliminar a ${d?.nombre || "este perro"} y su ficha tecnica?`)
    )
      return;
    try {
      await request(`/api/perros/${id}`, { method: "DELETE" });
      state.dogs = state.dogs.filter((x) => String(x.id) !== id);
      render();
      toast("Perro eliminado.");
    } catch (err) {
      toast(err.message, "error");
    }
    return;
  }
  if (a === "new-person") {
    if (!can("managePeople")) return actionDenied();
    return openModal("person", { type: b.dataset.type });
  }
  if (a === "edit-person") {
    if (!can("managePeople")) return actionDenied();
    const list =
      b.dataset.type === "veterinarios" ? state.vets : state.employees;
    return openModal("person", {
      ...list.find((x) => String(x.id) === id),
      type: b.dataset.type,
    });
  }
  if (a === "person-status") {
    if (!can("managePeople")) return actionDenied();
    const endpoint =
      b.dataset.type === "veterinarios"
        ? "/api/veterinarios"
        : "/api/empleados";
    try {
      await request(`${endpoint}/${id}/estado?activo=${b.dataset.value}`, {
        method: "PATCH",
      });
      const list =
        b.dataset.type === "veterinarios" ? state.vets : state.employees;
      list.find((x) => String(x.id) === id).activo = b.dataset.value === "true";
      render();
      toast("Estado actualizado.");
    } catch (err) {
      toast(err.message, "error");
    }
    return;
  }
  if (a === "new-medical") {
    if (!can("medical")) return actionDenied();
    return openModal("medical", { type: b.dataset.type });
  }
}
document.addEventListener("click", handleAction);
document.addEventListener("click", (e) => {
  const f = e.target.closest("[data-filter]");
  if (f) {
    state.dogFilter = f.dataset.filter;
    render();
  }
  const v = e.target.closest("[data-view]");
  if (v) {
    state.dogView = v.dataset.view;
    render();
  }
  if (e.target.closest("[data-close]") || e.target === $("#modal-backdrop"))
    closeModal();
});
document.addEventListener("input", (e) => {
  if (e.target.id === "dog-search") {
    state.dogSearch = e.target.value;
    $("#dog-results").innerHTML = filteredDogs().length
      ? state.dogView === "cards"
        ? `<section class="dog-grid">${filteredDogs().map(dogCard).join("")}</section>`
        : dogTable(filteredDogs())
      : `<div class="empty">${icon("search-x")}<div>No hay perros que coincidan con la consulta.</div></div>`;
    refreshIcons();
  }
  if (e.target.id === "file-search") {
    const q = e.target.value.toLowerCase();
    const visibleDogs =
      state.role === "adoptante"
        ? state.dogs.filter((d) => d.disponible)
        : state.dogs;
    $("#file-body").innerHTML = fileRows(
      visibleDogs
        .filter((d) =>
          `${d.ficha?.raza || ""} ${d.ficha?.sexo || ""} ${d.ficha?.colores || ""}`
            .toLowerCase()
            .includes(q),
        )
        .map((d) => ({ d, f: d.ficha || {} })),
    );
  }
  if (e.target.classList.contains("person-search")) {
    const isVet = e.target.dataset.type === "veterinarios",
      list = isVet ? state.vets : state.employees,
      q = e.target.value.toLowerCase();
    $(isVet ? "#vets-body" : "#employees-body").innerHTML = personRows(
      list.filter((p) =>
        `${p.nombre} ${p.apellido} ${p.cedula}`.toLowerCase().includes(q),
      ),
      isVet,
    );
  }
});
$("#modal-form").addEventListener("submit", submitForm);
$("#close-modal").addEventListener("click", closeModal);
$("#refresh-page").addEventListener("click", () => {
  ping();
  render();
});
$("#new-record").addEventListener("click", () => {
  if (["perros", "fichas"].includes(state.tab)) {
    if (!can("createDog")) return actionDenied();
    return openModal("dog");
  }
  if (["veterinarios", "empleados"].includes(state.tab)) {
    if (!can("managePeople")) return actionDenied();
    return openModal("person", { type: state.tab });
  }
  const type = ["chequeos", "vacunas", "desparasitacion"].includes(state.tab)
    ? state.tab
    : "chequeos";
  if (!can("medical")) return actionDenied();
  openModal("medical", { type });
});
$("#menu-button").addEventListener("click", () =>
  $("#sidebar").classList.toggle("open"),
);
$("#login-form").addEventListener("submit", (e) => {
  e.preventDefault();
  login(new FormData(e.currentTarget).get("role"));
});
$("#logout-button").addEventListener("click", logout);
document
  .querySelectorAll(".nav-item")
  .forEach((b) => b.addEventListener("click", () => setPage(b.dataset.tab)));
if (isLoggedIn()) {
  showLogin(false);
  if (!canSee(state.tab)) state.tab = roleConfig().tabs[0];
  const [kicker, title] = pages[state.tab];
  $("#page-kicker").textContent = kicker;
  $("#page-title").textContent = title;
  syncRoleUI();
  render();
  ping();
} else {
  showLogin(true);
}
refreshIcons();
