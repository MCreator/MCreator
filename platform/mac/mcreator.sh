#!/bin/zsh

# ---------------------------------------------------------------------
# MCreator Launcher file for macOS
# zsh is now used due to bash being missing on newer macOS distributions
# ---------------------------------------------------------------------

#WARNING: WHEN CHANGING THIS FILE, MCREATOR BINARIES NEED TO BE RECOMPILED WITH SHC.
#To compile for both binaries on an aarch64 machine, follow these instructions:
#1. Install Rosetta
#2. Install 2 instances of homebrew, one with the arch -x86_64 prefix
#3. Brew shc for both instances with brew install shc and arch -x86_64 brew install shc
#4. Compile x86_64 binary with CC="clang -mmacosx-version-min=11.0" /usr/local/bin/shc -v -r -f mcreator.sh -o mcreator_x64
#5. Compile aarch64 binary with CC="clang -mmacosx-version-min=11.0" shc -v -r -f mcreator.sh -o mcreator_aarch64

setopt +o nomatch
DIR=$(cd "$(dirname "$0")"; pwd)
cd "$DIR"
cd ..

./jdk.bundle/Contents/Home/bin/java \
  --add-opens=java.base/java.lang=ALL-UNNAMED \
  -Xdock:name=MCreator \
  -Xdock:icon=./Resources/mcreatorapp.icns \
  -Dapple.awt.application.appearance=system \
  -cp .:./lib/mcreator.jar:./lib/* \
  net.mcreator.Launcher