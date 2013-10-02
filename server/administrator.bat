@echo off
cls

set CLASSPATH=..\api\jogre.jar
set CLASSPATH=%CLASSPATH%;.
set CLASSPATH=%CLASSPATH%;classes
set CLASSPATH=%CLASSPATH%;lib\dom4j.jar

java org.jogre.server.administrator.JogreServerAdministrator
