CREATE TABLE player_list (
  id           INT AUTO_INCREMENT UNIQUE,
  uuid         VARCHAR(36) NOT NULL,
  display_name VARCHAR(20) NOT NULL,
  PRIMARY KEY (uuid)
);

CREATE TABLE player_login_history (
  id     INT AUTO_INCREMENT UNIQUE,
  uuid   VARCHAR(36) NOT NULL,
  ip     VARCHAR(24) NOT NULL,
  login  BIGINT      NOT NULL,
  logout BIGINT      NOT NULL,
  PRIMARY KEY (uuid, login)
);
