
DROP TABLE IF EXISTS SUBSCRIPTION_INFO_VALUES;
DROP TABLE IF EXISTS SUBSCRIPTION_INFO;

CREATE TABLE SUBSCRIPTION_INFO (
  ID	INTEGER NOT NULL AUTO_INCREMENT,
  CLIENT_ID	varchar(100) NOT NULL,
  AUDIENCE VARCHAR(250) NOT NULL,
  SECRET CHAR(36),
  ROLES VARCHAR(512),
  PRIMARY KEY (ID),
  constraint UniqueClientIdAndAudience unique (CLIENT_ID, AUDIENCE)
);

CREATE TABLE SUBSCRIPTION_INFO_VALUES (
  ID	INTEGER NOT NULL AUTO_INCREMENT,
  SUBSCRIPTION_INFO_ID	INTEGER NOT NULL,
  KEY	VARCHAR(100)	NOT NULL,
  VALUE	VARCHAR(100)	NOT NULL,
  PRIMARY KEY (ID),
  FOREIGN KEY (SUBSCRIPTION_INFO_ID) REFERENCES SUBSCRIPTION_INFO(ID)
);
