@echo off

rem Définir les autres variables
set src=src\
set lib=lib\
set bin=bin
set temp_src=temp_src

rem Créer le répertoire temporaire
mkdir %temp_src%

rem Passer au répertoire source
cd "%src%"

rem Copier tous les fichiers .java dans le répertoire temporaire
for /r %%F in (*.java) do (
    copy "%%F" "..\%temp_src%"
)

rem Compiler les fichiers Java
javac -cp ../%lib%* -g:vars -d ../%bin% ../%temp_src%/*.java

rem Revenir au répertoire parent
cd ../

rem Mettre en pause pour voir les résultats
pause
