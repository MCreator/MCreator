#!/bin/bash
#
# ---------------------------------------------------------------------
# MCreator Launcher file for MacOS
# ---------------------------------------------------------------------
#

# WARNING: WHEN CHANGING THIS FILE, "mcreator" binary needs to be recompiled using "shc -v -r -f mcreator.sh"
# TO COMPILE FOR INTEL ON AN ARM MACHINE, USE "arch -x86_64" PREFIX FOR THE X86 FILE

DIR=$(cd "$(dirname "$0")"; pwd)

cd "$DIR"

./jdk/bin/java \
  --add-opens=java.base/java.lang=ALL-UNNAMED \
  -Xdock:name=MCreator \
  -Xdock:icon=../Resources/mcreatorapp.icns \
  -Dapple.awt.application.appearance=system \
  -cp .:./lib/mcreator.jar:./lib/* \
  net.mcreator.Launcher