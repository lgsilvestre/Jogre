@echo off
cls

set CLASSPATH=..\api\jogre.jar
set CLASSPATH=%CLASSPATH%;.
set CLASSPATH=%CLASSPATH%;classes
set CLASSPATH=%CLASSPATH%;lib\dom4j.jar
set CLASSPATH=%CLASSPATH%;lib\mysql-connector-java-3.1.12-bin.jar
set CLASSPATH=%CLASSPATH%;lib\hsqldb.jar
set CLASSPATH=%CLASSPATH%;lib\ibatis-2.3.0.677.jar

java org.jogre.server.JogreServer
