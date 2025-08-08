CREATE TABLE topicos (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  titulo  VARCHAR(100)  NOT NULL,
  mensaje VARCHAR(600)  NOT NULL,
  autor   VARCHAR(100)  NOT NULL,
  curso   VARCHAR(100)  NOT NULL,
  creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_titulo_mensaje UNIQUE (titulo, mensaje)
);

