# fournisseur-identite

-Pour lancer le projet:
    lancer la commande : 
        -docker-compose build --no-cache
        -docker-compose up -d

-Pour la configuration:
    -Il y a la :
        -duree_vie_token (En seconde)
        -duree_vie_pin (En seconde)
        -limite_tentative
        -emailExpediteur
        -passwordExpediteur
    
    Tout ses configuration peuvent etre modifiees dans le ./conf/database.properties

    Si vous modifiez la conf:
    lancer les commandes suivantes: 
        -docker-compose down -v
        -docker-compose build --no-cache
        -docker-compose up -d

-Voici le guide d'utilisation des api:
    Inscription:
        -POST : http://localhost:8080/fournisseur-identite/api/inscription:
            Body:
                {
                    "email":"admin@gmail.com",
                    "mdp":"admin"
                }
        Si l'inscription est correcte,il y a une message dans l'email pour valider l'inscription
        
        Voici une exemple de lien dans l'email pour la validation d'inscription
        -GET :  http://localhost:8080/fournisseur-identite/api/inscription?validationToken=7d7df842675ffc66c4e043eb52b27f2f33ab984c1a78ad9c7e9f48adea00b5fc


    Authentification:
        -POST : http://localhost:8080/fournisseur-identite/api/login
            Body:
                {
                    "email":"admin@gmail.com",
                    "mdp":"admin"
                }
            Si le login est valide,il y a une email envoye au personne qui essaie de se connecter contenant un code pin pour confirmer son authentification.


    Confirmation Authentification(Avec code PIN):
        -POST : http://localhost:8080/fournisseur-identite/api/authentification
            Body:
                {
                    "email":"admin@gmail.com",
                    "pin":"506967"
                }
        Si l’authentification est validée : il y a une token générée.


    Gestion de compte:
        -PUT : http://localhost:8080/fournisseur-identite/api/updateCompte
            Body:
                {
                    "mdp":"adminadmin"
                }
            Authorisation:
                Bearer Token : 0426c7e801ea72827e2fa9c25204bae19fc29af4e82ce9271258514266de4326
        Pour modifer les informations (Ici , le mdp ) : 
        Il faut ajouter dans l'header le token genere pour l'utilisateur dans l'authentification


    Reinitialisation Tentative:
        -GET :  http://localhost:8080/fournisseur-identite/api/resetTentative?email=admin@gmail.com

        Si le compte est bloque(le nombre de tentative depasse la limite) : 
        Il y a une message envoyee dans l'email pour le reinitialiser 