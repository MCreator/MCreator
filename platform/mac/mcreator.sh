#!/bin/bash
#
# ---------------------------------------------------------------------
# MCreator Launcher file for MacOS
# ---------------------------------------------------------------------
#

# WARNING: WHEN CHANGING THIS FILE, "mcreator" binary needs to be recompiled using "zsh -r -f mcreator"

DIR=$(cd "$(dirname "$0")"; pwd)

cd "$DIR"

./jdk/Contents/Home/bin/java -Xdock:name=MCreator -Xdock:icon=../Resources/mcreatorapp.icns -cp .:./lib/mcreator.jar:./lib/* net.mcreator.Launcher