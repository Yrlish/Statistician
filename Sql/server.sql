DROP TABLE IF EXISTS server_uptime;

CREATE TABLE server_uptime (
  start TIMESTAMP NOT NULL PRIMARY KEY,
  stop  TIMESTAMP NOT NULL
);