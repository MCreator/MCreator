#!/bin/bash
#
# ---------------------------------------------------------------------
# MCreator Launcher file for MacOS
# ---------------------------------------------------------------------
#

# WARNING: WHEN CHANGING THIS FILE, "mcreator" binary needs to be recompiled using "shc -r -f mcreator.sh"

DIR=$(cd "$(dirname "$0")"; pwd)

cd "$DIR"

./jdk/Contents/Home/bin/java -Xdock:name=MCreator -Xdock:icon=../Resources/mcreatorapp.icns -Dapple.awt.application.appearance=system -cp .:./lib/mcreator.jar:./lib/* net.mcreator.Launcher