#!/bin/sh
java -jar --add-opens=java.base/java.lang.reflect=ALL-UNNAMED build/libs/briarjar-1.00-all.jar "$@"

