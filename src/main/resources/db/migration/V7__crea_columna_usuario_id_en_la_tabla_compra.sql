ALTER TABLE compra ADD COLUMN usuario_id BIGINT NOT NULL;
ALTER TABLE compra ADD CONSTRAINT fk_compra_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id);
