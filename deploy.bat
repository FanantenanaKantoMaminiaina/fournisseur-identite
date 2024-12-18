@echo off
set webapp=C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps
set name=fournisseur-identite
xcopy /s /e /i "bin\*" "web\WEB-INF\classes\"
xcopy /s /e /i "conf\database.properties" "web\WEB-INF\classes\"
xcopy /s /e /i "lib\*" "web\WEB-INF\lib\"
xcopy /s /e /i "assets\*" "web\assets\"

cd web
jar -cvf "%name%.war" .

del %webapp%\%name%.war
del %webapp%\%name%.war.deployed
copy "%name%.war" "%webapp%"

del %name%.war

pause