filename=%game_id%/client.bat
@echo off 
cls
java -classpath .;classes;..\..\api\jogre.jar org.jogre.%game_id%.client.%Game_id%ClientFrame