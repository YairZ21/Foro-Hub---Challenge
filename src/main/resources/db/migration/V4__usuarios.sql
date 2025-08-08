CREATE TABLE usuarios (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(200) NOT NULL,
  role     VARCHAR(20)  NOT NULL DEFAULT 'USER'
);

INSERT INTO usuarios (username, password, role)
VALUES ('joji', '{noop}123456', 'USER');
