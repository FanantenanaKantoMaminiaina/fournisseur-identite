INSERT INTO utilisateur (email, mdp)
VALUES 
('admin@gmail.com', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918');

INSERT INTO configuration (duree_vie_token, duree_vie_pin, limite_tentative)
VALUES (3600, 90, 3);
