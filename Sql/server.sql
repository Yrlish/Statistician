DROP TABLE IF EXISTS server_uptime;

CREATE TABLE server_uptime (
  uid   INT AUTO_INCREMENT PRIMARY KEY,
  start BIGINT NOT NULL UNIQUE,
  stop  BIGINT NOT NULL
);