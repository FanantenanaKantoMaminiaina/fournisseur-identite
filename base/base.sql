psql -U postgres -d postgres

create database fournisseur_identite;

\c fournisseur_identite;

psql -U postgres -d fournisseur_identite

CREATE TABLE utilisateur(
   id_utilisateur SERIAL,
   email VARCHAR(180)  NOT NULL,
   mdp VARCHAR(256)  NOT NULL,
   PRIMARY KEY(id_utilisateur),
   UNIQUE(email)
);

CREATE TABLE token(
   id_token SERIAL,
   token VARCHAR(100) ,
   expiration_token TIMESTAMP,
   id_utilisateur INTEGER NOT NULL,
   PRIMARY KEY(id_token),
   UNIQUE(token),
   FOREIGN KEY(id_utilisateur) REFERENCES utilisateur(id_utilisateur)
);

CREATE TABLE tentative(
   id_tentative SERIAL,
   nb SMALLINT,
   date_reconnexion TIMESTAMP,
   id_utilisateur INTEGER NOT NULL,
   PRIMARY KEY(id_tentative),
   FOREIGN KEY(id_utilisateur) REFERENCES utilisateur(id_utilisateur)
);

CREATE TABLE authentification(
   id_authentification SERIAL,
   code_pin VARCHAR(50) ,
   expiration_pin TIMESTAMP,
   id_utilisateur INTEGER NOT NULL,
   PRIMARY KEY(id_authentification),
   FOREIGN KEY(id_utilisateur) REFERENCES utilisateur(id_utilisateur)
);
