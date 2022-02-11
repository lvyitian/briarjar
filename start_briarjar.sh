#!/bin/sh
java -jar --add-opens=java.base/java.lang.reflect=ALL-UNNAMED build/libs/briarjar-0.1-all.jar $1

