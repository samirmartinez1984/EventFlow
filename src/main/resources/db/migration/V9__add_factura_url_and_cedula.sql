-- Añadir columna para la URL de la factura en la tabla de compras
ALTER TABLE compra ADD COLUMN factura_url VARCHAR(512);

-- Añadir columna para la cédula en la tabla de usuarios con restricción de unicidad
ALTER TABLE usuarios ADD COLUMN cedula VARCHAR(255) UNIQUE;
