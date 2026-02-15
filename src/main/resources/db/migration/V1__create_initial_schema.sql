-- V1__create_initial_schema.sql

-- 1. Tabla EVENTO
CREATE TABLE evento (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_vento VARCHAR(255) NOT NULL,
    fecha_evento TIMESTAMP NOT NULL,
    capacidad_maxima INT NOT NULL
);

-- 2. Tabla TIPO_BOLETO (Clave Foránea a Evento)
CREATE TABLE tipo_boleto (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    evento_id BIGINT NOT NULL,
    nombre_tipo VARCHAR(100) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    boletos_disponibles INT NOT NULL,

    FOREIGN KEY (evento_id) REFERENCES evento(id)
);

-- 3. Tabla COMPRA (Clave Foránea a Tipo_Boleto)
CREATE TABLE compra (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tipo_boleto_id BIGINT NOT NULL,
    cantidad_comprada INT NOT NULL,
    fecha_compra TIMESTAMP NOT NULL,

    FOREIGN KEY (tipo_boleto_id) REFERENCES tipo_boleto(id)
);