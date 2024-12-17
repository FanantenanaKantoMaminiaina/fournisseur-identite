psql -U postgres -d postgres

create database fournisseur_identite;

\c fournisseur_identite;

-- psql -U postgres -d fournisseur_identite

CREATE TABLE utilisateur(
   id_utilisateur SERIAL,
   email VARCHAR(180)  NOT NULL,
   mdp VARCHAR(256)  NOT NULL,
   PRIMARY KEY(id_utilisateur),
   UNIQUE(email)
);

INSERT INTO utilisateur (email, mdp)
VALUES
('alice.jones@gmail.com', '2bd806c97f0e00af1a1fc3328fa763a9269723c8db8fac4f93af71db186d6e90');



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

SELECT t.nb FROM tentative t  JOIN utilisateur u ON t.id_utilisateur = u.id_utilisateur WHERE u.email='alice@gmail.com';