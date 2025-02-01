\c fournisseur_identite

-- Création des tables
CREATE TABLE utilisateur (
   id_utilisateur SERIAL PRIMARY KEY,
   email VARCHAR(180) NOT NULL UNIQUE,
   mdp VARCHAR(256) NOT NULL
);

CREATE TABLE token (
   id_token SERIAL PRIMARY KEY,
   token VARCHAR(100) UNIQUE,
   expiration_token TIMESTAMP,
   id_utilisateur INTEGER NOT NULL,
   FOREIGN KEY (id_utilisateur) REFERENCES utilisateur (id_utilisateur)
);

CREATE TABLE tentative (
   id_tentative SERIAL PRIMARY KEY,
   nb SMALLINT,
   date_reconnexion TIMESTAMP,
   id_utilisateur INTEGER NOT NULL,
   FOREIGN KEY (id_utilisateur) REFERENCES utilisateur (id_utilisateur)
);

CREATE TABLE authentification (
   id_authentification SERIAL PRIMARY KEY,
   code_pin VARCHAR(50),
   expiration_pin TIMESTAMP,
   id_utilisateur INTEGER NOT NULL,
   FOREIGN KEY (id_utilisateur) REFERENCES utilisateur (id_utilisateur)
);

CREATE TABLE utilisateur_temp (
   id SERIAL PRIMARY KEY,
   email VARCHAR(50) NOT NULL UNIQUE,
   mdp VARCHAR(256) NOT NULL,
   validation_token TEXT NOT NULL UNIQUE,
   expiration_date TIMESTAMP,
   is_used BOOLEAN DEFAULT FALSE
);

INSERT INTO utilisateur (id_utilisateur, email, mdp) VALUES
(2 , 'girlspower434@gmail.com', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918');