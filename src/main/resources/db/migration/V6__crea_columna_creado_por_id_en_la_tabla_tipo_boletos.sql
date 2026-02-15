ALTER TABLE tipo_boleto ADD COLUMN creado_por_id BIGINT NOT NULL;
ALTER TABLE tipo_boleto ADD CONSTRAINT fk_tipo_boleto_usuario
 FOREIGN KEY (creado_por_id) REFERENCES usuarios(id);