#!/bin/sh

export CLASSPATH=../api/jogre.jar
export CLASSPATH=$CLASSPATH:.
export CLASSPATH=$CLASSPATH:classes
export CLASSPATH=$CLASSPATH:lib/dom4j.jar
export CLASSPATH=$CLASSPATH:lib/mysql-connector-java-3.1.12-bin.jar

java org.jogre.server.administrator.JogreServerAdministrator

