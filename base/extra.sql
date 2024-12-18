CREATE TABLE utilisateur_temp(
   id SERIAL,
   email VARCHAR(50)  NOT NULL,
   mdp VARCHAR(256) NOT NULL,
   validation_token TEXT NOT NULL,
   expiration_date TIMESTAMP,
   is_used VARCHAR(1) DEFAULT 'N',
   PRIMARY KEY(id),
   UNIQUE(email),
   UNIQUE(validation_token)
);

java -cp ".;N:\ITU\S5\WEB\ProjetCloud\Examen\fournisseur-identite\lib\*;" util.Test

