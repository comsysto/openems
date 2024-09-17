#!/usr/bin/zsh

echo "Starting OpenEMS UI"

SCRIPTS_DIR="$( cd "$(dirname "$0")" ; pwd -P )"
WORK_DIR="$(dirname "$SCRIPTS_DIR")"

cd "$WORK_DIR/ui"

# check if @angular/cli is installed globally
if ! command -v ng &> /dev/null
then
    echo "@angular/cli is not installed globally. Please run 'npm install -g @angular/cli'" >&2
    exit 1
fi

npm install
#Run OpenEMS UI
ng serve -c openems-edge-dev
