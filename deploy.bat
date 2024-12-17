@echo off
set webapp=C:\wildfly-26.1.2.Final\standalone\deployments
set name=fabrication-bloc
xcopy /s /e /i "bin\*" "web\WEB-INF\classes\"

cd web
jar -cvf "%name%.war" .

del %webapp%\%name%.war
del %webapp%\%name%.war.deployed
copy "%name%.war" "%webapp%"

del %name%.war

cd ..
standalone.bat
pause