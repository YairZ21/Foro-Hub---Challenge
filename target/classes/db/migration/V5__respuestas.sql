CREATE TABLE respuestas (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  mensaje VARCHAR(1000) NOT NULL,
  creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  topico_id BIGINT NOT NULL,
  usuario_id BIGINT NOT NULL,
  CONSTRAINT fk_respuesta_topico  FOREIGN KEY (topico_id)  REFERENCES topicos(id)  ON DELETE CASCADE,
  CONSTRAINT fk_respuesta_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_respuestas_topico  ON respuestas(topico_id);
CREATE INDEX idx_respuestas_usuario ON respuestas(usuario_id);
