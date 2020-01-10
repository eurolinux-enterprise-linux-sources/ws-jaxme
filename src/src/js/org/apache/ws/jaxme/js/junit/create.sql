CREATE CACHED TABLE MAIN (  ID BIGINT NOT NULL,  NAME VARCHAR(60) NOT NULL,  VER INT NOT NULL,  SIG BINARY(16) NOT NULL,  DATE DATE NOT NULL,  PRIMARY KEY (ID, VER));
CREATE CACHED TABLE SUB (  MTID BIGINT NOT NULL,  MTVER INT NOT NULL,  ADDRESS VARCHAR(60) NOT NULL,  ID VARCHAR(32) NOT NULL,  EMAIL VARCHAR(60),  PRIMARY KEY (ID),  FOREIGN KEY (MTID, MTVER) REFERENCES MAIN (ID, VER));
CREATE CACHED TABLE SUBSUB (  ID VARCHAR(32) NOT NULL,  MTID BIGINT NOT NULL,  MTVER INT NOT NULL,  SSTID VARCHAR(32) NOT NULL,  MTTS TIMESTAMP NOT NULL,  PRIMARY KEY (ID),  FOREIGN KEY (MTID, MTVER) REFERENCES MAIN (ID, VER),  FOREIGN KEY (SSTID) REFERENCES SUB (ID));
