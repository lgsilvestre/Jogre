#!/bin/sh

export CLASSPATH=../../../lib/hsqldb.jar

java org.hsqldb.Server -database.0 file:jogre_hsqldb -dbname.0 jogre_hsqldb &
