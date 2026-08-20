-- =============================================================================
-- Base de Datos: CanisCare (Veterinaria y Gestión Canina)
-- Motor: PostgreSQL 16
-- =============================================================================

-- 1. Tabla de Fichas Técnicas (Características biológicas y médicas de la raza/perro)
CREATE TABLE IF NOT EXISTS fichas (
    id BIGSERIAL PRIMARY KEY,
    raza VARCHAR(100) NOT NULL,
    sexo VARCHAR(20),
    fecha_nacimiento DATE,
    peso VARCHAR(50),
    altura VARCHAR(50),
    colores VARCHAR(150),
    pelaje VARCHAR(100),
    esperanza_de_vida VARCHAR(50),
    codigo_interno VARCHAR(50)
);

-- 2. Tabla de Perros
CREATE TABLE IF NOT EXISTS perros (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    edad INT,
    disponible BOOLEAN DEFAULT TRUE,
    codigo_interno VARCHAR(50),
    foto VARCHAR(255),
    ficha_id BIGINT UNIQUE,
    CONSTRAINT fk_perro_ficha FOREIGN KEY (ficha_id) REFERENCES fichas(id) ON DELETE CASCADE
);

-- 3. Tabla de Veterinarios
CREATE TABLE IF NOT EXISTS veterinarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    cedula VARCHAR(50) UNIQUE NOT NULL,
    telefono VARCHAR(50),
    email VARCHAR(150),
    especialidad VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE
);

-- 4. Tabla de Empleados / Personal
CREATE TABLE IF NOT EXISTS empleados (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    cedula VARCHAR(50) UNIQUE NOT NULL,
    telefono VARCHAR(50),
    email VARCHAR(150),
    direccion VARCHAR(200),
    activo BOOLEAN DEFAULT TRUE
);

-- 5. Tabla de Chequeos Médicos
CREATE TABLE IF NOT EXISTS chequeos (
    id BIGSERIAL PRIMARY KEY,
    perro_id BIGINT NOT NULL,
    temperatura NUMERIC(4,1),
    frecuencia_cardiaca INT,
    frecuencia_respiratoria INT,
    diagnostico TEXT,
    cantidad INT DEFAULT 30,
    unidad VARCHAR(20) DEFAULT 'DAYS',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_alerta TIMESTAMP,
    CONSTRAINT fk_chequeo_perro FOREIGN KEY (perro_id) REFERENCES perros(id) ON DELETE CASCADE
);

-- 6. Tabla de Vacunación
CREATE TABLE IF NOT EXISTS vacunas (
    id BIGSERIAL PRIMARY KEY,
    perro_id BIGINT NOT NULL,
    grupo VARCHAR(50) NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    cantidad INT DEFAULT 1,
    unidad VARCHAR(20) DEFAULT 'YEARS',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_alerta TIMESTAMP,
    CONSTRAINT fk_vacuna_perro FOREIGN KEY (perro_id) REFERENCES perros(id) ON DELETE CASCADE
);

-- 7. Tabla de Desparasitación
CREATE TABLE IF NOT EXISTS desparasitaciones (
    id BIGSERIAL PRIMARY KEY,
    perro_id BIGINT NOT NULL,
    grupo VARCHAR(50) NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    cantidad INT DEFAULT 3,
    unidad VARCHAR(20) DEFAULT 'MONTHS',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_alerta TIMESTAMP,
    CONSTRAINT fk_desparasitacion_perro FOREIGN KEY (perro_id) REFERENCES perros(id) ON DELETE CASCADE
);

-- =============================================================================
-- Datos de prueba iniciales (Seeds)
-- =============================================================================

-- Inserción de Fichas
INSERT INTO fichas (raza, sexo, fecha_nacimiento, peso, altura, colores, pelaje, esperanza_de_vida, codigo_interno)
VALUES 
('Golden Retriever', 'MACHO', '2022-04-10', '32 kg', '58 cm', 'Dorado claro', 'Largo y denso', '12-14 anos', 'FICHA-001'),
('Pastor Aleman', 'HEMBRA', '2021-08-15', '29 kg', '55 cm', 'Negro y fuego', 'Medio duro', '10-13 anos', 'FICHA-002'),
('Beagle', 'MACHO', '2023-01-20', '14 kg', '38 cm', 'Tricolor', 'Corto y suave', '12-15 anos', 'FICHA-003');

-- Inserción de Perros
INSERT INTO perros (nombre, edad, disponible, codigo_interno, ficha_id)
VALUES 
('Max', 3, TRUE, 'CAN-001', 1),
('Luna', 4, TRUE, 'CAN-002', 2),
('Rocky', 2, FALSE, 'CAN-003', 3);

-- Inserción de Veterinarios
INSERT INTO veterinarios (nombre, apellido, cedula, telefono, email, especialidad, activo)
VALUES 
('Carlos', 'Mendoza', '1098765432', '3151234567', 'carlos.mendoza@caniscare.com', 'Cirugia y Medicina General', TRUE),
('Ana', 'Gomez', '1098765433', '3169876543', 'ana.gomez@caniscare.com', 'Dermatologia y Vacunacion', TRUE);

-- Inserción de Empleados
INSERT INTO empleados (nombre, apellido, cedula, telefono, email, direccion, activo)
VALUES 
('Laura', 'Perez', '1012345678', '3201112233', 'laura.perez@caniscare.com', 'Calle 45 # 12-34', TRUE),
('David', 'Torres', '1012345679', '3214445566', 'david.torres@caniscare.com', 'Carrera 7 # 88-10', TRUE);

-- Inserción de Chequeos Medicos
INSERT INTO chequeos (perro_id, temperatura, frecuencia_cardiaca, frecuencia_respiratoria, diagnostico, fecha_alerta)
VALUES 
(1, 38.5, 95, 24, 'Chequeo general en perfecto estado de salud.', CURRENT_TIMESTAMP + INTERVAL '30 days'),
(2, 38.8, 100, 26, 'Control de peso y piel normal.', CURRENT_TIMESTAMP + INTERVAL '30 days');

-- Inserción de Vacunas
INSERT INTO vacunas (perro_id, grupo, tipo, fecha_alerta)
VALUES 
(1, 'adultos', 'Rabia anual', CURRENT_TIMESTAMP + INTERVAL '1 year'),
(2, 'adultos', 'Polivalente canina', CURRENT_TIMESTAMP + INTERVAL '1 year');

-- Inserción de Desparasitaciones
INSERT INTO desparasitaciones (perro_id, grupo, tipo, fecha_alerta)
VALUES 
(1, 'adultos', 'Tratamiento trimestral endoparasitos', CURRENT_TIMESTAMP + INTERVAL '3 months'),
(3, 'cachorros', 'Desparasitacion preventiva', CURRENT_TIMESTAMP + INTERVAL '1 month');
