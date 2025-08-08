ALTER TABLE topicos
  ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTO';


CREATE INDEX idx_topicos_creado_en ON topicos(creado_en);
