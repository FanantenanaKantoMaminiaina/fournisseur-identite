CREATE TABLE utilisateur(
   id INTEGER,
   nom VARCHAR(50)  NOT NULL,
   prenom VARCHAR(50) ,
   dtn DATE NOT NULL,
   email VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id),
   UNIQUE(email)
);

CREATE TABLE connexion(
   id VARCHAR(50) ,
   token VARCHAR(100) ,
   expiration_token TIMESTAMP,
   id_1 INTEGER NOT NULL,
   PRIMARY KEY(id),
   UNIQUE(token),
   FOREIGN KEY(id_1) REFERENCES utilisateur(id)
);

CREATE TABLE tentative(
   id VARCHAR(50) ,
   nb SMALLINT,
   date_reconnexion TIMESTAMP,
   id_1 INTEGER NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_1) REFERENCES utilisateur(id)
);

CREATE TABLE authentification(
   id VARCHAR(50) ,
   code_pin VARCHAR(50) ,
   expiration_pin TIMESTAMP,
   id_1 INTEGER NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_1) REFERENCES utilisateur(id)
);
