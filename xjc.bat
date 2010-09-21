@ECHO OFF

ECHO WARNING! All generated before Java classes will be overridden! 
ECHO Would you like to continue?
ECHO ...

PAUSE

ECHO Removing previously generated model classes
DEL /F /Q "src\main\java\com\viewer4d\config\model\*.java"

ECHO Generating model classes
xjc.exe -d "src/main/java" -extension "src/main/resources/PlainFigureConfig.xsd"

ECHO Done!