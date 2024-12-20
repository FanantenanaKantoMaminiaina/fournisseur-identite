#!/bin/bash

webapps="."
appName="fournisseur-identite"
temp="./temp"
src="./src"
lib="./lib"
web="./web"
bin="./bin"
conf="./conf"
tempjava="./tempjava"

rm -f "../../tomcat/webapps/$appName"
rm -rf "$temp"
rm -rf "$tempjava"

mkdir -p "$temp"
mkdir -p "$tempjava"
mkdir -p "$temp/WEB-INF/lib"
mkdir -p "$temp/WEB-INF/classes"

if [ -d "$web" ]; then
    cp -r "$web"/* "$temp/"
else
    echo "Le répertoire web est introuvable"
fi

if [ -d "$lib" ]; then
    cp -r "$lib"/* "$temp/WEB-INF/lib/"
else
    echo "Le répertoire lib est introuvable"
fi

if [ -d "$src/controller" ] && [ -d "$src/model" ] && [ -d "$src/util" ] && [ -d "$src/connexion" ]; then
    cp -r "$src"/controller/* "$tempjava"
    cp -r "$src"/model/* "$tempjava"
    cp -r "$src"/util/* "$tempjava"
    cp -r "$src"/connexion/* "$tempjava"
else
    echo "Les répertoires src/controller, src/model, src/connexion ou src/util sont introuvables"
fi
echo "Contenu de tempjava :"
ls -l "$tempjava"

if [ -d "$tempjava" ] && [ "$(ls -A $tempjava/*.java)" ]; then
    javac -parameters -cp "$temp/WEB-INF/lib/*" -d "$bin" "$tempjava"/*.java
else
    echo "Aucun fichier Java à compiler dans $tempjava"
fi

if [ -d "$bin" ]; then
    cp -r "$bin"/* "$temp/WEB-INF/classes"
else
    echo "Aucun fichier compilé trouvé dans $bin"
fi

if [ -d "$conf" ]; then
    cp -r "$conf"/* "$temp/WEB-INF/classes"
else
    echo "Aucun fichier compilé trouvé dans $conf"
fi

cd "$temp" && jar -cvf "../$appName.war" *

echo "Déploiement terminé."
