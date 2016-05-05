DROP TABLE IF EXISTS player_list;

CREATE TABLE player_list (
  uuid         VARCHAR(36) NOT NULL,
  display_name VARCHAR(20) NOT NULL,
  PRIMARY KEY (uuid)
);

DROP TABLE IF EXISTS player_login_history;

CREATE TABLE player_login_history (
  uuid   VARCHAR(36) NOT NULL,
  ip     VARCHAR(24) NOT NULL,
  login  TIMESTAMP      NOT NULL,
  logout TIMESTAMP      NOT NULL,
  PRIMARY KEY (uuid, login)
);
