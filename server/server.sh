#!/bin/sh

export CLASSPATH=../api/jogre.jar
export CLASSPATH=$CLASSPATH:.
export CLASSPATH=$CLASSPATH:classes
export CLASSPATH=$CLASSPATH:lib/dom4j.jar
export CLASSPATH=$CLASSPATH:lib/mysql-connector-java-3.1.12-bin.jar
export CLASSPATH=$CLASSPATH:lib/hsqldb.jar
export CLASSPATH=$CLASSPATH:lib/ibatis-2.3.0.677.jar

java org.jogre.server.JogreServer