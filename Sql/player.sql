DROP TABLE IF EXISTS player_list;

CREATE TABLE IF NOT EXISTS player_list (
  uuid         VARCHAR(36) NOT NULL,
  display_name VARCHAR(20) NOT NULL,
  PRIMARY KEY (uuid)
);

DROP TABLE IF EXISTS player_login_history;

CREATE TABLE IF NOT EXISTS player_login_history (
  uuid       VARCHAR(36) NOT NULL,
  ip         VARCHAR(24) NOT NULL,
  login      TIMESTAMP   NOT NULL,
  lastActive TIMESTAMP   NOT NULL,
  PRIMARY KEY (uuid, login)
);

DROP TABLE IF EXISTS player_travel_distance;

CREATE TABLE IF NOT EXISTS player_travel_distance (
  uuid     VARCHAR(36) NOT NULL,
  distance DOUBLE      NOT NULL,
  PRIMARY KEY (uuid)
);

DROP TABLE IF EXISTS player_deaths;

CREATE TABLE IF NOT EXISTS player_deaths (
  uuid   VARCHAR(36) NOT NULL,
  type   VARCHAR(26) NOT NULL,
  player VARCHAR(36) NULL,
  amount INTEGER     NOT NULL,
  PRIMARY KEY (uuid, type, player)
);