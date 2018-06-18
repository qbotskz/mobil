
CREATE TABLE access_level
(
  id INTEGER PRIMARY KEY NOT NULL,
  name VARCHAR(200),
  enum_name VARCHAR(200) NOT NULL
);
CREATE UNIQUE INDEX access_level_enum_name_uindex ON access_level (enum_name);


CREATE TABLE user
(
  id SERIAL PRIMARY KEY NOT NULL,
  chat_id BIGINT NOT NULL,
  user_name VARCHAR(200),
  user_surname VARCHAR(200),
  id_access_level INTEGER,
  CONSTRAINT user_access_level_id_fk FOREIGN KEY (id_access_level) REFERENCES access_level (id)
);
CREATE UNIQUE INDEX user_chat_id_uindex ON user (chat_id);

INSERT INTO access_level (id, name, enum_name) VALUES (1, 'Администратор', 'ADMIN');
INSERT INTO access_level (id, name, enum_name) VALUES (2, 'Чтение и запись', 'READ_AND_WRITE');
INSERT INTO access_level (id, name, enum_name) VALUES (3, 'Чтение', 'READ');
INSERT INTO access_level (id, name, enum_name) VALUES (4, 'Доступ запрещен', 'WITHOUT_ACCESS');