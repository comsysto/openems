#!/usr/bin/zsh

echo "Starting OpenEMS Edge"

SCRIPTS_DIR="$( cd "$(dirname "$0")" ; pwd -P )"
WORK_DIR="$(dirname "$SCRIPTS_DIR")"
BUILD_DIR="$WORK_DIR/build"
PATH_TO_OPENEMS_EDGE_JAR="$BUILD_DIR/openems-edge.jar"
FELIX_CM_DIR="$WORK_DIR/config.d"

# check if openems-edge.jar exists
if [ ! -f "$PATH_TO_OPENEMS_EDGE_JAR" ]; then
    echo "openems-edge.jar not found. Please run './gradlew buildEdge' first." >&2
    exit 1
fi

# check if Java 17 is active
if [[ $(java -version 2>&1) != *"17"* ]]; then
    echo "Java 17 is not active. Please run 'sdk use java 17'" >&2
    exit 1
fi


java -XX:+ExitOnOutOfMemoryError -Dfelix.cm.dir="$FELIX_CM_DIR" -Djava.util.concurrent.ForkJoinPool.common.parallelism=100 -jar "$PATH_TO_OPENEMS_EDGE_JAR"