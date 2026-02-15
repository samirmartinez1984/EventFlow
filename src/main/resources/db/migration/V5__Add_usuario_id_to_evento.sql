    -- Añadir la columna para la clave foránea del usuario en la tabla de eventos
    ALTER TABLE evento
    ADD COLUMN usuario_id BIGINT;

    -- Actualizar los eventos existentes para asignarles un usuario por defecto
    -- (IMPORTANTE: Reemplaza '1' con el ID de un usuario ADMIN que ya exista en tu tabla 'usuarios')
    UPDATE evento SET usuario_id = 1 WHERE usuario_id IS NULL;

    -- Hacer que la columna no permita valores nulos
    ALTER TABLE evento
    MODIFY COLUMN usuario_id BIGINT NOT NULL;

    -- Añadir la restricción de clave foránea
    ALTER TABLE evento
    ADD CONSTRAINT fk_evento_on_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id);
