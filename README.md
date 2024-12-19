# fournisseur-identite

-Inserer manuelement les tables dans le repertoire ./base

-Pour la configuration:
    -Il y a la :
        -duree_vie_token
        -duree_vie_pin
        -limite_tentative
        -emailExpediteur
        -passwordExpediteur
    
    Tout ses configuration peuvent etre modifiees dans le ./conf/database.properties


-Voici les api que vous pouvez utiliser:
    Inscription:
        -POST : http://localhost:8080/fournisseur-identite/api/inscription:
            Body:
                {
                    "email":"admin@gmail.com",
                    "mdp":"admin"
                }
        -GET :  http://localhost:8080/fournisseur-identite/api/inscription?validationToken=7d7df842675ffc66c4e043eb52b27f2f33ab984c1a78ad9c7e9f48adea00b5fc

    Login:
        -POST : http://localhost:8080/fournisseur-identite/api/login
            Body:
                {
                    "email":"admin@gmail.com",
                    "mdp":"admin"
                }

    Authentification:
        -POST : http://localhost:8080/fournisseur-identite/api/authentification
            Body:
                {
                    "email":"admin@gmail.com",
                    "pin":"506967"
                }
        
    Gestion de compte:
        -POST : http://localhost:8080/fournisseur-identite/api/updateCompte
            Body:
                {
                    "mdp":"adminadmin"
                }
            Authorisation:
                Bearer Token : 0426c7e801ea72827e2fa9c25204bae19fc29af4e82ce9271258514266de4326

    Reinitialisation Tentative:
        -GET :  http://localhost:8080/fournisseur-identite/api/resetTentative?email=admin@gmail.com